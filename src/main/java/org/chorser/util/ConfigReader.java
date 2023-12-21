package org.chorser.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.chorser.entity.Authentication;
import org.chorser.entity.Configuration;
import org.chorser.entity.Conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigReader {

    private static final String DEFAULT_CONVERSATION_PATH="conversations.json";
    private static final Gson gson;
    public static final ObjectMapper ymlMapper;

    private static final Logger log= LoggerFactory.getLogger(ConfigReader.class);

    static {
        gson = new Gson();
        ymlMapper= new ObjectMapper(new com.fasterxml.jackson.dataformat.yaml.YAMLFactory());
    }

    public static Authentication readAuthentication(String path) throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        Authentication authentication = new Authentication();
        JsonNode authenticationNode = ymlMapper.readTree(inputStream).path("authentication");
        if(authenticationNode.isMissingNode()) {
            return null;
        }
//        根据authenticationNode的内容，设置authentication的属性
        for (Field field : authentication.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String text = authenticationNode.path(field.getName()).asText();
            try {
                if(text!=null&&!text.isEmpty()){
                    if(field.getType().equals(Boolean.class)) {
                        field.set(authentication, Boolean.parseBoolean(text));
                    }else {
                        field.set(authentication, text);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return authentication;
    }

    public static List<Conversation> readConversations(String path){
        List<Conversation> conversations = new ArrayList<>();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if(inputStream==null){
                return conversations;
            }

            conversations=gson.fromJson(new String(inputStream.readAllBytes())
                    , new TypeToken<List<Conversation>>(){}.getType());
        } catch (IOException e) {
            return null;
        }

        return conversations;
    }


    public static Configuration readDefaultConfiguration(String configPath) throws IOException {
//        读取登录授权信息配置
        Configuration configuration = new Configuration();
        configuration.setAuthentication(readAuthentication(configPath));

//        读取其余配置文件路径
        JsonNode configNode = ymlMapper.readTree(Thread.currentThread().getContextClassLoader().getResourceAsStream(configPath)).path("config");
//        读取Conversation设置路径
        if (configNode.path("conversation").isMissingNode()) {
            configuration.setConversations(readConversations(DEFAULT_CONVERSATION_PATH));
        }else {
            JsonNode conversationNode = configNode.path("conversation");
//            设置对话发生概率
            configuration.setProbability(conversationNode.path("probability").isMissingNode()?
                    1d: conversationNode.path("probability").asDouble());
//            读取Conversation Path，读取写入
            configuration.setConversations(readConversations(
                    conversationNode.path("path").isMissingNode()?
                            DEFAULT_CONVERSATION_PATH: conversationNode.path("path").asText()));
        }

        return configuration;

    }
}
