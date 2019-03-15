package com.example.humanitarian_two;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class VerificationPage extends AppCompatActivity {
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();

    private static final int READ_REQUEST_CODE = 42;
    LinearLayout imageBox;
    Uri image;
    Button upload;
    FirebaseStorage storage= FirebaseStorage.getInstance();
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {

                upload.setText("save");
                upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final StorageReference storageRef = storage.getReference("certificates/"+user.getUid()+"/"+image.getLastPathSegment());
                        Task uploadTask = storageRef.putFile(image);


                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(),"Upload fail",Toast.LENGTH_SHORT);
                            }
                        }).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Intent intent=new Intent(getApplicationContext(),Home.class);
                                intent.putExtra("subject","ngos");
                                startActivity(intent);

                            }
                        });
                    }
                });


                image = resultData.getData();

                ImageView imageView=new ImageView(getApplicationContext());
                imageView.setImageURI(image);
                imageBox.addView(imageView);


            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_page);

         upload=findViewById(R.id.verificationPageButton);
        imageBox=findViewById(R.id.imageBox);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }
}
