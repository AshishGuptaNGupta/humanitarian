package com.example.humanitarian_two;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RequestViewHolder extends RecyclerView .ViewHolder{
    TextView type;
    TextView location;
    TextView description;
    TextView ngo;
    TextView user;
    Button Accept;
    Button Reject;


    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);
        type=itemView.findViewById(R.id.type);
        location=itemView.findViewById(R.id.location);
        description=itemView.findViewById(R.id.description);
        ngo=itemView.findViewById(R.id.ngo);
        user=itemView.findViewById(R.id.user);
        Accept=itemView.findViewById(R.id.accept);
        Reject=itemView.findViewById(R.id.reject);
    }
}
