package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.abhishek.financetracker.expensemanager.neopark.database.AppDatabase;
import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.user.User;
import com.example.abhishek.financetracker.expensemanager.neopark.interfaces.UserDao;
import com.example.abhishek.financetracker.expensemanager.neopark.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfileActivity extends AppCompatActivity {

    private UserRepository userRepository;
    private FirebaseUser firebaseUser;
    private Button editBtn;
    private Uri imageUri;
    AppDatabase db;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    ImageView mProfileImg;
    String userId;
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    UserDao userDao;

    private TextView fullNameTextView, fullNameTextViewHeader, userEmailAddressTextView, mUserMobileNumber;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRepository = new UserRepository(AppDatabase.getInstance(getApplicationContext()));
        db = AppDatabase.getInstance(this);

        fullNameTextView = findViewById(R.id.edit_full_name);
        fullNameTextViewHeader = findViewById(R.id.profile_name);
        userEmailAddressTextView = findViewById(R.id.edit_email);
        mUserMobileNumber = findViewById(R.id.edit_mobile_number);
        editBtn = findViewById(R.id.btn_save);
        mProfileImg = findViewById(R.id.profile_image);

        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            userRepository.getUserData(userId).observe(this, user -> {
                if (user != null) {
                    fullNameTextView.setText(user.getFullName());
                    fullNameTextViewHeader.setText(user.getFullName());
                    userEmailAddressTextView.setText(user.getEmail());
                    mUserMobileNumber.setText(user.getMobileNumber());
                }
            });
        }
        checkAndRequestPermissions();

        editBtn.setOnClickListener(view -> saveData());
        mProfileImg.setOnClickListener(v -> openGallery());

        // Back button
        ImageView backButton = findViewById(R.id.back_button_profile);
        backButton.setOnClickListener(view -> onBackPressed());

        // Initialize image picker launcher here!
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), imageUri);
                            saveImageToPreferences(bitmap);  // ✅ ADD this
                            mProfileImg.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveData() {
        if (firebaseUser == null) {
            Log.e("EditProfile", "Firebase user is null. Cannot save data.");
            return;
        }

        String userId = firebaseUser.getUid();
        String fullName = fullNameTextView.getText().toString().trim();
        String email = userEmailAddressTextView.getText().toString().trim();
        String mobileNumber = mUserMobileNumber.getText().toString().trim();

        Log.d("EditProfile", "Collected data - UserID: " + userId + ", Name: " + fullName + ", Email: " + email + ", Mobile: " + mobileNumber);

        if (fullName.isEmpty() || email.isEmpty() || mobileNumber.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            Log.w("EditProfile", "Validation failed - Empty fields");
            return;
        }

        User user = new User(userId, fullName, email, mobileNumber);

        Log.d("EditProfile", "Saving user data to Firebase...");
        userRepository.saveUserToFirebase(user);


        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
        Log.d("EditProfile", "Profile update complete. Finishing activity.");
        finish();
    }

    private void saveImageToPreferences(Bitmap bitmap) {
        android.util.Base64OutputStream base64OutputStream;
        try {
            java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedImage = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);

            getSharedPreferences("profile_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("profile_image_base64", encodedImage)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndRequestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_STORAGE_PERMISSION);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }


}
