package com.example.myfirstapp.model;

import java.util.function.Consumer;

public class UIBook {
    public final Book book;
    public final Consumer<Integer> removeAction;
    public UIBook(Book b, Consumer<Integer> c) {
        book = b;
        removeAction = c;
    }
}
