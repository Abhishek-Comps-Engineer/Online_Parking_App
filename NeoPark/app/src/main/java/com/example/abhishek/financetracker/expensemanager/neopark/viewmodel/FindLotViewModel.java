package com.example.abhishek.financetracker.expensemanager.neopark.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.abhishek.financetracker.expensemanager.neopark.model.PlotModel;
import com.example.abhishek.financetracker.expensemanager.neopark.repository.PlotRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class FindLotViewModel extends ViewModel {

    private final MutableLiveData<LatLng> userLocationLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> locationPermissionGrantedLiveData = new MutableLiveData<>(false);
    private final PlotRepository repository = new PlotRepository();
    private final LiveData<List<PlotModel>> plotsLiveData = repository.getPlotsLiveData();
    private final MutableLiveData<List<PlotModel>> allPlots = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<PlotModel>> filteredPlots = new MutableLiveData<>();

    public FindLotViewModel() {
        repository.getPlotsLiveData().observeForever(plots -> {
            allPlots.setValue(plots);
            filteredPlots.setValue(plots);
        });
    }

    public LiveData<List<PlotModel>> getFilteredPlots() {
        return filteredPlots;
    }




    public void searchPlots(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredPlots.setValue(allPlots.getValue()); // show all if empty
            return;
        }

        List<PlotModel> result = new ArrayList<>();
        for (PlotModel plot : allPlots.getValue()) {
            if (plot.getName().toLowerCase().contains(query.toLowerCase()) ||
                    plot.getAddress().toLowerCase().contains(query.toLowerCase())) {
                result.add(plot);
            }
        }
        filteredPlots.setValue(result);
    }





    // LiveData for observing user location
    public LiveData<LatLng> getUserLocation() {
        return userLocationLiveData;
    }

    // LiveData to check if location permission is granted
    public LiveData<Boolean> isLocationPermissionGranted() {
        return locationPermissionGrantedLiveData;
    }

    // Method to update user location
    public void updateUserLocation(Location location) {
        if (location != null) {
            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            userLocationLiveData.setValue(userLatLng);
        }
    }

}
