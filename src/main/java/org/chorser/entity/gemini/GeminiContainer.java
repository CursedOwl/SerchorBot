package org.chorser.entity.gemini;

public class GeminiContainer {
    private GeminiRequest geminiRequest;

    private Long timeStamp;

    public GeminiContainer(){

    }

    public GeminiContainer(GeminiRequest geminiRequest, Long timeStamp) {
        this.geminiRequest = geminiRequest;
        this.timeStamp = timeStamp;
    }

    public GeminiRequest getGeminiRequest() {
        return geminiRequest;
    }

    public void setGeminiRequest(GeminiRequest geminiRequest) {
        this.geminiRequest = geminiRequest;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "GeminiContainer{" +
                "geminiRequest=" + geminiRequest +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
