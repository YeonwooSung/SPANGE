package com.technonia.spange;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

        // initialise edit texts and buttons
        initEditTexts();
        initButtons();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private boolean terminate() {
        finish();
        return true;
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

    private void initEditTexts() {
        // find EditTexts
        editText_device_id = findViewById(R.id.device_id_edit_text);
        editText_user_id = findViewById(R.id.user_id_edit_text);

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
    }

    private void initButtons() {
        // find Buttons
        btn_set_device_id = findViewById(R.id.device_id_button);
        btn_exit = findViewById(R.id.exit_button_id);
        btn_manager = findViewById(R.id.manager_button_id);

        // set buttons clickable
        btn_set_device_id.setClickable(true);
        btn_exit.setClickable(true);
        btn_manager.setClickable(true);

        // set the OnClickListener to the button
        btn_set_device_id.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // get text from the EditText instance
                String device_id_str = getDeviceIdFromTextInput();
                String user_id_str = getUserIdFromTextInput();

                // check if user id is valid
                if (this.checkIfInputTextIsInvalid(user_id_str)) {
                    changeBackgroundOfEditText_userID(3);
                    setErrorToEditText(editText_user_id, getText(R.string.setting_toast_msg_empty_user_id), getText(R.string.setting_edit_text_error_msg_user_id_empty));
                    return;
                }

                // check if device id is valid
                if (this.checkIfInputTextIsInvalid(device_id_str)) {
                    changeBackgroundOfEditText_deviceID(3);
                    setErrorToEditText(editText_device_id, getText(R.string.setting_toast_msg_empty_device_id), getText(R.string.setting_edit_text_error_msg_device_id_empty));
                    return;
                }

                // If the system registered the device id successfully, then finish this activity, and go back to the Main activity
                //if (registerDeviceID(device_id_str)) terminate();
                showAlertDialog("Test Admin");
            }

            private boolean checkIfInputTextIsInvalid(String text) {
                return text == null || text.trim().isEmpty();
            }
        });

        btn_exit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                terminate();
            }
        });

        btn_manager.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //TODO
            }
        });
    }

    private void showAlertDialog(String adminName) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_for_register_success, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);

        //builder.show();

        AlertDialog d = builder.create();

        //d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.show();

        d.setContentView(R.layout.dialog_for_register_success);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int display_height = displayMetrics.heightPixels;
        int display_width = displayMetrics.widthPixels;
        int dialog_height = (int) (display_height * 0.4);
        int dialog_width = (int) (display_width * 0.9);

//        Dialog d = new Dialog(SettingsActivity.this);
//        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        d.setContentView(R.layout.dialog_for_register_success);

        //final TextView tv = (TextView) d.findViewById(R.id.textView1);

        final Button button = (Button) d.findViewById(R.id.dialog_button_ok_setting_activity);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminate();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = dialog_width;
        lp.height = dialog_height;
        //d.show();
        d.getWindow().setAttributes(lp);
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
            changeBackgroundOfEditText_deviceID(3);
            setErrorToEditText(editText_device_id, getText(R.string.setting_toast_msg_invalid_device_id), getText(R.string.setting_edit_text_error_msg_device_id_invalid));

            return false;

        // check if max user number is exceeded
        } else if (result_str.equals(ERR_MSG_EXCEED_MAX_USER_NUM)) {
            changeBackgroundOfEditText_deviceID(3);
            setErrorToEditText(editText_device_id, getText(R.string.setting_toast_msg_max_user_num), getText(R.string.setting_edit_text_error_msg_exceed_max_user));

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
}
