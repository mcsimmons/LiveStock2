package com.example.livestock;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    final LivestockAPI API = LivestockAPI.getInstance(this);
    private GoogleMap mMap;
    private long[] owner_ids;
    private double[] longitude;
    private double[] latitude;
    private String[] names;
    private long[] phones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Intent intent = getIntent();
        owner_ids= intent.getLongArrayExtra("owner_ids");
        longitude= intent.getDoubleArrayExtra("longitudes");
        latitude= intent.getDoubleArrayExtra("latitudes");
        names = intent.getStringArrayExtra("names");
        phones = intent.getLongArrayExtra("phones");

        setContentView(R.layout.location_search);


        DisplayMetrics displayMetrics = MapsActivity.this.getResources().getDisplayMetrics();
        float pxHeight = displayMetrics.heightPixels;
        float pxWidth = displayMetrics.widthPixels;
        Button BTNDone = (Button) findViewById(R.id.tcs_done);
        BTNDone.setWidth((int) ((pxWidth / 7) * 6));
        BTNDone.setVisibility(View.GONE);
        Button BTNInfo = (Button) findViewById(R.id.tcs_owner_info);
        BTNInfo.setWidth((int) (pxWidth - BTNDone.getWidth()));
        BTNInfo.setVisibility(View.GONE);

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
        final Button BTNDone = (Button) findViewById(R.id.tcs_done);
        final Button BTNInfo = (Button) findViewById(R.id.tcs_owner_info);




        mMap = googleMap;
        LatLng loc = new LatLng(latitude[0], longitude[0]);
        mMap.addMarker(new MarkerOptions().position(loc).title(LivestockAppData.UserFName + " " + LivestockAppData.UserLName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).setTag(-1);
        //

        //Zoom in on location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,12));

        //Add Pins
        // Add markers for all the owners
        for (int i = 1; i < latitude.length; i++){
            loc = new LatLng(latitude[i], longitude[i]);
            mMap.addMarker(new MarkerOptions().position(loc).title(names[i])).setTag(owner_ids[i]);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            /**
             * handle marker click event
             */
            @Override
            public boolean onMarkerClick(final Marker marker) {

                //marker.showInfoWindow();
                //CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLng(marker.getPosition());
                //mMap.animateCamera(mCameraUpdate);

                long marker_index = -1;

                try {
                    marker_index = (long) marker.getTag();
                }
                catch(Exception e){
                    marker_index = -1;
                }

                if (marker_index >= 0) {
                    // TODO Auto-generated method stub
                    BTNDone.setVisibility(View.VISIBLE);
                    BTNInfo.setVisibility(View.VISIBLE);

                    BTNDone.setText(marker.getTitle());
                    BTNDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            long phone = 0;

                            //get index of owner by id
                            for (int i = 0; i < owner_ids.length; i++) {
                                if (owner_ids[i] == (long) marker.getTag()) {
                                    phone = phones[i];
                                }
                            }

                            intent.setData(Uri.parse("tel:" + phone));
                            getApplicationContext().startActivity(intent);

                        }
                    });


                    BTNInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            showOwnerInfoPopup((long) marker.getTag(), marker.getTitle());

                        }
                    });

                    return false;
                }
                else{
                    //Clicked on blue tag
                    return false;
                }
            }

        });
    }


    public void showOwnerInfoPopup(long owner_id, final String name){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.owner_info_popup, null);




        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        try {
            popupWindow.setElevation(10);
        }
        catch (Exception e){
            //Don't bother on older phones
        }

        final TextView TXTName = (TextView) popupView.findViewById(R.id.TXT_Address_Label);
        final TextView TXTAddress = (TextView) popupView.findViewById(R.id.TXT_POP_Owner_Address);
        final TextView TXTNotes = (TextView) popupView.findViewById(R.id.TXT_POP_Owner_Notes);
        TXTNotes.setVisibility(View.GONE);

        API.getOwnerInfo(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jResponse = new JSONObject(response);

                    TXTAddress.setText(jResponse.get("address").toString());
                    TXTName.setText(name);

                    if(jResponse.has("notes")){
                        TXTNotes.setText(jResponse.get("notes").toString());
                        TXTNotes.setVisibility(View.VISIBLE);
                    }

                    // show the popup window
                    popupWindow.showAtLocation(MapsActivity.this.findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

                }
                catch(JSONException e)
                {
                    Toast.makeText(getApplicationContext(),"Invalid response from server", Toast.LENGTH_LONG).show();
                    Log.e("Login", "INVALID RESPONSE :" + response);
                    return;
                }
            }
        }, owner_id);



        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
