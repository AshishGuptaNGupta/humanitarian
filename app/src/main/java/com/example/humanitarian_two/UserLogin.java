package com.example.humanitarian_two;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.browser.browseractions.BrowserActionsIntent;


public class UserLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser=null;
    TextView email;
    TextView password;
    boolean login=true;
    Button signUpButton;
    Button loginButton;
    TextView forgot;
    LinearLayout linearLayout;
    Intent profile;
    Button submit;
    TextView notify;
    TextView emailLabel;
    Map<String, Object> user = new HashMap<>();
    ArrayList<String>following=new ArrayList<String>();
    TextView username;
    public void signUp(View view){
        login=false;
        signUpButton.setTextColor(Color.WHITE);
        loginButton.setTextColor(Color.parseColor("#D3D3D3"));
        forgot.setVisibility(View.INVISIBLE);
        username.setVisibility(View.VISIBLE);
    }

    public void login(View view){
        login=true;
        signUpButton.setTextColor(Color.parseColor("#D3D3D3"));
        loginButton.setTextColor(Color.WHITE);
        forgot.setVisibility(View.VISIBLE);
        username.setVisibility(View.INVISIBLE);
    }

    public void onEmergency(View view){
        Intent intent=new Intent(this,Emergency.class);
        startActivity(intent);

    }
    public void onClick(View view ){
        if(login) {
            try {
                if(email.getText()==""||password.getText()=="")
                {
                    Toast.makeText(this,"You forgot to enter email or passsword", Toast.LENGTH_LONG);

                }else {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                profile = new Intent(getApplicationContext(), Home.class);
                                startActivity(profile);
                                Log.i("Log", "Logged in ");

                            } else {
                                Log.i("Log", "Not Logged in ");
                            }
                        }
                    });
                }
            }
            catch (Exception e)
            {
                Log.i("Error",e.getMessage().toString());
            }
        }
        else {
            if(email.getText()==null||password.getText()==null||username.getText()==null)
            {
                Toast.makeText(getApplicationContext(),"You forgot to enter something", Toast.LENGTH_LONG);

            }
            else {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.i("SignUp", "Created user  ");
                                    currentUser = mAuth.getCurrentUser();
                                    user.put("email", email.getText().toString());
                                    user.put("uid", currentUser.getUid());
                                    user.put("following", following);
                                    user.put("username", username.getText().toString());
                                    db.collection("users").document(currentUser.getUid())
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.i("doc", "success");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("doc", "fail", e);
                                        }
                                    });
                                } else {
                                    Log.i("SignUp", "Not SignUp  ");
                                }
                            }
                        });
            }

        }

    }
    public void goBack(){
        password.setVisibility(View.VISIBLE);
        forgot.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        notify.setVisibility(View.INVISIBLE);
        emailLabel.setVisibility(View.INVISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick(v);
            }
        });
    }
    public void forgotPassword1(){
        mAuth.sendPasswordResetEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            notify.setText("An email to reset password has been sent to your email address");
                            notify.setVisibility(View.VISIBLE);
                        }
                    }
                });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    public void forgotPassword(View view){

        password.setVisibility(View.INVISIBLE);
        forgot.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
        emailLabel.setVisibility(View.VISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword1();
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        mAuth= FirebaseAuth.getInstance();

        username=findViewById(R.id.username);
        linearLayout=findViewById(R.id.linearLayout);
        notify=findViewById(R.id.notifyLabel);
        emailLabel=findViewById(R.id.emailLabel);
        emailLabel.setVisibility(View.INVISIBLE);
        notify.setVisibility(View.INVISIBLE);
        email=findViewById(R.id.name);
        password=findViewById(R.id.password);
        submit=findViewById(R.id.submit);
        currentUser = mAuth.getCurrentUser();
        signUpButton=findViewById(R.id.signUp);
        loginButton=findViewById(R.id.login);
        forgot=findViewById(R.id.forgot);
        signUpButton.setTextColor(Color.parseColor("#D3D3D3"));



    }
}
