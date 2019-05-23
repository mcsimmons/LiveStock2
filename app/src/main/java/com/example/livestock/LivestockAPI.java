package com.example.livestock;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

public class LivestockAPI {
    private static LivestockAPI mInstance;
    private RequestQueue mRequestQueue;
    private static StringRequest mStringRequest;
    private static Context mCtx;
    private static final String TAG = MainActivity.class.getName();
    private FusedLocationProviderClient mFusedLocationClient;
    private final String base_url = "http://www.thelivestockapp.com";


    private LivestockAPI(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mCtx);

    }

    public static synchronized LivestockAPI getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LivestockAPI(context);
        }
        return mInstance;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    // Network Requests
    public void performLogin(final String email, final String password, Response.Listener<String> onSuccess){

        mRequestQueue = Volley.newRequestQueue(mCtx);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            final String requestString = jsonBody.toString();

            // Create Request
            mStringRequest = new StringRequest(Request.Method.POST, base_url + "/users", onSuccess, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "ERROR :" + error.toString());
                    Toast.makeText(mCtx, "Network Error. Please check your connection and try again.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    //Set content type header
                    return "application/json; charset=UTF-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    //Set POST content
                    return (requestString).getBytes();
                }
            };

            mRequestQueue.add(mStringRequest);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    // Network Requests
    public void registerUser(final String f_name,final String l_name, final String r_key, final String email, final String password, Response.Listener<String> onSuccess){

        mRequestQueue = Volley.newRequestQueue(mCtx);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("f_name", f_name);
            jsonBody.put("l_name", l_name);
            jsonBody.put("r_key", r_key);
            final String requestString = jsonBody.toString();

            // Create Request
            mStringRequest = new StringRequest(Request.Method.POST, base_url + "/register", onSuccess, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "ERROR :" + error.toString());
                    Toast.makeText(mCtx, "Network Error. Please check your connection and try again.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    //Set content type header
                    return "application/json; charset=UTF-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    //Set POST content
                    return (requestString).getBytes();
                }
            };

            mRequestQueue.add(mStringRequest);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    // Network Requests
    public void addOwner(final String f_name,final String l_name, final String phone, final String street, final String city, final String state, final String zip, final String notes, Response.Listener<String> onSuccess){

        mRequestQueue = Volley.newRequestQueue(mCtx);

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("phone", phone);
            jsonBody.put("street", street);
            jsonBody.put("city", city);
            jsonBody.put("state", state);
            jsonBody.put("f_name", f_name);
            jsonBody.put("l_name", l_name);
            jsonBody.put("zip", zip);
            jsonBody.put("notes", notes);
            jsonBody.put("user_id", LivestockAppData.UserID);
            final String requestString = jsonBody.toString();

            // Create Request
            mStringRequest = new StringRequest(Request.Method.POST, base_url + "/owners", onSuccess, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "ERROR :" + error.toString());
                    Toast.makeText(mCtx, "Network Error. Please check your connection and try again.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    //Set content type header
                    return "application/json; charset=UTF-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    //Set POST content
                    return (requestString).getBytes();
                }
            };

            mRequestQueue.add(mStringRequest);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getOwners(Response.Listener<String> onSuccess){

        mRequestQueue = Volley.newRequestQueue(mCtx);

        // Create Request
        mStringRequest = new StringRequest(Request.Method.GET,base_url + "/owners", onSuccess, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error){
                Log.i(TAG, "ERROR :" + error.toString());
                Toast.makeText(mCtx, "Network Error. Please check your connection and try again.", Toast.LENGTH_LONG).show();
            }
        });

        mRequestQueue.add(mStringRequest);
    }

    public void getOwnerInfo(Response.Listener<String> onSuccess, long owner_id){

        mRequestQueue = Volley.newRequestQueue(mCtx);

        // Create Request
        mStringRequest = new StringRequest(Request.Method.GET,base_url + "/owner_info?owner_id=" + owner_id, onSuccess, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error){
                Log.i(TAG, "ERROR :" + error.toString());
                Toast.makeText(mCtx, "Network Error. Please check your connection and try again.", Toast.LENGTH_LONG).show();
            }
        });

        mRequestQueue.add(mStringRequest);
    }

}
