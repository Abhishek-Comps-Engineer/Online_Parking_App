package com.example.abhishek.financetracker.expensemanager.neopark.user;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String userId;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String imageDataUrl; // Store URL instead of byte array


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

        public User(String fullName, String email, String mobileNumber) {
            this.fullName = fullName;
            this.email = email;
            this.mobileNumber = mobileNumber;
        }

    public User(@NonNull String userId,String fullName, String email, String mobileNumber) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }

    public String getImageDataUrl() {
        return imageDataUrl;
    }

    public void setImageDataUrl(String imageDataUrl) {
        this.imageDataUrl = imageDataUrl;
    }

    public User(@NonNull String userId, String imageDataUrl) {
        this.userId = userId;
        this.imageDataUrl = imageDataUrl;
    }


// Getters and Setters

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

}
