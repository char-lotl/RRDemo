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
        String[] book_lexiles = res.getStringArray(R.array.book_lexile);
        String[] book_levels = res.getStringArray(R.array.book_level);
        String[] book_dras = res.getStringArray(R.array.book_dra);
        int num_titles = book_isbns.length;
        for (int i = 0; i < num_titles; i++) {
            allBooks.put(book_isbns[i], new Book(book_isbns[i], book_titles[i], book_prices[i], book_lexiles[i], book_levels[i], book_dras[i]));
            // The ISBN won't be displayed, but it needs to be part of the book data object so that
            // when we're saving carts, we can save books by their ISBN alone.
        }
    }

    public static HashMap<String, Book> getAllBooks(Context c) {
        if (allBooks == null) {
            loadBooks(c);
        } // Caching: only needs to be loaded once.
        return allBooks;
    }

}
