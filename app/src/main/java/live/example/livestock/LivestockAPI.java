package live.example.livestock;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import live.example.livestock.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

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

            InputStream keyStore = mCtx.getResources().openRawResource(R.raw.my);
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(), new HurlStack(null, getSocketFactory()));
        }
        return mRequestQueue;
    }

    private SSLSocketFactory getSocketFactory() {

        CertificateFactory cf = null;
        try {

            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = mCtx.getResources().openRawResource(R.raw.my);
            Certificate ca;
            try {

                ca = cf.generateCertificate(caInput);
                Log.e("CERT", "ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }


            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);


            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);


            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {

                    Log.e("CipherUsed", session.getCipherSuite());
                    Log.e("HOSTNAME", hostname);
                    //TODO: Fix this at some point
                    //return hostname.compareTo("www.thelivestockapp.com")==0; //The Hostname of your server.
                    return true;
                }
            };


            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            SSLContext context = null;
            context = SSLContext.getInstance("TLS");

            context.init(null, tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

            SSLSocketFactory sf = context.getSocketFactory();


            return sf;

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return  null;
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

    public void getOwnersList(int chunk_size,int chunk_index,String search_phrase,Response.Listener<String> onSuccess){

        mRequestQueue = Volley.newRequestQueue(mCtx);

        try {
            // Create Request
            mStringRequest = new StringRequest(Request.Method.GET, base_url + "/owner_info?chunk_size=" + chunk_size + "&chunk_index=" + chunk_index + "&search_phrase=" + URLEncoder.encode(search_phrase, "UTF-8"), onSuccess, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "ERROR :" + error.toString());
                    Toast.makeText(mCtx, "Network Error. Please check your connection and try again.", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (java.io.UnsupportedEncodingException e){
            Log.e("API",e.getMessage());
        }

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
