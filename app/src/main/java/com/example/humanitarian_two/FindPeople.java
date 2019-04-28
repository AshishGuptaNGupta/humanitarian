package com.example.humanitarian_two;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FindPeople extends Fragment {

    ArrayList<String> users = new ArrayList<String>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "msg";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DocumentReference docRef;
    ArrayList<String> following=new ArrayList<>();
    ListView userList;
    String currentUser_username;
    DocumentReference docRef1;
    FindPeopleAdapter adapter;
    ArrayList<String> profilePics=new ArrayList<>();
    FirebaseUser currentUser=mAuth.getCurrentUser();


    public void onCall() {
        CollectionReference usersCollection = db.collection("users");
        usersCollection.limit(30)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (!document.get("username").toString().equals(currentUser_username)) {
                                    users.add(document.getData().get("username").toString());
                                    if (document.getData().get("profilePicUrl") != null)
                                        profilePics.add(document.getData().get("profilePicUrl").toString());
                                    else
                                        profilePics.add("https://firebasestorage.googleapis.com/v0/b/everystepcounts-a0d73.appspot.com/o/general_male.jpg?alt=media&token=422d3019-deed-4004-8f35-069338d589cc");
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    public void currentUserdetails() {
        db.collection("users").document(currentUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentUser_username = document.get("username").toString();
                        following = (ArrayList<String>) document.get("following");

                        adapter.notifyDataSetChanged();

                        Log.i("current User name", currentUser_username);


                    }

                } else {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT);
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                onCall();
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_find_people, container, false);



        userList = (ListView) view.findViewById(R.id.userList);
        adapter = new FindPeopleAdapter(getContext(), users, profilePics);
        new LoadData().execute();
        userList.setAdapter(adapter);


        return view;
    }

    private class LoadData extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            currentUserdetails();
            docRef = db.collection("users").document(currentUser.getUid());



        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);



        }

        @Override
        protected String doInBackground(Void... voids) {

            return null;
        }
    }
}
