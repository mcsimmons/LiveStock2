package com.example.livestock;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        final LivestockAPI API = LivestockAPI.getInstance(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        TextView txtRegister = (TextView) findViewById(R.id.TXT_SignUp);
        txtRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Now switch views
                Intent myIntent = new Intent(MainActivity.this,RegisterUser.class);
                startActivityForResult(myIntent,0);
            }
        });

        //Login button
        final Button btnLogin = (Button) findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Login","Login button pressed");
                String email = ((EditText)findViewById(R.id.TXT_Email)).getText().toString();
                String password = ((EditText)findViewById(R.id.TXT_Password)).getText().toString();


                API.performLogin(email,password,new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response)
                    {   // Get Response
                        try {
                            JSONObject jResponse = new JSONObject(response);

                            if(jResponse.getString("l_name").length() > 1){
                                LivestockAppData.UserID = jResponse.getInt("id");
                                LivestockAppData.UserFName = jResponse.getString("f_name");
                                LivestockAppData.UserLName = jResponse.getString("l_name");

                                performLocationSearch();
                                //Now get the User name and switch views
                                //Intent myIntent = new Intent(MainActivity.this,TimeClock.class);
                                //startActivityForResult(myIntent,0);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),jResponse.toString(), Toast.LENGTH_LONG).show();

                            }
                        }
                        catch(JSONException e)
                        {
                            Toast.makeText(getApplicationContext(),response.replace("\"",""), Toast.LENGTH_LONG).show();
                            Log.e("Login", "INVALID RESPONSE :" + response);

                        }
                    }
                });

            }
        });
    }

    private void performLocationSearch(){
        final LivestockAPI API = LivestockAPI.getInstance(this);
        //Request Location permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {// We have already permission to use the location
            Log.i("TimeClock","Permission allowed");

            //Get location data
            mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
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
                                    Intent myIntent = new Intent(MainActivity.this,MapsActivity.class);
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
            }).addOnFailureListener(MainActivity.this, new OnFailureListener(){
                @Override
                public void onFailure(Exception e){
                    Log.i("TimeClock","Request failed");

                    Toast.makeText(getApplicationContext(),"Error: No LOCATION data found.", Toast.LENGTH_LONG).show();

                }
            });

        }
    }
}
