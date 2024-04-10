package org.chorser.config;

import org.chorser.entity.config.Authentication;
import org.chorser.entity.config.Conversation;
import org.chorser.entity.config.Function;

import java.util.List;

public class BotConfiguration {
    private Authentication authentication;

    private List<Conversation> conversations;

    private List<Function> functions;

    private String conversationPath;

    private Double probability;

    private String functionPath;

    private String gptToken;

    private String geminiApiKey;

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public void setGeminiApiKey(String geminiApiKey) {
        this.geminiApiKey = geminiApiKey;
    }

    public String getGptToken() {
        return gptToken;
    }

    public void setGptToken(String gptToken) {
        this.gptToken = gptToken;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public String getFunctionPath() {
        return functionPath;
    }

    public void setFunctionPath(String functionPath) {
        this.functionPath = functionPath;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public String getConversationPath() {
        return conversationPath;
    }

    public void setConversationPath(String conversationPath) {
        this.conversationPath = conversationPath;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }
}
