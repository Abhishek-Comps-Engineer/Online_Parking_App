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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryReservedLotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryReservedLotFragment extends Fragment {


    String TAG = "HistoryReservedLotFragment";
    private RecyclerView recyclerViewActive;
    private ReservedSlotAdapter slotAdapter;
    private ReservedViewModel reservedViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryReservedLotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryReservedLotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryReservedLotFragment newInstance(String param1, String param2) {
        HistoryReservedLotFragment fragment = new HistoryReservedLotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_history_reserved_lot, container, false);


        recyclerViewActive = v.findViewById(R.id.pastSlotList);

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
                slotAdapter.setSlotPastList(bookedSlots);
                Log.d(TAG, "Calling selSlotList of adapter. Total booked slots: " + bookedSlots.size());
            }
        });

        reservedViewModel.loadUsedSlots();
        Log.d(TAG, "Calling loadUsedSlot of ReservedView Model Class");


        return v;
    }
}