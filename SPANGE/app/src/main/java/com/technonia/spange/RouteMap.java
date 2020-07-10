package com.technonia.spange;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.Date;

public class RouteMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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

        //TODO  <https://stackoverflow.com/a/25597046/9012940>
        Date fromDate = new Date(System.currentTimeMillis()-24*60*60*1000 * 2);
        Date toDate = new Date(System.currentTimeMillis()-24*60*60*1000 * 1);

        String response = NetworkUtils.sendRequestToGetRoute(fromDate, toDate);
        JSONObject jsonObj = Utils.parseResponse(response);

        if (jsonObj != null) {
            //TODO on success
        } else {
            setMap_default();
        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void setMap_default() {
        LatLng default_latlng = new LatLng(Utils.DEFAULT_LATITUDE, Utils.DEFAULT_LONGITUDE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default_latlng, 18));
    }
}
