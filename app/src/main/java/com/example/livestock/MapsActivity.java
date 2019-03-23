package com.example.livestock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double[] longitude;
    private double[] latitude;
    private String[] names;
    private long[] phones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        longitude= intent.getDoubleArrayExtra("longitudes");
        latitude= intent.getDoubleArrayExtra("latitudes");
        names = intent.getStringArrayExtra("names");
        phones = intent.getLongArrayExtra("phones");

        setContentView(R.layout.location_search);

        //TextView TXTSuccessTime = (TextView) findViewById(R.id.tcs_success_time);
        //TXTSuccessTime.setText("Success Time: " + server_time);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.tcs_map_fragment);
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
        LatLng loc = new LatLng(latitude[0], longitude[0]);
        mMap.addMarker(new MarkerOptions().position(loc).title(LivestockAppData.UserFName + " " + LivestockAppData.UserLName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        //

        //Zoom in on location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,12));

        //Add Pins
        // Add markers for all the owners
        for (int i = 1; i < latitude.length; i++){
            loc = new LatLng(latitude[i], longitude[i]);
            mMap.addMarker(new MarkerOptions().position(loc).title(names[i])).setTag(phones[i]);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            /**
             * handle marker click event
             */
            @Override
            public boolean onMarkerClick(final Marker marker) {
                // TODO Auto-generated method stub

                Button BTNDone = (Button) findViewById(R.id.tcs_done);
                BTNDone.setText("Call " + marker.getTitle());
                BTNDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);

                        intent.setData(Uri.parse("tel:" + marker.getTag()));
                        getApplicationContext().startActivity(intent);

                    }
                });

                return true;
            }
        });
    }


}
