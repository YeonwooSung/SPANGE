package com.technonia.spange.manager;

public class UserAcceptance {
    private String userID;
    private boolean accept;

    public UserAcceptance(String userID, boolean accept) {
        this.userID = userID;
        this.accept = accept;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isAccepted() {
        return accept;
    }

    public void setAcceptance(boolean accept) {
        this.accept = accept;
    }
}
