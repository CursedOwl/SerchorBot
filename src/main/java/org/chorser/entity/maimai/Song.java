package org.chorser.entity.maimai;

import java.util.List;

public class Song {
    private String id;
    private String title;
    private String type;
    private List<Double> ds;
    private List<String> level;
    private List<Integer> cids;
    private List<Chart> charts;
    private BaseInfo baseInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getDs() {
        return ds;
    }

    public void setDs(List<Double> ds) {
        this.ds = ds;
    }

    public List<String> getLevel() {
        return level;
    }

    public void setLevel(List<String> level) {
        this.level = level;
    }

    public List<Integer> getCids() {
        return cids;
    }

    public void setCids(List<Integer> cids) {
        this.cids = cids;
    }

    public List<Chart> getCharts() {
        return charts;
    }

    public void setCharts(List<Chart> charts) {
        this.charts = charts;
    }

    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", ds=" + ds +
                ", level=" + level +
                ", cids=" + cids +
                ", charts=" + charts +
                ", baseInfo=" + baseInfo +
                '}';
    }
}
