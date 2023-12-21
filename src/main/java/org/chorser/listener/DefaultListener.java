package org.chorser.listener;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.chorser.entity.Authentication;
import org.chorser.entity.Conversation;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DefaultListener implements MessageCreateListener {
    private Double probability;
    private final Long applicationID;
    private final List<Conversation> conversations;

    private final Random random=new Random();
    private final Gson gson=new GsonBuilder().setPrettyPrinting().create();;
    private final Logger log= LoggerFactory.getLogger(DefaultListener.class);

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        String wholeMessageContent = messageCreateEvent.getMessageContent();
        log.info("["+messageCreateEvent.getMessageAuthor().getName()+"]:"+wholeMessageContent);
        if (messageCreateEvent.getMessageAuthor().getId()==applicationID) {
            return;
        }

        if(wholeMessageContent.contains("<@"+applicationID+">")){
            Pattern pattern = Pattern.compile("<@\\d+>\\s*(.*)");
            Matcher matcher = pattern.matcher(wholeMessageContent);
            boolean find = matcher.find();
            dealAtEvent(messageCreateEvent,matcher.group(1));
        }else {
            if(random.nextDouble()>probability){
                return;
            }
            dealReplyEvent(messageCreateEvent);
        }

    }

    private void dealReplyEvent(MessageCreateEvent messageCreateEvent) {
        String content = messageCreateEvent.getMessageContent();
        Conversation temp=null;

        for (Conversation conversation : conversations) {
            Pattern pattern = Pattern.compile(conversation.getRegex());
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                if(temp!=null){
                    if(temp.getPriority()<conversation.getPriority()){
                        temp=conversation;
                    }
                }else{
                    temp=conversation;
                }
            }
        }
        if(temp!=null){
            String answer = temp.getAnswers().get(random.nextInt(temp.getAnswers().size()));

            if (temp.getReplace()) {
                Pattern pattern = Pattern.compile(temp.getRegex());
                Matcher matcher = pattern.matcher(content.trim());
                matcher.find();
                String capture = matcher.group(1);
                answer=answer.replaceAll("\\$\\{Capture}",capture);
                answer=answer.replaceAll("\\$\\{Sender}",messageCreateEvent.getMessageAuthor().getDisplayName());
            }
            messageCreateEvent.getChannel().sendMessage(answer);
        }

    }

    private void dealAtEvent(MessageCreateEvent messageCreateEvent,String validContent) {
        switch (validContent){
            case "贴贴":{
                messageCreateEvent.getChannel().sendMessage("哇呜！");
                break;
            }
            case "对话配置":{
                messageCreateEvent.getChannel().sendMessage(gson.toJson(conversations));
                break;
            }
        }
    }

    public DefaultListener(Authentication authentication, List<Conversation> conversations) {
        this.applicationID = Long.parseLong(authentication.getApplicationID());
        this.conversations=conversations;
    }
}
