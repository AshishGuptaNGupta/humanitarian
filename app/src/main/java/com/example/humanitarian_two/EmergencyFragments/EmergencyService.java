package com.example.humanitarian_two.EmergencyFragments;

class EmergencyService{

    String name;
    String contact;

    public EmergencyService(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public EmergencyService() {

    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }




}
