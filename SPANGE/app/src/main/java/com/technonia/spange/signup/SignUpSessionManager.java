package com.technonia.spange.signup;

import com.technonia.spange.Utils;

import org.json.JSONObject;

public class SignUpSessionManager {
    private final int USER_NAME_MAX_LEN = 7;
    private final int PASSWORD_MIN_LEN = 8;
    private int errorCode = 0;

    public void initSession() {
        errorCode = 0;
    }

    public boolean validateEmail(String email) {
        //!email.contains("@")
        if (email.length() < 1) {
            errorCode = 1;
            return false;
        }

        return true;
    }

    public boolean validatePassword(String pw) {
        if (pw.length() < PASSWORD_MIN_LEN) {
            errorCode = 2;
            return false;
        }

        return true;
    }

    public boolean validateUsername(String userName) {
        if (userName.length() > USER_NAME_MAX_LEN) {
            errorCode = 3;
            return false;
        }

        return true;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void parseResponse(String res) {
        //TODO parse response to check if error occurred while doing the sign up process

        //TODO if error occurred, set errorCode = 4
    }
}
