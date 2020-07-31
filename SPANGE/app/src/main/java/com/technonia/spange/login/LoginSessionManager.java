package com.technonia.spange.login;

import android.content.SharedPreferences;

import com.technonia.spange.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class LoginSessionManager {
    public String storeSessionInfo(JSONObject jsonObject, String key, SharedPreferences sp) {
        SharedPreferences.Editor editor = sp.edit();
        String userId = getUserIdFromResponseJSON(jsonObject);

        if (userId != null) {
            editor.putString(key, userId);
            editor.apply();
        }

        return userId;
    }

    public String storeDeviceID(JSONObject jsonObject, String key, SharedPreferences sp) {
        SharedPreferences.Editor editor = sp.edit();
        String deviceID = getDeviceIdFromResponseJSON(jsonObject);

        if (deviceID != null) {
            Set<String> device_id_set = new HashSet<>();
            device_id_set.add(deviceID);
            editor.putStringSet(key, device_id_set);
            editor.apply();
        }

        return deviceID;
    }

    private String getUserIdFromResponseJSON(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                String userName = jsonObject.getString("username");
                return userName;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getDeviceIdFromResponseJSON(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                String deviceID = jsonObject.getString("device_id");
                return deviceID;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
