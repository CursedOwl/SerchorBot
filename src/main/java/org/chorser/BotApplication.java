package org.chorser;


import com.google.gson.reflect.TypeToken;
import org.chorser.entity.config.Authentication;
import org.chorser.config.BotConfiguration;
import org.chorser.entity.config.Conversation;
import org.chorser.entity.config.Function;
import org.chorser.entity.maimai.Song;
import org.chorser.listener.DefaultListener;
import org.chorser.listener.DiscInteractionListener;
import org.chorser.service.IDiscordService;
import org.chorser.service.impl.*;
import org.chorser.util.ConfigReader;
import org.chorser.util.HttpBuilder;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class BotApplication {

    private static final String CONFIG_PATH="config.yml";

    private static final String MAIMAI_SONG_URL="";

    private static BotConfiguration botConfiguration;
    private static Double probability=1d;
    private static Authentication authentication;
    private static List<Conversation> conversations;
    private static List<Function> functionList;
    private static String gptToken;

//    需要设置舞萌猜歌服务专门应对普通对话
    private static DiscGuessGameServiceImpl guessGameServiceCopy;
    private static DiscGeminiServiceImpl geminiServiceCopy;

    private static final List<String> exceptionAnswers=new ArrayList<>();
    private static final HashMap<String, IDiscordService> functions=new HashMap<>();


    private static final Base64.Decoder base64Decoder=Base64.getDecoder();
    private static final Logger log= LoggerFactory.getLogger(BotApplication.class);

    public static void main(String[] args) {

        try {
//            读取配置文件
           BotConfiguration configuration=ConfigReader.readDefaultConfiguration(CONFIG_PATH);
           botConfiguration=configuration;
//           读取配置文件中的属性，虽然我也不知道为什么要这么做
           authentication=configuration.getAuthentication();
           conversations=configuration.getConversations();
           functionList=configuration.getFunctions();
           probability=configuration.getProbability()==null?1:configuration.getProbability();
           gptToken=configuration.getGptToken();

            if(authentication==null){
                throw new IOException();
            }
            if(authentication.getToken()!=null){
                if(authentication.getBase64()){
                    byte[] bytes = base64Decoder.decode(authentication.getToken());
                    String token = new String(bytes);
                    authentication.setToken(token);
                }
                initialDiscordFunctions();
                initialJavacordConnection();
            }else {
                throw new IOException();
            }

        } catch (Exception e) {
            log.error("Fail to read login configuration");
            throw new RuntimeException(e);
        }

    }

    private static void initialJavacordConnection() {
        log.info("Starting with configuration:"+authentication);
//        defaultListener用于处理普通消息事件
        DefaultListener defaultListener = new DefaultListener(authentication,conversations,functions);
        defaultListener.setProbability(probability);
        defaultListener.setExceptionAnswers(exceptionAnswers);
        defaultListener.setGuessGameService(guessGameServiceCopy);
//        interactionListener用于处理交互反馈
        DiscInteractionListener discInteractionListener = new DiscInteractionListener(geminiServiceCopy);
//        设置代理
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809));
        DiscordApi api = new DiscordApiBuilder()
                .setProxy(proxy)
                .setToken(authentication.getToken())
                .setAllIntents()
                .login()
                .join();
        api.addListener(defaultListener);
        api.addListener(discInteractionListener);
        log.info("You can invite the bot by using the following url: " + api.createBotInvite());
    }

    private static void initialDiscordFunctions() {
//        0.初始化服务实现实例
        DiscReplyServiceImpl defaultReplyService = new DiscReplyServiceImpl();
//        1.初始化GPT服务
        if(gptToken==null){
            throw new RuntimeException("Fail to get gpt token");
        }
        DiscGPTServiceImpl defaultGPTService = new DiscGPTServiceImpl(gptToken);
//        2.初始化猜歌服务
        List<Song> responseList = HttpBuilder.getResponseList("https://www.diving-fish.com/api/maimaidxprober/music_data", Song.class);
        @SuppressWarnings("unchecked")
//      获取别名
        HashMap<Integer,List<String>> alias= (HashMap<Integer, List<String>>) HttpBuilder
                .getResponse("https://download.fanyu.site/maimai/alias.json",new TypeToken<HashMap<Integer,List<String>>>(){}.getType() );
        if(responseList.isEmpty()){
            throw new RuntimeException("Fail to get song list");
        }
        DiscGuessGameServiceImpl guessGameService = new DiscGuessGameServiceImpl(responseList,alias);
        guessGameServiceCopy=guessGameService;
//        3.初始化Gemini服务
        DiscGeminiServiceImpl geminiService = new DiscGeminiServiceImpl(botConfiguration.getGeminiApiKey());
        geminiServiceCopy=geminiService;

//        finally.初始化配置服务
        DiscActionRowServiceImpl discActionRowService = new DiscActionRowServiceImpl(geminiService);

        functionList.forEach(function -> {
            switch (function.getMode()){
                case -1:{
                    functions.put(function.getTrigger(),discActionRowService);
                }
                case 0:{
//                0.@机器人的异常回复（不存在于功能列表的）
                    exceptionAnswers.add(function.getAnswer());
                    break;
                }
//                1.普通对话回复
                case 1:{
                    defaultReplyService.add(function.getTrigger(),function.getAnswer());
                    functions.put(function.getTrigger(),defaultReplyService);
                    break;
                }
//                2.默认GPT对话
                case 2:{
                    functions.put(function.getTrigger(),defaultGPTService);
                    break;
                }
//                3.猜歌
                case 3:{
                    guessGameService.getTriggers().add(function.getTrigger());
                    guessGameService.getAnswers().add(function.getAnswer());
                    functions.put(function.getTrigger(),guessGameService);
                    break;
                }
//                4.Gemini
                case 4:{
                    functions.put(function.getTrigger(),geminiService);
                    break;
                }

            }
        });
    }

}