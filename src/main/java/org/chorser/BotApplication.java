package org.chorser;


import com.google.gson.reflect.TypeToken;
import org.chorser.entity.config.Authentication;
import org.chorser.config.BotConfiguration;
import org.chorser.entity.config.Conversation;
import org.chorser.entity.config.Function;
import org.chorser.entity.maimai.Song;
import org.chorser.listener.DefaultListener;
import org.chorser.service.IFunctionService;
import org.chorser.service.impl.GPTServiceImpl;
import org.chorser.service.impl.GuessGameServiceImpl;
import org.chorser.service.impl.ReplyServiceImpl;
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

    private static Double probability=1d;
    private static Authentication authentication;
    private static List<Conversation> conversations;
    private static List<Function> functionList;

    private static GuessGameServiceImpl guessGameServiceCopy;
    private static final List<String> exceptionAnswers=new ArrayList<>();
    private static final HashMap<String, IFunctionService> functions=new HashMap<>();


    private static final Base64.Decoder base64Decoder=Base64.getDecoder();
    private static final Logger log= LoggerFactory.getLogger(BotApplication.class);

    public static void main(String[] args) {

        try {
//            读取配置文件
           BotConfiguration configuration=ConfigReader.readDefaultConfiguration(CONFIG_PATH);
           authentication=configuration.getAuthentication();
           conversations=configuration.getConversations();
           functionList=configuration.getFunctions();
           probability=configuration.getProbability()==null?1:configuration.getProbability();

            if(authentication==null){
                throw new IOException();
            }
            if(authentication.getToken()!=null){
                if(authentication.getBase64()){
                    byte[] bytes = base64Decoder.decode(authentication.getToken());
                    String token = new String(bytes);
                    authentication.setToken(token);
                }
                initialFunctions();
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
        DefaultListener defaultListener = new DefaultListener(authentication,conversations,functions);
        defaultListener.setProbability(probability);
        defaultListener.setExceptionAnswers(exceptionAnswers);
        defaultListener.setGuessGameService(guessGameServiceCopy);
//        设置代理
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809));
        DiscordApi api = new DiscordApiBuilder()
                .setProxy(proxy)
                .setToken(authentication.getToken())
                .setAllIntents()
                .login()
                .join();
        api.addListener(defaultListener);
        log.info("You can invite the bot by using the following url: " + api.createBotInvite());
    }

    private static void initialFunctions() {
//        初始化服务实现实例
        ReplyServiceImpl defaultReplyService = new ReplyServiceImpl();
        GPTServiceImpl defaultGPTService = new GPTServiceImpl(null);
        List<Song> responseList = HttpBuilder.getResponseList("https://www.diving-fish.com/api/maimaidxprober/music_data", Song.class);
        @SuppressWarnings("unchecked")
        HashMap<Integer,List<String>> alias= (HashMap<Integer, List<String>>) HttpBuilder
                .getResponse("https://download.fanyu.site/maimai/alias.json",new TypeToken<HashMap<Integer,List<String>>>(){}.getType() );
        if(responseList.isEmpty()){
            throw new RuntimeException("Fail to get song list");
        }
        GuessGameServiceImpl guessGameService = new GuessGameServiceImpl(responseList,alias);
        guessGameServiceCopy=guessGameService;
        functionList.forEach(function -> {
            switch (function.getMode()){
                case 0:{
                    exceptionAnswers.add(function.getAnswer());
                    break;
                }
                case 1:{
                    defaultReplyService.add(function.getTrigger(),function.getAnswer());
                    functions.put(function.getTrigger(),defaultReplyService);
                    break;
                }
                case 2:{
                    functions.put(function.getTrigger(),defaultGPTService);
                    break;
                }
                case 3:{
                    guessGameService.getTriggers().add(function.getTrigger());
                    guessGameService.getAnswers().add(function.getAnswer());
                    functions.put(function.getTrigger(),guessGameService);
                    break;
                }
            }
        });
    }

}