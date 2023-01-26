package com.example.myfirstapp.database;

//import androidx.room.Delete;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
//import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface CustomerBookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertList(List<CustomerBook> customer_books);

}
