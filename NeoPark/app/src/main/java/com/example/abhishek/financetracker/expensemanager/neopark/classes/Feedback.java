package com.example.abhishek.financetracker.expensemanager.neopark.classes;

public class Feedback {

    String feedbackId, email, title, message;
    float rating;
    boolean followUp;
    public Feedback(String feedbackId, String email, String title, String message, float rating,boolean followUp){
        this.feedbackId = feedbackId;
        this.email = email;
        this.title = title;
        this.message = message;
        this.rating = rating;
        this.followUp = followUp;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isFollowUp() {
        return followUp;
    }

    public void setFollowUp(boolean followUp) {
        this.followUp = followUp;
    }
}
