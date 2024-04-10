package org.chorser.service.impl;

import com.google.gson.Gson;
import okhttp3.*;
import org.chorser.entity.gemini.GeminiResponse;
import org.chorser.entity.gemini.request.Content;
import org.chorser.entity.gemini.GeminiRequest;
import org.chorser.entity.gemini.request.Part;
import org.chorser.service.IDiscordService;
import org.javacord.api.event.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;

public class DiscGeminiServiceImpl extends IDiscordService {

    private final String GeminiApiKey;

    private Boolean keepConversation;
//    秒做单位，需要用到的时候再转化为Long
    private Integer conversationTime;

    private final HashMap<Integer,GeminiRequest> conversationMap=new HashMap<>();

    private final String BASE_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";

//    工具
    private final Gson gson=new Gson();
    private static final OkHttpClient httpClient;
    private static final Logger log= LoggerFactory.getLogger(DiscGeminiServiceImpl.class);


    static {
        httpClient=new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1",10809)))
                .build();
    }

    public DiscGeminiServiceImpl(String geminiApiKey) {
        this.keepConversation=false;
        this.conversationTime=0;
        this.GeminiApiKey = geminiApiKey;
    }

    @Override
    public String response(String input, MessageCreateEvent event) {
        String geminiInput = input.substring(input.indexOf('-')+1);
//        log.info("截取到Gemini-Input内容:"+geminiInput);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                gson.toJson(new GeminiRequest.Builder().addContent(
                        new Content.Builder()
                                .addPart(new Part(geminiInput))
                                .build()))
                );
        Request request = new Request.Builder()
                .url(BASE_URL + "?key=" + GeminiApiKey)
                .post(requestBody)
                .build();

        try (Response execute = httpClient.newCall(request).execute()) {
            if (execute.body() != null) {
                String executeBody = execute.body().string();
                log.info("Gemini返回内容:"+executeBody);
                GeminiResponse response = gson.fromJson(executeBody, GeminiResponse.class);

                String answer = response.getCandidates().get(0).getContent().getParts().get(0).getText();
                event.getChannel().sendMessage(answer);
                return null;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Boolean getKeepConversation() {
        return keepConversation;
    }

    public void setKeepConversation(Boolean keepConversation) {
        this.keepConversation = keepConversation;
    }

    public Integer getConversationTime() {
        return conversationTime;
    }

    public void setConversationTime(Integer conversationTime) {
        this.conversationTime = conversationTime;
    }
}
