package com.example.abhishek.onlineparking.adminneopark.ui;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abhishek.onlineparking.adminneopark.R;
import com.example.abhishek.onlineparking.adminneopark.adapter.PlotAdapter;
import com.example.abhishek.onlineparking.adminneopark.models.PlotModel;
import com.example.abhishek.onlineparking.adminneopark.viewmodels.PlotViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LotAndSlotFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlotAdapter adapter;
    private PlotViewModel viewModel;

    public LotAndSlotFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lot_and_slot, container, false);

        recyclerView = view.findViewById(R.id.plotRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlotAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(PlotViewModel.class);
        viewModel.getPlots().observe(getViewLifecycleOwner(), plots -> {
            adapter.submitList(new ArrayList<>(plots));
        });

        adapter.setOnItemClickListener(new PlotAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(PlotModel model) {
                Intent intent = new Intent(getContext(), AddPlotMapActivity.class);
                intent.putExtra("plotLatitude", model.getLatitude());
                intent.putExtra("plotLongitude", model.getLongitude());
                intent.putExtra("plotName", model.getName());
                intent.putExtra("plotAddress", model.getAddress());
                intent.putExtra("isFromView", true); // Add this line
                startActivity(intent);
            }

            @Override
            public void onEditClick(PlotModel model) {
                showEditDialog(model);
            }

            @Override
            public void onDeleteClick(PlotModel model) {
                viewModel.deletePlot(model);
                Toast.makeText(getContext(), "Plot deleted", Toast.LENGTH_SHORT).show();
            }
        });

        EditText editTextSearch = view.findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase(Locale.ROOT);

                adapter.filter(query);
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }


    private void showEditDialog(PlotModel model) {

        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_plot, null);

        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        EditText twoWheelerSlotsEditText = dialogView.findViewById(R.id.twoWheelerSlotsEditText);
        EditText fourWheelerSlotsEditText = dialogView.findViewById(R.id.fourWheelerSlotsEditText);
        EditText twoWheelerTimeEditText = dialogView.findViewById(R.id.twoWheelerTimeEditText);
        EditText twoWheelerPriceEditText = dialogView.findViewById(R.id.twoWheelerPriceEditText);
        EditText fourWheelerTimeEditText = dialogView.findViewById(R.id.fourWheelerTimeEditText);
        EditText fourWheelerPriceEditText = dialogView.findViewById(R.id.fourWheelerPriceEditText);
        EditText totalSlotsEditText = dialogView.findViewById(R.id.totalSlotsEditText);
        EditText dateEditText = dialogView.findViewById(R.id.dateEditText);

        ProgressBar progressBar = dialogView.findViewById(R.id.createPlotProgressbar);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnCreate = dialogView.findViewById(R.id.btnCreate);

//        EditText totalSlotsEditText = dialogView.findViewById(R.id.totalSlotsEditText);

        // Set current plot values
        titleEditText.setText(model.getName());
        twoWheelerSlotsEditText.setText(String.valueOf(model.getTwoWheelerSlots()));
        fourWheelerSlotsEditText.setText(String.valueOf(model.getFourWheelerSlots()));
        twoWheelerPriceEditText.setText(String.valueOf(model.getTwoPrice()));
        totalSlotsEditText.setText(String.valueOf(model.getTotalSlots()));
        twoWheelerTimeEditText.setText(String.valueOf(model.getTwoTime()));
        fourWheelerPriceEditText.setText(String.valueOf(model.getFourPrice()));
        fourWheelerTimeEditText.setText(String.valueOf(model.getFourTime()));
        dateEditText.setText(String.valueOf(model.getDate()));
        btnCancel.setVisibility(GONE);
        btnCreate.setVisibility(GONE);
        totalSlotsEditText.setEnabled(false);
        dateEditText.setFocusable(false);

        // Auto-calculate total slots when user types
        TextWatcher slotWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String twoStr = twoWheelerSlotsEditText.getText().toString().trim();
                String fourStr = fourWheelerSlotsEditText.getText().toString().trim();

                int two = twoStr.isEmpty() ? 0 : Integer.parseInt(twoStr);
                int four = fourStr.isEmpty() ? 0 : Integer.parseInt(fourStr);

                totalSlotsEditText.setText(String.valueOf(two + four));
            }
        };

        twoWheelerSlotsEditText.addTextChangedListener(slotWatcher);
        fourWheelerSlotsEditText.addTextChangedListener(slotWatcher);

        Calendar calendar = Calendar.getInstance();
        dateEditText.setOnClickListener(v -> {
            DatePickerDialog dialogs = new DatePickerDialog(requireContext(), (view1, year, month, dayOfMonth) -> {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                dateEditText.setText(date);
                model.setDate(date);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialogs.show();
        });

        // Create and show dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Plot")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newName = titleEditText.getText().toString().trim();
                    String newTwoStr = twoWheelerSlotsEditText.getText().toString().trim();
                    String newFourStr = fourWheelerSlotsEditText.getText().toString().trim();
                    String twoTimeStr = twoWheelerTimeEditText.getText().toString().trim();
                    String twoPriceStr = twoWheelerPriceEditText.getText().toString().trim();
                    String fourTimeStr = fourWheelerTimeEditText.getText().toString().trim();
                    String fourPriceStr = fourWheelerPriceEditText.getText().toString().trim();
                    String date = dateEditText.getText().toString().trim();

                    if (newName.isEmpty() || newTwoStr.isEmpty() || newFourStr.isEmpty()|| twoPriceStr.isEmpty() || twoTimeStr.isEmpty() || fourPriceStr.isEmpty() || fourTimeStr.isEmpty() || date.isEmpty()) {
                        Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int two = Integer.parseInt(newTwoStr);
                        int four = Integer.parseInt(newFourStr);
                        int total = two + four;
                        double twoPrice = Double.parseDouble(twoPriceStr);
                        double fourPrice = Double.parseDouble(fourPriceStr);


                        model.setName(newName);
                        model.setTwoWheelerSlots(two);
                        model.setFourWheelerSlots(four);
                        model.setTotalSlots(total);
                        model.setTwoTime(twoTimeStr);
                        model.setFourTime(fourTimeStr);
                        model.setTwoPrice(twoPrice);
                        model.setFourPrice(fourPrice);
                        model.setDate(date);

                        viewModel.updatePlot(model); // Update through ViewModel
                        Toast.makeText(requireContext(), "Plot updated successfully", Toast.LENGTH_SHORT).show();

                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Enter valid numeric slot values", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
