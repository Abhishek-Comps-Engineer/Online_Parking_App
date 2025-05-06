package com.example.abhishek.onlineparking.adminneopark.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.abhishek.onlineparking.adminneopark.adapter.VacantSlotsAdapter;
import com.example.abhishek.onlineparking.adminneopark.viewmodels.VaccantSlotViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhishek.onlineparking.adminneopark.databinding.FragmentVaccantSheetListDialogBinding;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     VaccantSheetFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class VaccantSheetFragment extends BottomSheetDialogFragment {
    String TAG = "VacantSlotsSheet";
    private FragmentVaccantSheetListDialogBinding binding;
    private VaccantSlotViewModel viewModel;
    private VacantSlotsAdapter slotAdapter;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentVaccantSheetListDialogBinding.inflate(inflater, container, false);



        Log.d(TAG, "onCreateView: Inflating layout");

        // RecyclerView setup
        binding.vaccantSlotList.setLayoutManager(new LinearLayoutManager(requireContext()));
        slotAdapter = new VacantSlotsAdapter();
        binding.vaccantSlotList.setAdapter(slotAdapter);

        // ViewModel setup
        viewModel = new ViewModelProvider(this).get(VaccantSlotViewModel.class);

        viewModel.getUsedSlotsLiveData().observe(getViewLifecycleOwner(), slotModels -> {
            Log.d(TAG, "LiveData updated with " + slotModels.size() + " Vaccaslots");
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
        binding = null;
    }
}