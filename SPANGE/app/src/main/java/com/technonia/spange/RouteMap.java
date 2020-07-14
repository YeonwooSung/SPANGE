package com.technonia.spange;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RouteMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private LatLng[] latLngs;
    private long[] times;

    private final int DEFAULT_ZOOM_LEVEL = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        initUISettings();
        redrawMap(false);
    }

    private void initUISettings() {
        mUiSettings = mMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void redrawMap(boolean removeAll) {
        if (removeAll)
            mMap.clear(); // Removes all markers, overlays, and polylines from the map.

        //TODO  <https://stackoverflow.com/a/25597046/9012940>
        Date fromDate = new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
        Date toDate = new Date();

        String response = NetworkUtils.sendRequestToGetRoute(fromDate, toDate);
        JSONArray jsonArray = Utils.parseResponseArray(response);

        if (jsonArray != null)
            getDataFromJsonObject(jsonArray);
        else
            setMap_default();

        if (latLngs != null) {
            int finalIndex = latLngs.length - 1;
            for (int i = 0; i < finalIndex; i++) {
                long t = times[i];
                Date date = new Date(t * 1000L);
                SimpleDateFormat transFormat = new SimpleDateFormat("HH시 mm분 ss초");
                String dateString = transFormat.format(date);

                LatLng currentLatLng = latLngs[i];
                mMap.addMarker(new MarkerOptions().position(currentLatLng).title(dateString));

                // add polyline
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.add(currentLatLng, latLngs[i + 1]);
                polylineOptions.width(5);
                polylineOptions.color(Color.BLUE);

                Polyline line = mMap.addPolyline(polylineOptions);
                line.setClickable(false);  // make the polyline un-clickable
            }

            LatLng currentLocation = latLngs[finalIndex];
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM_LEVEL));
        } else {
            setMap_default();
        }
    }

    private void getDataFromJsonObject(JSONArray jsonArray) {
        int length = jsonArray.length();
        if (length < 1) {
            latLngs = null;
            times = null;
            return;
        }

        latLngs = new LatLng[length];
        times = new long[length];
        try {
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                long date = jsonObject.getLong("reg_date");
                times[i] = date;

                double latitude = jsonObject.getDouble("latitude");
                double longitude = jsonObject.getDouble("longitude");
                LatLng latLng = new LatLng(latitude, longitude);
                latLngs[i] = latLng;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            latLngs = null;
            times = null;
        }
    }

    private void setMap_default() {
        LatLng default_latlng = new LatLng(Utils.DEFAULT_LATITUDE, Utils.DEFAULT_LONGITUDE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default_latlng, DEFAULT_ZOOM_LEVEL));
    }
}
