package com.example.myfirstapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CustomerBook {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "isbn")
    public String isbn;

    public CustomerBook(Customer c, String i) {
        uid = c.uid;
        isbn = i;
    }

    public CustomerBook(int uid, String isbn) {
        this.uid = uid;
        this.isbn = isbn;
    }

}

