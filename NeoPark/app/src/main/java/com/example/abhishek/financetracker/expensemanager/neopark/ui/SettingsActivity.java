package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.adapter.SettingsAdapter;
import com.example.abhishek.financetracker.expensemanager.neopark.model.SettingsItemModel;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSettings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<SettingsItemModel> settingsList = new ArrayList<>();
        settingsList.add(new SettingsItemModel(R.drawable.ic_key, "Account", "Security notifications, change number"));
        settingsList.add(new SettingsItemModel(R.drawable.baseline_security, "Privacy", "Block contacts, disappearing messages"));
        settingsList.add(new SettingsItemModel(R.drawable.ic_notification, "Notification", "Message, Check Upcomings"));
        settingsList.add(new SettingsItemModel(R.drawable.ic_storage, "Storage and data", "Network usage, auto-download"));
        settingsList.add(new SettingsItemModel(R.drawable.ic_language, "App language", "Change app language"));
        settingsList.add(new SettingsItemModel(R.drawable.ic_updates, "App Updates","Stay informed with latest features."));
        settingsList.add(new SettingsItemModel(R.drawable.ic_invite, "Invite","Share app with your friends"));

        SettingsAdapter adapter = new SettingsAdapter(settingsList);
        recyclerView.setAdapter(adapter);

        ImageView backButton = findViewById(R.id.settings_button);
        backButton.setOnClickListener(view -> onBackPressed());

    }
}