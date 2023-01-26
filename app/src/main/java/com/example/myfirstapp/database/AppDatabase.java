package com.example.myfirstapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Customer.class, CustomerBook.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase app_db;
    public static AppDatabase getInstance(Context context) {
        if (null == app_db) {
            app_db = buildDatabaseInstance(context);
        }
        return app_db;
    }

    private static AppDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "customer_records").allowMainThreadQueries().build();
    }
    public abstract CustomerDao customerDao();
    public abstract CustomerBookDao customerBookDao();
}

