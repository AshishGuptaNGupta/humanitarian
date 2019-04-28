package com.example.humanitarian_two;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

class VolunteerRequestAdapter extends RecyclerView.Adapter<RequestViewHolder>
{


    ArrayList<RequestModel> requests=new ArrayList<>();
    Context context;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    public VolunteerRequestAdapter(ArrayList<RequestModel> requests, Context context)
    {
        this.requests = requests;
        this.context = context;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType)
    {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout

        View photoView = inflater.inflate(R.layout.volunteer_request_row,
                parent, false);

        RequestViewHolder viewHolder = new RequestViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RequestViewHolder viewHolder,
                                 final int position)
    {

        viewHolder.type.setText(requests.get(position).donationType);
        viewHolder.location.setText(requests.get(position).location);
        viewHolder.description.setText(requests.get(position).description);
        viewHolder.ngo.setText(requests.get(position).ngo);
        viewHolder.user.setText(requests.get(position).user);
        viewHolder.Accept.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            db.collection("volunteerRequest").whereEqualTo("donationId",requests.get(position).donationId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                String docId=null;
                                for(QueryDocumentSnapshot document:task.getResult()) {
                                    docId=document.getId();
                                }
                                db.collection("volunteerRequest").document(docId).update("status","accept")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                sendNotification(position);
                                                viewHolder.Accept.setBackgroundColor(Color.parseColor("#BEBEBE"));
                                                viewHolder.Accept.setEnabled(false);
                                                viewHolder.Reject.setEnabled(false);
                                            }
                                        });

                            }
                            else
                            {
                                Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    });
        viewHolder.Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("volunteerRequest").whereEqualTo("donationId",requests.get(position).donationId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    String docId=null;
                                    for(QueryDocumentSnapshot document:task.getResult()) {
                                        docId=document.getId();
                                    }
                                    db.collection("volunteerRequest").document(docId).update("status","reject")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    viewHolder.Reject.setBackgroundColor(Color.parseColor("#BEBEBE"));
                                                    viewHolder.Accept.setEnabled(false);
                                                    viewHolder.Reject.setEnabled(false);
                                                }
                                            });


                                }
                                else
                                {
                                    Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return requests.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
    // Insert a new item to the RecyclerView on a predefined position

    public void sendNotification(int position){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="https://us-central1-humanitarian-dbe38.cloudfunctions.net/requestAccepted?"+
                "user="+requests.get(position).user+"&"+"donationId="+requests.get(position).donationId+
                "volunteerName="+requests.get(position).volunteerUsername;
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
}


