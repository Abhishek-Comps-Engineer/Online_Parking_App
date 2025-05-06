package com.example.abhishek.onlineparking.adminneopark.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abhishek.onlineparking.adminneopark.R;
import com.example.abhishek.onlineparking.adminneopark.models.PlotModel;
import com.example.abhishek.onlineparking.adminneopark.ui.SlotViewActivity;

import java.util.ArrayList;
import java.util.List;

public class PlotAdapter extends RecyclerView.Adapter<PlotAdapter.ViewHolder> {

    private List<PlotModel> itemList = new ArrayList<>();

    private List<PlotModel> fullList = new ArrayList<>();

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onViewClick(PlotModel model);
        void onEditClick(PlotModel model);
        void onDeleteClick(PlotModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<PlotModel> newList) {
        itemList.clear();
        itemList.addAll(newList);
        fullList.clear();
        fullList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plot, parent, false);
        return new ViewHolder(view);
    }

    public void filter(String query) {
        itemList.clear();
        if (query == null || query.trim().isEmpty()) {
            itemList.addAll(fullList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (PlotModel model : fullList) {
                if (model.getName().toLowerCase().contains(lowerCaseQuery) ||
                        model.getAddress().toLowerCase().contains(lowerCaseQuery)) {
                    itemList.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull PlotAdapter.ViewHolder holder, int position) {
        PlotModel model = itemList.get(position);

        holder.tvPlotName.setText(model.getName().toString());
        holder.tvAddress.setText(model.getAddress().toString());
        holder.tvTotalSlots.setText("Total Slots: " + model.getTotalSlots());

        holder.btnView.setOnClickListener(v -> {
            if (listener != null) listener.onViewClick(model);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(model);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(model);
        });


        holder.itemView.setOnClickListener(v     -> {
            Intent intent = new Intent(holder.itemView.getContext(), SlotViewActivity.class);
            intent.putExtra("plotId", model.getPlotId());
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlotName, tvAddress, tvTotalSlots;
        Button btnView, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPlotName = itemView.findViewById(R.id.tvPlotName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvTotalSlots = itemView.findViewById(R.id.tvTotalSlots);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
