package com.example.livestock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class AddOwner extends AppCompatActivity {
    final LivestockAPI API = LivestockAPI.getInstance(this);
    private DrawerLayout sideMenu;
    private FusedLocationProviderClient mFusedLocationClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addowner);

        Toolbar toolbar = AddOwner.this.findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AddOwner.this);

        //Initialize side menu
        sideMenu = AddOwner.this.findViewById(R.id.side_menu_2);
        NavigationView navigationView = AddOwner.this.findViewById(R.id.nav_view_2);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        if (menuItem.getTitle().equals("Location Search")){
                            setResult(LivestockAppData.RESULT_LOCATION_SEARCH);
                            finish();
                        }
                        else if  (menuItem.getTitle().equals("Home")){
                            //Show Home Screen
                            //Intent myIntent = new Intent(AddOwner.this,HomeScreen.class);
                            //startActivityForResult(myIntent,0);
                            setResult(RESULT_OK);
                            finish();
                        }
                        else if  (menuItem.getTitle().equals("Logout")){
                            //Return to login
                            setResult(LivestockAppData.RESULT_LOGOUT);
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

        ((EditText)findViewById(R.id.TXT_OwnerFName)).requestFocus();


        Button btnRegister = (Button) findViewById(R.id.addOwner);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addOwnerInfo();
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sideMenu.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addOwnerInfo(){
        EditText fname = (EditText) findViewById(R.id.TXT_OwnerFName);
        EditText lname = (EditText) findViewById(R.id.TXT_OwnerLName);
        EditText phone = (EditText) findViewById(R.id.TXT_OwnerPhone);
        EditText street = (EditText) findViewById(R.id.TXT_OwnerStreet);
        EditText city = (EditText) findViewById(R.id.TXT_OwnerCity);
        EditText state = (EditText) findViewById(R.id.TXT_OwnerState);
        EditText zip = (EditText) findViewById(R.id.TXT_OwnerZip);
        EditText notes =  findViewById(R.id.TXT_OwnerNotes);
        API.addOwner(fname.getText().toString(),lname.getText().toString(),phone.getText().toString(),street.getText().toString(),city.getText().toString(),state.getText().toString(),zip.getText().toString(),notes.getText().toString(),new Response.Listener<String>(){
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
