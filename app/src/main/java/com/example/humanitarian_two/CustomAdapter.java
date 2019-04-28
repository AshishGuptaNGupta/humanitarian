package com.example.humanitarian_two;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.humanitarian_two.model.DonationModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.ColorProto;

import java.util.ArrayList;
import java.util.Arrays;


public class CustomAdapter extends BaseAdapter {
    Context c;
    ArrayList<DonationModel> donations;
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    public CustomAdapter(Context c, ArrayList<DonationModel> donations) {
        this.c = c;
        this.donations = donations;
    }



    @Override
    public int getCount() {
        return donations.size();
    }

    @Override
    public Object getItem(int position) {
        return donations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int i=position;
        Arrays.sort(donations.toArray());
        LayoutInflater inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.donation_row_item, null,true);
        TextView description = (TextView) rowView.findViewById(R.id.description);
        TextView user = (TextView) rowView.findViewById(R.id.user);
        TextView location= (TextView) rowView.findViewById(R.id.location);
        TextView time= (TextView) rowView.findViewById(R.id.time);
        final Button received= rowView.findViewById(R.id.received);

        received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(donations.get(position).getId()!=null)
              {
                    sendNotification(position);
              }

            }
        });

        description.setText(donations.get(position).getDescription());
        time.setText(donations.get(position).getTb().toDate().toString());
        user.setText(donations.get(position).getUser());
        location.setText(donations.get(position).getLocation());


        return  rowView;

    }
    public void sendNotification(int position){
        RequestQueue queue = Volley.newRequestQueue(c);
        String url ="https://us-central1-humanitarian-dbe38.cloudfunctions.net/donationDelivered?"+
                "user="+donations.get(position).getUser()+"&"+"donationId="+donations.get(position).getId()+
                "donationType="+donations.get(position).getDonationType();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("HttpRequest","sent");

                        // Display the first 500 characters of the response string.
//                            textView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("HttpRequestError","Not able to send");
//                    textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
