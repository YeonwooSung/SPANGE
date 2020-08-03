package com.technonia.spange;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.technonia.spange.signup.SignUpSessionManager;
import com.technonia.spange.signup.SignupFormValidator;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    private EditText editText_email;
    private EditText editText_pw;
    private EditText editText_username;
    private Button button_signUp;
    private Button button_goBackToLogin;
    private SignUpSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initScreen();
    }

    private void initScreen() {
        editText_email = findViewById(R.id.edit_text_email_sign_up);
        editText_pw = findViewById(R.id.edit_text_password_sign_up);
        editText_username = findViewById(R.id.edit_text_username_sign_up);

        button_signUp = findViewById(R.id.button_register_sign_up);
        button_goBackToLogin = findViewById(R.id.goBack_to_login_screen_button);

        SignupFormValidator signupFormValidator = new SignupFormValidator(editText_email, editText_pw, editText_username, button_signUp);
        editText_email.addTextChangedListener(signupFormValidator);
        editText_pw.addTextChangedListener(signupFormValidator);
        editText_username.addTextChangedListener(signupFormValidator);

        sessionManager = new SignUpSessionManager();
        addEventHandlersToButtons();
    }

    private void addEventHandlersToButtons() {
        button_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateSignUp();
            }
        });

        button_goBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminate();
            }
        });
    }

    private void validateSignUp() {
        String email = editText_email.getText().toString().trim();
        String pw = editText_pw.getText().toString().trim();
        String username = editText_username.getText().toString().trim();

        sessionManager.initSession();

        if (validateInputs(email, pw, username))
            signUp(email, pw, username);

        int errorCode = sessionManager.getErrorCode();
        switch (errorCode) {
            case 0:
                terminate();
                break;
            case 1:
                editText_email.setError("이메일 주소를 다시 확인해 주십시오");
                break;
            case 2:
                editText_pw.setError("비밀번호의 최소 길이는 8자리입니다");
                break;
            case 3:
                editText_username.setError("사용자 이름의 최대 길이는 7글자 입니다.");
                break;
            case 4:
                //TODO network error
        }
    }

    private void signUp(String email, String pw, String username) {
        String baseURL = getString(R.string.baseURL);
        String res = NetworkUtils.sendRequestForSignUp(baseURL, email, pw, username);

        sessionManager.parseResponse(res);
    }

    private boolean validateInputs(String email, String pw, String username) {
        return sessionManager.validateEmail(email) && sessionManager.validatePassword(pw) && sessionManager.validateUsername(username);
    }

    private void terminate() {
        finish();
    }
}
