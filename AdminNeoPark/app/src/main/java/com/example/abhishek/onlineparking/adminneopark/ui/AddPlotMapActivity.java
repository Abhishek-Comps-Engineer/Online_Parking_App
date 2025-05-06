package com.example.abhishek.onlineparking.adminneopark.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.abhishek.onlineparking.adminneopark.R;
import com.example.abhishek.onlineparking.adminneopark.models.AddPlotMapViewModel;
import com.example.abhishek.onlineparking.adminneopark.models.PlotModel;
import com.example.abhishek.onlineparking.adminneopark.models.SlotModel;
import com.example.abhishek.onlineparking.adminneopark.viewmodels.PlotViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

public class AddPlotMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private AddPlotMapViewModel addPlotMapViewModel;
    private Toolbar backPress;
    private GoogleMap mMap;
    Intent intent;
    private PlotViewModel viewModel;
    private LatLng selectedLocation;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plot_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAddPlot);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        viewModel = new ViewModelProvider(this).get(PlotViewModel.class);

        intent = getIntent();


        backPress = findViewById(R.id.toolbarBack);
        backPress.setOnClickListener(v -> super.onBackPressed());
    }

    private void performLocationTask(double latitude, double longitude) {
        // Create LatLng object
        LatLng latLng = new LatLng(latitude, longitude);

        // Optional: Get address from coordinates (reverse geocoding)
//        String address = getAddressFromLatLng(latitude, longitude);
        String title = intent.getStringExtra("plotName");
        String addressView = intent.getStringExtra("plotAddress");
        // Add Marker on Map
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title +"\n"+addressView)
                .snippet(addressView));

        // Move and zoom the camera to the location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLocation = new LatLng(19.3919, 72.8397);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);


        if (intent != null){
            double latitude = intent.getDoubleExtra("plotLatitude", 0.0);
            double longitude = intent.getDoubleExtra("plotLongitude", 0.0);
            boolean isFromView = intent.getBooleanExtra("isFromView", false); // Check this flag

            // Perform any task here
            if (isFromView) {
                performLocationTask(latitude, longitude);
            }
        }

        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            addPlotMapViewModel = new AddPlotMapViewModel(this.getBaseContext(), latLng);
            String address;
            addPlotMapViewModel.getAddressFromLatLngAsync(new AddPlotMapViewModel.AddressCallback() {
                @Override
                public void onAddressFound(String address) {
                    runOnUiThread(() -> showPlotDialog(latLng, address));  // ✅ safe on main thread
                }

                @Override
                public void onError(String error) {

                }
            });
        });
    }

            private void showPlotDialog (LatLng latLng, String address){
                this.address = address;
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_plot, null);
                bottomSheetDialog.setContentView(dialogView);

                EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
                EditText twoWheelerSlotsEditText = dialogView.findViewById(R.id.twoWheelerSlotsEditText);
                EditText twoWheelerTimeEditText = dialogView.findViewById(R.id.twoWheelerTimeEditText);
                EditText twoWheelerPriceEditText = dialogView.findViewById(R.id.twoWheelerPriceEditText);
                EditText fourWheelerSlotsEditText = dialogView.findViewById(R.id.fourWheelerSlotsEditText);
                EditText fourWheelerTimeEditText = dialogView.findViewById(R.id.fourWheelerTimeEditText);
                EditText fourWheelerPriceEditText = dialogView.findViewById(R.id.fourWheelerPriceEditText);
                EditText totalSlotsEditText = dialogView.findViewById(R.id.totalSlotsEditText);
                EditText dateEditText = dialogView.findViewById(R.id.dateEditText);

                ProgressBar progressBar = dialogView.findViewById(R.id.createPlotProgressbar);
                Button btnCancel = dialogView.findViewById(R.id.btnCancel);
                Button btnCreate = dialogView.findViewById(R.id.btnCreate);
                dateEditText.setFocusable(false);
                totalSlotsEditText.setFocusable(false);

                progressBar.setVisibility(View.GONE);

                // Auto calculate total slots
                TextWatcher slotWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            int two = Integer.parseInt(twoWheelerSlotsEditText.getText().toString());
                            int four = Integer.parseInt(fourWheelerSlotsEditText.getText().toString());
                            totalSlotsEditText.setText(String.valueOf(two + four));
                        } catch (Exception e) {
                            totalSlotsEditText.setText("0");
                        }
                    }
                };
                twoWheelerSlotsEditText.addTextChangedListener(slotWatcher);
                fourWheelerSlotsEditText.addTextChangedListener(slotWatcher);

                btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());


                // Date Picker
                Calendar calendar = Calendar.getInstance();
                dateEditText.setOnClickListener(v -> {
                    DatePickerDialog dialog = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateEditText.setText(date);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                });

                // Time Picker for Two-Wheeler
//                twoWheelerTimeEditText.setOnClickListener(v -> {
//                    TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
//                        twoWheelerTimeEditText.setText(hourOfDay + ":" + (minute < 10 ? "0" + minute : minute));
//                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
//                    timePicker.show();
//                });

//                // Time Picker for Four-Wheeler
//                fourWheelerTimeEditText.setOnClickListener(v -> {
//                    TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
//                        fourWheelerTimeEditText.setText(hourOfDay + ":" + (minute < 10 ? "0" + minute : minute));
//                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
//                    timePicker.show();
//                });

                btnCreate.setOnClickListener(v -> {
                    String title = titleEditText.getText().toString().trim();
                    String twoSlotsStr = twoWheelerSlotsEditText.getText().toString().trim();
                    String twoTimeStr = twoWheelerTimeEditText.getText().toString().trim();
                    String twoPriceStr = twoWheelerPriceEditText.getText().toString().trim();
                    String fourSlotsStr = fourWheelerSlotsEditText.getText().toString().trim();
                    String fourTimeStr = fourWheelerTimeEditText.getText().toString().trim();
                    String fourPriceStr = fourWheelerPriceEditText.getText().toString().trim();
                    String date = dateEditText.getText().toString().trim();

                    if (title.isEmpty() || twoSlotsStr.isEmpty() || twoTimeStr.isEmpty() || twoPriceStr.isEmpty()
                            || fourSlotsStr.isEmpty() || fourTimeStr.isEmpty() || fourPriceStr.isEmpty() || date.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);


                        int twoSlots = Integer.parseInt(twoSlotsStr);
                        int fourSlots = Integer.parseInt(fourSlotsStr);
                        int totalSlots = twoSlots + fourSlots;
                        double twoPrice = Double.parseDouble(twoPriceStr);
                        double fourPrice = Double.parseDouble(fourPriceStr);

                        PlotModel plot = new PlotModel(
                                UUID.randomUUID().toString(),
                                title, address, latLng.latitude, latLng.longitude,
                                twoSlots, twoTimeStr, twoPrice,
                                fourSlots, fourTimeStr, fourPrice,
                                totalSlots, date
                        );
// Create a Map to hold SlotModel objects with slotId as the key
                    Map<String, SlotModel> slotsMap = new HashMap<>();

// Add Two-Wheeler slots to the map
                    for (int i = 1; i <= twoSlots; i++) {
                        SlotModel slot = new SlotModel(
                                UUID.randomUUID().toString(),          // Generate unique slot ID
                                "Two-Wheeler Slot " + i,              // Slot name
                                "Two-Wheeler",                        // Slot type
                                twoTimeStr,                           // Time
                                twoPrice,                             // Price
                                false,                                // Available (initially false)
                                date                                  // Date
                        );
                        slotsMap.put(slot.getSlotId(), slot);      // Put slot in the map with slotId as key
                    }

// Add Four-Wheeler slots to the map
                    for (int i = 1; i <= fourSlots; i++) {
                        SlotModel slot = new SlotModel(
                                UUID.randomUUID().toString(),           // Generate unique slot ID
                                "Four-Wheeler Slot " + i,              // Slot name
                                "Four-Wheeler",                        // Slot type
                                fourTimeStr,                           // Time
                                fourPrice,                             // Price
                                false,                                 // Available (initially false)
                                date                                   // Date
                        );
                        slotsMap.put(slot.getSlotId(), slot);      // Put slot in the map with slotId as key
                    }

// Now set the slots map in the PlotModel
                    plot.setSlots(slotsMap);

// Add the plot with the slots map to the view model or repository
                    viewModel.addPlot(plot);

                            bottomSheetDialog.dismiss();
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(title)
                                    .snippet(address));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                            Toast.makeText(this, "Plot Created at: " + address, Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();
                });

                bottomSheetDialog.show();
            }
}