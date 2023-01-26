package com.example.myfirstapp.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfirstapp.R;
import com.example.myfirstapp.communication.FragmentLabel;
import com.example.myfirstapp.database.Customer;
import com.example.myfirstapp.databinding.FragmentSecondBinding;
import com.example.myfirstapp.model.RRViewModel;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private RRViewModel rrvm;
    private EditText name;
    private EditText email;
    private EditText phone;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        //rrvm.

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rrvm = new ViewModelProvider(requireActivity()).get(RRViewModel.class);
        name = view.findViewById(R.id.editTextPersonName);
        email = view.findViewById(R.id.editTextEmail);
        phone = view.findViewById(R.id.editTextPhone);

        binding.buttonSecond.setOnClickListener(v -> {
            Customer c = new Customer(
                    name.getText().toString(),
                    email.getText().toString(),
                    phone.getText().toString()
            );
            rrvm.storeCustomerSession(c, this.requireContext());
            rrvm.resetStudentCarts();
            rrvm.navigatedToFragment(FragmentLabel.CLASSROOM);
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_ZerothFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}