package com.example.abhishek.onlineparking.adminneopark.models;

public class UserModel {
    private String uid;
    private String email;
    private String role;

    // Required empty constructor for Firebase
    public UserModel() {}

    public UserModel(String uid, String email, String role) {
        this.uid = uid;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
