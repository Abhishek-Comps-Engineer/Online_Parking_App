package com.example.abhishek.financetracker.expensemanager.neopark.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SlotModel implements Parcelable {
    private String slotId;
    private String slotName;
    private String vehicleType;
    private String availableTime;
    private double price;
    private String status;
    private int stability;
    private boolean isBooked;
    String date;


    // Required empty constructor for Firebase
    public SlotModel() {
    }

    public SlotModel(String slotId, String slotName, String vehicleType, String availableTime, double price, boolean isBooked,String date) {
        this.slotId = slotId;
        this.slotName = slotName;
        this.vehicleType = vehicleType;
        this.availableTime = availableTime;
        this.price = price;
        this.isBooked = isBooked;
        this.date = date ;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
//    public String getStability() {
//        return stability;
//    }

    public void setStability(int stability) {
        this.stability = stability;
    }

    protected SlotModel(Parcel in) {
        slotId = in.readString();
        slotName = in.readString();
        vehicleType = in.readString();
        availableTime = in.readString();
        price = in.readDouble();
        status = in.readString();
        isBooked = in.readByte() != 0;
    }

    public static final Creator<SlotModel> CREATOR = new Creator<SlotModel>() {
        @Override
        public SlotModel createFromParcel(Parcel in) {
            return new SlotModel(in);
        }

        @Override
        public SlotModel[] newArray(int size) {
            return new SlotModel[size];
        }
    };

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(slotId);
        dest.writeString(slotName);
        dest.writeString(vehicleType);
        dest.writeString(availableTime);
        dest.writeDouble(price);
        dest.writeString(status);
        dest.writeByte((byte) (isBooked ? 1 : 0));
    }
}