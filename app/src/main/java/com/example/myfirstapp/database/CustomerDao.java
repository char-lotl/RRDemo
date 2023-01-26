package com.example.myfirstapp.database;

import androidx.room.Dao;
//import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import io.reactivex.Completable;
//import androidx.room.Query;

@Dao
public interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertCustomer(Customer customer);

}
