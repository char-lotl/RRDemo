package com.example.myfirstapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfirstapp.R;
import com.example.myfirstapp.model.Book;
import com.example.myfirstapp.model.UIBook;
import com.example.myfirstapp.text.Money;

import java.util.ArrayList;
import java.util.function.Consumer;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    // This inner class actually needs to get refactored as its own class.

    private final LiveData<ArrayList<UIBook>> bookList;
    private final Consumer<Integer> removalHint;

    public BookAdapter(LiveData<ArrayList<UIBook>> bl, Consumer<Integer> rh) {
        super();
        bookList = bl;
        removalHint = rh;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new BookViewHolder(adapterLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        UIBook uiBook = bookList.getValue().get(position);
        Consumer<Integer> removeAction = uiBook.removeAction;
        Book book = bookList.getValue().get(position).book;
        holder.titleView.setText(book.title);
        String price_string = Money.decimalize(book.price);
        holder.priceView.setText(price_string);
        holder.lexileView.setText(book.lexile);
        holder.levelView.setText(book.level);
        holder.draView.setText(book.dra);
        holder.removeButton.setOnClickListener(view -> {
            int index = holder.getAdapterPosition();
            Log.i("ButtonMessages", "Removed item " + index + ".");
            removalHint.accept(index); // Give the cart fragment a hint about the coming cart update.
            removeAction.accept(index); // Actually perform the cart update.
        });
    }

    @Override
    public int getItemCount() {
        return bookList.getValue().size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView priceView;
        public Button removeButton;
        public TextView lexileView;
        public TextView levelView;
        public TextView draView;

        BookViewHolder(View view) {
            super(view);
            titleView = view.findViewById(R.id.item_title);
            priceView = view.findViewById(R.id.item_price);
            removeButton = view.findViewById(R.id.remove_button);
            lexileView = view.findViewById(R.id.lexile_score);
            levelView = view.findViewById(R.id.level_score);
            draView = view.findViewById(R.id.dra_score);
        }
    }

}
