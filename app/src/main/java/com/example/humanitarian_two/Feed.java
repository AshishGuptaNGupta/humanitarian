package com.example.humanitarian_two;

import android.graphics.Color;
import android.net.Uri;
import android.print.PrintAttributes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class Feed extends Fragment {
    Button postButton;
    TextView postText;
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser currentUser=mAuth.getCurrentUser();
    Timestamp tb=new Timestamp(new Date());
    String TAG="msg";
    ArrayList<Post> posts=new ArrayList<Post>();
    LinearLayout linearLayout;
    ArrayList<String>userFollowing=new ArrayList<>();
    CollectionReference collectionRef;
    ArrayList<String> usersUid=new ArrayList<String>();
    Post post;

    public void post()
    {

        Map<String, Object> post = new HashMap<>();
        post.put("post", postText.getText().toString());
        post.put("createdAt", tb.now());
        post.put("uid",currentUser.getUid());



        db.collection("posts")
                .document(tb.now().toDate().toString())
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),"Post Created", Toast.LENGTH_SHORT);
                        postText.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Failure in posting", Toast.LENGTH_SHORT);
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_feed, container, false);

        postButton=view.findViewById(R.id.post);
        postText=view.findViewById(R.id.postText);
        linearLayout=(LinearLayout)view.findViewById(R.id.feed_linearLayout);
        usersUid.add(currentUser.getUid());



        db.collection("users").document(currentUser.getUid())
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userFollowing= (ArrayList<String>)document.get("following");
                        Log.i("userFollowingList",userFollowing.get(1));

                    }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                getUid();
            }
        });





        //post listener
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });



        return view;
    }


    public void getUid(){

        for(String username:userFollowing){
            db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    usersUid.add(document.get("uid").toString());


                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(String temp:usersUid) {

                        db.collection("posts").limit(10).orderBy("createdAt", Query.Direction.DESCENDING)
                                .whereEqualTo("uid",temp)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                post = document.toObject(Post.class);
                                                posts.add(post);

                                                createPost(post);

                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }
                }
            });
        }
    }
    public void createPost(Post post) {
        final ImageView dp=new ImageView(getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
        dp.setLayoutParams(layoutParams);

        final TextView username = new TextView(getContext());
        db.collection("users").document(post.uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userDisplayName=document.get("name").toString();
                        Uri photo=currentUser.getPhotoUrl();
                        Picasso.with(getContext()).load(photo)
                                .transform(new CropCircleTransformation())
                                .into(dp);
//
                        username.setText(userDisplayName);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        TextView postBody = new TextView(getContext());

            postBody.setTextSize(50);
            postBody.setTextColor(Color.BLACK);
            TextView createdAt = new TextView(getContext());
            Date date = post.createdAt.toDate();
            String dateText = date.toString();
            String[] regex = dateText.split("GMT", 0);
            createdAt.setText(regex[0]);
            postBody.setText(post.post);
            LinearLayout card = new LinearLayout(getContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.card);
            card.addView(dp);
            card.addView(username);
            card.addView(createdAt);
            card.addView(postBody);
            linearLayout.addView(card);
        }


}
