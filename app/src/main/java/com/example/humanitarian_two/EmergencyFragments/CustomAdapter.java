package com.example.humanitarian_two.EmergencyFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.humanitarian_two.R;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context c;
    ArrayList<EmergencyService> emergencyServices;
    LayoutInflater inflater;

    public CustomAdapter(Context c, ArrayList<EmergencyService> emergencyServices) {
        this.c = c;
        this.emergencyServices = emergencyServices;
    }



    @Override
    public int getCount() {
        return emergencyServices.size();
    }

    @Override
    public Object getItem(int position) {
      return emergencyServices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int i=position;
        LayoutInflater inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.row_item, null,true);
        TextView stationName = (TextView) rowView.findViewById(R.id.stationName);
        Button call= rowView.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+emergencyServices.get(i).contact));
                c.startActivity(intent);
            }
        });
        stationName.setText(emergencyServices.get(position).name);
        return  rowView;
    }
}


