package org.chorser.listener;

import org.chorser.service.impl.DiscGeminiServiceImpl;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.chorser.common.ActionRowConstants.*;

public class DiscInteractionListener implements MessageComponentCreateListener {

    private DiscGeminiServiceImpl geminiService;

    private final Logger log= LoggerFactory.getLogger(DiscInteractionListener.class);

    public DiscInteractionListener(DiscGeminiServiceImpl geminiService) {
        this.geminiService = geminiService;
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction interaction = event.getMessageComponentInteraction();
//        DiscInteractionListener只处理SelectMenuInteraction
        SelectMenuInteraction selectMenuInteraction = interaction.asSelectMenuInteraction().get();
        switch (selectMenuInteraction.getCustomId()) {
//            1.Gemini是否保存对话
            case ID_GEMINI_CONVERSATION_KEEP:{
                switch (selectMenuInteraction.getChosenOptions().get(0).getValue()){
                    case OPTION_GEMINI_KEEP_YES:{
                        geminiService.setKeepConversation(true);
                        break;
                    }
                    case OPTION_GEMINI_KEEP_NO:{
                        geminiService.setKeepConversation(false);
                        break;
                    }
                }
            }
//            2.Gemini保持对话时长
            case ID_GEMINI_CONVERSATION_TIME:{
                switch (selectMenuInteraction.getChosenOptions().get(0).getValue()){
                    case OPTION_GEMINI_TIME_0S:{
                        geminiService.setConversationTime(0);
                        break;
                    }
                    case OPTION_GEMINI_TIME_30S:{
                        geminiService.setConversationTime(30);
                        break;
                    }
                    case OPTION_GEMINI_TIME_1MIN:{
                        geminiService.setConversationTime(60);
                        break;
                    }
                    case OPTION_GEMINI_TIME_5MIN:{
                        geminiService.setConversationTime(300);
                        break;
                    }
                    case OPTION_GEMINI_TIME_10MIN:{
                        geminiService.setConversationTime(600);
                        break;
                    }
                }
            }
        }
        interaction.acknowledge().thenAccept(ignore->log.info("Deal option:"+selectMenuInteraction.getCustomId()));

    }
}
