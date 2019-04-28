
package com.example.humanitarian_two;

import com.google.firebase.Timestamp;

public  class RequestModel{
    String description;
    String donationId;
    String donationType;
    String location;
    String ngo;
    String requestId;
    String status;
    Timestamp time;
    String user;
    String volunteerUid;
    String volunteerUsername;
    String requesteeUsername;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getRequesteeUsername() {
        return requesteeUsername;
    }

    public void setRequesteeUsername(String requesteeUsername) {
        this.requesteeUsername = requesteeUsername;
    }



    public RequestModel(){}

    public RequestModel(String description, String donationId, String donationType, String location, String ngo, String requestId, String status, Timestamp time, String user, String volunteerUid, String volunteerUsername, String requesteeUsername) {
        this.description = description;
        this.donationId = donationId;
        this.donationType = donationType;
        this.location = location;
        this.ngo = ngo;
        this.requestId = requestId;
        this.status = status;
        this.time = time;
        this.user = user;
        this.volunteerUid = volunteerUid;
        this.volunteerUsername = volunteerUsername;
        this.requesteeUsername = requesteeUsername;
    }



}