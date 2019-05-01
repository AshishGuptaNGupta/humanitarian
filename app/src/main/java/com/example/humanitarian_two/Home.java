package com.example.humanitarian_two;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.humanitarian_two.Ngo.NgoProfile;
import com.example.humanitarian_two.User.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class Home extends AppCompatActivity {
    Intent intent;
    String subject;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user=mAuth.getCurrentUser();
    String TAG="msg";
    public static SharedPreferences sharedpreferences ;
    public static final String MyPREFERENCES = "Bequest";
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
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.i ("initFCM: token: " ,token);
                        sendRegistrationToServer(token);

                    }
                });

    }
    private void sendRegistrationToServer(String token) {
        if(subject.equals("users"))
        db.collection("users").document(user.getUid()).update("token",token);
        else
            db.collection("ngos").document(user.getUid()).update("token",token);


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
                                setTitle("Profile");
                                if(subject.equals("users"))
                                selectedFragment = new UserProfile();
                                else
                                    selectedFragment = new NgoProfile();
                                loadFragment(selectedFragment);
                                return true;
                            case R.id.findPeople:
                                setTitle("Find Philantropist");
                                selectedFragment = new FindPeople();
                                loadFragment(selectedFragment);
                                return true;
                            case R.id.donate:
                                setTitle("Donate");
                                if(subject.equals("users"))
                                selectedFragment = new donate();
                                else
                                    selectedFragment = new Donations();
                                loadFragment(selectedFragment);
                                return true;
                            case R.id.home:
                                setTitle("Home");
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
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        Intent locationService=new Intent(getApplicationContext(),LocationService.class);
        startService(locationService);
        FloatingActionButton fab = findViewById(R.id.fab);


        if(subject.equals("users")){
            editor.putString("subject","users");
            editor.commit();

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), VolunteerRequest.class);
                    startActivity(intent);
                }
            });
        }else
        {
            ((ViewGroup)fab.getParent()).removeView(fab);
            editor.putString("subject","ngos");
            editor.commit();
        }

        //load feed
        loadFragment(new Feed());

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
