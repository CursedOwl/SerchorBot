package org.chorser.service.impl;

import org.chorser.service.IDiscordService;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.event.message.MessageCreateEvent;

import java.lang.reflect.Array;
import static org.chorser.common.ActionRowConstants.*;

import java.util.Arrays;


public class DiscActionRowServiceImpl extends IDiscordService {

    private DiscGeminiServiceImpl geminiService;

    public DiscActionRowServiceImpl(DiscGeminiServiceImpl geminiService) {
        this.geminiService = geminiService;
    }

    @Override
    public String response(String input, MessageCreateEvent event) {
//        Gemini是否保存对话
        new MessageBuilder().setContent("Gemini是否保持对话？").addComponents(
                        ActionRow.of(SelectMenu.create( ID_GEMINI_CONVERSATION_KEEP, geminiService.getKeepConversation().toString(), 1, 1, Arrays.asList(
                                SelectMenuOption.create("true", OPTION_GEMINI_KEEP_YES),
                                SelectMenuOption.create("false", OPTION_GEMINI_KEEP_NO)))
                        ))
                .send(event.getChannel());
//        保持对话时长
        Integer conversationTime = geminiService.getConversationTime();
        String timeFormat= conversationTime>60? String.format("%dmin",conversationTime/60): String.format("%ds",conversationTime);
        new MessageBuilder().append("Gemini保持对话时长").addComponents(
                        ActionRow.of(SelectMenu.create( ID_GEMINI_CONVERSATION_TIME, timeFormat, 1, 1, Arrays.asList(
                                SelectMenuOption.create("0s", OPTION_GEMINI_TIME_0S),
                                SelectMenuOption.create("30s", OPTION_GEMINI_TIME_30S),
                                SelectMenuOption.create("1min", OPTION_GEMINI_TIME_1MIN),
                                SelectMenuOption.create("5min", OPTION_GEMINI_TIME_5MIN),
                                SelectMenuOption.create("10min", OPTION_GEMINI_TIME_10MIN)))
                        ))
                .send(event.getChannel());


        return null;
    }


}
