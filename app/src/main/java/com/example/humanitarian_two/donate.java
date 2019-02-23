package com.example.humanitarian_two;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class donate extends AppCompatActivity {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser user= mAuth.getCurrentUser();
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    Intent intent;
    public void onClick(View view){
        switch (view.getId()){
            case R.id.food:
                 intent=new Intent(this,FoodDonation.class);
                startActivity(intent);
                Log.i("button","pressed");
                break;
            case R.id.medicine:
                 intent=new Intent(this,MedicineDonation.class);
                startActivity(intent);
                Log.i("button","pressed");
                break;
            case R.id.cloth:
                intent=new Intent(this,ClothDonation.class);
                startActivity(intent);
                Log.i("button","pressed");
                break;

        }



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

    }
}
