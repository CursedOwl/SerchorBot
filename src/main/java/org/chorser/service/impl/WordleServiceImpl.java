package org.chorser.service.impl;

import org.chorser.service.IFunctionService;
import org.javacord.api.event.message.MessageCreateEvent;

public class WordleServiceImpl extends IFunctionService {
    @Override
    public String response(String input, MessageCreateEvent event) {
        String word="apple";
        for (int i = 0; i < 5; i++) {
            if(input.charAt(i)==word.charAt(i)) {
                System.out.println("第"+i+"个字母"+input.charAt(i)+"正确");
            }
        }
        return null;
    }
}
