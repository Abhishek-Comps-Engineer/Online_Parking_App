package com.example.abhishek.onlineparking.adminneopark.firebases;

import android.util.Log;

import com.example.abhishek.onlineparking.adminneopark.models.PlotModel;
import com.example.abhishek.onlineparking.adminneopark.models.SlotModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";
    private final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("plots");
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void addPlot(PlotModel plot) {
        executorService.execute(() -> {
            String plotId = plot.getPlotId();

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Log.e(TAG, "User not authenticated");
                return;
            }

            Map<String, Object> plotMap = new HashMap<>();
            plotMap.put("plotId", plot.getPlotId());
            plotMap.put("name", plot.getName());
            plotMap.put("address", plot.getAddress());
            plotMap.put("latitude", plot.getLatitude());
            plotMap.put("longitude", plot.getLongitude());
            plotMap.put("twoWheelerSlots", plot.getTwoWheelerSlots());
            plotMap.put("twoWheelerTime", plot.getTwoTime());
            plotMap.put("twoWheelerPrice", plot.getTwoPrice());
            plotMap.put("fourWheelerSlots", plot.getFourWheelerSlots());
            plotMap.put("fourWheelerTime", plot.getFourTime());
            plotMap.put("fourWheelerPrice", plot.getFourPrice());
            plotMap.put("totalSlots", plot.getTotalSlots());
            plotMap.put("availableFromDate", plot.getDate());

            // Convert slots list into a Map<slotId, SlotModel>
            Map<String, Object> slotsMap = new HashMap<>();
            Map<String,SlotModel> slots = plot.getSlots();
            if (slots != null) {
                for (Map.Entry<String, SlotModel> entry : slots.entrySet()) {
                    slotsMap.put(entry.getKey(), entry.getValue());
                }
            }
            plotMap.put("slots", slotsMap);

            databaseRef.child(plotId).setValue(plotMap)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Plot added successfully: " + plotId))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to add plot: " + plotId, e));
        });
    }

    public void updatePlot(PlotModel plot) {
        // Same as addPlot because we overwrite the plot
        addPlot(plot);
    }

    public void deletePlot(String plotId) {
        databaseRef.child(plotId).removeValue()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Plot deleted: " + plotId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete plot: " + plotId, e));
    }

    public void getAllPlots(ValueEventListener listener) {
        executorService.execute(() -> {
            Log.d(TAG, "Fetching all plots...");
            databaseRef.addValueEventListener(listener);
        });
    }

    public void getAllSlots(String plotId, ValueEventListener listener) {
        DatabaseReference slotRef = databaseRef.child(plotId).child("slots");
        executorService.execute(() -> {
            Log.d(TAG, "Fetching all slots for plot: " + plotId);
            slotRef.addValueEventListener(listener);
        });
    }

    public void addOrUpdateSlot(String plotId, SlotModel slot) {
        String slotId = slot.getSlotId();
        DatabaseReference slotRef = databaseRef.child(plotId).child("slots").child(slotId);
        executorService.execute(() -> {
            slotRef.setValue(slot)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Slot added/updated: " + slotId))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to add/update slot: " + slotId, e));
        });
    }

    public void deleteSlot(String plotId, String slotId) {
        DatabaseReference slotRef = databaseRef.child(plotId).child("slots").child(slotId);
        executorService.execute(() -> {
            slotRef.removeValue()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Slot deleted: " + slotId))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to delete slot: " + slotId, e));
        });
    }
}
