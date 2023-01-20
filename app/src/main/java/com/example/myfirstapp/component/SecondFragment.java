package com.example.myfirstapp.component;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfirstapp.R;
import com.example.myfirstapp.databinding.FragmentSecondBinding;
import com.example.myfirstapp.model.RRViewModel;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private RRViewModel rrvm;

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

        binding.buttonSecond.setOnClickListener(v -> {
            rrvm.resetStudentCarts();
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