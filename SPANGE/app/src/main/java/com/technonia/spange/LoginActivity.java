package com.technonia.spange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.technonia.spange.login.LoginFormValidator;
import com.technonia.spange.login.LoginSessionManager;

import org.json.JSONObject;

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

        // use strict mode to allow the main thread to use the networking functions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        // send request for log in
        String baseURL = getString(R.string.baseURL);
        String res = NetworkUtils.sendRequestForLogin(baseURL, email, pw);

        Log.d("Response", res);

        if (res.startsWith("Error")) return false;

        JSONObject jsonObject = Utils.parseResponse(res);

        String sp_name = getString(R.string.shared_preferences_file_name);
        String username_key = getString(R.string.user_id_key);
        SharedPreferences sp = getSharedPreferences(sp_name, MODE_PRIVATE);

        String user_id = sessionManager.storeSessionInfo(jsonObject, username_key, sp);
        if (user_id == null) return false;


        // get device id
        String device_id_key = getString(R.string.device_id_key);
        String response_str = NetworkUtils.sendRequestToGetDeviceID(baseURL, user_id);
        JSONObject jsonObject_deviceID = Utils.parseResponse(response_str);

        String device_id = sessionManager.storeDeviceID(jsonObject_deviceID, device_id_key, sp);
        if (device_id == null) return false;

        return true;
    }

    public void navigateToMainScreen() {
        Intent intent = new Intent(this, RealtimeMap.class);
        startActivity(intent);
        finish();
    }

    public void navigateToRegisterScreen() {
        //TODO replace class name in the intent with suitable class name (register activity) !!!!
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
