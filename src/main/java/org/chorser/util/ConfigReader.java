package org.chorser.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.chorser.entity.config.Authentication;
import org.chorser.config.BotConfiguration;
import org.chorser.entity.config.Conversation;
import org.chorser.entity.config.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfigReader {

    private static final String DEFAULT_CONVERSATION_PATH="conversations.json";
    private static final String DEFAULT_FUNCTION_PATH="functions.json";

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

        conversations.forEach(conversation -> {
            if(conversation.getMemory()){
                conversation.setCount(new AtomicInteger());
            }
        });
        return conversations;
    }

    private static List<Function> readFunctions(String path) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if(inputStream==null){
                return null;
            }
            return gson.fromJson(new String(inputStream.readAllBytes())
                    , new TypeToken<List<Function>>(){}.getType());
        } catch (IOException e) {
            return null;
        }
    }

    public static BotConfiguration readDefaultConfiguration(String configPath) throws IOException {
//        读取登录授权信息配置
        BotConfiguration configuration = new BotConfiguration();
        configuration.setAuthentication(readAuthentication(configPath));

//        读取其余配置文件路径
        JsonNode configNode = ymlMapper.readTree(Thread.currentThread().getContextClassLoader().getResourceAsStream(configPath)).path("config");

        //        读取Conversation设置路径
        if (configNode.path("conversation").isMissingNode()) {
            configuration.setConversationPath(DEFAULT_CONVERSATION_PATH);
            configuration.setProbability(1d);
        }else {
            JsonNode conversationNode = configNode.path("conversation");
//            设置对话发生概率
            configuration.setProbability(conversationNode.path("probability").isMissingNode()?
                    1d: conversationNode.path("probability").asDouble());
//            读取Conversation Path，读取写入
            configuration.setConversationPath(conversationNode.path("path").isMissingNode()?
                    DEFAULT_CONVERSATION_PATH: conversationNode.path("path").asText());
        }
        configuration.setConversations(readConversations(configuration.getConversationPath()));

//        读取Function设置路径
        if(configNode.path("function").isMissingNode()) {
            configuration.setFunctionPath(DEFAULT_FUNCTION_PATH);
        }else {
            JsonNode functionNode = configNode.path("function");
            configuration.setFunctionPath(functionNode.path("path").isMissingNode()?
                    DEFAULT_FUNCTION_PATH: functionNode.path("path").asText());
//            读取GPT-TOKEN
            configuration.setGptToken(functionNode.path("gpt-token").asText());
//            读取Gemini-API-KEY
            configuration.setGeminiApiKey(functionNode.path("gemini-api-key").asText());
        }
        configuration.setFunctions(readFunctions(configuration.getFunctionPath()));


        return configuration;

    }
}
