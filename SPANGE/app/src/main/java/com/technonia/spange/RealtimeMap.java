package com.technonia.spange;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
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

public class RealtimeMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Handler m_handler;
    private Runnable runnable;

    private double previousLatitude;
    private double previousLongitude;

    private final long interval_time = 60000;
    private static String userID = "app_test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_map);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // remove callbacks
        m_handler.removeCallbacks(runnable);
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
        previousLatitude = 0;
        previousLongitude = 0;

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

        previousLatitude = latitude;
        previousLongitude = longitude;
        setLocationForMap(latitude, longitude, deviceName, false);
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

                if (latitude != previousLatitude && longitude != previousLongitude) {
                    mMap.clear(); // Removes all markers, overlays, and polylines from the map.
                    setLocationForMap(latitude, longitude, deviceName, true);
                }

                previousLatitude = latitude;
                previousLongitude = longitude;
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
        Button btn_start_location = (Button)findViewById(R.id.btn_route_realtime);
        btn_start_location.setClickable(true);
        btn_start_location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent navigation = new Intent(RealtimeMap.this, RouteMap.class);
                startActivity(navigation);
            }
        });

        Button btn_stop_location = (Button)findViewById(R.id.btn_setting_realtime);
        btn_stop_location.setClickable(true);
        btn_stop_location.setOnClickListener(new Button.OnClickListener() {
            private String user_id = userID;
            public void onClick(View v) {
                // navigate to settings
                Intent navigation_intent = new Intent(RealtimeMap.this, SettingsActivity.class);
                navigation_intent.putExtra(getString(R.string.extra_str_key_user_id), user_id);
                startActivity(navigation_intent);
            }
        });
    }
}
