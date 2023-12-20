package org.chorser;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.chorser.entity.Authentication;
import org.chorser.listener.DefaultListener;
import org.chorser.util.YmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.io.InputStream;

public class BotApplication {

    private static final String CONFIG_PATH="config.yml";

    private static final BASE64Decoder base64Decoder=new BASE64Decoder();

    private static Logger log= LoggerFactory.getLogger(BotApplication.class);

    public static void main(String[] args) {
        try {
            Authentication authentication = YmlReader.readAuthentication(CONFIG_PATH);
            if(authentication==null){
                throw new IOException();
            }
            if(authentication.getToken()!=null){
                byte[] bytes = base64Decoder.decodeBuffer(authentication.getToken());
                String token = new String(bytes);
                log.info(token);
                initialDiscordConnection(token);
            }

        } catch (Exception e) {
            log.error("Fail to read configuration");
            throw new RuntimeException(e);
        }

    }

    private static void initialDiscordConnection(String token) {
        JDA build = JDABuilder.createDefault(token)
                .addEventListeners(new DefaultListener())
                .build();
    }
}