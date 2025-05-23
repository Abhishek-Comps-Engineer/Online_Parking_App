package com.example.abhishek.financetracker.expensemanager.neopark.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.abhishek.financetracker.expensemanager.neopark.user.User;
import com.example.abhishek.financetracker.expensemanager.neopark.interfaces.UserDao;

@Database(entities = {User.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();


    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "user_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

