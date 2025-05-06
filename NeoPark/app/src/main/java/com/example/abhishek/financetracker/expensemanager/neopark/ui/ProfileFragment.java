package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.repository.UserRepository;
import com.example.abhishek.financetracker.expensemanager.neopark.database.AppDatabase;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private TextView fullNameTextView, fullNameTextViewHeader, userEmailAddressTextView, mUserMobileNumber;
    private ProgressBar progressBarProfile;
    private MaterialButton editBtn;
    private UserRepository userRepository;
    private FirebaseUser firebaseUser;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fullNameTextView = view.findViewById(R.id.userName_profile);
        fullNameTextViewHeader = view.findViewById(R.id.headerProfileName);
        progressBarProfile = view.findViewById(R.id.progressbar_profile);
        mUserMobileNumber = view.findViewById(R.id.usercontact_profile);
        userEmailAddressTextView = view.findViewById(R.id.userEmailAddress_profile);
        editBtn = view.findViewById(R.id.edit_button_profile);
        profileImage = view.findViewById(R.id.profileImageView);
        loadImageFromPreferences();

        progressBarProfile.setVisibility(View.VISIBLE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRepository = new UserRepository(AppDatabase.getInstance(requireContext()));

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            userRepository.getUserData(userId).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    fullNameTextView.setText(user.getFullName());
                    fullNameTextViewHeader.setText(user.getFullName());
                    userEmailAddressTextView.setText(user.getEmail());
                    mUserMobileNumber.setText(user.getMobileNumber());
                    progressBarProfile.setVisibility(View.GONE);
                }
            });
        }

        editBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), EditProfileActivity.class)));

        return view;
    }

    private void loadImageFromPreferences() {
        String encodedImage = requireContext()
                .getSharedPreferences("profile_prefs", getContext().MODE_PRIVATE)
                .getString("profile_image_base64", null);

        if (encodedImage != null) {
            byte[] decodedBytes = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            profileImage.setImageBitmap(bitmap);
        }
    }

}
