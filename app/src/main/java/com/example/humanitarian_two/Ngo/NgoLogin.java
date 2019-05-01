package com.example.humanitarian_two.Ngo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.humanitarian_two.Emergency;
import com.example.humanitarian_two.Home;
import com.example.humanitarian_two.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class
NgoLogin extends AppCompatActivity {
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
    Intent intent;
    Button submit;
    TextView notify;
    TextView emailLabel;
    TextView name;
    TextView username;
    Map<String, Object> user = new HashMap<>();

    public void signUp(View view){
        login=false;
        signUpButton.setTextColor(Color.WHITE);
        loginButton.setTextColor(Color.parseColor("#D3D3D3"));
        forgot.setVisibility(View.INVISIBLE);
        name.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);

    }

    public void login(View view){
        login=true;
        signUpButton.setTextColor(Color.parseColor("#D3D3D3"));
        loginButton.setTextColor(Color.WHITE);
        forgot.setVisibility(View.VISIBLE);
        username.setVisibility(View.INVISIBLE);
        name.setVisibility(View.INVISIBLE);
    }

    public void onEmergency(View view){
        Intent intent=new Intent(this, Emergency.class);
        startActivity(intent);

    }
    public void onClick(View view ){
        if(login) {
            try {
                if(email.getText().toString().isEmpty()||password.getText().toString().isEmpty())
                {
                    notify.setText("You forgot to enter email or passsword");
                }else {
                    db.collection("ngos").whereEqualTo("email",email.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                                if(task.getResult().isEmpty()) {
                                    email.setError("this is not ngo email");
                                }else
                                {
                                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new Activity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                intent = new Intent(getApplicationContext(), Home.class);
                                                intent.putExtra("subject","ngos");
                                                startActivity(intent);
                                                Log.i("Log", "Logged in ");

                                            } else {
                                                Log.i("Log", "Not Logged in ");
                                            }
                                        }
                                    });
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

            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i("SignUp", "Created user  ");
                                currentUser=mAuth.getCurrentUser();
                                user.put("email",email.getText().toString());
                                user.put("name",name.getText().toString());
                                user.put("uid",currentUser.getUid());
                                user.put("username",username.getText().toString());
                                user.put("verified",false);
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name.getText().toString())
                                        .build();
                                currentUser.updateProfile(profileUpdates);
                                db.collection("ngos").document(currentUser.getUid())
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("doc", "success");
                                                intent = new Intent(getApplicationContext(), Home.class);
                                                intent.putExtra("subject","ngos");
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        currentUser.delete();
                                        Log.i("doc", "fail", e);
                                    }
                                });
                            } else {
                                Log.i("SignUp", "Not SignUp  ");
                                currentUser.delete();
                            }
                        }
                    });

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
        setContentView(R.layout.activity_ngo_login);
        mAuth= FirebaseAuth.getInstance();

        linearLayout=findViewById(R.id.linearLayout);
        notify=findViewById(R.id.notifyLabel);
        emailLabel=findViewById(R.id.emailLabel);
        emailLabel.setVisibility(View.INVISIBLE);
        username=findViewById(R.id.ngoUsername);
        name=findViewById(R.id.ngoName);
        name.setFilters(new InputFilter[]{EMOJI_FILTER});
        name.setVisibility(View.INVISIBLE);
        notify.setVisibility(View.INVISIBLE);
        email=findViewById(R.id.ngoEmail);
        email.setFilters(new InputFilter[]{EMOJI_FILTER});
        password=findViewById(R.id.password);
        password.setFilters(new InputFilter[]{EMOJI_FILTER});
        username.setVisibility(View.INVISIBLE);
        username.setFilters(new InputFilter[]{EMOJI_FILTER});
        submit=findViewById(R.id.submit);
        currentUser = mAuth.getCurrentUser();
        signUpButton=findViewById(R.id.signUp);
        loginButton=findViewById(R.id.login);
        forgot=findViewById(R.id.forgot);
        signUpButton.setTextColor(Color.parseColor("#D3D3D3"));

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(email.getText());
                    if(matcher.matches()){
                        email.setError(null);

                    }
                    else
                        email.setError("Please enter valid email");
                }else
                {
                    TextView email=v.findViewById(R.id.email);
                    email.setError(null);
                }

            }
        });




    }

    public static InputFilter EMOJI_FILTER = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {
                int type = Character.getType(source.charAt(index));
                if (type == Character.SURROGATE) {
                    return "";
                }
            }
            return null;
        }
    };
}
