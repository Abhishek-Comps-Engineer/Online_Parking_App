package com.example.abhishek.onlineparking.adminneopark.models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddPlotMapViewModel {
    private final Context context;
    private final LatLng latLng;
    private final ExecutorService executorService;

    public AddPlotMapViewModel(Context context, LatLng latLng) {
        this.context = context;
        this.latLng = latLng;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // Callback interface
    public interface AddressCallback {
        void onAddressFound(String address);
        void onError(String error);
    }

    // Get address asynchronously
    public void getAddressFromLatLngAsync(AddressCallback callback) {
        executorService.execute(() -> {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String addressLine = addresses.get(0).getAddressLine(0);
                    callback.onAddressFound(addressLine);
                } else {
                    callback.onError("Address not found.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                callback.onError("Geocoder failed: " + e.getMessage());
            }
        });
    }

    private void runOnMainThread(Runnable runnable) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(runnable);
    }
}
