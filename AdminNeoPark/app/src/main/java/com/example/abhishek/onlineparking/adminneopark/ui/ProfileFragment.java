package com.example.abhishek.onlineparking.adminneopark.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.abhishek.onlineparking.adminneopark.R;
import com.example.abhishek.onlineparking.adminneopark.RegisterLogin.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {


    private TextView userName, userMobileNumber, userEmail, userHeaderName;
    MaterialButton logoutButton ;

    private CircleImageView profileImageView;

    public ProfileFragment() {
        // Required empty public constructor
    }


    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);

                            // Save to internal storage
                            saveImageToInternalStorage(bitmap, "profile_image.png");

                            // Load and display
                            profileImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userName = view.findViewById(R.id.userName_profile);
        userMobileNumber = view.findViewById(R.id.usercontact_profile);
        userEmail = view.findViewById(R.id.userEmailAddress_profile);
        userHeaderName = view.findViewById(R.id.headerProfileName);
        profileImageView = view.findViewById(R.id.profileImageView);
        logoutButton = view.findViewById(R.id.userAdminLogout);

        // 🔁 Load stored text
        userName.setText(loadFromPrefs(R.id.userName_profile, "User Name"));
        userMobileNumber.setText(loadFromPrefs(R.id.usercontact_profile, "+91 98xxxxxx17"));
        userEmail.setText(loadFromPrefs(R.id.userEmailAddress_profile, "johndoe@example.com"));

        String name = userName.getText().toString();
        userHeaderName.setText(name);

        profileImageView.setOnClickListener(v -> {
            checkPermissionAndPickImage(); // ✅ Launch permission check and then gallery
        });

        Bitmap bitmap = loadImageFromInternalStorage("profile_image.png");
        if (bitmap != null) {
            profileImageView.setImageBitmap(bitmap);
        }



        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            new AlertDialog.Builder(getContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Clear SharedPreferences
                        requireContext().getSharedPreferences("UserProfile", getContext().MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();

                        // Optional: Clear saved image
                        File imageFile = new File(requireContext().getFilesDir(), "profile_image.png");
                        if (imageFile.exists()) imageFile.delete();

                        // Redirect to LoginActivity
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Long click listeners
        userName.setOnLongClickListener(v -> {
            updateText("Edit Name", userName);
            return true;
        });

        userMobileNumber.setOnLongClickListener(v -> {
            updateText("Edit Mobile Number", userMobileNumber);
            return true;
        });

        userEmail.setOnLongClickListener(v -> {
            updateText("Edit Email", userEmail);
            return true;
        });

        return view;
    }


    private void saveToPrefs(int id, String value) {
        requireContext().getSharedPreferences("UserProfile", getContext().MODE_PRIVATE)
                .edit()
                .putString(String.valueOf(id), value)
                .apply();
    }

    private String loadFromPrefs(int id, String defaultValue) {
        return requireContext()
                .getSharedPreferences("UserProfile", getContext().MODE_PRIVATE)
                .getString(String.valueOf(id), defaultValue);
    }

    public void saveImageToInternalStorage(Bitmap bitmap, String filename) {
        try {
            File file = new File(requireContext().getFilesDir(), filename);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadImageFromInternalStorage(String filename) {
        try {
            File file = new File(requireContext().getFilesDir(), filename);
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static final int STORAGE_PERMISSION_CODE = 101;

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) { // TIRAMISU = API 33
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery(); // No permission needed on Android 13+
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(getContext(), "Permission denied to access gallery", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateText(String editName, TextView textView) {
        EditText editText = new EditText(requireContext());
        editText.setText(textView.getText().toString()); // Set current text

        LinearLayout container = new LinearLayout(getContext());
        container.setPadding(40, 30, 40, 30); // Add padding in pixels
        container.setBackgroundResource(R.drawable.squareborder);
        container.addView(editText);

        new AlertDialog.Builder(getContext())
                .setTitle(editName)
                .setView(container)
                .setPositiveButton("OK", (d, which) -> {
                    String userInput = editText.getText().toString().trim();
                    textView.setText(userInput);

                    // 🔐 Save to SharedPreferences
                    saveToPrefs(textView.getId(), userInput);

                })
                .setNegativeButton("Cancel", null)
                .show();
        String name = userName.getText().toString();
        userHeaderName.setText(name);
    }
}