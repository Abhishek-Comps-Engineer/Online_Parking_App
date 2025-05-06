package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    // UI components for user registration inputs
    private EditText fullNameTextView, mUserEmailEditText, userPhone, mUserPasswordEditText, userConfirmPassword;
    private TextView mLoginTextView;
    private ProgressBar registerProgressBar;
    private Button submitDetails;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    // Matcher used for mobile number validation
    private Matcher mobileMatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable full-screen edge-to-edge layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Adjust view padding to account for system status and navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase for the application context
        FirebaseApp.initializeApp(getApplicationContext());

        // Initialize all UI components and Firebase Auth instance
        initVariable();

        // If a user is already logged in, redirect to HomeActivity
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        // Set up click listener for the login text view to switch to LoginActivity
        mLoginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up click listener for the submit button to validate and register user details
        submitDetails.setOnClickListener(view -> {
            // Retrieve data entered by the user
            String fullname = fullNameTextView.getText().toString();
            String emailAddress = mUserEmailEditText.getText().toString();
            String mobileNumber = userPhone.getText().toString();
            String password = mUserPasswordEditText.getText().toString();
            String confirmPassword = userConfirmPassword.getText().toString();

            // Define a regular expression to validate Indian mobile numbers (10 digits starting with 6-9)
            String mobileRegex = "[6-9][0-9]{9}";
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            mobileMatcher = mobilePattern.matcher(mobileNumber);

            // Validate inputs; if valid, show the progress bar and begin registration
            if (validateInputs(fullname, emailAddress, mobileNumber, password, confirmPassword)) {
                registerProgressBar.setVisibility(View.VISIBLE);
                registrationDetails(fullname, emailAddress, mobileNumber, password);
            }
        });
    }

    /**
     * Registers the user using Firebase Authentication and saves user details in Firebase Realtime Database.
     *
     * @param fullname      The full name of the user.
     * @param emailAddress  The user's email address.
     * @param mobileNumber  The user's mobile number.
     * @param password      The user's password.
     */
    private void registrationDetails(String fullname, String emailAddress, String mobileNumber, String password) {
        // Create a new user with the provided email and password
        mAuth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If registration is successful, save additional user data
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Get the unique user ID provided by Firebase
                                String userId = user.getUid();

                                // Create a new User object with the registration details
                                User userData = new User(fullname, emailAddress, mobileNumber);

                                // Get a reference to the "users" node in Firebase Realtime Database
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

                                // Save the User object under a child node named with the userId
                                databaseReference.child(userId).setValue(userData)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                // Registration and data saving succeeded; notify user and go to HomeActivity
                                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Data saving failed; show an error message
                                                Toast.makeText(RegisterActivity.this, "Failed to save user data: "
                                                        + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Registration failed; show an error message with details
                            Toast.makeText(RegisterActivity.this, "Error: "
                                    + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        // Hide the progress bar once the process is complete
                        registerProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Validates the user input fields for registration.
     *
     * @param fullname        The full name input.
     * @param emailAddress    The email input.
     * @param mobileNumber    The mobile number input.
     * @param password        The password input.
     * @param confirmPassword The confirmation password input.
     * @return true if all inputs are valid; false otherwise.
     */
    private boolean validateInputs(String fullname, String emailAddress, String mobileNumber, String password, String confirmPassword) {
        if (TextUtils.isEmpty(fullname)) {
            showError(fullNameTextView, "Enter your name");
            return false;
        } else if (TextUtils.isEmpty(emailAddress)) {
            showError(mUserEmailEditText, "Enter your email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            showError(mUserEmailEditText, "Enter a valid email address");
            return false;
        } else if (TextUtils.isEmpty(mobileNumber)) {
            showError(userPhone, "Enter your mobile number");
            return false;
        } else if (mobileNumber.length() != 10) {
            showError(userPhone, "Enter a valid 10-digit mobile number");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            showError(mUserPasswordEditText, "Enter your password");
            return false;
        } else if (password.length() < 6) {
            showError(mUserPasswordEditText, "Password must be at least 6 characters");
            return false;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            showError(userConfirmPassword, "Re-enter your password");
            return false;
        } else if (!password.equals(confirmPassword)) {
            showError(userConfirmPassword, "Passwords do not match");
            return false;
        } else if (!mobileMatcher.find()) {
            showError(userPhone, "Enter a valid 10-digit mobile number");
            return false;
        }
        return true;
    }

    /**
     * Displays an error message on the EditText field and as a Toast.
     *
     * @param editText The EditText where the error occurred.
     * @param message  The error message to display.
     */
    private void showError(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Initializes all UI components and Firebase Authentication instance.
     */
    private void initVariable() {
        // Link UI components from the layout file
        submitDetails = findViewById(R.id.submitDetails);
        fullNameTextView = findViewById(R.id.userName);
        mUserEmailEditText = findViewById(R.id.userEmailAddress);
        userPhone = findViewById(R.id.usercontact);
        mUserPasswordEditText = findViewById(R.id.password);
        registerProgressBar = findViewById(R.id.registerProgressBar);
        userConfirmPassword = findViewById(R.id.confirmpassword);
        mLoginTextView = findViewById(R.id.login_button);

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();
    }
}
