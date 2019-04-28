package com.example.humanitarian_two.User;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.humanitarian_two.Home;
import com.example.humanitarian_two.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditUserProfile extends AppCompatActivity {

    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    TextView name;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Map<String,Object> userData=new HashMap<>();
    Spinner gender;
    ArrayAdapter spinnerAdapter;

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

    public void save(View view) {
        userData.put("name",name.getText().toString());
        userData.put("gender",gender.getSelectedItem().toString());

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            db.collection("users").document(user.getUid()).set(userData, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Intent intent = new Intent(getApplicationContext(), Home.class);
                                                intent.putExtra("subject","users");
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }else
                        {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        name = findViewById(R.id.nameEdit);
        name.setFilters(new InputFilter[]{EMOJI_FILTER});
        name.setText(user.getDisplayName());
        gender = findViewById(R.id.genderEdit);

        ArrayList<String> genders=new ArrayList<>();
        genders.add("Male");
        genders.add("Female");
        spinnerAdapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,genders);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(spinnerAdapter);
        gender.setPrompt("Select Gender");

    }
}
