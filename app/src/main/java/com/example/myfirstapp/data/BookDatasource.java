package com.example.myfirstapp.data;

import com.example.myfirstapp.model.Book;
import com.example.myfirstapp.R;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashMap;

public class BookDatasource {

    private static HashMap<String, Book> allBooks;

    public static void loadBooks(Context c) {
        allBooks = new HashMap<>();
        Resources res = c.getResources();
        String[] book_isbns = res.getStringArray(R.array.book_isbn);
        String[] book_titles = res.getStringArray(R.array.book_title);
        String[] book_prices = res.getStringArray(R.array.book_price);
        int num_titles = book_isbns.length;
        for (int i = 0; i < num_titles; i++) {
            allBooks.put(book_isbns[i], new Book(book_titles[i], book_prices[i]));
        }
    }

    public static HashMap<String, Book> getAllBooks(Context c) {
        if (allBooks == null) {
            loadBooks(c);
        }
        return allBooks;
    }

    public static HashMap<String, Book> getAllBooks() {
        return allBooks;
    }
}
