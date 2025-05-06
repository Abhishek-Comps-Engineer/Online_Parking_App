package com.example.abhishek.financetracker.expensemanager.neopark.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.abhishek.financetracker.expensemanager.neopark.ui.ActiveReservedLotFragment;
import com.example.abhishek.financetracker.expensemanager.neopark.ui.HistoryReservedLotFragment;
import com.example.abhishek.financetracker.expensemanager.neopark.ui.UpcomingReservedLotFragment;

public class ReservedLotAdapter extends FragmentStateAdapter {


    public ReservedLotAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ReservedLotAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public ReservedLotAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0 ){
            return new ActiveReservedLotFragment();
        }else if (position == 1 ){
            return new UpcomingReservedLotFragment();
        }else{
            return new HistoryReservedLotFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
