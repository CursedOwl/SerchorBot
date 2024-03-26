package org.chorser.listener;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.chorser.entity.config.Authentication;
import org.chorser.entity.config.Conversation;
import org.chorser.service.IFunctionService;
import org.chorser.service.impl.GuessGameServiceImpl;
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

    //    需要设置舞萌猜歌服务专门应对普通对话
    private GuessGameServiceImpl guessGameService;
    private final HashMap<String, IFunctionService> functions;
    private List<String> exceptionAnswers=new ArrayList<>();

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
//        如果为艾特机器人的消息则是功能回复
        if(wholeMessageContent.contains("<@"+applicationID+">")){
            Pattern pattern = Pattern.compile("<@\\d+>\\s*(.*)");
            Matcher matcher = pattern.matcher(wholeMessageContent);
            boolean find = matcher.find();
            dealFunctionEvent(messageCreateEvent,matcher.group(1));
        }else {
//            反之为对话回复
            if(random.nextDouble()>probability){
                return;
            }
            dealReplyEvent(messageCreateEvent);
        }

    }

    private void dealReplyEvent(MessageCreateEvent messageCreateEvent) {
        String content = messageCreateEvent.getMessageContent();
        Conversation temp=null;
//        先去查看舞萌猜歌
        if(guessGameService.guessSong(messageCreateEvent,content)){
            return;
        }
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
            String answer;
            if(temp.getMemory()){
                int count=temp.getCount().incrementAndGet();
                if(count>=temp.getAnswers().size()){
                    temp.getCount().set(0);
                }
                answer=temp.getAnswers().get(count-1);
            }else {
                answer=temp.getAnswers().get(random.nextInt(temp.getAnswers().size()));
            }

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

    private void dealFunctionEvent(MessageCreateEvent messageCreateEvent, String validContent) {
        if("对话配置".equals(validContent)){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("设置关键对话配置如下哦：\n");
            for (int i = 0; i < conversations.size(); i++) {
                stringBuilder.append(i+1)
                        .append(".")
                        .append(conversations.get(i).getRegex())
                        .append('\n');
            }
            messageCreateEvent.getChannel().sendMessage(stringBuilder.toString());
        }else {
            IFunctionService iFunctionService = functions.get(validContent);
            if(iFunctionService==null){
                messageCreateEvent.getChannel().sendMessage(exceptionAnswers.get(random.nextInt(exceptionAnswers.size())));
                return;
            }
            String response = iFunctionService.response(validContent, messageCreateEvent);
            if(response!=null&&!response.isEmpty()){
                messageCreateEvent.getChannel().sendMessage(response);
            }
        }
    }

    public DefaultListener(Authentication authentication, List<Conversation> conversations, HashMap<String, IFunctionService> functions) {
        this.applicationID = Long.parseLong(authentication.getApplicationID());
        this.conversations=conversations;
        this.functions=functions;
    }

    public void setGuessGameService(GuessGameServiceImpl guessGameService) {
        this.guessGameService = guessGameService;
    }

    public void setExceptionAnswers(List<String> exceptionAnswers) {
        this.exceptionAnswers = exceptionAnswers;
    }
}
