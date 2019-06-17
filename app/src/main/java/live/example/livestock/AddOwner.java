package live.example.livestock;

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
import live.example.livestock.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import static live.example.livestock.LivestockAppData.RESULT_LOCATION_SEARCH;

public class AddOwner extends AppCompatActivity {
    final LivestockAPI API = LivestockAPI.getInstance(this);
    private DrawerLayout sideMenu;
    private FusedLocationProviderClient mFusedLocationClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addowner);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AddOwner.this);

        Toolbar toolbar = AddOwner.this.findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        //Initialize side menu
        sideMenu = AddOwner.this.findViewById(R.id.side_menu_2);
        NavigationView navigationView = AddOwner.this.findViewById(R.id.nav_view_2);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        if (menuItem.getTitle().equals("Location Search")){
                            setResult(RESULT_LOCATION_SEARCH);
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
                        else if  (menuItem.getTitle().equals("Owner Search")){
                            //Return to login
                            Intent myIntent = new Intent(AddOwner.this,OwnerSearch.class);
                            startActivityForResult(myIntent,0);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == LivestockAppData.RESULT_LOGOUT) {
                //Logout called
                setResult(RESULT_OK);
                finish();
            }
            else if(resultCode == RESULT_LOCATION_SEARCH){
                setResult(RESULT_LOCATION_SEARCH);
                finish();
            }
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
        String fname = ((EditText) findViewById(R.id.TXT_OwnerFName)).getText().toString();
        String lname = ((EditText) findViewById(R.id.TXT_OwnerLName)).getText().toString();
        String phone = ((EditText) findViewById(R.id.TXT_OwnerPhone)).getText().toString();
        String street =( (EditText) findViewById(R.id.TXT_OwnerStreet)).getText().toString();
        String city = ((EditText) findViewById(R.id.TXT_OwnerCity)).getText().toString();
        String state = ((EditText) findViewById(R.id.TXT_OwnerState)).getText().toString();
        String zip = ((EditText) findViewById(R.id.TXT_OwnerZip)).getText().toString();
        String notes =  ((EditText) findViewById(R.id.TXT_OwnerNotes)).getText().toString();

        //remove any special characters from phone number
        phone = phone.replaceAll("[^\\d.]", "");

        //Validate form
        if(fname.length() < 1 || lname.length() < 1){
            Toast.makeText(getApplicationContext(), "You must enter a name for the owner+.", Toast.LENGTH_LONG).show();
            return;
        }
        if(phone.length() < 9){
            Toast.makeText(getApplicationContext(), "Please enter a valid phone number with area code.", Toast.LENGTH_LONG).show();
            return;
        }

        API.addOwner(fname,lname,phone,street,city,state,zip,notes,new Response.Listener<String>(){
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
