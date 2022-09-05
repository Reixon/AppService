package com.myproject.appservice.controllers.viewMainBusiness;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.myproject.appservice.R;

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
        return inflater.inflate(R.layout.fragment_customers_view, container, false);
    }
}