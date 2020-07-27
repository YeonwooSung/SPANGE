package com.technonia.spange;

public class UserAcceptance {
    private String userName;
    private boolean accept;

    UserAcceptance(String userName, boolean accept) {
        this.userName = userName;
        this.accept = accept;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isAccepted() {
        return accept;
    }
}
