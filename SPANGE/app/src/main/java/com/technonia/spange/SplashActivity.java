package com.technonia.spange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if (checkIfAutoLoginIsAvailable()) {
            intent = new Intent(this, RealtimeMap.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private boolean checkIfAutoLoginIsAvailable() {
        String sp_name = getString(R.string.shared_preferences_file_name);
        String user_id_key = getString(R.string.user_id_key);

        SharedPreferences sp = getSharedPreferences(sp_name, MODE_PRIVATE);
        String user_id = sp.getString(user_id_key, null);

        return user_id != null;
    }
}
