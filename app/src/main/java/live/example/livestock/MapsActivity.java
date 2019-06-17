package live.example.livestock;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import live.example.livestock.Adapter.ListAdapter;
import live.example.livestock.Model.ListItem;
import live.example.livestock.R;
import live.example.livestock.Utils.PhonePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    final LivestockAPI API = LivestockAPI.getInstance(this);
    private GoogleMap mMap;
    private long[] owner_ids;
    private double[] longitude;
    private double[] latitude;
    private String[] names;
    //private long[] phones;
    private String owner_name;
    private long[] owner_phones;
    private String owner_note;
    private String owner_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Intent intent = getIntent();
        owner_ids= intent.getLongArrayExtra("owner_ids");
        longitude= intent.getDoubleArrayExtra("longitudes");
        latitude= intent.getDoubleArrayExtra("latitudes");
        names = intent.getStringArrayExtra("names");
        //phones = intent.getarraylist("phones");

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


    //Called when app is brought back to the foreground
    @Override
    public void onResume(){
        super.onResume();

        //If app memory was cleared, return to the login screen (login is required to reload data)
        if(LivestockAppData.UserLName == null){
            finish();
        }
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
                    API.getOwnerInfo(new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jResponse = new JSONObject(response);

                                owner_address = (jResponse.get("address").toString());
                                owner_name = (marker.getTitle());
                                owner_phones = new long[jResponse.getJSONArray("phones").length()];

                                for(int i = 0; i<owner_phones.length;i++){
                                    owner_phones[i] = jResponse.getJSONArray("phones").getLong(i);
                                }

                                if(jResponse.has("notes")){
                                    owner_note = (jResponse.get("notes").toString());
                                }
                                else{
                                    owner_note = null;
                                }

                            }
                            catch(JSONException e)
                            {
                                Toast.makeText(getApplicationContext(),"Invalid response from server", Toast.LENGTH_LONG).show();
                                Log.e("Login", "INVALID RESPONSE :" + response);
                                return;
                            }
                        }
                    }, (long) marker.getTag());
                    // TODO Auto-generated method stub
                    BTNDone.setVisibility(View.VISIBLE);
                    BTNInfo.setVisibility(View.VISIBLE);

                    BTNDone.setText(marker.getTitle());
                    BTNDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(owner_phones.length == 0){
                                Toast.makeText(getApplicationContext(),"No phone number found for this owner.",Toast.LENGTH_LONG).show();
                            }
                            else if(owner_phones.length == 1) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                long phone = owner_phones[0];

                                intent.setData(Uri.parse("tel:" + phone));
                                getApplicationContext().startActivity(intent);
                            }
                            else{

                                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                                View popupView = inflater.inflate(R.layout.pick_phone_number_popup, null);
                                PhonePicker picker = new PhonePicker(getApplicationContext(),owner_phones,popupView,findViewById(android.R.id.content));
                                picker.showPhonePickerPopup();
                                //Multiple numbers found so have user pick one
                              /*  LinearLayoutManager myLinearLayout = new LinearLayoutManager(getApplicationContext());
                                 RecyclerView recyclerView;
                                 RecyclerView.Adapter adapter;
                                recyclerView = (RecyclerView) findViewById(R.id.ol_recycler);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(myLinearLayout);


                                List<ListItem> listItems = new ArrayList<>();

                                for(Long phone : owner_phones){
                                    listItems.add(new ListItem(0,""+phone,""));
                                }

                                adapter = new ListAdapter(MapsActivity.this, listItems);
                                recyclerView.setAdapter(adapter);*/
                            }
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


                    TXTAddress.setText(owner_address);
                    TXTName.setText(name);

                    if(owner_note != null){
                        TXTNotes.setText(owner_note);
                        TXTNotes.setVisibility(View.VISIBLE);
                    }

                    // show the popup window
                    popupWindow.showAtLocation(MapsActivity.this.findViewById(android.R.id.content), Gravity.CENTER, 0, 0);




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
