package com.example.humanitarian_two;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.humanitarian_two.EmergencyFragments.FireBrigade;
import com.example.humanitarian_two.EmergencyFragments.HospitalFragment;
import com.example.humanitarian_two.EmergencyFragments.PoliceFragment;

public class Emergency extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    switch (menuItem.getItemId()){
                        case R.id.police:
                            selectedFragment=new PoliceFragment();
                            loadFragment(selectedFragment);
                            return true;
                        case R.id.hospital:
                            selectedFragment=new HospitalFragment();
                            loadFragment(selectedFragment);
                            return true;
                        case R.id.firebrigade:
                            selectedFragment=new FireBrigade();
                            loadFragment(selectedFragment);
                            return true;

                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.emergency_menu);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        loadFragment(new PoliceFragment());

    }
    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
