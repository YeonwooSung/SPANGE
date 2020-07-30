package com.technonia.spange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.technonia.spange.login.LoginFormValidator;
import com.technonia.spange.login.LoginSessionManager;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText editText_email;
    private EditText editText_pw;
    private Button signUpBtn;
    private Button loginBtn;

    private LoginSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initScreen();
        addEventListenerToButtons();

        sessionManager = new LoginSessionManager();
    }

    private void initScreen() {
        editText_email = findViewById(R.id.edit_text_username);
        editText_pw = findViewById(R.id.edit_text_password);
        signUpBtn = findViewById(R.id.sign_up_button);
        loginBtn = findViewById(R.id.login_button);

        LoginFormValidator formValidator = new LoginFormValidator(editText_email, editText_pw, loginBtn);

        editText_email.addTextChangedListener(formValidator);
        editText_pw.addTextChangedListener(formValidator);
    }

    private void addEventListenerToButtons() {
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OnClick", "sign up button is clicked");

                navigateToRegisterScreen();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OnClick", "log in button is clicked");

                if (login_SPANGE()) navigateToMainScreen();
            }
        });
    }

    public boolean login_SPANGE() {
        String email = editText_email.getText().toString().trim();
        String pw = editText_pw.getText().toString().trim();

        if (email.isEmpty() || pw.isEmpty()) return false;

        //TODO send request for log in

        String res = "abcdefg"; //TODO this is the response from the server, which is the result of log in request

        //TODO return false if error occurred

        String sp_name = getString(R.string.shared_preferences_file_name);
        SharedPreferences sp = getSharedPreferences(sp_name, MODE_PRIVATE);

        sessionManager.storeSessionInfo(res, sp);
        return true;
    }

    public void navigateToMainScreen() {
        Intent intent = new Intent(this, RealtimeMap.class);
        startActivity(intent);
        finish();
    }

    public void navigateToRegisterScreen() {
        //TODO replace class name in the intent with suitable class name (register activity) !!!!
        Intent intent = new Intent(this, RealtimeMap.class);
        startActivity(intent);
    }
}
