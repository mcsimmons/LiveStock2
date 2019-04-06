package com.example.livestock;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeScreen extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    private DrawerLayout sideMenu;
    final LivestockAPI API = LivestockAPI.getInstance(this);

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(HomeScreen.this);

        //Initialize side menu
        sideMenu = findViewById(R.id.side_menu);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        if (menuItem.getTitle().equals("Location Search")){
                            performLocationSearch();
                        }
                        else if  (menuItem.getTitle().equals("Add Owner")){
                            //Show Add Owner Screen
                            Intent myIntent = new Intent(HomeScreen.this,AddOwner.class);
                            startActivityForResult(myIntent,0);
                        }
                        else if  (menuItem.getTitle().equals("Logout")){
                            //Return to login
                            finish();
                        }

                        else
                        {
                            Toast.makeText(getApplicationContext(),menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        }
                        // close drawer when item is tapped
                        sideMenu.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sideMenu.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void performLocationSearch(){
        final LivestockAPI API = LivestockAPI.getInstance(this);
        //Request Location permission
        if (ContextCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {// We have already permission to use the location
            Log.i("TimeClock","Permission allowed");

            //Get location data
            mFusedLocationClient.getLastLocation().addOnSuccessListener(HomeScreen.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.i("TimeClock","Success");

                    if (location != null) {
                        //Get location data
                        final double longitude = location.getLongitude();
                        final double latitude = location.getLatitude();

                        //API call
                        API.getOwners(new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response)
                            {   // Get Response
                                try {
                                    JSONArray jResponse = new JSONArray(response);

                                    Toast.makeText(getApplicationContext(),jResponse.getJSONObject(0).getString("name"), Toast.LENGTH_LONG).show();

                                    double[] longitudes = new double[jResponse.length() + 1];
                                    double[] latitudes = new double[jResponse.length() + 1];
                                    String[] names = new String[jResponse.length() + 1];
                                    long[] phones = new long[jResponse.length() + 1];

                                    //Start with user's current location
                                    longitudes[0] = longitude;
                                    latitudes[0] = latitude;
                                    names[0] = "You are here";
                                    phones[0] = 0;

                                    //Extract all coordinates and names from response
                                    for (int i = 1; i <= jResponse.length(); i++){
                                        JSONObject job = jResponse.getJSONObject(i-1);
                                        longitudes[i] = job.getDouble("longitude");
                                        latitudes[i] = job.getDouble("latitude");
                                        names[i] = job.getString("name");
                                        phones[i] = job.getLong("phone");
                                    }
                                    //Now show success map view
                                    Intent myIntent = new Intent(HomeScreen.this,MapsActivity.class);
                                    myIntent.putExtra("longitudes",longitudes);
                                    myIntent.putExtra("latitudes",latitudes);
                                    myIntent.putExtra("names",names);
                                    myIntent.putExtra("phones",phones);
                                    startActivityForResult(myIntent,0);
                                }
                                catch(JSONException e)
                                {
                                    Toast.makeText(getApplicationContext(),"Invalid response from server", Toast.LENGTH_LONG).show();
                                    Log.e("Login", "INVALID RESPONSE :" + response);

                                }
                            }
                        });


                    } else {
                        Toast.makeText(getApplicationContext(),"Error: No LOCATION data found.", Toast.LENGTH_LONG).show();
                        Log.i("TimeClock","No last location");

                    }
                }
            }).addOnFailureListener(HomeScreen.this, new OnFailureListener(){
                @Override
                public void onFailure(Exception e){
                    Log.i("TimeClock","Request failed");

                    Toast.makeText(getApplicationContext(),"Error: No LOCATION data found.", Toast.LENGTH_LONG).show();

                }
            });

        }
    }

}
