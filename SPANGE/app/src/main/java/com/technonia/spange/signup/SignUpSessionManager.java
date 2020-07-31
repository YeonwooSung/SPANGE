package com.technonia.spange.signup;

public class SignUpSessionManager {
    private final int USER_NAME_MAX_LEN = 7;

    public boolean validateEmail(String email) {
        if (!email.contains("@")) return false;

        return true;
    }

    public boolean validatePassword(String pw) {
        if (pw.length() < 8) return false;

        return true;
    }

    public boolean validateUsername(String userName) {
        if (userName.length() > USER_NAME_MAX_LEN) return false;

        return true;
    }
}
