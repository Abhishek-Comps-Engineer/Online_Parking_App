package com.example.abhishek.financetracker.expensemanager.neopark.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.model.SlotModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservedSlotAdapter extends RecyclerView.Adapter<ReservedSlotAdapter.ReservedViewHolder> {

    private List<SlotModel> currentList = new ArrayList<>();
    public enum Mode { TODAY, UPCOMING, PAST }
    private Mode currentMode = Mode.TODAY;

    public void setSlotList(List<SlotModel> allSlots) {
        this.currentList = filterTodaySlots(allSlots);
        currentMode = Mode.TODAY;
        notifyDataSetChanged();
    }

    public void setSlotUpcomingList(List<SlotModel> allSlots) {
        this.currentList = filterUpcomingSlots(allSlots);
        currentMode = Mode.UPCOMING;
        notifyDataSetChanged();
    }

    public void setSlotPastList(List<SlotModel> allSlots) {
        this.currentList = filterPastSlots(allSlots);
        currentMode = Mode.PAST;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReservedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
        return new ReservedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservedViewHolder holder, int position) {
        SlotModel slot = currentList.get(position);

        holder.slotNameTextView.setText(slot.getSlotName());
        holder.priceTextView.setText("₹" + slot.getPrice() + "/hr");
        holder.vehicleTypeTextView.setText("Vehicle: " + slot.getVehicleType());

        String time = "Time: " + slot.getAvailableTime();
        String date = slot.getDate() != null ? slot.getDate() : "";
        holder.timeTextView.setText(time + " , Date: " + date);

        holder.statusButton.setText("Booked");
        holder.statusButton.setEnabled(false);
        holder.statusButton.setBackgroundTintList(holder.statusButton.getContext()
                .getResources().getColorStateList(R.color.green));
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    static class ReservedViewHolder extends RecyclerView.ViewHolder {
        TextView slotNameTextView, priceTextView, vehicleTypeTextView, timeTextView;
        MaterialButton statusButton;

        ReservedViewHolder(View itemView) {
            super(itemView);
            slotNameTextView = itemView.findViewById(R.id.slotNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            vehicleTypeTextView = itemView.findViewById(R.id.vehicleTypeTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            statusButton = itemView.findViewById(R.id.statusButton);
        }
    }



    private List<SlotModel> filterUpcomingSlots(List<SlotModel> slots) {
        List<SlotModel> filtered = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        SimpleDateFormat standardFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            String todayFormatted = standardFormat.format(new Date());
            Date today = standardFormat.parse(todayFormatted); // Normalized today's date

            for (SlotModel slot : slots) {
                if (slot.isBooked() && slot.getDate() != null && !slot.getDate().trim().isEmpty()) {
                    try {
                        Date slotDate = inputFormat.parse(slot.getDate().trim());
                        String slotFormatted = standardFormat.format(slotDate);
                        Date normalizedSlotDate = standardFormat.parse(slotFormatted); // Normalize slot date

                        if (normalizedSlotDate.after(today)) {
                            filtered.add(slot);
                            Log.d("ReservedSlotAdapter", "Upcoming Slot: " + slot.getSlotName() + " on " + slotFormatted);
                        }
                    } catch (Exception e) {
                        Log.e("ReservedSlotAdapter", "Date parsing failed: " + slot.getSlotName(), e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ReservedSlotAdapter", "Failed to parse today’s date", e);
        }

        return filtered;
    }


    private List<SlotModel> filterPastSlots(List<SlotModel> slots) {
        List<SlotModel> filtered = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        SimpleDateFormat standardFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            String todayFormatted = standardFormat.format(new Date());
            Date today = standardFormat.parse(todayFormatted); // Normalized today's date

            for (SlotModel slot : slots) {
                if (slot.isBooked() && slot.getDate() != null && !slot.getDate().trim().isEmpty()) {
                    try {
                        Date slotDate = inputFormat.parse(slot.getDate().trim());
                        String slotFormatted = standardFormat.format(slotDate);
                        Date normalizedSlotDate = standardFormat.parse(slotFormatted); // Normalize slot date

                        if (normalizedSlotDate.before(today)) {
                            filtered.add(slot);
                            Log.d("ReservedSlotAdapter", "Past Slot: " + slot.getSlotName() + " on " + slotFormatted);
                        }
                    } catch (Exception e) {
                        Log.e("ReservedSlotAdapter", "Date parsing failed: " + slot.getSlotName(), e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ReservedSlotAdapter", "Failed to parse today’s date", e);
        }

        return filtered;
    }


    private List<SlotModel> filterTodaySlots(List<SlotModel> slots) {
        List<SlotModel> filtered = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()); // Accepts 4/5/2025
        SimpleDateFormat standardFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // For logging
        String todayFormatted = standardFormat.format(new Date());

        for (SlotModel slot : slots) {
            if (slot.isBooked() && slot.getDate() != null && !slot.getDate().trim().isEmpty()) {
                try {
                    Date slotDate = inputFormat.parse(slot.getDate().trim());
                    String slotFormatted = standardFormat.format(slotDate);

                    if (todayFormatted.equals(slotFormatted)) {
                        filtered.add(slot);
                        Log.d("ReservedSlotAdapter", "Slot matched: " + slot.getSlotName() + " on " + slotFormatted);
                    } else {
                        Log.d("ReservedSlotAdapter", "Slot date mismatch: " + slotFormatted + " != " + todayFormatted);
                    }
                } catch (Exception e) {
                    Log.e("ReservedSlotAdapter", "Date parsing failed for slot: " + slot.getSlotName()
                            + " with date: " + slot.getDate(), e);
                }
            } else {
                Log.d("ReservedSlotAdapter", "Skipping slot due to invalid date or not booked: " + slot.getSlotName());
            }
        }

        return filtered;
    }

}
