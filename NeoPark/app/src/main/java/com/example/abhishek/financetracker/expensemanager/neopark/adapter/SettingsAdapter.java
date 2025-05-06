package com.example.abhishek.financetracker.expensemanager.neopark.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.model.SettingsItemModel;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {

    private List<SettingsItemModel> settingsList;

    public SettingsAdapter(List<SettingsItemModel> settingsList) {
        this.settingsList = settingsList;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_setting, parent, false);
        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        SettingsItemModel item = settingsList.get(position);
        holder.imageViewIcon.setImageResource(item.getIconResId());
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewSubtitle.setText(item.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    public static class SettingsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewTitle, textViewSubtitle;

        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewSubtitle = itemView.findViewById(R.id.textViewSubtitle);
        }
    }
}
