package org.chorser.entity.maimai;

import java.util.List;

public class Chart {
    private List<Integer> notes;
    private String charter;

    public List<Integer> getNotes() {
        return notes;
    }

    public void setNotes(List<Integer> notes) {
        this.notes = notes;
    }

    public String getCharter() {
        return charter;
    }

    public void setCharter(String charter) {
        this.charter = charter;
    }

    @Override
    public String toString() {
        return "Chart{" +
                "notes=" + notes +
                ", charter='" + charter + '\'' +
                '}';
    }
}
