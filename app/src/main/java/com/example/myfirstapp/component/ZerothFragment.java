package com.example.myfirstapp.component;

import android.content.res.Resources;
import android.os.Bundle;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfirstapp.R;
import com.example.myfirstapp.communication.CartStatusCode;
import com.example.myfirstapp.communication.FragmentLabel;
import com.example.myfirstapp.databinding.FragmentZerothBinding;
import com.example.myfirstapp.model.RRViewModel;
import com.example.myfirstapp.model.Student;
import com.example.myfirstapp.model.UIStudent;

import java.util.ArrayList;

public class ZerothFragment extends Fragment {
    private FragmentZerothBinding binding;

    private RRViewModel rrvm;
    private LiveData<ArrayList<UIStudent>> allStudents;

    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private Resources res;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentZerothBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rrvm = new ViewModelProvider(requireActivity()).get(RRViewModel.class);

        rrvm.navigatedToFragment(FragmentLabel.CLASSROOM);

        allStudents = rrvm.getClassroom(requireActivity());
        recyclerView = view.findViewById(R.id.recycler_view_classroom);
        studentAdapter = new StudentAdapter(allStudents);

        recyclerView.setAdapter(studentAdapter);
        res = view.getResources();

        binding.orderButton.setOnClickListener(view1 -> NavHostFragment
                .findNavController(ZerothFragment.this)
                .navigate(R.id.action_ZerothFragment_to_SecondFragment));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
        private final LiveData<ArrayList<UIStudent>> studentList;

        public StudentAdapter(LiveData<ArrayList<UIStudent>> sl) {
            super();
            studentList = sl;
        }

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View adapterLayout = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.student_card, parent, false);
            return new StudentViewHolder(adapterLayout);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
            UIStudent uiStudent = studentList.getValue().get(position);
            Student student = uiStudent.student;
            holder.cartButton.setText(String.format(res.getString(R.string.student_button_label), student.studentID));
            CartStatusCode cartStatus = rrvm.getCartStatus(student.studentID);
            int tintColor = R.color.bedford_purple;
            if (cartStatus == CartStatusCode.PARTIAL) {
                tintColor = R.color.bedford_orange;
            } else if (cartStatus == CartStatusCode.FULL) {
                tintColor = R.color.bedford_teal;
            }
            holder.cartButton.setBackgroundTintList(ContextCompat.getColorStateList(holder.cartButton.getContext(), tintColor));
            holder.cartButton.setOnClickListener(view -> {
                rrvm.clickedStudent(student.studentID);
                NavHostFragment.findNavController(ZerothFragment.this)
                        .navigate(R.id.action_ZerothFragment_to_FirstFragment);
            });
        }

        @Override
        public int getItemCount() {
            return studentList.getValue().size();
        }

        class StudentViewHolder extends RecyclerView.ViewHolder {
            public Button cartButton;

            StudentViewHolder (View v) {
                super(v);
                cartButton = v.findViewById(R.id.student_card);
            }
        }

    }

    /*

    public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ItemViewHolder> {

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            UIBook uiBook = bookList.getValue().get(position);
            Consumer<Integer> removeAction = uiBook.removeAction;
            Book book = bookList.getValue().get(position).book;
            holder.titleView.setText(book.title);
            String price_string = Money.decimalize(book.price);
            holder.priceView.setText(price_string);
            holder.removeButton.setOnClickListener(view -> {
                int index = holder.getAdapterPosition();
                Log.i("ButtonMessages", "Removed item " + index + ".");
                FirstFragment.this.listAction = ListAction.REMOVE_BOOK;
                FirstFragment.this.actionIndex = index;
                removeAction.accept(index);
            });
        }

        @Override
        public int getItemCount() {
            return bookList.getValue().size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            //private View view;
            public TextView titleView;
            public TextView priceView;
            public Button removeButton;
            ItemViewHolder(View view) {
                super(view);
                titleView = view.findViewById(R.id.item_title);
                priceView = view.findViewById(R.id.item_price);
                removeButton = view.findViewById(R.id.remove_button);
            }
        }

    }

     */

}