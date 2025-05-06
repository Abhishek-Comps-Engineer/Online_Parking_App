package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.adapter.ReservedSlotAdapter;
import com.example.abhishek.financetracker.expensemanager.neopark.model.SlotModel;
import com.example.abhishek.financetracker.expensemanager.neopark.viewmodel.ReservedViewModel;
import java.util.ArrayList;
import java.util.List;

public class ActiveReservedLotFragment extends Fragment {

    String TAG = "ActiveReservedLotFragment";
    private RecyclerView recyclerViewActive;
    private ReservedSlotAdapter slotAdapter;
    private ReservedViewModel reservedViewModel;

    public ActiveReservedLotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active_reserved_lot, container, false);
        recyclerViewActive = view.findViewById(R.id.activeSlotList);

        recyclerViewActive.setLayoutManager(new LinearLayoutManager(getContext()));
        slotAdapter = new ReservedSlotAdapter(); // `true` to show only booked
        recyclerViewActive.setAdapter(slotAdapter);

        reservedViewModel = new ViewModelProvider(this).get(ReservedViewModel.class);
        reservedViewModel.getUsedSlotsLiveData().observe(getViewLifecycleOwner(), new Observer<List<SlotModel>>() {
            @Override
            public void onChanged(List<SlotModel> slotModels) {
                List<SlotModel> bookedSlots = new ArrayList<>();
                for (SlotModel slot : slotModels) {
                    if (slot.isBooked()) {
                        Log.d(TAG, "All plots processed. Total booked slots: " + slotModels.size());
                        bookedSlots.add(slot);
                    }
                }
                slotAdapter.setSlotList(bookedSlots);
                Log.d(TAG, "Calling selSlotList of adapter. Total booked slots: " + bookedSlots.size());
            }
        });

        reservedViewModel.loadUsedSlots();
        Log.d(TAG, "Calling loadUsedSlot of ReservedView Model Class");

        return view;
    }
}
