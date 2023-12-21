package org.chorser.service.impl;

import com.theokanning.openai.service.OpenAiService;
import org.chorser.service.GPTService;

public class DefaultGPTServiceImpl implements GPTService {

    private final OpenAiService openAiService;

    public DefaultGPTServiceImpl(String token){
        openAiService=new OpenAiService(token);
    }

    @Override
    public String answer(String question) {
        return null;
    }
}
