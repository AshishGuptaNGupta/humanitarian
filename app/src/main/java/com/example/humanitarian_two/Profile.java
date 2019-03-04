package com.example.humanitarian_two;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.http.Url;


public class Profile extends Fragment {
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    TextView email;
    TextView name;
    boolean update=false;
    Button updateButton;
    FirebaseStorage storage;
    ImageButton profilePic;
    Uri image;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int READ_REQUEST_CODE = 42;
    Map<String, Object> userMap = new HashMap<>();
    Button signOut;


    public void performFileSearch(View view) {


        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                image = resultData.getData();
                Log.i("Image", "Uri: " + image.toString());
                final StorageReference storageRef = storage.getReference("profilepic/"+user.getUid()+"/"+image.getLastPathSegment());
                Task uploadTask = storageRef.putFile(image);


                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(),"Upload fail",Toast.LENGTH_SHORT);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl();
                        profilePic.setImageURI(image);
                        Toast.makeText(getContext(),"Upload complete",Toast.LENGTH_SHORT);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(image)
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.i("update", user.getPhotoUrl().toString());
                                        }
                                    }
                                });
                    }
                });

            }
        }
    }
    public void openPic(View view) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.profile_pic_display,
                (ViewGroup) view.findViewById(R.id.custom_toast_container));
        ImageView profilepic= view.findViewById(R.id.biggerProfilePic);
        Context context = profilePic.getContext();
        Picasso.with(context).load(image).into(profilePic);
        Toast toast = new Toast(getContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void update(View view) {
        if(update){
            update=false;
            name.setEnabled(false);
            email.setEnabled(false);
            updateButton.setText("update");
        }
        else {
            name.setEnabled(true);
            email.setEnabled(true);
            updateButton.setText("save");
            update = true;
        }
        if(update==false)
        {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name.getText().toString())
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i("update", "User profile updated.");
                            }
                        }
                    });
            userMap.put("name",name.getText().toString());
            userMap.put("email",email.getText().toString());
            db.collection("users").document(user.getUid())
                    .update(userMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("doc", "success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("doc", "fail",e);
                }
            });

        }
    }

    public void signOut(View view){
        mAuth.getInstance().signOut();
        Intent intent=new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        storage= FirebaseStorage.getInstance();

        updateButton=view.findViewById(R.id.update);
        profilePic=view.findViewById(R.id.profilePic);
        email=view.findViewById(R.id.email);
        name=view.findViewById(R.id.name);

        name.setEnabled(false);
        email.setEnabled(false);
        //color
        name.setTextColor(Color.BLACK);
        email.setTextColor(Color.BLACK);
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());

//        email.setText(user.getEmail().toString());
//        name.setText(user.getDisplayName().toString());
//        Log.i("url", storageRef.toString());
        if(user.getPhotoUrl()!=null) {
            StorageReference storageRef = storage.getReference("profilepic/" + user.getUid() + "/" + user.getPhotoUrl().getLastPathSegment());

            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Context context = profilePic.getContext();
                    Picasso.with(context).load(uri).into(profilePic);

                }
            });
        }


        signOut=view.findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut(v);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(v);
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch(v);
            }
        });

        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//
//
//
//
//
//
//    }
}
