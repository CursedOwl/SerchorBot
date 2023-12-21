package org.chorser.entity;

import java.util.List;

public class Configuration {
    private Authentication authentication;

    private List<Conversation> conversations;

    private String conversationPath;

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
