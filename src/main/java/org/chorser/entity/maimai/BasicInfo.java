package org.chorser.entity.maimai;

import com.google.gson.annotations.SerializedName;

public class BasicInfo {
    private String title;

    private String artist;

    private String genre;

    private int bpm;

    @SerializedName("release_date")
    private String releaseDate;

    private String from;

    @SerializedName("is_new")
    private boolean isNew;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        this.isNew = aNew;
    }

    @Override
    public String toString() {
        return "BaseInfo{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", genre='" + genre + '\'' +
                ", bpm=" + bpm +
                ", release_date='" + releaseDate + '\'' +
                ", from='" + from + '\'' +
                ", is_new=" + isNew +
                '}';
    }
}
