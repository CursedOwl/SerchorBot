package org.chorser.entity;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Conversation {
    private String regex;

    private Boolean replace;

    private Boolean memory;
    private Integer priority;

    private AtomicInteger count;

    private ArrayList<String> answers;

    public Conversation(){}
    public Conversation(String regex, Integer priority) {
        this.regex = regex;
        this.priority = priority;
        count=new AtomicInteger();
    }

    public Boolean getMemory() {
        return memory;
    }

    public void setMemory(Boolean memory) {
        this.memory = memory;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public void setCount(AtomicInteger count) {
        this.count = count;
    }

    public Boolean getReplace() {
        return replace;
    }

    public void setReplace(Boolean replace) {
        this.replace = replace;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "PriorityRegex{" +
                "regex='" + regex + '\'' +
                ", priority=" + priority +
                '}';
    }
}
