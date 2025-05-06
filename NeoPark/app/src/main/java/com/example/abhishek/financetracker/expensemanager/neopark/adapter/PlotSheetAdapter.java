package com.example.abhishek.financetracker.expensemanager.neopark.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.model.SlotModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class PlotSheetAdapter extends RecyclerView.Adapter<PlotSheetAdapter.PlotSheetViewHolder> {

    private List<SlotModel> slotList = new ArrayList<>();
    private OnBookingClickListener bookingClickListener;

    public void setSlotList(List<SlotModel> slots) {
        Log.d("PlotSheetAdapter", "Setting slot list. Size: " + (slots != null ? slots.size() : 0));
        this.slotList = slots;
        notifyDataSetChanged();
    }


    public interface OnBookingClickListener {
        void onBookingClick(SlotModel slot);
    }

    // Constructor
    public PlotSheetAdapter(OnBookingClickListener listener) {
        this.bookingClickListener = listener;
    }
    @NonNull
    @Override
    public PlotSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slot, parent, false);
        return new PlotSheetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlotSheetViewHolder holder, int position) {
        SlotModel slot = slotList.get(position);
        Log.d("PlotSheetAdapter", "Binding slot: " + slot.getSlotName() + ", Status: " + slot.isBooked());
        holder.slotNameTextView.setText(slot.getSlotName());
        holder.vehicleTypeTextView.setText("Vehicle: " + slot.getVehicleType());
        holder.timeTextView.setText("Time: " + slot.getAvailableTime() + " , Date: " + slot.getDate());
        holder.priceTextView.setText(String.valueOf(slot.getPrice()));

        if (!slot.isBooked()) {
            holder.statusButton.setText("Click to Book");
            holder.statusButton.setEnabled(true);
            holder.statusButton.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.green));

            holder.statusButton.setOnClickListener(v -> {
                // Trigger the booking click listener
                if (bookingClickListener != null) {
                    bookingClickListener.onBookingClick(slot);
                }
            });
        } else {
            holder.statusButton.setText("Booked");
            holder.statusButton.setEnabled(false);
            holder.statusButton.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.gray));
        }
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    static class PlotSheetViewHolder extends RecyclerView.ViewHolder {
        TextView slotNameTextView, priceTextView, vehicleTypeTextView, timeTextView;
        MaterialButton statusButton;

        public PlotSheetViewHolder(@NonNull View itemView) {
            super(itemView);
            slotNameTextView = itemView.findViewById(R.id.slotNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            vehicleTypeTextView = itemView.findViewById(R.id.vehicleTypeTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            statusButton = itemView.findViewById(R.id.statusButton);
        }
    }
}

