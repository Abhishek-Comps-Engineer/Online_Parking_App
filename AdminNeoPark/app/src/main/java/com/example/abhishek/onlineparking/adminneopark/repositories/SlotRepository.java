package com.example.abhishek.onlineparking.adminneopark.repositories;

import com.example.abhishek.onlineparking.adminneopark.firebases.FirebaseHelper;
import com.example.abhishek.onlineparking.adminneopark.models.SlotModel;
import com.google.firebase.database.ValueEventListener;

public class SlotRepository {
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();

    public void getAllSlots(String plotId, ValueEventListener listener) {
        firebaseHelper.getAllSlots(plotId, listener);
    }

    public void addOrUpdateSlot(String plotId, SlotModel slot) {
        firebaseHelper.addOrUpdateSlot(plotId, slot);
    }

    public void deleteSlot(String plotId, String slotId) {
        firebaseHelper.deleteSlot(plotId, slotId);
    }
}

