package com.example.abhishek.financetracker.expensemanager.neopark.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.abhishek.financetracker.expensemanager.neopark.model.SlotModel;
import com.example.abhishek.financetracker.expensemanager.neopark.repository.SlotRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlotViewModel extends ViewModel {
    private static final String TAG = "SlotViewModel"; // ✅ Add a TAG

    private final SlotRepository slotRepository = new SlotRepository();
    private final MutableLiveData<List<SlotModel>> slotsLiveData = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Call this to fetch slots for a particular plot
    public void init(String plotId) {
        loadSlots(plotId);
    }

    private void loadSlots(String plotId) {
        executorService.execute(() -> {
            slotRepository.getAllSlots(plotId, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<SlotModel> slotList = new ArrayList<>();
                        for (DataSnapshot slotSnapshot : dataSnapshot.getChildren()) {
                            String slotId = slotSnapshot.getKey(); // SlotId key
                            SlotModel slot = slotSnapshot.getValue(SlotModel.class);
                            if (slot != null && !slot.isBooked()) { // Filter non-booked slots
                                slotList.add(slot);
                            } else {
                                Log.e(TAG, "Slot object is NULL or is booked for: " + slotSnapshot.getKey());
                            }
                        }
                        // Update LiveData with the fetched non-booked slots
                        slotsLiveData.postValue(slotList);
                        Log.d(TAG, "Final non-booked slots to display: " + slotList.size());
                    } else {
                        // Handle empty data case
                        Log.d(TAG, "No slots found for plot: " + plotId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                    // Optionally, you could expose an error LiveData to show errors in the UI
                }
            });
        });
    }

    // Add or update a slot (this could be called for booking/unbooking)
    public void addOrUpdateSlot(String plotId, SlotModel slotModel) {
        executorService.execute(() -> {
            slotRepository.addOrUpdateSlot(plotId, slotModel);
            // Optionally, refresh the slots after updating
            loadSlots(plotId);
        });
    }

    public LiveData<List<SlotModel>> getSlotsLiveData() {
        return slotsLiveData;
    }
}
