package org.chorser.entity;

public class Authentication {
    private String token;

    private String applicationID;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "token='" + token + '\'' +
                ", applicationID='" + applicationID + '\'' +
                '}';
    }
}
