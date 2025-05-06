package com.example.abhishek.financetracker.expensemanager.neopark.repository;

import com.example.abhishek.financetracker.expensemanager.neopark.model.SlotModel;
import com.google.firebase.database.ValueEventListener;

public class SlotRepository {
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();

    public void getAllSlots(String plotId, ValueEventListener listener) {
        firebaseHelper.getAllSlots(plotId, listener);
    }

    public void addOrUpdateSlot(String plotId, SlotModel slot) {
        firebaseHelper.addOrUpdateSlot(plotId, slot);
    }


}

