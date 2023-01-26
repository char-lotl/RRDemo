package com.example.myfirstapp.model;

public class Book {
    public String isbn;
    public String title;
    public Long price;
    public String lexile;
    public String level;
    public String dra;
    public Book(String i, String t, String p, String lex, String lev, String dra) {
        isbn = i;
        title = t;
        lexile = lex;
        level = lev;
        this.dra = dra;
        long discounted = Long.parseLong(p) * 4 / 5; // 20% discount to all titles
        if (discounted < 500) price = 500L; // $5 floor
        else price = discounted;
    }
}
