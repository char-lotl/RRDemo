package com.example.myfirstapp.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfirstapp.R;
import com.example.myfirstapp.adapter.BookAdapter;
import com.example.myfirstapp.communication.FragmentLabel;
import com.example.myfirstapp.databinding.FragmentFirstBinding;
import com.example.myfirstapp.model.RRViewModel;
import com.example.myfirstapp.model.UIBook;
import com.example.myfirstapp.text.Money;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    enum ListAction {
        NONE,
        REMOVE_BOOK
    }
    // When a book is scanned or a delete button is pressed, the cart
    // will be updated -- and FirstFragment is only allowed to listen to the LiveData
    // object for the actual changes that occur, due to the Single Source of Truth model
    // the app is built around. However, delete buttons send "hints" to FirstFragment
    // when clicked, in the form of change-of-state (listAction and actionIndex), so that
    // it can quickly ascertain (when the cart contents change) which item was deleted
    // (so that it can provide the correct index to RecyclerView#notifyItemRemoved)
    // without having to employ a diff algorithm.

    private FragmentFirstBinding binding;

    private RecyclerView recyclerView;
    private TextView numInCartView;
    private TextView priceSumView;
    private BookAdapter bookAdapter;

    private RRViewModel rrvm;
    private LiveData<ArrayList<UIBook>> cartData;
    // The LiveData object we set up an observer for, to listen for
    // changes to the cart data as they occur, originating from
    // RRViewModel, which acts as this app's Single Source of Truth.

    private ListAction listAction; // holds the type of a pending list action
    private int actionIndex; // holds the index of a pending list action
    // currently this is only ever deletion, but could expand to other things in the future,
    // like reordering.
    private int currentCartLength;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rrvm = new ViewModelProvider(requireActivity()).get(RRViewModel.class);
        listAction = ListAction.NONE;

        rrvm.navigatedToFragment(FragmentLabel.SHOPPING_CART);
        // Alert the ViewModel that we've arrived at the Cart screen.

        cartData = rrvm.getCart();
        currentCartLength = cartData.getValue().size();

        // Set up behavior that occurs whenever the cart data changes (item deleted or scanned).
        cartData.observe(getViewLifecycleOwner(), cart -> {
            if (listAction == ListAction.REMOVE_BOOK) {
                remove_book(actionIndex);
                listAction = ListAction.NONE;
            } else if (cart.size() == currentCartLength + 1) { // Compare to last known cart size.
                book_added(currentCartLength);
            } else {
                bookAdapter.notifyDataSetChanged();
                updateSummary();
                // Probably this case shouldn't fire, but I didn't want to have to think too hard
                // about UI edge-cases.
            }
            currentCartLength = cart.size();
        });

        numInCartView = view.findViewById(R.id.textview_number_in_cart);
        priceSumView = view.findViewById(R.id.textview_price_sum);
        recyclerView = view.findViewById(R.id.recycler_view_cart);

        bookAdapter = new BookAdapter(cartData, this::give_removal_hint);
        recyclerView.setAdapter(bookAdapter);

        binding.finishedButton.setOnClickListener(view1 ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_ZerothFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void book_added(int endOfCartIndex) {
        bookAdapter.notifyItemInserted(endOfCartIndex);
        updateSummary();
    }

    private void remove_book(int index) {
        bookAdapter.notifyItemRemoved(index);
        updateSummary();
    }

    private void give_removal_hint(int index) {
        listAction = ListAction.REMOVE_BOOK;
        actionIndex = index;
    }

    private void updateSummary() { // Updates the text displayed in the cart summary bar.
        numInCartView.setText(String.valueOf(cartData.getValue().size()));
        priceSumView.setText(Money.decimalize(UIBook.sum_prices(cartData.getValue())));
    }

}