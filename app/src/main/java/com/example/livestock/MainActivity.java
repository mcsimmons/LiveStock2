package com.example.livestock;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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


                                //Now get the User name and switch views
                                Intent myIntent = new Intent(MainActivity.this,HomeScreen.class);
                                startActivityForResult(myIntent,0);
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


}
