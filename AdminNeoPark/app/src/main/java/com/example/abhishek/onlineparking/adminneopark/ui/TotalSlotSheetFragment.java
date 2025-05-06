package com.example.abhishek.onlineparking.adminneopark.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.abhishek.onlineparking.adminneopark.adapter.TotalSlotCheetAdapter;
import com.example.abhishek.onlineparking.adminneopark.viewmodels.TotalSlotSheetViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhishek.onlineparking.adminneopark.databinding.FragmentTotalSlotSheetListDialogBinding;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     TotalSlotSheetFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class TotalSlotSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "TotalSlotsSheetFragment";
    private TotalSlotSheetViewModel viewModel;
    private TotalSlotCheetAdapter slotAdapter;
    private FragmentTotalSlotSheetListDialogBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentTotalSlotSheetListDialogBinding.inflate(inflater, container, false);
        slotAdapter = new TotalSlotCheetAdapter();
        binding.totalSlotList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.totalSlotList.setAdapter(slotAdapter);

        // ViewModel setup
        viewModel = new ViewModelProvider(this).get(TotalSlotSheetViewModel.class);

        viewModel.getUsedSlotsLiveData().observe(getViewLifecycleOwner(), slotModels -> {
            Log.d(TAG, "LiveData updated with " + slotModels.size() + " Totalslots");
            if (slotModels != null && !slotModels.isEmpty()) {
                Log.d("TotalSlotFragment", "Received slot models with size: " + slotModels.size());
                slotAdapter.setSlotData(slotModels); // Update adapter with new data
            } else {
                Log.d("TotalSlotFragment", "No slots found to display");
            }        });

        // Load initial data
        Log.d(TAG, "Calling viewModel.loadUsedSlots()");
        viewModel.loadUsedSlots();
        return binding.getRoot();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}