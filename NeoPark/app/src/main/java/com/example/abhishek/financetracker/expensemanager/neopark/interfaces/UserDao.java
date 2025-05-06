package com.example.abhishek.financetracker.expensemanager.neopark.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.abhishek.financetracker.expensemanager.neopark.user.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE UserId = :userId")
    LiveData<User> getUserById(String userId);
}

