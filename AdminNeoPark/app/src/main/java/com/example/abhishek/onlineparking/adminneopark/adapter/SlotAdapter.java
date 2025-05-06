package com.example.abhishek.onlineparking.adminneopark.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abhishek.onlineparking.adminneopark.R;
import com.example.abhishek.onlineparking.adminneopark.models.SlotModel;

import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    private final List<SlotModel> slotList;
    private final SlotClickListener listener;

    public SlotAdapter(List<SlotModel> slotList, SlotClickListener listener) {
        this.slotList = slotList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        SlotModel slot = slotList.get(position);
        Context context = holder.itemView.getContext();
        String priceStr = String.valueOf(slot.getPrice());            // convert back to String


        // Bind data
        holder.name.setText(slot.getSlotName().toString());
        holder.vehicleType.setText("Vehicle: " + slot.getVehicleType().toString());
        holder.time.setText("Time: " + slot.getAvailableTime().toString()+ " And Date: "+slot.getDate());

        holder.price.setText("₹" + priceStr);

        // Set status button text and color
        if (slot.isBooked()) {
            holder.statusText.setText("Full");
            holder.statusText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red));
        } else {
            holder.statusText.setText("Available");
            holder.statusText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
        }

        // Click listeners
//        holder.editButton.setOnClickListener(v -> listener.onEdit(slot, position));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(slot, position));
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView name, vehicleType, time, price;
        Button editButton, deleteButton, statusText;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.slotNameTextView);
            vehicleType = itemView.findViewById(R.id.vehicleTypeTextView);
            time = itemView.findViewById(R.id.timeTextView);
            price = itemView.findViewById(R.id.priceTextView);
//            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            statusText = itemView.findViewById(R.id.statusButton); // Make sure this matches your XML
        }
    }

    // Interface for click events
    public interface SlotClickListener {
//        void onEdit(SlotModel slot, int position);
        void onDelete(SlotModel slot, int position);
    }

    public void updateList(List<SlotModel> newSlots) {
        Log.d("SlotAdapter", "updateList() called with " + newSlots.size() + " slots");

        slotList.clear();
        slotList.addAll(newSlots);

        for (SlotModel slot : newSlots) {
            Log.d("SlotAdapter", "Slot ID: " + slot.getSlotId() + ", Name: " + slot.getSlotName());
        }

        notifyDataSetChanged();
        Log.d("SlotAdapter", "Adapter notified of data change");
    }

}
