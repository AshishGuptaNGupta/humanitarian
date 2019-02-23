package com.example.humanitarian_two.EmergencyFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.example.humanitarian_two.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PoliceFragment extends Fragment{
    FirebaseFirestore db=FirebaseFirestore.getInstance();


    String TAG="msg";
    ArrayList<EmergencyService> helplineContacts=new ArrayList<EmergencyService>();
    ArrayList<String> names=new ArrayList<String>();
    ArrayList<String> contact=new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    private static CustomAdapter adapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_hospital, container, false);
        ListView listView=(ListView)view.findViewById(R.id.listView);


        db.collection("emergency").document("police").collection("ashish")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    Log.i("DOC data",   document.getData().toString());
                                    EmergencyService police = document.toObject(EmergencyService.class);
                                    helplineContacts.add(police);
                                    names.add(police.name);
                                    contact.add(police.contact);
                                }
                                catch (Exception e){
                                    System.out.println(e.getMessage());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });




        adapter=new CustomAdapter(getContext(),helplineContacts );
        listView.setAdapter(adapter);

//        arrayAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,names);
//
//        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+contact.get(position)));
                startActivity(intent);
            }
        });

        return  view;

    }
}

