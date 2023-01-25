package com.example.myfirstapp.component;

import android.content.res.Resources;
import android.os.Bundle;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.example.myfirstapp.text.Money;

import java.util.ArrayList;

public class ZerothFragment extends Fragment {
    private FragmentZerothBinding binding;

    private RRViewModel rrvm;
    private ArrayList<UIStudent> allStudents;

    private RecyclerView recyclerView;
    private TextView totalView;
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
        totalView = view.findViewById(R.id.textview_classroom_cart_total);
        studentAdapter = new StudentAdapter(allStudents);

        recyclerView.setAdapter(studentAdapter);
        res = view.getResources();

        totalView.setText(Money.decimalize(rrvm.getClassTotal()));

        binding.orderButton.setOnClickListener(view1 -> {
            rrvm.navigatedToFragment(FragmentLabel.DEMO_COMPLETE);
            NavHostFragment.findNavController(ZerothFragment.this)
                    .navigate(R.id.action_ZerothFragment_to_SecondFragment);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
        private final ArrayList<UIStudent> studentList;

        public StudentAdapter(ArrayList<UIStudent> sl) {
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
            UIStudent uiStudent = studentList.get(position);
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
            return studentList.size();
        }

        class StudentViewHolder extends RecyclerView.ViewHolder {
            public Button cartButton;

            StudentViewHolder (View v) {
                super(v);
                cartButton = v.findViewById(R.id.student_card);
            }
        }

    }

}