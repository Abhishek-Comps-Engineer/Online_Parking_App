package com.example.abhishek.financetracker.expensemanager.neopark.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.abhishek.financetracker.expensemanager.neopark.database.AppDatabase;
import com.example.abhishek.financetracker.expensemanager.neopark.interfaces.UserDao;
import com.example.abhishek.financetracker.expensemanager.neopark.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final UserDao userDao;
    private final DatabaseReference firebaseRef;
    private StorageReference storageRef;
    private final FirebaseUser firebaseUser;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public UserRepository(AppDatabase db) {
        this.userDao = db.userDao();
        this.firebaseRef = FirebaseDatabase.getInstance().getReference("users");
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public LiveData<User> getUserData(String userId) {
        Log.d(TAG, "Fetching user from Room DB with ID: " + userId);

        LiveData<User> userFromRoom = userDao.getUserById(userId);

        userFromRoom.observeForever(user -> {
            if (user != null) {
                Log.d(TAG, "User found in Room DB: " + user.getFullName());
                userLiveData.postValue(user);
            } else {
                Log.d(TAG, "User not found in Room DB. Fetching from Firebase...");
                fetchUserFromFirebase(userId);
            }
        });

        return userLiveData;
    }

    public void fetchUserFromFirebase(String userId) {
        firebaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String mobileNumber = snapshot.child("mobileNumber").getValue(String.class);

                    User user = new User(userId, fullName, email, mobileNumber);

                    executorService.execute(() -> {
                        Log.d(TAG, "Inserting user into Room DB: " + user.toString());  // Log user details
                        userDao.insert(user);
                        userDao.update(user);
                        Log.d(TAG, "User inserted into Room DB successfully: " + user.getFullName());
                    });

                    userLiveData.postValue(user);
                } else {
                    Log.w(TAG, "User not found in Firebase!");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Firebase Data Fetch Cancelled: " + error.getMessage());
            }
        });
    }

    public void saveUserToFirebase(User user) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Log.e(TAG, "User is not authenticated");
            // Handle this scenario
        } else {
            Log.d(TAG, "Authenticated user: " + firebaseUser.getUid());
        }

        firebaseRef.child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ User data saved to Firebase successfully: " + user.getUserId());

                    // Also update Room DB
                    executorService.execute(() -> {
                        try {
                            userDao.insert(user);  // insert = upsert
                            Log.d(TAG, "✅ User data saved to Room DB for userId: " + user.getUserId());
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Failed to save user data to Room DB", e);
                        }
                    });
                })
                .addOnFailureListener(e -> Log.e(TAG, "❌ Failed to save user data to Firebase", e));
    }



//    private void uploadImage(Uri imageUri) {
//        if (imageUri != null) {
//            // Store image with a unique name
//            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
//
//            fileRef.putFile(imageUri)
//                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                        String imageUrl = uri.toString();  // Get image URL
//
//                        // Save URL in Firebase Realtime Database
//                        String uploadId = firebaseRef.push().getKey();
//                        firebaseRef.child(uploadId).setValue(imageUrl);
//
//                    }));
//        }
//    }
}


