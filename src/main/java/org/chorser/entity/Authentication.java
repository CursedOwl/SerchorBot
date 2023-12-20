package org.chorser.entity;

public class Authentication {
    private String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "token='" + token + '\'' +
                '}';
    }
}
