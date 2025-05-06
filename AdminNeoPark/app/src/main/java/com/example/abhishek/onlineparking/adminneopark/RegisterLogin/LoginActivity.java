package com.example.abhishek.onlineparking.adminneopark.RegisterLogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.abhishek.onlineparking.adminneopark.R;
import com.example.abhishek.onlineparking.adminneopark.models.UserModel;
import com.example.abhishek.onlineparking.adminneopark.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import android.util.Patterns;

public class LoginActivity extends AppCompatActivity {

    private EditText userIdEditText, userPasswordEditText;
    private Button loginButton;
    private ProgressBar progressBar;

    private static final String ADMIN_EMAIL = "admin@admin.com";
    private static final String ADMIN_PASSWORD = "admin12";

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Auto login if already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMain();
        }

        userIdEditText = findViewById(R.id.userId);
        userPasswordEditText = findViewById(R.id.userPassword);
        loginButton = findViewById(R.id.loginbutton);
//        progressBar = findViewById(R.id.progressBar);  // Assuming you have a ProgressBar in your layout
//        createAdminAccountIfNotExists();
        loginButton.setOnClickListener(v -> validateLogin());

    }

    private void validateLogin() {
        String email = userIdEditText.getText().toString().trim();
        String password = userPasswordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format");
            return;
        }

//        showProgress(true);  // Show progress while logging in
        signInUser(email, password);
    }


    // Call this only if you know the admin user isn't already created
    private void createAdminAccountIfNotExists() {
        mAuth.fetchSignInMethodsForEmail(ADMIN_EMAIL)
                .addOnCompleteListener(task -> {
                    boolean userExists = !task.getResult().getSignInMethods().isEmpty();
                    if (!userExists) {
                        createAccountAndSaveToDB(ADMIN_EMAIL, ADMIN_PASSWORD);
                    }
                });
    }


    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
//                    showProgress(false);  // Hide progress after the task is done
                    if (task.isSuccessful()) {
                        showToast("Login Successful!");
                        navigateToMain();
                    } else {
                        handleLoginFailure(email, password, task.getException());
                    }
                });
    }

    private void handleLoginFailure(String email, String password, Exception exception) {
        // If login fails and it's not the admin account, create the account
        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            showToast("Admin login failed: " + exception.getMessage());
        }
    }

    private void createAccountAndSaveToDB(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            String uid = user.getUid();
                            UserModel userModel = new UserModel(uid, email, "user");  // Dynamic role assignment

                            database.getReference("AdminUsers")
                                    .child(uid)
                                    .setValue(userModel)
                                    .addOnSuccessListener(unused -> {
                                        showToast("Account created successfully!");
                                    })
                                    .addOnFailureListener(e -> {
                                        showToast("Database Error: " + e.getMessage());
                                    });
                        }
                    } else {
                        showToast("Auth Error: " + task.getException().getMessage());
                    }
                });
    }

    private void navigateToMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

//    private void showProgress(boolean show) {
//        progressBar.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.GONE);
//    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
