package com.technonia.spange;

public class UserAcceptance {
    private String userName;
    private String userID;
    private boolean accept;

    UserAcceptance(String userName, String userID, boolean accept) {
        this.userName = userName;
        this.userID = userID;
        this.accept = accept;
    }

    public String getUserName() {
        return userName;
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
