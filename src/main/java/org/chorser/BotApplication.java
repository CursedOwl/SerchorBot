package org.chorser;


import org.chorser.entity.Authentication;
import org.chorser.config.BotConfiguration;
import org.chorser.entity.Conversation;
import org.chorser.entity.Function;
import org.chorser.listener.DefaultListener;
import org.chorser.service.IFunctionService;
import org.chorser.service.impl.DefaultGPTServiceImpl;
import org.chorser.service.impl.DefaultReplyServiceImpl;
import org.chorser.util.ConfigReader;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class BotApplication {

    private static final String CONFIG_PATH="config.yml";

    private static Double probability=1d;
    private static Authentication authentication;
    private static List<Conversation> conversations;
    private static List<Function> functionList;
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
        DefaultReplyServiceImpl defaultReplyService = new DefaultReplyServiceImpl();
        DefaultGPTServiceImpl defaultGPTService = new DefaultGPTServiceImpl(null);

        functionList.forEach(function -> {
            switch (function.getMode()){
                case 1:{
                    defaultReplyService.add(function.getTrigger(),function.getAnswer());
                    functions.put(function.getTrigger(),defaultReplyService);
                    break;
                }
                case 2:{
                    functions.put(function.getTrigger(),defaultGPTService);
                }
            }
        });
    }

}