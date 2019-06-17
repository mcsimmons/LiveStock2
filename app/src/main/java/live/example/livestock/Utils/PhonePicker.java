package live.example.livestock.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import live.example.livestock.Adapter.ListAdapter;
import live.example.livestock.MapsActivity;
import live.example.livestock.Model.ListItem;
import live.example.livestock.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PhonePicker implements ListAdapter.OnNoteListener {
    Context mCtx;
    List<ListItem> listItems;
    View holderView;


    public PhonePicker(Context ctx, long[] phone_numbers,View popupView, View holderView){
        this.mCtx = ctx;
        this.holderView = holderView;



        listItems = new ArrayList<>();

        for(Long phone : phone_numbers){
            listItems.add(new ListItem(0,""+phone,""));
        }

    }

    @Override
    public void onNoteClick(int position) {
        Log.d("NoteClick", "onNoteClick: PhonePicker " + position);
        ListItem record = listItems.get(position);

        Intent intent = new Intent(Intent.ACTION_DIAL);
        long phone = 0;

        try {
            phone = Long.parseLong(record.getName());
        }
        catch (NumberFormatException e)
        {
            Log.e("NoteClick", "onNoteClick: NumberFormatException",e );
        }
        if (phone > 0) {
            intent.setData(Uri.parse("tel:" + phone));
            mCtx.getApplicationContext().startActivity(intent);
        }
        else{
            Toast.makeText(mCtx.getApplicationContext(),"No phone number found.",Toast.LENGTH_SHORT).show();
        }
    }

    public void showPhonePickerPopup(){
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                mCtx.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pick_phone_number_popup, null);


        // create the popup window
        int width = 600;//LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = 300 * listItems.size();//LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        try {
            popupWindow.setElevation(10);
        }
        catch (Exception e){
            //Don't bother on older phones
        }

        // show the popup window
        popupWindow.showAtLocation(holderView, Gravity.CENTER, 0, 0);

        RecyclerView recyclerView = (RecyclerView) popupView.findViewById(R.id.pn_recycler);
        LinearLayoutManager myLinearLayout = new LinearLayoutManager(mCtx.getApplicationContext());
        RecyclerView.Adapter adapter;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(myLinearLayout);
        adapter = new ListAdapter(mCtx, listItems,PhonePicker.this);
        recyclerView.setAdapter(adapter);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(!v.equals(this)) {
                    popupWindow.dismiss();
                    return true;
                }
                else{
                    return false;
                }
            }
        });
    }
}
