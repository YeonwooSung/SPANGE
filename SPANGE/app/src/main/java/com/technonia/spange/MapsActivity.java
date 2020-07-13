package com.technonia.spange;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

    private final double DEFAULT_LATITUDE = 0.0;
    private final double DEFAULT_LONGITUDE = 0.0;

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

        try {
            double d1 = Double.parseDouble(latitude_str);
            double d2 = Double.parseDouble(longitude_str);

            this.setLatLng(d1, d2);
        } catch (NumberFormatException e) {
            Log.e("NumberFormat", e.getMessage());

            this.setLatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        }

        // check if latitude and longitude are default values
        if (latitude == DEFAULT_LATITUDE && longitude == DEFAULT_LONGITUDE) {
            setLatLng(Utils.DEFAULT_LATITUDE, Utils.DEFAULT_LONGITUDE);

            // Generate alert message to let the user know that there is some problem with latitude and longitude value.
            generateMessageToHandleLatLngError();
        }

        LatLng lat_lng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(lat_lng).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_lng, 18));
    }

    private void setLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void generateMessageToHandleLatLngError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.maps_activity_alert_msg_title)).setMessage(getText(R.string.maps_activity_alert_msg_body));


        /**
         * If the user press the ok button, the app will navigate to the RealtimeMap screen.
         */
        builder.setPositiveButton(getText(R.string.maps_activity_alert_ok_button_str), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), getText(R.string.maps_activity_toast_msg_ok), Toast.LENGTH_SHORT).show();

                // Generate intent for navigation, and navigate to the RealtimeMap Activity
                Intent navigateToMain = new Intent(MapsActivity.this, RealtimeMap.class);
                startActivity(navigateToMain);

                finish();  // finish this activity
            }
        });


        /**
         * If the user press the cancel button, the app will remain in the current screen
         */
        builder.setNegativeButton(getText(R.string.maps_activity_alert_no_button_str), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), getText(R.string.maps_activity_toast_msg_cancel), Toast.LENGTH_SHORT).show();
            }
        });


        // generate alert instance, and make it visible
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
