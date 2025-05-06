package com.example.abhishek.onlineparking.adminneopark.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

public class PlotModel implements Parcelable {
    private String plotId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int twoWheelerSlots;
    private int fourWheelerSlots;
    private double twoPrice;
    private double fourPrice;
    private int totalSlots;
    private String twoTime;
    private String fourTime;
    private String date;
    private Map<String,SlotModel> slots;
    int stability;

    public static final Creator<PlotModel> CREATOR = new Creator<PlotModel>() {
        @Override
        public PlotModel createFromParcel(Parcel in) {
            return new PlotModel(in);
        }

        @Override
        public PlotModel[] newArray(int size) {
            return new PlotModel[size];
        }
    };

    public void setStability(int stability) {
        this.stability = stability;
    }

    // Default constructor required for Firebase
    public PlotModel() {
    }

    // Constructor with slots
    public PlotModel(String plotId, String name, String address, double lat, double lon,int twoWheelerSlots, String twoTime, double twoPrice,  int fourWheelerSlots,
                     String fourTime, double fourPrice, int totalSlots, String date) {
        this.plotId = plotId;
        this.name = name;
        this.address = address;
        this.latitude = lat;
        this.longitude = lon;
        this.slots = slots;
        this.twoTime = twoTime;
        this.twoPrice = twoPrice;
        this.fourPrice = fourPrice;
        this.fourTime = fourTime;
        this.totalSlots = totalSlots;
        this.twoWheelerSlots = twoWheelerSlots;
        this.fourWheelerSlots = fourWheelerSlots;
        this.date = date;
    }

    // Constructor without slots
//    public PlotModel(String plotId, String name, String address, double lat, double lon,
//                     int totalSlots, int twoWheelerSlots, int fourWheelerSlots) {
//        this(plotId, name, address, lat, lon, null, totalSlots, twoWheelerSlots, fourWheelerSlots);
//    }

    public String getPlotId() {
        return plotId;
    }

    public double getTwoPrice() {
        return twoPrice;
    }

    public void setTwoPrice(double twoPrice) {
        this.twoPrice = twoPrice;
    }

    public double getFourPrice() {
        return fourPrice;
    }

    public void setFourPrice(double fourPrice) {
        this.fourPrice = fourPrice;
    }

    public String getTwoTime() {
        return twoTime;
    }

    public void setTwoTime(String twoTime) {
        this.twoTime = twoTime;
    }

    public String getFourTime() {
        return fourTime;
    }

    public void setFourTime(String fourTime) {
        this.fourTime = fourTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlotId(String plotId) {
        this.plotId = plotId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getTwoWheelerSlots() {
        return twoWheelerSlots;
    }

    public void setTwoWheelerSlots(int twoWheelerSlots) {
        this.twoWheelerSlots = twoWheelerSlots;
    }

    public int getFourWheelerSlots() {
        return fourWheelerSlots;
    }

    public void setFourWheelerSlots(int fourWheelerSlots) {
        this.fourWheelerSlots = fourWheelerSlots;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }


//    public void setStability(String stability) {
//        this.stability = stability;
//    }

    public Map<String,SlotModel> getSlots() {
        return slots;
    }

    public void setSlots(Map<String,SlotModel> slots) {
        this.slots = slots;
    }

    protected PlotModel(Parcel in) {
        plotId = in.readString();
        name = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        twoWheelerSlots = in.readInt();
        fourWheelerSlots = in.readInt();
        totalSlots = in.readInt();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(plotId);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(twoWheelerSlots);
        dest.writeInt(fourWheelerSlots);
        dest.writeDouble(twoPrice);
        dest.writeDouble(fourPrice);
        dest.writeInt(totalSlots);
        dest.writeString(twoTime);
        dest.writeString(fourTime);
        dest.writeString(date);
        dest.writeInt(stability);
    }
}