package com.example.abhishek.onlineparking.adminneopark.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.abhishek.onlineparking.adminneopark.adapter.UsedSlotAdapter;
import com.example.abhishek.onlineparking.adminneopark.databinding.FragmentUsedSlotsSheetListDialogBinding;
import com.example.abhishek.onlineparking.adminneopark.viewmodels.UsedSlotSheetViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class UsedSlotsSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "UsedSlotsSheetFragment";
    private FragmentUsedSlotsSheetListDialogBinding binding;
    private UsedSlotSheetViewModel viewModel;
    private UsedSlotAdapter slotAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Inflating layout");
        binding = FragmentUsedSlotsSheetListDialogBinding.inflate(inflater, container, false);

        // RecyclerView setup
        binding.usedSlotList.setLayoutManager(new LinearLayoutManager(requireContext()));
        slotAdapter = new UsedSlotAdapter();
        binding.usedSlotList.setAdapter(slotAdapter);

        // ViewModel setup
        viewModel = new ViewModelProvider(this).get(UsedSlotSheetViewModel.class);

        viewModel.getUsedSlotsLiveData().observe(getViewLifecycleOwner(), slotModels -> {
            Log.d(TAG, "LiveData updated with " + slotModels.size() + " Usedslots");
            if (slotModels != null && !slotModels.isEmpty()) {
                slotAdapter.setSlotData(slotModels); // Update adapter with new data
            } else {
                Log.d("UsedSlotsSheetFragment", "No slots found to display");
            }        });

        // Load initial data
        Log.d(TAG, "Calling viewModel.loadUsedSlots()");
        viewModel.loadUsedSlots();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: Clearing binding");
        binding = null;
    }

    // Adapter class for slots
}
