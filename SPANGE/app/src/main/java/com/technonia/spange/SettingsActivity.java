package com.technonia.spange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private EditText editText_device_id;
    private EditText editText_user_id;
    private Button btn_set_device_id;

    private final String ERR_MSG_INVALID_DEVICE_ID = "Invalid device id";
    private final String ERR_MSG_EXCEED_MAX_USER_NUM = "Exceeded the max user number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        editLayoutParams();  // Edit the Layout parameters

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private TextWatcher inputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            validateTextInput();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            validateTextInput();
        }

        private void validateTextInput() {
            String device_id = editText_device_id.getText().toString().trim();
            String user_id = editText_user_id.getText().toString().trim();

            boolean enableButton = !device_id.isEmpty() && !user_id.isEmpty();
            btn_set_device_id.setEnabled(enableButton);
        }
    };

    private void editLayoutParams() {
        editText_device_id = findViewById(R.id.device_id_edit_text);
        editText_user_id = findViewById(R.id.user_id_edit_text);
        btn_set_device_id = findViewById(R.id.device_id_button);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int display_height = displayMetrics.heightPixels;
        int display_width = displayMetrics.widthPixels;

        int component_height = display_height / 12;
        int component_width = display_width / 4 * 3;

        // get LayoutParams of components
        LayoutParams text_input_lp = (LinearLayout.LayoutParams) editText_device_id.getLayoutParams();
        LayoutParams device_id_btn_lp = (LinearLayout.LayoutParams) btn_set_device_id.getLayoutParams();

        // change the value of the width of the components
        text_input_lp.width = component_width;
        device_id_btn_lp.width = component_width;

        // change the value of the height of the components
        text_input_lp.height = component_height;
        device_id_btn_lp.height = component_height;

        // Apply the updated layout parameters to components
        editText_device_id.setLayoutParams(text_input_lp);
        editText_user_id.setLayoutParams(text_input_lp);
        btn_set_device_id.setLayoutParams(device_id_btn_lp);

        // Add TextWatcher as an event listener to each EditText instance
        editText_user_id.addTextChangedListener(inputTextWatcher);
        editText_device_id.addTextChangedListener(inputTextWatcher);

        addButtonEventListener(); // add event listener to the button
    }

    private void addButtonEventListener() {
        btn_set_device_id.setClickable(true);

        // set the OnClickListener to the button
        btn_set_device_id.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // get text from the EditText instance
                String device_id_str = getDeviceIdFromTextInput();
                String user_id_str = getUserIdFromTextInput();

                if (this.checkIfInputTextIsInvalid(user_id_str)) {
                    setErrorToEditText(editText_user_id, getText(R.string.setting_toast_msg_empty_user_id), getText(R.string.setting_edit_text_error_msg_user_id_empty));
                    return;
                }

                if (this.checkIfInputTextIsInvalid(device_id_str)) {
                    setErrorToEditText(editText_device_id, getText(R.string.setting_toast_msg_empty_device_id), getText(R.string.setting_edit_text_error_msg_device_id_empty));
                    return;
                }

                // If the system registered the device id successfully, then finish this activity, and go back to the Main activity
                if (registerDeviceID(device_id_str)) finish();
            }

            private boolean checkIfInputTextIsInvalid(String text) {
                return text == null || text.trim().isEmpty();
            }
        });
    }

    private void setErrorToEditText(EditText editText, CharSequence toastMsg, CharSequence errorMsg) {
        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
        editText.setError(errorMsg);
    }

    private boolean registerDeviceID(String device_id_str) {
        String user_id = getUserIdFromTextInput();

        String fileName = getString(R.string.shared_preferences_file_name);
        SharedPreferences sp = getSharedPreferences(fileName, MODE_PRIVATE);

        // get required strings
        String baseURL = getString(R.string.baseURL);
        String not_found = getString(R.string.not_found_invalid_key);
        String fcm_token_str = sp.getString(getString(R.string.fcm_token_key),not_found);

        String result_str = NetworkUtils.sendRequestToRegisterDevice(baseURL, user_id, device_id_str);
        Log.d("RegisterDevice", result_str);

        // check if result_str is equal to the error message
        if (result_str.equals(ERR_MSG_INVALID_DEVICE_ID)) {
            setErrorToEditText(editText_device_id, getText(R.string.setting_toast_msg_invalid_device_id), getText(R.string.setting_edit_text_error_msg_device_id_invalid));

            return false;
        } else if (result_str.equals(ERR_MSG_EXCEED_MAX_USER_NUM)) {
            //TODO alert

            return false;
        }

        // update GCM token if the user_id is valid
        updateGCMTokenIfUserIDIsValid(baseURL, fcm_token_str, user_id);

        // store the device_id_str
        storeDeviceId_localStorage(sp, device_id_str);

        return true;
    }

    private void updateGCMTokenIfUserIDIsValid(String baseURL, String fcm_token_str, String user_id) {
        String not_found = getString(R.string.not_found_invalid_key);

        /*
         * If the user_id is valid, send request to register (or update) the GCM token
         */
        if (user_id != null && !user_id.equals("")&& !fcm_token_str.equals(not_found)) {
            String result_str = NetworkUtils.sendRequestForRegisterUser(baseURL, user_id, fcm_token_str);
            Log.d("NewDeviceRequest", result_str);
        }
    }

    private void storeDeviceId_localStorage(SharedPreferences sp, String new_device_id) {
        String stringSetKey = getString(R.string.device_id_key);
        Set<String> device_id_set = sp.getStringSet(stringSetKey, new HashSet<String>());
        device_id_set.add(new_device_id);

        // debugging message
        Log.d("Set<String>", device_id_set.toString());

        // edit the content of the local storage
        Editor editor = sp.edit();
        editor.putStringSet(stringSetKey, device_id_set);
        editor.apply();
    }

    private String getDeviceIdFromTextInput() {
        return getInputFromEditText(editText_device_id);
    }

    private String getUserIdFromTextInput() {
        return getInputFromEditText(editText_user_id);
    }

    private String getInputFromEditText(EditText text_input) {
        return text_input.getText().toString().trim();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
