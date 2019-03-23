package com.example.livestock;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterUser extends Activity {
    final LivestockAPI API = LivestockAPI.getInstance(this);
    private FusedLocationProviderClient mFusedLocationClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(RegisterUser.this);

        Button btnRegister = (Button) findViewById(R.id.register);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                register();
            }
        });
    }

    public void register(){
        EditText fname = (EditText) findViewById(R.id.TXT_FName);
        EditText lname = (EditText) findViewById(R.id.TXT_LName);
        EditText email = (EditText) findViewById(R.id.TXT_RegisterEmail);
        EditText password = (EditText) findViewById(R.id.TXT_RegisterPassword);
        EditText rkey = (EditText) findViewById(R.id.TXT_RKey);
        API.registerUser(fname.getText().toString(),lname.getText().toString(),rkey.getText().toString(),email.getText().toString(),password.getText().toString(),new Response.Listener<String>(){
            @Override
            public void onResponse(String response)
            {   // Get Response

                if(response.contains("Account created")){
                    finish();
                }

               Toast.makeText(getApplicationContext(),response.replace("\"",""), Toast.LENGTH_LONG).show();


            }});
    }
}
