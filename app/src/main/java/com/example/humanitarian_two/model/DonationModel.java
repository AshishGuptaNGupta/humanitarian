

package com.example.humanitarian_two.model;


import android.support.annotation.NonNull;

import com.example.humanitarian_two.Post;
import com.google.firebase.Timestamp;

 public class DonationModel implements Comparable<DonationModel>{
    String description;
    String location;
    String ngo;
    Timestamp time;
    String user;
    String donationId;
    String donationType;






    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

     public DonationModel(String description, String location, String ngo, Timestamp tb, String user) {
         this.description = description;
         this.location = location;
         this.ngo = ngo;
         this.time = tb;
         this.user = user;
     }

    public DonationModel(String description, String location, String ngo, Timestamp tb, String user,String donationId,String donationType) {
        this.description = description;
        this.location = location;
        this.ngo = ngo;
        this.time = tb;
        this.user = user;
        this.donationId=donationId;
        this.donationType=donationType;
    }

     @Override
     public int compareTo(@NonNull DonationModel donation) {
         return donation.time.compareTo(this.time);
     }

}