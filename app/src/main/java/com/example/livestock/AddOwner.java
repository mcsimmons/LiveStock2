package com.example.livestock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class AddOwner extends Activity {
    final LivestockAPI API = LivestockAPI.getInstance(this);
    private FusedLocationProviderClient mFusedLocationClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addowner);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AddOwner.this);

        ((EditText)findViewById(R.id.TXT_OwnerFName)).requestFocus();


        Button btnRegister = (Button) findViewById(R.id.addOwner);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addOwnerInfo();
            }
        });
    }

    public void addOwnerInfo(){
        EditText fname = (EditText) findViewById(R.id.TXT_OwnerFName);
        EditText lname = (EditText) findViewById(R.id.TXT_OwnerLName);
        EditText phone = (EditText) findViewById(R.id.TXT_OwnerPhone);
        EditText street = (EditText) findViewById(R.id.TXT_OwnerStreet);
        EditText city = (EditText) findViewById(R.id.TXT_OwnerCity);
        EditText state = (EditText) findViewById(R.id.TXT_OwnerState);
        EditText zip = (EditText) findViewById(R.id.TXT_OwnerZip);
        API.addOwner(fname.getText().toString(),lname.getText().toString(),phone.getText().toString(),street.getText().toString(),city.getText().toString(),state.getText().toString(),zip.getText().toString(),new Response.Listener<String>(){
            @Override
            public void onResponse(String response)
            {   // Get Response

                if(response.contains("Owner added")){
                    Toast.makeText(getApplicationContext(), response.replace("\"", ""), Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), response.replace("\"", ""), Toast.LENGTH_LONG).show();
                }

            }});
    }
}
