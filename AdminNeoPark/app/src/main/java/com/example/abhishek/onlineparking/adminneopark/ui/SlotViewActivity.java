package com.example.abhishek.onlineparking.adminneopark.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abhishek.onlineparking.adminneopark.R;
import com.example.abhishek.onlineparking.adminneopark.adapter.SlotAdapter;
import com.example.abhishek.onlineparking.adminneopark.models.SlotModel;
import com.example.abhishek.onlineparking.adminneopark.viewmodels.SlotViewModel;

import java.util.ArrayList;
import java.util.List;

public class SlotViewActivity extends AppCompatActivity {

    private RecyclerView slotRecyclerView;
    private SlotAdapter slotAdapter;
    private List<SlotModel> slotList = new ArrayList<>();
    private String plotId;
    private SlotViewModel slotViewModel;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_slot_view);

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar  = findViewById(R.id.toolbarSlot);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        // ViewModel
        slotViewModel = new ViewModelProvider(this).get(SlotViewModel.class);


        // Get plotId from intent
        plotId = getIntent().getStringExtra("plotId");
        if (plotId == null) {
            Toast.makeText(this, "No plot ID passed!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up RecyclerView
        slotRecyclerView = findViewById(R.id.slotRecyclerview);
        slotRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe slot data
        slotViewModel.init(plotId);


        // Set up adapter
        slotAdapter = new SlotAdapter(slotList, new SlotAdapter.SlotClickListener() {
//            @Override
//            public void onEdit(SlotModel slot, int position) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(SlotViewActivity.this);
//                builder.setTitle("Edit Slot");
//
//                final EditText input = new EditText(SlotViewActivity.this);
//                input.setText(slot.getSlotName());
//                builder.setView(input);
//
//                builder.setPositiveButton("Update", (dialog, which) -> {
//                    String updatedName = input.getText().toString().trim();
//                    if (!updatedName.isEmpty()) {
//                        slot.setSlotName(updatedName);
//                        slotAdapter.notifyItemChanged(position);
//                        Toast.makeText(SlotViewActivity.this, "Slot updated", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//                builder.show();            }

            @Override
            public void onDelete(SlotModel slot, int position) {
                slotList.remove(position);
                slotAdapter.notifyItemRemoved(position);
                Toast.makeText(SlotViewActivity.this, "Slot deleted", Toast.LENGTH_SHORT).show();            }
        });

        slotRecyclerView.setAdapter(slotAdapter);


        slotViewModel.getSlotsLiveData().observe(this, slots -> {
            Log.d("UIObserver", "Received " + slots.size() + " slots from ViewModel");
            if (slots != null) {
                Log.d("UIObserver", "Slot: " + slots + " - " + slots.toString());
                slotAdapter.updateList(slots);  // ✅ Update your adapter with new data
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Go back when clicked
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
