package org.chorser.service.impl;

import cn.hutool.cron.task.Task;
import com.google.gson.Gson;
import okhttp3.*;
import org.chorser.entity.gemini.GeminiContainer;
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
import java.util.List;
import java.util.concurrent.*;

public class DiscGeminiServiceImpl extends IDiscordService {

    private final String GeminiApiKey;

    private Boolean keepConversation;
//    秒做单位，需要用到的时候再转化为Long
    private Integer conversationTime;

    private final ConcurrentHashMap<Long, GeminiContainer> conversationMap=new ConcurrentHashMap<>();

    private final String BASE_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";

//    工具
    private final Gson gson=new Gson();
    private static final OkHttpClient httpClient;
    private static final ScheduledExecutorService scheduler= Executors.newScheduledThreadPool(1);

    private static final Logger log= LoggerFactory.getLogger(DiscGeminiServiceImpl.class);


    static {
        httpClient=new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1",10809)))
                .build();
    }

    public DiscGeminiServiceImpl(String geminiApiKey) {
        this.keepConversation=false;
        this.conversationTime=30;
        this.GeminiApiKey = geminiApiKey;
    }

    @Override
    public String response(String input, MessageCreateEvent event) {
        String geminiInput = input.substring(input.indexOf('-')+1);
//        log.info("截取到Gemini-Input内容:"+geminiInput);
        GeminiRequest requestToSend;
        long id = event.getMessageAuthor().getId();
        if (keepConversation){
//            保存对话的情况下将上下文保存在ConcurrentHashMap中
            GeminiContainer geminiContainer = conversationMap.get(id);
            if(geminiContainer!=null){
                synchronized (geminiContainer){
                    if(conversationMap.containsKey(id)){
                        List<Content> contents = geminiContainer.getGeminiRequest().getContents();
                        contents.add(new Content.Builder().role("user").addPart(new Part(geminiInput)).build());
                        requestToSend=geminiContainer.getGeminiRequest();
                        geminiContainer.setTimeStamp(System.currentTimeMillis());
                    }else {
                        requestToSend=new GeminiRequest.Builder().addContent(new Content.Builder().role("user").addPart(new Part(geminiInput)).build()).build();
                        GeminiContainer newGeminiContainer = new GeminiContainer();
                        newGeminiContainer.setGeminiRequest(requestToSend);
                        newGeminiContainer.setTimeStamp(System.currentTimeMillis());
                        conversationMap.put(id,newGeminiContainer);
                    }
                }
            }else {
                GeminiRequest.Builder builder = new GeminiRequest.Builder();
                builder.addContent(new Content.Builder().role("user").addPart(new Part(geminiInput)).build());
                requestToSend= builder.build();
                GeminiContainer newGeminiContainer = new GeminiContainer();
                newGeminiContainer.setGeminiRequest(requestToSend);
                newGeminiContainer.setTimeStamp(System.currentTimeMillis());
                conversationMap.put(id,newGeminiContainer);
            }
            scheduler.schedule(()->{
                GeminiContainer geminiContainerFuture = conversationMap.get(id);
                if(geminiContainerFuture!=null){
                    synchronized (geminiContainerFuture){
//                        不需要双重检查，因为假设id的KV不存在了，也仍然能移除
                        if(System.currentTimeMillis()-geminiContainerFuture.getTimeStamp()>(conversationTime-2)* 1000L){
                            conversationMap.remove(id);
                            log.info("删除GeminiContainer信息:"+gson.toJson(geminiContainerFuture));
                        }
                    }
                }
            }, conversationTime, TimeUnit.SECONDS);

        }else {
            requestToSend=new GeminiRequest.Builder().addContent(new Content.Builder().role("user").addPart(new Part(geminiInput)).build()).build();

        }
        log.info("Request-Content信息:"+gson.toJson(requestToSend));
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                gson.toJson(requestToSend));
        Request request = new Request.Builder()
                .url(BASE_URL + "?key=" + GeminiApiKey)
                .post(requestBody)
                .build();

        try (Response execute = httpClient.newCall(request).execute()) {
            if (execute.body() != null) {
                String executeBody = execute.body().string();
//                log.info("Gemini返回内容:"+executeBody);
                GeminiResponse response = gson.fromJson(executeBody, GeminiResponse.class);
                Content content = response.getCandidates().get(0).getContent();
//                开始写入model对话，同样需要考虑并发安全
                GeminiContainer geminiContainer = conversationMap.get(id);
                if(geminiContainer!=null){
                    synchronized (geminiContainer){
                        if(conversationMap.containsKey(id)){
                           geminiContainer.getGeminiRequest().getContents().add(content);
                        }
                    }
                }


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
