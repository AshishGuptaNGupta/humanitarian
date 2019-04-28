package com.example.humanitarian_two;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class FindPeopleAdapter extends BaseAdapter {
    Context c;
    ArrayList<String> users=new ArrayList<>();
    ArrayList<String> following=new ArrayList<>();
    ArrayList<String> profilePic=new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    DocumentReference docRef1;
    String currentUser_username;
    Button followButton;
    String docId;
    static String TAG="Error";

    public FindPeopleAdapter(Context c, ArrayList<String> users, ArrayList<String> profilePic) {
        this.c = c;
        this.users = users;
        this.profilePic = profilePic;

        db.collection("users").document(currentUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentUser_username = document.get("username").toString();
                        following = (ArrayList<String>) document.get("following");


                    }

                }
            }
        });

    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.find_people_row, null, true);
        TextView username = (TextView) rowView.findViewById(R.id.username);
        ImageView profileImage = rowView.findViewById(R.id.profilePic);
        followButton = rowView.findViewById(R.id.followButton);

        Picasso.with(c).load(profilePic.get(position))
                .transform(new CropCircleTransformation()).into(profileImage);

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction(position);
            }
        });

            if (following.contains(users.get(position))) {
                followButton.setText("UnFollow");
            } else {
                followButton.setText("Follow");
            }





        username.setText(users.get(position));


        return rowView;

    }

    public void doAction(int position) {

            if (following.contains(users.get(position))) {
                db.collection("users").document(currentUser.getUid())
                        .update("following", FieldValue.arrayRemove(users.get(position)));

                db.collection("users").whereEqualTo("username", users.get(position))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        db.collection("users").document(document.getId())
                                                .update("followers", FieldValue.arrayRemove(currentUser_username));

                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                following.remove(users.get(position));
                Log.i("unFollow", "unFollow");
                followButton.setText("Follow");
                notifyDataSetChanged();
            } else {
                db.collection("users").document(currentUser.getUid())
                        .update("following", FieldValue.arrayUnion(users.get(position)));
                db.collection("users")
                        .whereEqualTo("username", users.get(position))
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("users").document(document.getId())
                                        .update("followers", FieldValue.arrayUnion(currentUser_username))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                sendNotification(document.getId());
                                            }
                                        });

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
                following.add(users.get(position));
                Log.i("Follow", "Follow");
                followButton.setText("UnFollow");
                notifyDataSetChanged();
            }
        }

        public void sendNotification(String following){
            RequestQueue queue = Volley.newRequestQueue(c);
            String url ="https://us-central1-humanitarian-dbe38.cloudfunctions.net/followNotification?"+
                        "following="+following+"&"+"follower="+currentUser_username;
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
