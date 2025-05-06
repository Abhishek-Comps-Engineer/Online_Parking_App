package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.classes.Feedback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedBackActivity extends AppCompatActivity {

    private EditText feedbackTitle, feedbackMessage;
    private CheckBox followUpCheckBox, supportRequestCheckBox, noPersonalInfoCheckBox;
    private Button submitButton;
    private TextView emailText;
    private RatingBar ratingBar;
    private ImageView backButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference; // Firebase database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feed_back);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> onBackPressed());

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");

        // Initialize UI elements
        feedbackTitle = findViewById(R.id.feedback_title);
        feedbackMessage = findViewById(R.id.feedback_message);
        followUpCheckBox = findViewById(R.id.follow_up_checkbox);
        supportRequestCheckBox = findViewById(R.id.support_request_checkbox);
        noPersonalInfoCheckBox = findViewById(R.id.no_personal_info_checkbox);
        submitButton = findViewById(R.id.submit_button);
        emailText = findViewById(R.id.email);
        ratingBar = findViewById(R.id.rating_bar); // Add a RatingBar in XML
        backButton = findViewById(R.id.back_button);

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Submit button functionality
        submitButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
                String email = emailText.getText().toString().trim();
                String title = feedbackTitle.getText().toString().trim();
                String message = feedbackMessage.getText().toString().trim();
                float rating = ratingBar.getRating(); // Get rating value
                boolean followUp = followUpCheckBox.isChecked();
                boolean supportRequest = supportRequestCheckBox.isChecked();
                boolean noPersonalInfo = noPersonalInfoCheckBox.isChecked();

                // Validate required fields
                if (TextUtils.isEmpty(title) || title.length() < 3) {
                    feedbackTitle.setError("Title must be at least 3 characters.");
                    return;
                }
                if (TextUtils.isEmpty(message) || message.length() < 15) {
                    feedbackMessage.setError("Feedback must be at least 15 characters.");
                    return;
                }
                if (!supportRequest) {
                    Toast.makeText(this, "You must agree that this is not a support request.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!noPersonalInfo) {
                    Toast.makeText(this, "You must confirm no personal information is included.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Generate a unique ID for each feedback
                String feedbackId = databaseReference.push().getKey();

                // Create a Feedback object
                Feedback feedback = new Feedback(feedbackId, email, title, message, rating, followUp);

                // Store in Firebase
                assert feedbackId != null;
                databaseReference.child(feedbackId).setValue(feedback).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(FeedBackActivity.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity
                    } else {
                        Log.e("FirebaseError","Failed"+task.getException());
                        Toast.makeText(FeedBackActivity.this, "Failed to submit feedback. Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}