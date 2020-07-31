package com.technonia.spange.signup;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class SignupFormValidator implements TextWatcher  {
    private EditText editText_email;
    private EditText editText_pw;
    private EditText editText_username;
    private Button signUpButton;

    public SignupFormValidator(EditText editText_email, EditText editText_pw, EditText editText_username, Button signUpButton) {
        this.editText_email = editText_email;
        this.editText_pw = editText_pw;
        this.editText_username = editText_username;
        this.signUpButton = signUpButton;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        String email = editText_email.getText().toString().trim();
        String pw = editText_pw.getText().toString().trim();
        String userName = editText_username.getText().toString().trim();

        boolean clickable = validateInputTexts(email, pw, userName);
        signUpButton.setClickable(clickable);
        signUpButton.setEnabled(clickable);
    }

    private boolean validateInputTexts(String email, String pw, String userName) {
        if (isEmptyString(email)) return false;
        if (isEmptyString(pw)) return false;
        if (isEmptyString(userName)) return false;

        return true;
    }

    private boolean isEmptyString(String str) {
        return str == null || str.isEmpty() || str.trim().isEmpty();
    }
}
