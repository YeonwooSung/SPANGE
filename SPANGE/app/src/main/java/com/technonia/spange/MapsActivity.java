package com.technonia.spange;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
        mMap.addMarker(new MarkerOptions().position(lat_lng).title("스팡이 사무실"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_lng, 18));
    }

    private void setLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void generateMessageToHandleLatLngError() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View dialogLayout = inflater.inflate(R.layout.dialog_for_fail_map, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);


        // generate alert instance
        final AlertDialog alertDialog = builder.create();

        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // make background transparent, and set gravity to "center"
        Window window = alertDialog.getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);

        // make dialog alert visible
        alertDialog.show();

        alertDialog.setContentView(R.layout.dialog_for_fail_map);


        // add event listeners to buttons

        final Button noButton = (Button) alertDialog.findViewById(R.id.map_popup_button_no);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), getText(R.string.maps_activity_toast_msg_cancel), Toast.LENGTH_SHORT).show();
            }
        });

        final Button yesButton = (Button) alertDialog.findViewById(R.id.map_popup_button_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), getText(R.string.maps_activity_toast_msg_ok), Toast.LENGTH_SHORT).show();

                // Generate intent for navigation, and navigate to the RealtimeMap Activity
                Intent navigateToMain = new Intent(MapsActivity.this, RealtimeMap.class);
                startActivity(navigateToMain);

                finish();  // finish this activity
            }
        });
    }
}
