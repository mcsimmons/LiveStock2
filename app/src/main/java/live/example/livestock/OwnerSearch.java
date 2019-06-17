package live.example.livestock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import live.example.livestock.Adapter.ListAdapter;
import live.example.livestock.Model.ListItem;
import live.example.livestock.Utils.EndlessRecyclerViewScrollListener;
import live.example.livestock.Utils.PhonePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import static live.example.livestock.LivestockAppData.RESULT_LOCATION_SEARCH;

public class OwnerSearch extends AppCompatActivity implements ListAdapter.OnNoteListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DrawerLayout sideMenu;
    private List<ListItem> listItems = new ArrayList<>();
    private LivestockAPI API = LivestockAPI.getInstance(this);
    private String search_phrase = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_search);


        Toolbar toolbar = OwnerSearch.this.findViewById(R.id.toolbar_3);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        //Initialize side menu
        sideMenu = OwnerSearch.this.findViewById(R.id.side_menu_3);
        NavigationView navigationView = OwnerSearch.this.findViewById(R.id.nav_view_3);
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
                        else if  (menuItem.getTitle().equals("Add Owner")){
                            //Return to login
                            Intent myIntent = new Intent(OwnerSearch.this,AddOwner.class);
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

        LinearLayoutManager myLinearLayout = new LinearLayoutManager(this);

        EndlessRecyclerViewScrollListener recy_scrol_listen = new EndlessRecyclerViewScrollListener(myLinearLayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getRows(page);
            }
        };

        final ImageButton btnSearch = (ImageButton) findViewById(R.id.btnOwnerSearch);
        final EditText txtSearch = (EditText) findViewById(R.id.txtOwnerSearch);

        txtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            btnSearch.callOnClick();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_phrase = txtSearch.getText().toString();
                Log.i("search_phrase",search_phrase);
                listItems.clear();
                adapter = new ListAdapter(OwnerSearch.this, listItems,OwnerSearch.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                getRows(0);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.ol_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(myLinearLayout);

        recyclerView.addOnScrollListener(recy_scrol_listen);

           /* listItems = new ArrayList<>();

            for (int i=0; i < 10; i++){
                ListItem item = new ListItem(i + " Hi", "Mark");

                listItems.add(item);
            }

            adapter = new ListAdapter(this, listItems);
            recyclerView.setAdapter(adapter);*/

           getRows(0);
    }


    public void onResume(){
        super.onResume();
        //Logout if UserID is cleared
        if(LivestockAppData.UserID == 0){
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
    private void getRows(final int index){
        final DateFormat tformatter = DateFormat.getDateInstance();
        final int chunk_size = 10;

        API.getOwnersList( chunk_size,chunk_size * index,search_phrase,new Response.Listener<String>(){
            @Override
            public void onResponse(String response)
            {   // Get Response
                try {
                    JSONArray jArray = new JSONArray(response);

                     for(int i = 0; i < jArray.length(); i++){
                         JSONObject jResponse = jArray.getJSONObject(i);




                         listItems.add(new ListItem(jResponse.getInt("id"),jResponse.getString("name"),jResponse.getString("address")));
                     }

                    //On initial load, we create the adapter
                    if(adapter == null) {
                        adapter = new ListAdapter(OwnerSearch.this, listItems,OwnerSearch.this);
                        recyclerView.setAdapter(adapter);
                    }
                    else //otherwise, we just add rows
                    {
                        adapter.notifyItemRangeInserted(chunk_size * index,chunk_size);
                    }

                }
                catch(JSONException e)
                {
                    Toast.makeText(getApplicationContext(),"Invalid response from server", Toast.LENGTH_LONG).show();
                    Log.e("TimeClock", "INVALID RESPONSE :" + response);

                }
            }});
    }

    @Override
    public void onNoteClick(int position){
        ListItem record = listItems.get(position);
        Log.d("NoteClick", "onNoteClick: " + record.getName());

        API.getOwnerInfo(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jResponse = new JSONObject(response);

                   // owner_address = (jResponse.get("address").toString());
                   // owner_name = (marker.getTitle());
                    long[] owner_phones;
                    owner_phones = new long[jResponse.getJSONArray("phones").length()];

                    for(int i = 0; i<owner_phones.length;i++){
                        owner_phones[i] = jResponse.getJSONArray("phones").getLong(i);
                    }

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    long phone = 0;
                    if (owner_phones.length == 1){
                        phone = owner_phones[0];
                        intent.setData(Uri.parse("tel:" + phone));
                        getApplicationContext().startActivity(intent);
                    }
                    else if (owner_phones.length > 1){
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.pick_phone_number_popup, null);
                        PhonePicker picker = new PhonePicker(getApplicationContext(),owner_phones,popupView,findViewById(android.R.id.content));
                        picker.showPhonePickerPopup();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No phone number found.",Toast.LENGTH_SHORT).show();
                    }
                    //if(jResponse.has("notes")){
                    //    owner_note = (jResponse.get("notes").toString());
                    //}
                    //else{
                    //    owner_note = null;
                    //}

                }
                catch(JSONException e)
                {
                    Toast.makeText(getApplicationContext(),"Invalid response from server", Toast.LENGTH_LONG).show();
                    Log.e("Login", "INVALID RESPONSE :" + response);
                    return;
                }
            }
        }, record.getId());
    }
}
