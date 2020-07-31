package com.technonia.spange.login;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class LoginFormValidator implements TextWatcher {
    private EditText editText_email;
    private EditText editText_pw;
    private Button loginButton;

    public LoginFormValidator(EditText editText_email, EditText editText_pw, Button loginButton) {
        this.editText_email = editText_email;
        this.editText_pw = editText_pw;
        this.loginButton = loginButton;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        String email = editText_email.getText().toString().trim();
        String pw = editText_pw.getText().toString().trim();

        boolean clickable = validateText(email, pw);
        loginButton.setClickable(clickable);
        loginButton.setEnabled(clickable);
    }

    private boolean validateText(String email, String pw) {
        if (isEmptyString(email)) return false;
        if (isEmptyString((pw))) return false;

        return true;
    }

    private boolean isEmptyString(String str) {
        return str == null || str.isEmpty() || str.trim().isEmpty();
    }
}
