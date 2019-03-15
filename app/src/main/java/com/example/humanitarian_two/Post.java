package com.example.humanitarian_two;

import com.google.firebase.Timestamp;

import java.lang.reflect.Array;

public class Post {
    String post;
    Timestamp createdAt;
    String uid;
    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public Post(String post, Timestamp createdAt, String uid) {
        this.post = post;
        this.createdAt = createdAt;
        this.uid = uid;
    }

    public Post(){}







}
