package com.example.humanitarian_two;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.print.PrintAttributes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class Feed extends Fragment {
    Button postButton;
    TextView postText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    Timestamp tb = new Timestamp(new Date());
    String TAG = "msg";
    ArrayList<Post> posts = new ArrayList<Post>();
    LinearLayout linearLayout;
    ArrayList<String> userFollowing = new ArrayList<>();
    CollectionReference collectionRef;
    ArrayList<String> usersUid = new ArrayList<String>();
    Post post;
    String currentUser_userName;

    public void post() {

        Map<String, Object> post = new HashMap<>();
        post.put("post", postText.getText().toString());
        post.put("createdAt", tb.now());
        post.put("uid", currentUser.getUid());


        db.collection("posts")
                .document()
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Post Created", Toast.LENGTH_SHORT);
                        postText.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failure in posting", Toast.LENGTH_SHORT);
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_feed, container, false);

        postButton = view.findViewById(R.id.post);
        postText = view.findViewById(R.id.postText);
        linearLayout = (LinearLayout) view.findViewById(R.id.feed_linearLayout);
        usersUid.add(currentUser.getUid());
        Log.i("current uid", usersUid.get(0));


        db.collection("users").document(currentUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userFollowing = (ArrayList<String>) document.get("following");
                        currentUser_userName = document.get("username").toString();
                        asd();
                        if (userFollowing != null) {
                            getUsersUid();
                        }else
                        {
                            createPost(post);
                        }
                    }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
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





    void asd(){

            Log.i("usersUid", usersUid.get(usersUid.size()-1));
            db.collection("posts").limit(10).orderBy("createdAt", Query.Direction.DESCENDING)
                    .whereEqualTo("uid", usersUid.get(usersUid.size()-1))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
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



    void getUsersUid() {

        for (String username : userFollowing) {
            Log.i("usernames",username);
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
                    asd();
                }
            });
        }

        Arrays.sort(posts.toArray());
        for(Post post:posts) {
            createPost(post);
        }

    }




    public void createPost(Post post) {
        final ImageView dp = new ImageView(getContext());
        final LinearLayout header = new LinearLayout(getContext());
        header.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
        layoutParams.setMargins(10, 5, 0, 0);
        dp.setLayoutParams(layoutParams);

        final TextView username = new TextView(getContext());
        username.setTextSize(25);
        username.setTextColor(Color.BLACK);
        db.collection("users").document(post.uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {


                        String userDisplayName = document.get("name").toString();
                        Uri photo = currentUser.getPhotoUrl();
                        Picasso.with(getContext()).load(photo)
                                .transform(new CropCircleTransformation())
                                .into(dp);
//
                        username.setText(userDisplayName);
                        header.addView(dp);
                        header.addView(username);

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
        String[] regex = new String[0];
        regex = dateText.split("GMT", 2);
        createdAt.setText(regex[0]);
        postBody.setText(post.post);
        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams cardViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardViewLayoutParams.setMargins(5, 10, 5, 10);
        cardView.setLayoutParams(cardViewLayoutParams);
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.addView(header);
        card.addView(postBody);
        card.addView(createdAt);
        cardView.addView(card);

        linearLayout.addView(cardView);
    }
}