package org.chorser.entity.gemini.request;

public class Part {
    String text;

    public Part(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
