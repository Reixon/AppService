package com.myproject.appservice.controllers.viewMainBusiness;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.myproject.appservice.databinding.FragmentCustomersViewBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerView} factory method to
 * create an instance of this fragment.
 */
public class CustomerView extends Fragment {

    public CustomerView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentCustomersViewBinding binding = FragmentCustomersViewBinding.inflate(inflater, container, false);
        TextView txt = binding.txtCount;
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                txt.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                txt.setText("done!");
            }
        }.start();
        return binding.getRoot();
    }
}