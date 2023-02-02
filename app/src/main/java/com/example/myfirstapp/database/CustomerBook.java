package com.example.myfirstapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CustomerBook {
    @PrimaryKey
    public String entry_id;

    @ColumnInfo(name = "user")
    public int uid;

    @ColumnInfo(name = "isbn")
    public String isbn;

    @ColumnInfo(name = "student_id")
    public String student_id;

    public CustomerBook(Customer c, String i, String s) {
        entry_id = c.uid + i;
        uid = c.uid;
        isbn = i;
        student_id = s;
    }

    public CustomerBook(int uid, String isbn, String student_id) {
        this.entry_id = uid + isbn;
        this.uid = uid;
        this.isbn = isbn;
        this.student_id = student_id;
    }

}

