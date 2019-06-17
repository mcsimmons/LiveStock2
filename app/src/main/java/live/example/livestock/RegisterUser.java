package live.example.livestock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import live.example.livestock.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class RegisterUser extends Activity {
    final LivestockAPI API = LivestockAPI.getInstance(this);
    private FusedLocationProviderClient mFusedLocationClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(RegisterUser.this);

        ((EditText)findViewById(R.id.TXT_FName)).requestFocus();


        Button btnRegister = (Button) findViewById(R.id.register);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                register();
            }
        });
    }

    public void register(){
        String fname = ((EditText) findViewById(R.id.TXT_FName)).getText().toString();
        String lname = ((EditText) findViewById(R.id.TXT_LName)).getText().toString();
        String email = ((EditText) findViewById(R.id.TXT_RegisterEmail)).getText().toString();
        String confirm_email = ((EditText) findViewById(R.id.TXT_RegisterEmailConfirm)).getText().toString();
        String password = ((EditText) findViewById(R.id.TXT_RegisterPassword)).getText().toString();
        String confirm_password = ((EditText) findViewById(R.id.TXT_RegisterPasswordConfirm)).getText().toString();
        String rkey = ((EditText) findViewById(R.id.TXT_RKey)).getText().toString();

        //Validate form
        if(fname.length() < 1 || lname.length() < 1 || email.length() < 5){
            Toast.makeText(getApplicationContext(), "You must complete all fields.", Toast.LENGTH_LONG).show();
            return;
        }

        if(!email.equals(confirm_email)) {
            Toast.makeText(getApplicationContext(), "Confirm Email does not match.", Toast.LENGTH_LONG).show();
            return;
        }

        if(password.length() < 6 || !isValidPassword(password )){
            Toast.makeText(getApplicationContext(), "Password must contain a Capital, a Number, and be at least 6 characters long.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(confirm_password)){
            Toast.makeText(getApplicationContext(), "Confirm Password does not match.", Toast.LENGTH_LONG).show();
            return;
        }
        if (rkey.length() < 4){
            Toast.makeText(getApplicationContext(), "You must enter a valid registration key.", Toast.LENGTH_LONG).show();
            return;
        }





        API.registerUser(fname , lname , rkey , email , password  , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {   // Get Response

                if (response.contains("Account created")) {
                    finish();
                }

                Toast.makeText(getApplicationContext(), response.replace("\"", ""), Toast.LENGTH_LONG).show();


            }
        });
    }

    public boolean isValidPassword(String s) {
        String n = ".*[0-9].*";
        String a = ".*[A-Z].*";
        return s.matches(n) && s.matches(a);
    }
}
