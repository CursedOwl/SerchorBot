package org.chorser.service.impl;

import com.theokanning.openai.service.OpenAiService;
import org.chorser.service.IFunctionService;
import org.javacord.api.event.message.MessageCreateEvent;

public class DefaultGPTServiceImpl extends IFunctionService {

    private final OpenAiService openAiService;

    public DefaultGPTServiceImpl(String token){
        openAiService=new OpenAiService(token);
    }


    @Override
    public String response(String input, MessageCreateEvent event) {
        return null;
    }
}
