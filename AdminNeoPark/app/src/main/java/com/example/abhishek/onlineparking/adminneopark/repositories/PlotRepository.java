package com.example.abhishek.onlineparking.adminneopark.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.abhishek.onlineparking.adminneopark.firebases.FirebaseHelper;
import com.example.abhishek.onlineparking.adminneopark.models.PlotModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlotRepository {

    private static final String TAG = "PlotRepository";

    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private final MutableLiveData<List<PlotModel>> plotsLiveData = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public PlotRepository() {
        fetchAllPlots();
    }

    private void fetchAllPlots() {
        executorService.execute(() -> {
            firebaseHelper.getAllPlots(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<PlotModel> plotList = new ArrayList<>();

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        try {
                            PlotModel plot = childSnapshot.getValue(PlotModel.class);
                            if (plot != null) {
                                plotList.add(plot);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing plot data: ", e);
                        }
                    }

                    plotsLiveData.postValue(plotList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error fetching plots: " + error.getMessage());
                }
            });
        });
    }

    public void addPlot(PlotModel plot) {
        executorService.execute(() -> firebaseHelper.addPlot(plot));
    }

    public void updatePlot(PlotModel plot) {
        executorService.execute(() -> firebaseHelper.updatePlot(plot));
    }

    public void deletePlot(PlotModel plotModel) {
        executorService.execute(() -> firebaseHelper.deletePlot(plotModel.getPlotId()));
    }

    public LiveData<List<PlotModel>> getPlotsLiveData() {
        return plotsLiveData;
    }
}
