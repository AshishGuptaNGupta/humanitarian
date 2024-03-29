package com.example.humanitarian_two;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MedicineDonation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser= mAuth.getCurrentUser();
    Criteria criteria = new Criteria();
    Location userLocation;
    TextView locationText;
    Map<String, Object> medicineDonation = new HashMap<>();
    TextView description;
    Spinner spinner;
    ArrayList<String> ngoNames=new ArrayList<String>();
    ArrayAdapter adapter;
    Timestamp tb=new Timestamp(new Date());
    Map<String,Object> locationHashMap=new HashMap<>();
    Map<String,Object> userMap=new HashMap<>();
    String currentUserUsername;



    public void onSubmit(View view){
        DocumentReference ref = db.collection("medicineDonations").document();
        String myId = ref.getId();
        final String descriptionText=description.getText().toString();
        final String userDisplayName=currentUser.getDisplayName();
        final String ngo=spinner.getSelectedItem().toString();
        locationHashMap.put("address",locationText.getText().toString());
        locationHashMap.put("lat",userLocation.getLatitude());
        locationHashMap.put("long",userLocation.getLongitude());

        userMap.put("uid",currentUser.getUid());
        userMap.put("username",currentUserUsername);

        medicineDonation.put("description",description.getText().toString());
        medicineDonation.put("location",locationHashMap);
        medicineDonation.put("ngo",spinner.getSelectedItem().toString());
        medicineDonation.put("user",userMap);
        medicineDonation.put("time",tb.now().toDate());
        medicineDonation.put("donationId",myId);
        medicineDonation.put("donationType","medicine donation");
        medicineDonation.put("status","inProgress");


        Map<String, Object> post = new HashMap<>();
        post.put("post", currentUser.getDisplayName()+" donated Medicine "+" to needy people ");
        post.put("createdAt", tb.now());
        post.put("uid",currentUser.getUid());



        WriteBatch batch = db.batch();
        db.collection("medicineDonations").document()
                .set(medicineDonation);
        db.collection("posts").document(tb.now().toDate().toString())
                .set(post);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendNotification(descriptionText,userDisplayName,ngo,"medicine donation");
                locationManager.removeUpdates(locationListener);
                createDialog();


            }
        });

    }

    public  void createDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Select mode of delivery");
        alertDialogBuilder.setPositiveButton("Self ",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("Find volunteer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getApplicationContext(),VolunteerNearMe.class);
                intent.putExtra("donation", (Serializable) medicineDonation);
                intent.putExtra("location",userLocation);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void sendNotification(String descrition,String user,String ngo,String type){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://us-central1-humanitarian-dbe38.cloudfunctions.net/donationNotification?"+
                "description="+descrition+"&"+"user="+user+"&"+"ngo="+ngo+"&"+"type="+type;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("HttpRequest","sent");

                        // Display the first 500 characters of the response string.
//                            textView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("HttpRequestError","Not able to send");
//                    textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(1000,1,criteria,locationListener,null);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_donation);

        db.collection("users").document(currentUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                             currentUserUsername=document.get("username").toString();
                            }
                        }
                    }
                });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationText=findViewById(R.id.location);
        description=findViewById(R.id.description);
        spinner=findViewById(R.id.spinner);
        adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,ngoNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Select Ngo");
        db.collection("ngos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    ngoNames.add(document.get("username").toString());
                                }
                                catch (Exception e){
                                    System.out.println(e.getMessage());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }
                });



}


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(18);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                LatLng temp=new LatLng(location.getLatitude(),location.getLongitude());
                userLocation=location;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
                locationText.setText(Double.toString(location.getLatitude())+Double.toString(location.getLongitude()));
                mMap.addMarker(new MarkerOptions().position(temp));
                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddress= (List<Address>) geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(listAddress!=null&&listAddress.size()>0){
                        Log.i("address",listAddress.get(0).toString());
                        locationText.setText(listAddress.get(0).getAddressLine(0));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        };
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(1000,1,criteria,locationListener,null);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            } else {
                locationManager.requestLocationUpdates(1000,1,criteria,locationListener,null);
            }
        }

    }
}
