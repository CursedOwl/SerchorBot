package org.chorser.service.impl;

import org.chorser.service.IFunctionService;
import org.javacord.api.event.message.MessageCreateEvent;

public class GPTServiceImpl extends IFunctionService {

//    private final OpenAiService openAiService;

    public GPTServiceImpl(String token){
//        openAiService=new OpenAiService(token);
    }


    @Override
    public String response(String input, MessageCreateEvent event) {
        return null;
    }
}
