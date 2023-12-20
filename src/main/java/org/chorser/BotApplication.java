package org.chorser;


import org.chorser.entity.Authentication;
import org.chorser.util.YmlReader;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;

public class BotApplication {

    private static final String CONFIG_PATH="config.yml";
    private static final Base64.Decoder base64Decoder=Base64.getDecoder();

    private static Logger log= LoggerFactory.getLogger(BotApplication.class);

    public static void main(String[] args) {

        try {
            Authentication authentication = YmlReader.readAuthentication(CONFIG_PATH);
            if(authentication==null){
                throw new IOException();
            }
            if(authentication.getToken()!=null){
                byte[] bytes = base64Decoder.decode(authentication.getToken());
                String token = new String(bytes);
                log.info(token);
                initialJavacordConnection(token);
            }

        } catch (Exception e) {
            log.error("Fail to read configuration");
            throw new RuntimeException(e);
        }

    }

    private static void initialJavacordConnection(String token) {

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809));
        DiscordApi api = new DiscordApiBuilder()
                .setProxy(proxy)
                .setToken(token)
                .setAllIntents()
                .addMessageCreateListener(event -> {
                    if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                        event.getChannel().sendMessage("Pong!");
                    }
                })
                .login()
                .join();

        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }

}