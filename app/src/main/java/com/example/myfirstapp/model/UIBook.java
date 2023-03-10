package com.example.myfirstapp.model;

import java.util.ArrayList;
import java.util.function.Consumer;

public class UIBook {
    public final Book book;
    public final String student;
    public final Consumer<Integer> removeAction;
    public UIBook(Book b, String s, Consumer<Integer> c) {
        book = b;
        student = s;
        removeAction = c;
    }

    public static Long sum_prices(ArrayList<UIBook> bl) {
        long s = 0;
        for (UIBook b : bl) s += b.book.price;
        return s;
    }

    public String get_isbn() {
        return book.isbn;
    }

    public String get_student() { return student; }
}
