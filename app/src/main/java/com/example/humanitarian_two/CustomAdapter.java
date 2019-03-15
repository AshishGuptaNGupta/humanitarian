package com.example.humanitarian_two;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.humanitarian_two.model.DonationModel;

import java.util.ArrayList;


public class CustomAdapter extends BaseAdapter {
    Context c;
    ArrayList<DonationModel> donations;


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
    public View getView(int position, View convertView, ViewGroup parent) {
        final int i=position;
        LayoutInflater inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.donation_row_item, null,true);
        TextView description = (TextView) rowView.findViewById(R.id.description);
        TextView user = (TextView) rowView.findViewById(R.id.user);
        TextView location= (TextView) rowView.findViewById(R.id.location);
        TextView time= (TextView) rowView.findViewById(R.id.time);

        description.setText(donations.get(position).getDescription());
        time.setText(donations.get(position).getTb().toDate().toString());
        user.setText(donations.get(position).getUser());
        location.setText(donations.get(position).getLocation());

        return  rowView;

    }
}
