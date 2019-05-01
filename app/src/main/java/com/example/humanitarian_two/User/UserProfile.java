package com.example.humanitarian_two.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.humanitarian_two.BloodActivity;
import com.example.humanitarian_two.MainActivity;
import com.example.humanitarian_two.R;
import com.example.humanitarian_two.User.EditUserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.example.humanitarian_two.Home.MyPREFERENCES;
import static com.example.humanitarian_two.Home.sharedpreferences;


public class UserProfile extends Fragment {
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    TextView email;

    TextView name;
    ImageButton updateButton;
    FirebaseStorage storage;
    ImageButton profilePic;
    Uri image;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int READ_REQUEST_CODE = 42;
    Button signOut;
    TextView username;
    TextView gender;
    Uri compressedUri;
    StorageReference currentPicStorageRef;
    LinearLayout parent;




    public void performFileSearch(View view) {


        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public Uri getImageUri(Context inContext) {
        Context c=getContext();
        Bitmap bitmap=null;
        String path=null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap( c.getContentResolver(),image);
            Bitmap inImage =bitmap;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            path= MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, image.getLastPathSegment(), null);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.parse(path);
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                image = resultData.getData();

                compressedUri=getImageUri(getContext());

                Log.i("Image", "Uri: " + compressedUri.toString());
                final StorageReference storageRef = storage.getReference("profilepic/"+user.getUid()+"/"+image.getLastPathSegment());
                Task uploadTask = storageRef.putFile(compressedUri);



                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(),"Upload fail",Toast.LENGTH_SHORT);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl();
                        Picasso.with(getContext()).load(compressedUri)
                                .transform(new CropCircleTransformation()).into(profilePic);
                        if(currentPicStorageRef!=null){
                            currentPicStorageRef.delete();
                        }

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

                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                db.collection("users").document(user.getUid()).update("profilePicUrl",uri.toString());
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
        Picasso.with(context).load(image)
                .transform(new CropCircleTransformation()).into(profilePic);
        Toast toast = new Toast(getContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void update(View view) {
        Intent intent= new Intent(getContext(), EditUserProfile.class);
        startActivity(intent);
    }

    public void signOut(View view){

        SharedPreferences.Editor editor=sharedpreferences.edit();
        editor.clear();
        editor.commit();
        db.collection("users").document(user.getUid()).update("token", FieldValue.delete())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mAuth.getInstance().signOut();
                        Intent intent=new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                });

    }

    //    public String getRealPathFromURI(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//            String[] proj = { MediaStore.Images.Media.DATA };
//            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_profile, container, false);
        storage= FirebaseStorage.getInstance();


        updateButton=view.findViewById(R.id.update);
        profilePic=view.findViewById(R.id.profilePic);


        email=view.findViewById(R.id.emailPro);
        name=view.findViewById(R.id.namePro);
        username=view.findViewById(R.id.userNamePro);
        gender=view.findViewById(R.id.genderPro);
        name=view.findViewById(R.id.namePro);


        parent=view.findViewById(R.id.infoBox);


        email.setText(user.getEmail());
        name.setText(user.getDisplayName());




        db.collection("users").document(user.getUid()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i("userdata",document.getData().toString());
                        username.setText(document.get("username").toString());
                        if(document.get("gender")!=null)
                            gender.setText(document.get("gender").toString());
                        if(document.get("bloodInfo")!=null)
                         getBloodInfo(document.get("bloodInfo.bloodGroup").toString());
                        else
                            setBloodInfo();


                    }
                } else {
                    Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(user.getPhotoUrl()!=null) {
            Log.i("photo uri",user.getPhotoUrl().getLastPathSegment());
            currentPicStorageRef = storage.getReference("profilepic/" + user.getUid() + "/" + user.getPhotoUrl().getLastPathSegment());

            currentPicStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Context context = profilePic.getContext();
                    Picasso.with(getContext()).load(uri)
                            .transform(new CropCircleTransformation())
                            .into(profilePic);
                }
            });
        }
        else
        {
            String url="https://firebasestorage.googleapis.com/v0/b/humanitarian-dbe38.appspot.com/o/man.png?alt=media&token=f445025c-30b7-42d6-a64e-a4fe3767a4c3";
            Picasso.with(getContext()).load(url)
                    .transform(new CropCircleTransformation())
                    .into(profilePic);
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





    public void getBloodInfo(String bloodGroup){
        LinearLayout bloodBox= new LinearLayout(getContext());
        bloodBox.setOrientation(LinearLayout.HORIZONTAL);
        TextView label= new TextView(getContext());
        label.setText("Blood Group:");
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
        bloodBox.addView(label);
        TextView type= new TextView(getContext());
        type.setText(bloodGroup);
        type.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
        bloodBox.addView(type);

        Button button =new Button(getContext());
        button.setText("Change");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BloodActivity=new Intent(getContext(), com.example.humanitarian_two.BloodActivity.class);
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





}
