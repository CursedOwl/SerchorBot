package org.chorser.service.impl;

import org.chorser.service.IDiscordService;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.HashMap;

public class DiscReplyServiceImpl extends IDiscordService {

    private HashMap<String,String> replies=new HashMap<>();

    @Override
    public String response(String input, MessageCreateEvent event) {
        if (replies.containsKey(input)) {
            return replies.get(input);
        }
        return null;
    }


    public void add(String input,String answer){
        replies.put(input,answer);
    }


}
