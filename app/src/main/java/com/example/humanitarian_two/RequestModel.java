
package com.example.humanitarian_two;

import com.google.firebase.Timestamp;

import java.util.Map;

public  class RequestModel{
    public RequestModel() {
    }

    String description;
    Map<String,Object> location;
    String ngo;
    Timestamp time;
    Map<String,Object> user;
    String donationId;
    String donationType;
    String status;
    String volunteerUid;
    String volunteerUsername;

    public RequestModel(String description, Map<String, Object> location, String ngo, Timestamp time, Map<String, Object> user, String donationId, String donationType, String status, String volunteerUid, String volunteerUsername) {
        this.description = description;
        this.location = location;
        this.ngo = ngo;
        this.time = time;
        this.user = user;
        this.donationId = donationId;
        this.donationType = donationType;
        this.status = status;

        this.volunteerUid = volunteerUid;
        this.volunteerUsername = volunteerUsername;
    }






    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Object> location) {
        this.location = location;
    }

    public String getNgo() {
        return ngo;
    }

    public void setNgo(String ngo) {
        this.ngo = ngo;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Map<String, Object> getUser() {
        return user;
    }

    public void setUser(Map<String, Object> user) {
        this.user = user;
    }

    public String getDonationId() {
        return donationId;
    }

    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    public String getDonationType() {
        return donationType;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVolunteerUid() {
        return volunteerUid;
    }

    public void setVolunteerUid(String volunteerUid) {
        this.volunteerUid = volunteerUid;
    }

    public String getVolunteerUsername() {
        return volunteerUsername;
    }

    public void setVolunteerUsername(String volunteerUsername) {
        this.volunteerUsername = volunteerUsername;
    }







}