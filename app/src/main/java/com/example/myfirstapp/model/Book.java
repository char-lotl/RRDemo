package com.example.myfirstapp.model;

public class Book {
    public String title;
    public Long price;
    public Book(String t, String p) {
        title = t;
        price = Long.parseLong(p);
    }
}
