package com.technonia.spange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import java.util.HashSet;
import java.util.Set;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        editLayoutParams();  // Edit the Layout parameters

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // get intent, and check the passed values
        Intent myIntent = getIntent(); // gets the previously created intent
        user_id = myIntent.getStringExtra(getString(R.string.extra_str_key_user_id));

        //TODO If user_id == null (or empty string, etc), get user_id from local storage
    }

    private void editLayoutParams() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int display_height = displayMetrics.heightPixels;
        int display_width = displayMetrics.widthPixels;

        int component_height = display_height / 12;
        int component_width = display_width / 4 * 3;

        // find components
        Button btn_set_device_id = findViewById(R.id.device_id_button);
        EditText text_input = findViewById(R.id.device_id_edit_text);

        // get LayoutParams of components
        LayoutParams text_input_lp = (LinearLayout.LayoutParams) text_input.getLayoutParams();
        LayoutParams device_id_btn_lp = (LinearLayout.LayoutParams) btn_set_device_id.getLayoutParams();

        // change the value of the width of the components
        text_input_lp.width = component_width;
        device_id_btn_lp.width = component_width;

        // change the value of the height of the components
        text_input_lp.height = component_height;
        device_id_btn_lp.height = component_height;

        // Apply the updated layout parameters to components
        text_input.setLayoutParams(text_input_lp);
        btn_set_device_id.setLayoutParams(device_id_btn_lp);

        addButtonEventListener(); // add event listener to the button
    }

    private void addButtonEventListener() {
        Button btn_set_device_id = findViewById(R.id.device_id_button);
        btn_set_device_id.setClickable(true);

        // set the OnClickListener to the button
        btn_set_device_id.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // get text from the EditText instance
                String device_id_str = getDeviceIdFromTextInput();

                //TODO validate the device id

                String fileName = getString(R.string.shared_preferences_file_name);
                SharedPreferences sp = getSharedPreferences(fileName, MODE_PRIVATE);

                // get required strings
                String baseURL = getString(R.string.baseURL);
                String not_found = getString(R.string.not_found_invalid_key);
                String fcm_token_str = sp.getString(getString(R.string.fcm_token_key),not_found);

                // If the FCM token is stored in the local storage send the POST request to the server for new device_id
                if (!fcm_token_str.equals(not_found)) {
                    NetworkUtils.sendRequestForNewDeviceID(baseURL, device_id_str, fcm_token_str);

                    NetworkUtils.sendRequestToRegisterDevice(baseURL, user_id, device_id_str);
                } else {
                    Log.d("NotFound", "Not Found!!!!");
                }

                // store the device_id_str
                storeDeviceId(sp, device_id_str);

                // finish this activity, and go back to the Main activity
                finish();
            }
        });
    }

    private void storeDeviceId(SharedPreferences sp, String new_device_id) {
        String stringSetKey = getString(R.string.device_id_key);
        Set<String> device_id_set = sp.getStringSet(stringSetKey, new HashSet<String>());
        device_id_set.add(new_device_id);

        // debugging message
        Log.d("Set<String>", device_id_set.toString());

        // edit the content of the local storage
        Editor editor = sp.edit();
        editor.putStringSet(stringSetKey, device_id_set);
        editor.commit();
    }

    private String getDeviceIdFromTextInput() {
        EditText text_input = findViewById(R.id.device_id_edit_text);
        return text_input.getText().toString();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
