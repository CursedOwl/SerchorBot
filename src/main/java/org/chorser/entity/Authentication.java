package org.chorser.entity;

public class Authentication {
    private String token;

    private String applicationID;

    private Boolean base64;

    public Boolean getBase64() {
        return base64;
    }

    public void setBase64(Boolean base64) {
        this.base64 = base64;
    }

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
