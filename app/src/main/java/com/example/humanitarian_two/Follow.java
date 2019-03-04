package com.example.humanitarian_two;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Follow extends Fragment {
    ArrayAdapter arrayAdapter;
    ArrayList<String> users = new ArrayList<String>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "msg";
    FirebaseUser currentUser;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DocumentReference docRef;
    String currentUser_Username;
    ArrayList<String> userNames;
    ListView userList;




    public void onCall() {
        CollectionReference usersCollection = db.collection("users");
        usersCollection.limit(30)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("username"));
                                if(currentUser_Username!=document.getData().get("username").toString())
                                users.add(document.getData().get("username").toString());
                            }

                            for (String username : userNames) {
                                Log.i("username", username);
                                if (users.contains(username)) {
                                    userList.setItemChecked(users.indexOf(username), true);
                                }
                            }
                            arrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_follow, container, false);


        currentUser = mAuth.getCurrentUser();
        docRef = db.collection("users").document(currentUser.getUid());
        userList = (ListView) view.findViewById(R.id.userList);
        userList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_checked, users);


        db.collection("users").document(currentUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userNames = (ArrayList<String>) document.get("following");
//                        Log.i("a",userNames.get(0));

                        onCall();


                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


       db.collection("users").document(currentUser.getUid())
        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentUser_Username=document.get("username").toString();
                        Log.i("current user username", document.get("username").toString());
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        userList.setAdapter(arrayAdapter);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {

                    docRef = db.collection("users").document(currentUser.getUid());
                    docRef.update("following", FieldValue.arrayUnion(users.get(position)));

                } else {

                    docRef.update("following", FieldValue.arrayRemove(users.get(position)));
                }
            }
        });
        return view;
    }


}


