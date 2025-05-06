package com.example.abhishek.onlineparking.adminneopark.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.abhishek.onlineparking.adminneopark.R;
import com.example.abhishek.onlineparking.adminneopark.viewmodels.TotalSlotSheetViewModel;
import com.example.abhishek.onlineparking.adminneopark.viewmodels.VaccantSlotViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class DashBoardFragment extends Fragment {


    private static final String PREF_NAME = "slot_data";
    private static final String KEY_TOTAL_SLOTS = "total_slots";
    private static final String KEY_VACANT_SLOTS = "vacant_slots";
    private static final String KEY_USED_SLOTS = "used_slots";
    private BarChart barChart;
    private TotalSlotSheetViewModel viewModelBar;
    TextView totalSlotCount, totalVacantSlotCount;
    private VaccantSlotViewModel viewModel;

    private int bookedCount = 0;
    private int notBookedCount = 0;

    public DashBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);

        setupButtons(view);
        setupBarChart(view);
        setupViewModel();
        totalVacantSlotCount = view.findViewById(R.id.totalVacantSlotCount);

        VaccantSlotViewModel vaccantSlotViewModel = new ViewModelProvider(this).get(VaccantSlotViewModel.class);
        Log.d("DashBoardFragment","VacantViewModel Initialized");

        vaccantSlotViewModel.getVacantSlotCount().observe(getViewLifecycleOwner(),VacantSlotCount -> {
            Log.d("DashBoardFragment","Vacant Slot Observing");
            if(VacantSlotCount != null ){
                totalVacantSlotCount.setText(String.valueOf(VacantSlotCount));
            }
        });

        return view;
    }

    private void setupButtons(View view) {
        view.findViewById(R.id.addPlotButton).setOnClickListener(v ->
                startActivity(new Intent(getContext(), AddPlotMapActivity.class)));

        view.findViewById(R.id.totalUsedSlots).setOnClickListener(v -> {
            UsedSlotsSheetFragment sheetFragment = new UsedSlotsSheetFragment();
            sheetFragment.show(requireActivity().getSupportFragmentManager(), sheetFragment.getTag());
            Log.d("DashBoardFragment", "Opened Used Slots Bottom Sheet");
        });

        view.findViewById(R.id.totalVacantSlots).setOnClickListener(v -> {
            VaccantSheetFragment sheetFragment = new VaccantSheetFragment();
            sheetFragment.show(requireActivity().getSupportFragmentManager(), sheetFragment.getTag());
            Log.d("DashBoardFragment", "Opened Vacant Slots Bottom Sheet");
        });

        view.findViewById(R.id.totalSlotsButton).setOnClickListener(v -> {
            TotalSlotSheetFragment sheetFragment = new TotalSlotSheetFragment();
            sheetFragment.show(requireActivity().getSupportFragmentManager(), sheetFragment.getTag());
            Log.d("DashBoardFragment", "Opened Total Slots Bottom Sheet");
        });
    }

    private void setupBarChart(View view) {
        barChart = view.findViewById(R.id.barChart);

        // Enable zoom & touch features
        barChart.setTouchEnabled(true);
        barChart.setPinchZoom(true);
        barChart.setDoubleTapToZoomEnabled(true);
        barChart.setScaleEnabled(true);

        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(12f);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

        barChart.animateY(1000);
    }

    private void setupViewModel() {
        viewModelBar = new ViewModelProvider(this).get(TotalSlotSheetViewModel.class);

        viewModelBar.getUsedSlotsLiveData().observe(getViewLifecycleOwner(), slotModels -> {
            if (slotModels != null) {
                bookedCount = 0;
                notBookedCount = 0;

                for (var slot : slotModels) {
                    if (slot.isBooked()) {
                        bookedCount++;
                    } else {
                        notBookedCount++;
                    }
                }

                updateBarChart();
            } else {
                Log.d("DashBoardFragment", "No slot data found.");
            }
        });

        viewModelBar.loadUsedSlots();
    }

    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, bookedCount));
        entries.add(new BarEntry(1, notBookedCount));

        BarDataSet dataSet = new BarDataSet(entries, "Slot Status");
        dataSet.setColors(Color.rgb(0, 155, 0), Color.rgb(200, 0, 0)); // Booked - Green, Not Booked - Red
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
//        barChart.zoom(1f, 1f, 0, 0); // Zoom 1.5x on X-axis (horizontal)
        String[] labels = {"Booked", "Not Booked"};
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        barChart.invalidate(); // Refresh chart
    }
    private void saveData() {
        SharedPreferences preferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Get values from the TextViews and store them in SharedPreferences
        int totalSlots = Integer.parseInt(totalSlotCount.getText().toString());
        int vacantSlots = Integer.parseInt(totalVacantSlotCount.getText().toString());
        int usedSlots = Integer.parseInt(((TextView) getView().findViewById(R.id.totalUsedSlotCount)).getText().toString());

        // Save the values to SharedPreferences
        editor.putInt(KEY_TOTAL_SLOTS, totalSlots);
        editor.putInt(KEY_VACANT_SLOTS, vacantSlots);
        editor.putInt(KEY_USED_SLOTS, usedSlots);
        editor.apply();
    }


    // Load data from SharedPreferences
    private void loadData() {
        SharedPreferences preferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Retrieve the saved values from SharedPreferences
        int totalSlots = preferences.getInt(KEY_TOTAL_SLOTS, 0);  // Default is 0 if not found
        int vacantSlots = preferences.getInt(KEY_VACANT_SLOTS, 0);  // Default is 0 if not found
        int usedSlots = preferences.getInt(KEY_USED_SLOTS, 0);  // Default is 0 if not found

        // Set the values to the TextViews
        totalSlotCount.setText(String.format("%d", totalSlots));
        totalVacantSlotCount.setText(String.format("%d", vacantSlots));
        ((TextView) getView().findViewById(R.id.totalUsedSlotCount)).setText(String.format("%d", usedSlots));
    }

}
