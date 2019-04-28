package com.example.humanitarian_two;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.humanitarian_two.model.DonationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class VolunteerAdapter extends BaseAdapter {
    Context c;
    ArrayList<String> names;
    ArrayList<String> uid;
    Map<String, Object> foodDonation = new HashMap<>();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();



    public VolunteerAdapter(Context c, ArrayList<String> name,ArrayList<String>uid,Map<String,Object>foodDonation) {
        this.c = c;
        this.names = name;
        this.uid = uid;
        this.foodDonation=foodDonation;
    }



    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        final LayoutInflater inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.volunteers_row_item, null,true);
        TextView name = (TextView) rowView.findViewById(R.id.volunteerName);
        name.setText(names.get(position));
        Button select=  rowView.findViewById(R.id.selectVolunteer);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DocumentReference docRef=db.collection("volunteerRequest").document();
                String id=docRef.getId();
                foodDonation.put("requestId",id);
                foodDonation.put("volunteerUid",uid.get(position));
                foodDonation.put("volunteerUsername",names.get(position));
                foodDonation.put("status","pending");
                foodDonation.put("requesteeUsername",currentUser.getDisplayName());
                docRef.set(foodDonation)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Snackbar.make(v, "Your request submitted successfully", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    Intent intent=new Intent(c,Home.class);
                                    intent.putExtra("subject","users");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    c.startActivity(intent);
                                }
                            }
                        });
            }
        });



        return  rowView;

    }
}
