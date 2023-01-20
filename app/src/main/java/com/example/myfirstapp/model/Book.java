package com.example.myfirstapp.model;

public class Book {
    public String title;
    public Long price;
    public Book(String t, String p) {
        title = t;
        long discounted = Long.parseLong(p) * 4 / 5; // 20% discount to all titles
        if (discounted < 500) price = 500L; // $5 floor
        else price = discounted;
    }
}
