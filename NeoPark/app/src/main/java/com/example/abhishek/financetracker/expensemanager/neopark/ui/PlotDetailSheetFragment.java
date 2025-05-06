package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.example.abhishek.financetracker.expensemanager.neopark.adapter.PlotSheetAdapter;
import com.example.abhishek.financetracker.expensemanager.neopark.model.SlotModel;
import com.example.abhishek.financetracker.expensemanager.neopark.viewmodel.SlotViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.databinding.FragmentPlotDetailSheetListDialogBinding;

import java.util.List;

public class PlotDetailSheetFragment extends BottomSheetDialogFragment implements PlotSheetAdapter.OnBookingClickListener {

private FragmentPlotDetailSheetListDialogBinding binding;


    private SlotViewModel viewModel;
    private String plotId;
    private static final String ARG_NAME = "name";
    private static final String ARG_ADDRESS = "address";
    private static final String TAG = "PlotDetailSheetFragment"; // ✅ TAG for logging
    private static final String ARG_TOTAL_SLOT = "arg_total_slot";
    private static final String ARG_PLOT_ID = "plot_Id";

    public static PlotDetailSheetFragment newInstance(String name, String address, String totalSlot, String plotId) {
        PlotDetailSheetFragment fragment = new PlotDetailSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_ADDRESS, address);
        args.putString(ARG_TOTAL_SLOT,totalSlot);
        args.putString(ARG_PLOT_ID,plotId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPlotDetailSheetListDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot(); // Use binding.getRoot()

        String title = getArguments() != null ? getArguments().getString(ARG_NAME) : "";
        String address = getArguments() != null ? getArguments().getString(ARG_ADDRESS) : "";
        String totalSlot = getArguments() != null ? getArguments().getString(ARG_TOTAL_SLOT) : "";
        plotId = getArguments() != null ? getArguments().getString(ARG_PLOT_ID) : "";


        // Example: set to TextViews
        TextView titleView = view.findViewById(R.id.tvPlotName);
        TextView addressView = view.findViewById(R.id.tvAddress);
        TextView totalSlotText = view.findViewById(R.id.tvTotalSlots);

        titleView.setText(title);
        addressView.setText(address);
        totalSlotText.setText("Total Slots : "+totalSlot);

        Log.d(TAG, "PlotId received: " + plotId); // ✅ Log Plot ID



        viewModel = new ViewModelProvider(this).get(SlotViewModel.class);
        viewModel.init(plotId); // fetch slots

        RecyclerView recyclerView = view.findViewById(R.id.SlotSheetRecyclerView);
        PlotSheetAdapter adapter = new PlotSheetAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getSlotsLiveData().observe(getViewLifecycleOwner(), slots -> {
            Log.d("PlotDetailSheetFragment", "Slots fetched: " + (slots != null ? slots.size() : 0));
            adapter.setSlotList(slots);
        });


        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void showBookingSuccessDialog(String plotId,SlotModel slot, String slotName) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_booking_success, null);
        builder.setView(dialogView);

        android.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false); // User must click OK to close

        slot.setBooked(true);  // Booked -> true
        viewModel.addOrUpdateSlot(plotId,slot);
        TextView tvSuccess = dialogView.findViewById(R.id.tvBookingSuccess);
        tvSuccess.setText("Booking Confirmed for " + slotName + "!");

        dialogView.findViewById(R.id.btnOk).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onBookingClick(SlotModel slot) {
        showBookingSuccessDialog(plotId,slot, slot.getSlotName());
        Toast.makeText(requireContext(), "Booking " + slot.getSlotName() + slot.isBooked(), Toast.LENGTH_SHORT).show();
    }
}