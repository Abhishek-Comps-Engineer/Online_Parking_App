package com.example.abhishek.onlineparking.adminneopark.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.abhishek.onlineparking.adminneopark.models.SlotModel;
import com.example.abhishek.onlineparking.adminneopark.repositories.SlotRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlotViewModel extends ViewModel {

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
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d("SlotFetch", "Slot count: " + snapshot.getChildrenCount());
                    List<SlotModel> slotList = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        SlotModel slot = child.getValue(SlotModel.class);
//                        Log.d("SlotFetch", "Slot ID: " + slot.getSlotId());
                        if (slot != null) {
                            slotList.add(slot);
                        }
                    }
                    slotsLiveData.postValue(slotList);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("SlotFetch", "Database error: " + error.getMessage());
                    // Handle error (you can expose a LiveData for error messages if needed)
                }
            });
        });
    }

    public LiveData<List<SlotModel>> getSlotsLiveData() {
        return slotsLiveData;
    }

    public void addOrUpdateSlot(String plotId, SlotModel slotModel) {
        executorService.execute(() -> {
            slotRepository.addOrUpdateSlot(plotId, slotModel);
        });
    }

    public void deleteSlot(String plotId, String slotId) {
        executorService.execute(() -> {
            slotRepository.deleteSlot(plotId, slotId);
        });
    }
}
