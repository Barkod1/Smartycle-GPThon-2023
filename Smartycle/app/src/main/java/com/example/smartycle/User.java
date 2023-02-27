package com.example.smartycle;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String key;
    public String uid;//user uid
    public String email;
    public int points;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public User(String uid, String email,String key) {
        this.uid = uid;
        this.email = email;
        this.key = key;
        this.points = 0;
    }



}