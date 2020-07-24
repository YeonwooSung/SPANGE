package com.technonia.spange;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {
    private EditText editText_device_id;
    private EditText editText_user_id;
    private Button btn_set_device_id;
    private Button btn_exit;
    private Button btn_manager;

    private final String ERR_MSG_INVALID_DEVICE_ID = "Invalid device id";
    private final String ERR_MSG_EXCEED_MAX_USER_NUM = "Exceeded the max user number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        initComponents();  // initialise edit texts and buttons

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void changeBackgroundOfEditText_deviceID(int type) {
        Drawable bg = null;

        switch (type) {
            case 1:
                bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_device_on);
                break;
            case 2:
                bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_device_off);
                break;
            case 3:
                bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_device_caution);
                break;
            default:
                bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_device_off);
        }

        editText_device_id.setBackground(bg);
    }

    private void changeBackgroundOfEditText_userID(int type) {
        Drawable bg = null;

        switch (type) {
            case 1:
                bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_user_on);
                break;
            case 2:
                bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_user_off);
                break;
            case 3:
                bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_user_caution);
                break;
            default:
                bg = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blank_user_off);
        }

        editText_user_id.setBackground(bg);
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

    private void initComponents() {
        // find EditTexts
        editText_device_id = findViewById(R.id.device_id_edit_text);
        editText_user_id = findViewById(R.id.user_id_edit_text);

        // find Buttons
        btn_set_device_id = findViewById(R.id.device_id_button);
        btn_exit = findViewById(R.id.exit_button_id);
        btn_manager = findViewById(R.id.manager_button_id);

        // Add TextWatcher as an event listener to each EditText instance
        editText_user_id.addTextChangedListener(inputTextWatcher);
        editText_device_id.addTextChangedListener(inputTextWatcher);

        // Add OnTouchListener to detect onTouch event, so that the app could change the background image of the corresponding EditText instance
        editText_device_id.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                changeBackgroundOfEditText_deviceID(1);
                return false;
            }
        });
        editText_user_id.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                changeBackgroundOfEditText_userID(1);
                return false;
            }
        });

        // Add event listener to handle onFocusChange event
        editText_device_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // when EditText gets focus
                    changeBackgroundOfEditText_deviceID(1);
                } else {
                    // when EditText loses focus
                    changeBackgroundOfEditText_deviceID(2);
                }
            }
        });
        editText_user_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // when EditText gets focus
                    changeBackgroundOfEditText_userID(1);
                } else {
                    // when EditText loses focus
                    changeBackgroundOfEditText_userID(2);
                }
            }
        });

        addButtonEventListener(); // add event listener to the button
    }

    private void addButtonEventListener() {
        btn_set_device_id.setClickable(true);
        btn_exit.setClickable(true);
        btn_manager.setClickable(true);

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

        btn_exit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        btn_manager.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //
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
            //TODO alert??

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
