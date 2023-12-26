package org.chorser.entity.maimai;

public class BaseInfo {
    private String title;
    private String artist;
    private String genre;
    private int bpm;
    private String release_date;
    private String from;
    private boolean is_new;
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

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isIs_new() {
        return is_new;
    }

    public void setIs_new(boolean is_new) {
        this.is_new = is_new;
    }

    @Override
    public String toString() {
        return "BaseInfo{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", genre='" + genre + '\'' +
                ", bpm=" + bpm +
                ", release_date='" + release_date + '\'' +
                ", from='" + from + '\'' +
                ", is_new=" + is_new +
                '}';
    }
}
