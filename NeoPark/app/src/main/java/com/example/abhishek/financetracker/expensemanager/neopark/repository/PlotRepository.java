package com.example.abhishek.financetracker.expensemanager.neopark.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.abhishek.financetracker.expensemanager.neopark.model.PlotModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlotRepository {
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private final MutableLiveData<List<PlotModel>> plotsLiveData = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public PlotRepository() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                firebaseHelper.getAllPlots(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<PlotModel> plotList = new ArrayList<>();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Log.d("PlotData", "Child data: " + child.getValue());
                            PlotModel plot = child.getValue(PlotModel.class);
                            plotList.add(plot);
                        }

                        plotsLiveData.postValue(plotList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("PlotData", "Error fetching data: " + error.getMessage());
                    }
                });
            }
        });
    }


    public LiveData<List<PlotModel>> getPlotsLiveData() {
        return plotsLiveData;
    }

    public void updatePlot(PlotModel plot) {
        firebaseHelper.updatePlot(plot);
    }

}
