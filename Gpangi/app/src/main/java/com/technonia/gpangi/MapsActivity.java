package com.technonia.gpangi;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

        // get intent, and check the passed values
        Intent myIntent = getIntent(); // gets the previously created intent
        String latitude_str = myIntent.getStringExtra(getString(R.string.push_notification_key_latitude));
        String longitude_str = myIntent.getStringExtra(getString(R.string.push_notification_key_longitude));
        String pushNotification = myIntent.getStringExtra(getString(R.string.push_notification_key_push_notification));

        //TODO
        if (!pushNotification.equals("yes")) finish();

        try {
            latitude = Double.parseDouble(latitude_str);
            longitude = Double.parseDouble(longitude_str);
        } catch (NumberFormatException e) {
            //TODO
            //finish();
            latitude = 37.401782989502;
            longitude = 126.7320098877;
        }

        // Add a marker in Sydney and move the camera
        LatLng lat_lng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(lat_lng).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_lng, 18));
    }
}
