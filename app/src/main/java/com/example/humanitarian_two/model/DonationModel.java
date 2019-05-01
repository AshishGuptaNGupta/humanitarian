

package com.example.humanitarian_two.model;


import android.support.annotation.NonNull;

import com.example.humanitarian_two.Post;
import com.google.firebase.Timestamp;

import java.util.Map;

public class DonationModel implements Comparable<DonationModel>{
    String description;
    Map <String,Object> location;
    String ngo;
    Timestamp time;
    Map<String,Object> user;
    String donationId;
    String donationType;
    String status;

    public DonationModel(String description, Map<String, Object> location, String ngo, Timestamp time, Map<String, Object> user, String donationId, String donationType, String status, String deliveryType) {
        this.description = description;
        this.location = location;
        this.ngo = ngo;
        this.time = time;
        this.user = user;
        this.donationId = donationId;
        this.donationType = donationType;
        this.status = status;
    }





    public Map<String, Object> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Object> location) {
        this.location = location;
    }

    public Map<String, Object> getUser() {
        return user;
    }

    public void setUser(Map<String, Object> user) {
        this.user = user;
    }


     public String getStatus() {
         return status;
     }

     public void setStatus(String status) {
         this.status = status;
     }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNgo() {
        return ngo;
    }

    public void setNgo(String ngo) {
        this.ngo = ngo;
    }

    public Timestamp getTb() {
        return time;
    }

    public void setTb(Timestamp tb) {
        this.time = tb;
    }


     public String getId() {
         return donationId;
     }

     public void setId(String donationId) {
         this.donationId = donationId;
     }

     public String getDonationType() {
         return donationType;
     }

     public void setDonationType(String donationType) {
         this.donationType = donationType;
     }




     public DonationModel(){}





    @Override
     public int compareTo(@NonNull DonationModel donation) {
         return donation.time.compareTo(this.time);
     }

}