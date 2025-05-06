package com.example.abhishek.financetracker.expensemanager.neopark.model;

public class SettingsItemModel {
    private int iconResId;
    private String title;
    private String subtitle;

    public SettingsItemModel(int iconResId, String title, String subtitle) {
        this.iconResId = iconResId;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
