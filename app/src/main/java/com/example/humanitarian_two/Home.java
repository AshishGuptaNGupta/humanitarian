package com.example.humanitarian_two;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.humanitarian_two.EmergencyFragments.PoliceFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

public class Home extends AppCompatActivity {
    Intent intent;
    String subject;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    public void donate(View view){
        Intent intent = new Intent(this,donate.class);
        startActivity(intent);

    }
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
    private void initFCM(){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i ("initFCM: token: " ,token);
        sendRegistrationToServer(token);

    }
    private void sendRegistrationToServer(String token) {
        db.collection("users").document(user.getUid()).update("token",token);


    }


    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    try {
                        Fragment selectedFragment = null;
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        switch (menuItem.getItemId()) {
                            case R.id.profile:
                                if(subject.equals("users"))
                                selectedFragment = new UserProfile();
                                else
                                    selectedFragment = new Profile();
                                loadFragment(selectedFragment);
                                return true;
                            case R.id.findPeople:
                                selectedFragment = new Follow();
                                loadFragment(selectedFragment);
                                return true;
                            case R.id.donate:
                                if(subject.equals("users"))
                                selectedFragment = new donate();
                                else
                                    selectedFragment = new Donations();
                                loadFragment(selectedFragment);
                                return true;
                            case R.id.home:
                                selectedFragment = new Feed();
                                loadFragment(selectedFragment);
                                return true;
                        }
                    }
                    catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    return false;
                }
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent=getIntent();
        subject=intent.getStringExtra("subject");
        Log.i("subject",subject);

        initFCM();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

    }
    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
