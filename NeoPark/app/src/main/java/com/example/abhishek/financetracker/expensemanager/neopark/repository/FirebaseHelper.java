package com.example.abhishek.financetracker.expensemanager.neopark.repository;

import android.util.Log;

import com.example.abhishek.financetracker.expensemanager.neopark.model.PlotModel;
import com.example.abhishek.financetracker.expensemanager.neopark.model.SlotModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";
    private final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("plots");
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();



    public void getAllPlots(ValueEventListener listener) {
        executorService.execute(() -> {
            Log.d(TAG, "Fetching all plots...");
            databaseRef.addValueEventListener(listener);
        });
    }


    public void updatePlot(PlotModel plot) {
        String plotId = plot.getPlotId();
        databaseRef.child(plotId).setValue(plot)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseHelper", "Plot updated successfully"))
                .addOnFailureListener(e -> Log.e("FirebaseHelper", "Failed to update plot", e));
    }

    public void getAllSlots(String plotId, ValueEventListener listener) {
        DatabaseReference slotRef = databaseRef.child(plotId).child("slots");
        executorService.execute(() -> {
            Log.d(TAG, "Fetching all slots for plot: " + plotId);
            slotRef.addValueEventListener(listener);
            });
    }

    public void addOrUpdateSlot(String plotId, SlotModel slot) {
        if (plotId == null || slot == null || slot.getSlotId() == null) {
            Log.e(TAG, "Plot ID or Slot or Slot ID is null");
            return;
        }

        DatabaseReference slotRef = databaseRef.child(plotId).child("slots").child(slot.getSlotId());

        slotRef.setValue(slot)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Slot added/updated: " + slot.getSlotId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add/update slot: " + slot.getSlotId(), e));
    }





}
