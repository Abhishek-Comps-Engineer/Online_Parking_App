package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.database.AppDatabase;
import com.example.abhishek.financetracker.expensemanager.neopark.repository.UserRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    Toolbar mHomeToolbar;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView profileImag;
    TextView headerName, headerEmail;
    private UserRepository userRepository;
    private FirebaseUser firebaseUser;

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up edge-to-edge UI and toolbar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mHomeToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(mHomeToolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.opendrawer, R.string.closedrawer);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Load the default fragment (FindLotFragment) when the app starts
        if (savedInstanceState == null) {
            loadFragments(new FindLotFragment(), true, "FindLotFragment");
        }

        // Set up navigation drawer
        navigationView = findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);

        profileImag = headerView.findViewById(R.id.headerProfile);
        headerName = headerView.findViewById(R.id.userNameDrawer);
        headerEmail = headerView.findViewById(R.id.emailIdDrawer);

        loadImageFromPreferences();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRepository = new UserRepository(AppDatabase.getInstance(getApplicationContext()));

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            userRepository.getUserData(userId).observe(this, user -> {
                if (user != null) {
                    headerName.setText(user.getFullName());
                    headerEmail.setText(user.getEmail());
                }
            });
        }



        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            mDrawerLayout.close();

            if (itemId == R.id.profile) {
                setSupportActionBar(mHomeToolbar);
                getSupportActionBar().setTitle(R.string.profile_header);
                loadFragments(new ProfileFragment(), true, "ProfileFragment");
            } else if (itemId == R.id.find_lot) {
                setSupportActionBar(mHomeToolbar);
                getSupportActionBar().setTitle(R.string.app_name);
                getMenuInflater().inflate(R.menu.three_dots_home_menu,mHomeToolbar.getMenu());
                loadFragments(new FindLotFragment(), true, "FindLotFragment");
//            } else if (itemId == R.id.my_vehicles) {
//                setSupportActionBar(mHomeToolbar);
//                getSupportActionBar().setTitle(R.string.app_name);
//                loadFragments(new MyVehicleFragment(), true, "MyVehicleFragment");
            } else if (itemId == R.id.reserved_lot) {
                setSupportActionBar(mHomeToolbar);
                getSupportActionBar().setTitle(R.string.app_name);
                loadFragments(new ReservedLotFragment(), true, "ReservedLotFragment");
//            } else if (itemId == R.id.his) {
//                setSupportActionBar(mHomeToolbar);
//                getSupportActionBar().setTitle(R.string.app_name);
//            } else if (itemId == R.id.settings) {
//                Intent intent6 = new Intent(getApplicationContext(), SettingsActivity.class);
//                startActivity(intent6);
            }else if (itemId == R.id.help) {
                Intent intent5 = new Intent(getApplicationContext(), HelpAndSupportActivity.class);
                startActivity(intent5);
            } else if (itemId == R.id.feedback) {
               Intent intent4 = new Intent(getApplicationContext(), FeedBackActivity.class);
               startActivity(intent4);
            } else if (itemId == R.id.logout) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }

            return true;
        });

    // Set up bottom navigation
    bottomNavigationView = findViewById(R.id.bottom_view_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
        int itemId = item.getItemId();
        mDrawerLayout.close();

        if (itemId == R.id.profileHome) {
            loadFragments(new ProfileFragment(), true, "ACCOUNT_FRAGMENT");
        } else if ( itemId == R.id.find_lot) {
            loadFragments(new FindLotFragment(), true, "FIND_LOT_FRAGMENTS");
        }else{
            loadFragments(new ReservedLotFragment(),true,"ReservedLotFragment");
        }

        return true;
    });

        // Handle the back press to manage back stack properly
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Check if the back stack has any fragments
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    // Pop the fragment from the back stack one by one
                    getSupportFragmentManager().popBackStack();
                } else {
                    // If there's only one fragment, finish the activity
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.three_dots_home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method is used to load fragments.
     *
     * @param fragment      Fragment to load
     * @param addToBackStack If true, fragment will be added to back stack.
     * @param tag           Fragment's tag (used to identify fragment)
     */
    public void loadFragments(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
        FragmentTransaction fragmentTransaction = null;
        if (existingFragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            // Pop the fragment from the back stack if it's already present
            fragmentManager.popBackStackImmediate();

            // Replace the existing fragment instead of adding a new one
            fragmentTransaction.replace(R.id.framelayout_container, existingFragment, tag);
        }else {
           fragmentTransaction = fragmentManager.beginTransaction();
            // Replace the current fragment with the new one
            fragmentTransaction.replace(R.id.framelayout_container, fragment, tag);
            // Add the transaction to the back stack if necessary
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(tag);
            }
        }
            // Commit the transaction
            fragmentTransaction.commit();

    }

    private void loadImageFromPreferences() {
        String encodedImage = getApplicationContext()
                .getSharedPreferences("profile_prefs", getApplicationContext().MODE_PRIVATE)
                .getString("profile_image_base64", null);

        if (encodedImage != null) {
            byte[] decodedBytes = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            profileImag.setImageBitmap(bitmap);
        }
    }
}
