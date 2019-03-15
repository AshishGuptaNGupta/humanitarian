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
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class UserProfile extends Fragment {
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
    TextView username;
    File original;
    File compressed;
    String verified;
    LinearLayout nameBox;
    LinearLayout genderBox;
    LinearLayout parent;

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
            username.setEnabled(false);
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

    public void selectGender(){
        ArrayList<String>genders=new ArrayList<>();
        genders.add("Male");
        genders.add("Female");
        genders.add("Agender");
        final Spinner gender=new Spinner(getContext());
        gender.setPrompt("Select Gender");

        ArrayAdapter spinnerAdapter=
                new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,genders);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(spinnerAdapter);
        genderBox.addView(gender);

        Button saveGender=new Button(getContext());
        saveGender.setText("save");
        saveGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(user.getUid())
                        .update("gender",gender.getSelectedItem().toString());
            }
        });
        genderBox.addView(saveGender);
    }

    public void getBloodInfo(Map bloodInfo){
        LinearLayout bloodBox= new LinearLayout(getContext());
        bloodBox.setOrientation(LinearLayout.HORIZONTAL);
        TextView label= new TextView(getContext());
        label.setText("Blood Group:");
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
        bloodBox.addView(label);
        TextView type= new TextView(getContext());
        type.setText(bloodInfo.get("bloodGroup").toString());
        type.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
        bloodBox.addView(type);

        Button button =new Button(getContext());
        button.setText("Change");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BloodActivity=new Intent(getContext(),BloodActivity.class);
                startActivity(BloodActivity);
            }
        });
        bloodBox.addView(button);
        parent.addView(bloodBox);
    }
    public void setBloodInfo(){
        LinearLayout bloodBox= new LinearLayout(getContext());
        bloodBox.setOrientation(LinearLayout.VERTICAL);
        TextView msg= new TextView(getContext());
        msg.setText("You are not registered as Blood donor. To Register yourself click below");
        Button button =new Button(getContext());
        button.setText("Register as blood donor");
        bloodBox.addView(msg);
        bloodBox.addView(button);
        parent.addView(bloodBox);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BloodActivity=new Intent(getContext(),BloodActivity.class);
                startActivity(BloodActivity);
            }
        });

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_profile, container, false);
        storage= FirebaseStorage.getInstance();
        updateButton=view.findViewById(R.id.update);
        profilePic=view.findViewById(R.id.profilePic);
        email=view.findViewById(R.id.profileEmail);
        name=view.findViewById(R.id.profileName);
        username=view.findViewById(R.id.profileUsername);
        nameBox=view.findViewById(R.id.nameBox);
        genderBox=view.findViewById(R.id.genderBox);
        parent=view.findViewById(R.id.container);






        username.setEnabled(false);
        name.setEnabled(false);
        email.setEnabled(false);
        //color
        username.setTextColor(Color.BLACK);
        name.setTextColor(Color.BLACK);
        email.setTextColor(Color.BLACK);
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());



        db.collection("users").document(user.getUid()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username.setText(document.get("username").toString());
                        if(document.get("gender")!=null&&document.get("gender")!="")
                        {
                            TextView genderText=new TextView(getContext());
                            genderText.setText(document.get("gender").toString());
                            genderText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
                            genderText.setTextColor(Color.BLACK);
                            genderBox.addView(genderText);
                        }
                        else
                        {
                            selectGender();
                        }
                        if(document.get("bloodInfo")!=null)
                        {
                           getBloodInfo((Map<String,String>)document.get("bloodInfo"));
                        }
                        else
                        {

                            setBloodInfo();
                        }
                    }
                } else {
                    Log.d("problem", "get failed with ", task.getException());
                }
            }
        });

        if(user.getPhotoUrl()!=null) {
            StorageReference storageRef = storage.getReference("profilepic/" + user.getUid() + "/" + user.getPhotoUrl().getLastPathSegment());

            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Context context = profilePic.getContext();
                    Picasso.with(getContext()).load(uri)
                            .transform(new CropCircleTransformation())
                            .into(profilePic);

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








}
