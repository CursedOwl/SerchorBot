package org.chorser.listener;


import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DefaultListener implements MessageCreateListener {
    private final String applicationID;
    private final String regex="<@\\d+>\\s*(.*)";
    private final Logger log= LoggerFactory.getLogger(DefaultListener.class);

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {

        String wholeMessageContent = messageCreateEvent.getMessageContent();
        log.info("[MSG]:"+wholeMessageContent);

        if(wholeMessageContent.contains("<@"+applicationID+">")){
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(wholeMessageContent);
            boolean find = matcher.find();
            dealCertainEvent(messageCreateEvent,matcher.group(1));
        }
    }

    private void dealCertainEvent(MessageCreateEvent messageCreateEvent,String validContent) {
        switch (validContent){
            case "贴贴":{
                messageCreateEvent.getChannel().sendMessage("哇呜！");
                break;
            }
        }
    }

    public DefaultListener(String applicationID) {
        this.applicationID = applicationID;
    }
}
