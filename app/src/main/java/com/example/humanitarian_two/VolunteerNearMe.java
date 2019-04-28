package com.example.humanitarian_two;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VolunteerNearMe extends AppCompatActivity  {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Location userLocation;
    protected LocationManager locationManager;
    VolunteerAdapter adapter;
    ListView listView;
    String TAG="msg";
    ArrayList<String>names=new ArrayList<>();
    ArrayList<String>uid=new ArrayList<>();
    MyLocationListener locationListener;
    Map<String, Object> foodDonation = new HashMap<>();
    String currentUserUid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_near_me);

        currentUserUid=currentUser.getUid();

        userLocation= getIntent().getParcelableExtra("location");
        foodDonation=(HashMap<String,Object>)getIntent().getSerializableExtra("donation");

        listView=findViewById(R.id.volunteerList);


        adapter=new VolunteerAdapter(getApplicationContext(),names,uid,foodDonation);
        listView.setAdapter(adapter);

        new getVolunteer().execute();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

    }


    void findAnotherPoint(){

    }
    class getVolunteer extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Double Lat,Lng;
                                    float[] result = new float[1];

                                    if(document.get("location.latitude")!=null&&document.get("location.longitude")!=null&&!document.get("uid").equals(currentUserUid)) {
                                        Lat = Double.parseDouble(document.get("location.latitude").toString());
                                        Lng = Double.parseDouble(document.get("location.longitude").toString());
                                        Location.distanceBetween(userLocation.getLatitude(),userLocation.getLongitude(),Lat,Lng,result);
                                        if(result[0]<1000){
                                            names.add(document.get("username").toString());
                                            uid.add(document.get("uid").toString());
                                            Log.i("distance",Float.toString(result[0]));
                                        }
                                    }


                                }
                                if(names.isEmpty()){
                                    ViewParent parent=listView.getParent();
                                    ((ViewGroup)parent).removeView(listView);
                                    TextView textView=new TextView(getApplicationContext());
                                    textView.setText("No volunteer near available");
                                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
                                    ((ViewGroup) parent).addView(textView);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    adapter.notifyDataSetChanged();
                }
            });

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);



        }
    }

    class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.d(TAG,"from volunteer"+loc);
            userLocation=loc;
            new getVolunteer().execute();
            locationManager.removeUpdates(locationListener);
            }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }





}
