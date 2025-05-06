package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.renderscript.RenderScript;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.model.PlotModel;
import com.example.abhishek.financetracker.expensemanager.neopark.viewmodel.FindLotViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FindLotFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LinearLayout mNearBySeachTxt ;
    private  View myView;
    private FindLotViewModel mapViewModel;
    EditText searchInput;
    LinearLayout bottomLayoutSearch;
    LatLngBounds.Builder boundsBuilder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_find_lot, container, false);
        mNearBySeachTxt = myView.findViewById(R.id.find_near_search_txt);
        mapViewModel = new ViewModelProvider(this).get(FindLotViewModel.class);
        searchInput = myView.findViewById(R.id.editTextSearch);
        bottomLayoutSearch = myView.findViewById(R.id.bottomParkingSearch);
        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNearBySeachTxt.setOnClickListener(v -> {
            getViewModelFilteredPlots();
    });
        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // FloatingActionButton to find the user's location
        FloatingActionButton btnFindLocation = view.findViewById(R.id.btn_find_location);

        // Observe the user location in ViewModel
        mapViewModel.getUserLocation().observe(getViewLifecycleOwner(), latLng -> {
            if (latLng != null) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            }
        });

        // Observe permission granted
        mapViewModel.isLocationPermissionGranted().observe(getViewLifecycleOwner(), isGranted -> {
            if (!isGranted) {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        });

        btnFindLocation.setOnClickListener(v -> getUserLocation());

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Default marker (optional)
        LatLng defaultLocation = new LatLng(19.391, 72.839);
        googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Admin Office Vasai"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f));


        getViewModelFilteredPlots();


        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                mapViewModel.searchPlots(s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

    }

    private void getViewModelFilteredPlots() {
        mapViewModel.getFilteredPlots().observe(getViewLifecycleOwner(), plots -> {
            googleMap.clear();
                if (plots == null || plots.isEmpty()) return;

                boundsBuilder = new LatLngBounds.Builder();

                for (PlotModel plot : plots) {
                    LatLng latLngBounds = new LatLng(plot.getLatitude(), plot.getLongitude());
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(latLngBounds)
                            .title(plot.getName())
                            .snippet(plot.getAddress()));
                    if (marker != null) {
                        marker.showInfoWindow(); // Show the title/snippet by default
                        googleMap.setOnMarkerClickListener(marker1 -> {


                            marker.getTitle();
                            marker.getSnippet();
                            String totalSlotStr = String.valueOf(plot.getTotalSlots());
                            String titleaStr = plot.getName();
                            String addressStr = plot.getAddress();
                            String plotId = plot.getPlotId();

                            PlotDetailSheetFragment bottomSheet = PlotDetailSheetFragment.newInstance(titleaStr, addressStr,totalSlotStr,plotId);
                            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
                            return true;
                        });
                    }
                    boundsBuilder.include(latLngBounds);
                }
                googleMap.setOnMapLoadedCallback(() -> {
                    try {
                        LatLngBounds bounds = boundsBuilder.build();
                        int padding = 200; // Adjust padding to control zoom and center
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                    } catch (Exception e) {
                        Log.e("FindLotFragment", "Error centering camera: " + e.getMessage());
                    }
                });
        });
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("FindLotFragment", "Location permission not granted. Requesting permission...");
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
            return;
        }

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext());
        if (status != ConnectionResult.SUCCESS) {
            Toast.makeText(requireContext(), "Google Play Services not available", Toast.LENGTH_SHORT).show();
            return;
        }
//         Thank You for Watching .....
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        Log.d("FindLotFragment", "Current location: " + location);
                        mapViewModel.updateUserLocation(location);
                    } else {
                        Toast.makeText(requireContext(), "Failed to get current location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FindLotFragment", "Location error", e);
                    Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                });
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
