package org.chorser.entity.config;

public class Function {

    private Integer mode;

    private String trigger;

    private String answer;

    public Function(Integer mode, String trigger, String answer) {
        this.mode = mode;
        this.trigger = trigger;
        this.answer = answer;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Function{" +
                "mode=" + mode +
                ", trigger='" + trigger + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
