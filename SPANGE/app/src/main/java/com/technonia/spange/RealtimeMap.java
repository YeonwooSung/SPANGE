package com.technonia.spange;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class RealtimeMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Handler m_handler;
    private Runnable runnable;

    private double previousLatitude;
    private double previousLongitude;

    private final long interval_time = 10000;
    private static String userID = "";
    private static String adminUserID = "대표사용자";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_map);

        // get userID
        RealtimeMap.userID = getUserIDFromLocalStorage();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // assert not null - to avoid NullPointerException
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        setUpButtons();

        // Reference for Handler:  <https://developer.android.com/reference/android/os/Handler.html>
        m_handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                getDataAndUpdateMap();
                m_handler.postDelayed(this, interval_time);
            }
        };
        m_handler.postDelayed(runnable, interval_time);

        // send request to register (or update) gcm token
        Handler tokenRegistrationHandler = new Handler();
        Runnable tokenRegistrationThread = new Runnable() {
            @Override
            public void run() {
                registerGCMTokenWithDeviceId();
            }
        };
        tokenRegistrationHandler.postDelayed(tokenRegistrationThread, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // remove callbacks
        m_handler.removeCallbacks(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // remove callbacks
        m_handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restart callbacks
        m_handler.postDelayed(runnable, 5000);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        this.setPreviousLatLng(Utils.DEFAULT_LATITUDE, Utils.DEFAULT_LONGITUDE);

        // initialise the screen
        init();

        String response = NetworkUtils.sendRequestToGetRecentLocation();
        JSONObject jsonObj = Utils.parseResponse(response);

        double latitude;
        double longitude;
        String deviceName = null;

        // Utils.parseResponse() method returns null if it founds some error in the response string.
        // Thus, if jsonObj is not null, it would be possible to assume that there is no error in the response.
        if (jsonObj != null) {
            try {
                latitude = jsonObj.getDouble("latitude");
                longitude = jsonObj.getDouble("longitude");
                deviceName = jsonObj.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
                latitude = Utils.DEFAULT_LATITUDE;
                longitude = Utils.DEFAULT_LONGITUDE;
            }
        } else {
            latitude = Utils.DEFAULT_LATITUDE;
            longitude = Utils.DEFAULT_LONGITUDE;
        }

        this.setPreviousLatLng(latitude, longitude);
        setLocationForMap(latitude, longitude, deviceName, false);
    }

    private void init() {
        String sp_name = getString(R.string.shared_preferences_file_name);
        String device_id_key = getString(R.string.device_id_key);
        SharedPreferences sp = getSharedPreferences(sp_name, MODE_PRIVATE);

        Set<String> deviceIdSet = sp.getStringSet(device_id_key, new HashSet<String>());

        //TODO error handling
        if (deviceIdSet.isEmpty()) return;

        //TODO deviceIdSet.toArray()[0]
        String deviceID = (String) deviceIdSet.toArray()[0];
        Log.d("deviceID", deviceID);

        String baseURL = getString(R.string.baseURL);
        String res = NetworkUtils.sendRequestToGetMemberKey(baseURL, deviceID);
        if (res.startsWith("Error")) return;

        // get member key
        String memberKey = getMemberKeyByProcessResponse(res);
        if (memberKey == null) return;

        // set member key and device id
        Utils.setDeviceID(deviceID);
        Utils.setmMemberKey(memberKey);
    }

    private void registerGCMTokenWithDeviceId() {
        String sp_name = getString(R.string.shared_preferences_file_name);
        SharedPreferences sp = getSharedPreferences(sp_name, MODE_PRIVATE);

        String not_found = getString(R.string.not_found_invalid_key);
        String fcm_token_str = sp.getString(getString(R.string.fcm_token_key),not_found);
        String baseURL = getString(R.string.baseURL);

        NetworkUtils.sendRequestForRegisterUser(baseURL, userID, fcm_token_str);
    }

    private String getUserIDFromLocalStorage() {
        String sp_name = getString(R.string.shared_preferences_file_name);
        String user_id_key = getString(R.string.user_id_key);
        SharedPreferences sp = getSharedPreferences(sp_name, MODE_PRIVATE);

        return sp.getString(user_id_key, "");
    }

    private String getMemberKeyByProcessResponse(String res) {
        JSONObject jsonObject = Utils.parseResponse(res);

        if (jsonObject != null) {
            try {
                String adminUser = jsonObject.getString("admin_user");
                RealtimeMap.adminUserID = adminUser;

                return jsonObject.getString("device_member_key");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void getDataAndUpdateMap() {
        String response = NetworkUtils.sendRequestToGetRecentLocation();
        JSONObject jsonObj = Utils.parseResponse(response);

        // Utils.parseResponse() method returns null if it founds some error in the response string.
        // Thus, if jsonObj is not null, it would be possible to assume that there is no error in the response.
        if (jsonObj != null) {
            try {
                double latitude = jsonObj.getDouble("latitude");
                double longitude = jsonObj.getDouble("longitude");
                String deviceName = jsonObj.getString("name");

                // check if at least one of latitude or longitude are changed.
                //if (latitude != previousLatitude || longitude != previousLongitude) {
                    mMap.clear(); // Removes all markers, overlays, and polylines from the map.
                    setLocationForMap(latitude, longitude, deviceName, true);
                //}

                this.setPreviousLatLng(latitude, longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLocationForMap(double latitude, double longitude, String deviceName, boolean needToAnimate) {
        LatLng latLng = new LatLng(latitude, longitude);
        if (deviceName != null)
            mMap.addMarker(new MarkerOptions().position(latLng).title(deviceName));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
        if (needToAnimate)
            mMap.moveCamera(cameraUpdate);
        else
            mMap.animateCamera(cameraUpdate);
    }

    private void setUpButtons() {
        Button btn_settings = (Button)findViewById(R.id.btn_setting_realtime);
        btn_settings.setClickable(true);

        // add the event listener for the button
        btn_settings.setOnClickListener(new Button.OnClickListener() {
            private String user_id = userID;
            public void onClick(View v) {
                // navigate to settings
                Intent navigation_intent = new Intent(RealtimeMap.this, SettingsActivity.class);
                navigation_intent.putExtra(getString(R.string.extra_str_key_user_id), user_id);
                startActivity(navigation_intent);
            }
        });
    }

    private void setPreviousLatLng(double previousLatitude, double previousLongitude) {
        this.previousLatitude = previousLatitude;
        this.previousLongitude = previousLongitude;
    }

    public static String getUserID() {
        return userID;
    }

    public static String getAdminUserID() {
        return adminUserID;
    }
}
