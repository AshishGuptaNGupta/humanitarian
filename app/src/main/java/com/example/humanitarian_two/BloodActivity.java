package com.example.humanitarian_two;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BloodActivity extends AppCompatActivity {
    LinearLayout parent;
    Spinner bloodType;
    Map<String,Object> bloodInfo=new HashMap<>();
    TextView address;
    FirebaseFirestore db= FirebaseFirestore.getInstance();

    public void onRadioButtonClicked(View view) {


        boolean checked = ((RadioButton) view).isChecked();


        switch(view.getId()) {
            case R.id.yes:
                if (checked)
                    bloodInfo.put("donor","yes");
                    break;
            case R.id.no:
                if (checked)
                    bloodInfo.put("donor","no");

                    break;
        }
    }



    public void save(){


        final Button save = new Button(getApplicationContext());
        save.setText("Save");
        parent.addView(save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodInfo.put("bloodGroup",bloodType.getSelectedItem());

                    if(address.getText()==null||address.getText().toString()=="")
                    {
                        address.setError("You forgot to fill this");
                    }
                    else if(bloodType.getSelectedItem()=="Select blood Type"){
                        bloodType.setOutlineAmbientShadowColor(Color.RED);

                }
                    else {
                        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .update("bloodInfo", bloodInfo,
                                        "address", address.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        TextView success = new TextView(getApplicationContext());
                                        success.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                                        success.setTextColor(Color.GREEN);
                                        success.setText("Info saved successfully");
                                        parent.addView(success);
                                        Intent intent=new Intent(getApplicationContext(),Home.class);
                                        intent.putExtra("subject","users");
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        TextView success = new TextView(getApplicationContext());
                                        success.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                                        success.setTextColor(Color.RED);
                                        success.setText("Info not saved");
                                        parent.addView(success);
                                    }
                                });
                    }

            }
        });
    }
    public  void spinnerIntialization()
    {
        ArrayList<String>bloodArray=new ArrayList<>();
        bloodArray.add("Select blood Type");
        bloodArray.add("A+");
        bloodArray.add("A-");
        bloodArray.add("B+");
        bloodArray.add("B-");
        bloodArray.add("AB+");
        bloodArray.add("AB-");
        bloodArray.add("O+");
        bloodArray.add("O-");
        ArrayAdapter spinnerAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,bloodArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodType.setAdapter(spinnerAdapter);
        bloodType.setPrompt("Select blood Type");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);
        address=findViewById(R.id.addressText);
        parent=findViewById(R.id.bloodActivityParent);
        bloodType=findViewById(R.id.bloodType);

        spinnerIntialization();

        save();
    }
}
