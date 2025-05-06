package com.example.abhishek.onlineparking.adminneopark.adapter;

import static android.view.View.GONE;
import static android.view.View.TEXT_ALIGNMENT_TEXT_END;
import static android.view.View.TEXT_ALIGNMENT_VIEW_END;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abhishek.onlineparking.adminneopark.R;
import com.example.abhishek.onlineparking.adminneopark.models.SlotModel;

import java.util.ArrayList;
import java.util.List;

public  class UsedSlotAdapter extends RecyclerView.Adapter<UsedSlotAdapter.UsedSlotViewHolder> {


    String TAG   =  "UsedSlotAdapter";
    private final List<SlotModel> slotList = new ArrayList<>();

    @NonNull
    @Override
    public UsedSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
        return new UsedSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsedSlotViewHolder holder, int position) {
        SlotModel slot = slotList.get(position);
        Context context = holder.itemView.getContext();

        if (slotList.size() > 0) {
            holder.name.setText(slot.getSlotName());
            holder.vehicleType.setText("Vehicle: " + slot.getVehicleType());
            holder.time.setText("Time: " + slot.getAvailableTime() + " | Date: " + slot.getDate());
            holder.price.setText("₹" + slot.getPrice());
            if (slot.isBooked()) {
                holder.statusText.setText("Full");
                holder.statusText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red));
            } else {
                holder.statusText.setText("Available");
                holder.statusText.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            }
            Log.d("UsedSlotAdapter", "No slots to display");
        }
        holder.deleteButton.setVisibility(GONE);
//        holder.editButton.setVisibility(GONE);
        holder.statusText.setGravity(TEXT_ALIGNMENT_VIEW_END);

    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public void setSlotData(List<SlotModel> newSlots) {
        Log.d(TAG, "setSlotData: Updating slot list with " + newSlots.size() + " items");
        slotList.clear();
        slotList.addAll(newSlots);
        notifyDataSetChanged();
    }

    static class UsedSlotViewHolder extends RecyclerView.ViewHolder {
        TextView name, vehicleType, time, price , noSlots;
        Button editButton, deleteButton, statusText;

        public UsedSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.slotNameTextView);
            vehicleType = itemView.findViewById(R.id.vehicleTypeTextView);
            time = itemView.findViewById(R.id.timeTextView);
            price = itemView.findViewById(R.id.priceTextView);
//            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            statusText = itemView.findViewById(R.id.statusButton);
        }
    }
}
