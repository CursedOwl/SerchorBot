package org.chorser.entity.gemini;

import org.chorser.entity.gemini.request.Content;

import java.util.ArrayList;
import java.util.List;

public class GeminiRequest {
    private List<Content> contents;

    public static class Builder{
        private final List<Content> contents=new ArrayList<>();

        public Builder addContent(Content content){
            contents.add(content);
            return this;
        }

        public Builder addContents(List<Content> content){
            contents.addAll(content);
            return this;
        }

        public GeminiRequest build(){
            return new GeminiRequest(this);
        }
    }

    private GeminiRequest(Builder builder){
        this.contents=builder.contents;
    }


    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "GeminiRequest{" +
                "contents=" + contents +
                '}';
    }
}
