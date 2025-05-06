package com.example.abhishek.onlineparking.adminneopark.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.abhishek.onlineparking.adminneopark.models.PlotModel;
import com.example.abhishek.onlineparking.adminneopark.models.SlotModel;
import com.example.abhishek.onlineparking.adminneopark.repositories.PlotRepository;
import com.example.abhishek.onlineparking.adminneopark.repositories.SlotRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VaccantSlotViewModel extends ViewModel {

    private static final String TAG = "VacantSlotViewModel";

    private final PlotRepository plotRepository = new PlotRepository();
    private final SlotRepository slotRepository = new SlotRepository();
    private final MutableLiveData<List<SlotModel>> usedSlotsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> vacantSlotCount = new MutableLiveData<>();


    public LiveData<Integer> getVacantSlotCount(){
        return vacantSlotCount;
    }


    public LiveData<List<SlotModel>> getUsedSlotsLiveData() {
        return usedSlotsLiveData;
    }

    public void loadUsedSlots() {
        plotRepository.getPlotsLiveData().observeForever(plots -> {
            if (plots == null || plots.isEmpty()) {
                Log.w(TAG, "No plots found.");
                return;
            }

            List<SlotModel> bookedSlots = new ArrayList<>();
            int totalPlots = plots.size();
            int[] processedCount = {0};

            for (PlotModel plot : plots) {
                String plotId = plot.getPlotId();
                Log.d(TAG, "Fetching slots for plot: " + plotId);

                slotRepository.getAllSlots(plotId, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot child : snapshot.getChildren()) {
                            SlotModel slot = child.getValue(SlotModel.class);

                            if (slot == null) {
                                Log.w(TAG, "Null slot found in plot: " + plotId);
                                continue;
                            }

                            Log.d(TAG, "Slot: " + slot.getSlotName() + ", isBooked: " + slot.isBooked());

                            if (!slot.isBooked()) {
                                bookedSlots.add(slot);
                                Log.d(TAG, "Booked slot added: " + slot.getSlotName());
                            }
                        }

                        processedCount[0]++;
                        if (processedCount[0] == totalPlots) {
                            usedSlotsLiveData.postValue(new ArrayList<>(bookedSlots));
                            vacantSlotCount.postValue(bookedSlots.size());
                            Log.d(TAG, "All plots processed. Total Vacant slots: " + bookedSlots.size());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading slots for plotId: " + plotId + ", Error: " + error.getMessage());
                        processedCount[0]++;
                        if (processedCount[0] == totalPlots) {
                            usedSlotsLiveData.postValue(new ArrayList<>(bookedSlots));
                            vacantSlotCount.postValue(bookedSlots.size());

                        }
                    }
                });
            }
        });
    }
}
