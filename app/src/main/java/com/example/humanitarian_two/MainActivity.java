package com.example.humanitarian_two;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    Intent intent;
     Button userLogin;
     Button ngoLogin;
    public void userLogin(View view){
         intent=new Intent(this, UserLogin.class);
        startActivity(intent);
    }
    public void ngoLogin(View view){
        intent=new Intent(this,  NgoLogin.class);
        startActivity(intent);
    }

    public void onEmergency(View view){
        Intent intent=new Intent(this,Emergency.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
