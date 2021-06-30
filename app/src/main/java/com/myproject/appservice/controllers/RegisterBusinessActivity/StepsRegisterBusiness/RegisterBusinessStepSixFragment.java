package com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.myproject.appservice.R;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.adapters.AdapterListCategories;
import com.myproject.appservice.Common;
import com.myproject.appservice.databinding.FragmentStepSixBinding;

import java.util.ArrayList;

import javax.annotation.Nullable;


public class RegisterBusinessStepSixFragment extends Fragment {

    private final String TAG = "STEP6-REGISTER";
    private LocalBroadcastManager localBroadcastManager;
    private FragmentStepSixBinding binding;
    private ListView listCategories;
    private String[] categories;
    private static RegisterBusinessStepSixFragment instance;
    private View view;
    private Button bt;
    private ArrayList<String> categoriesSelected;

    public static RegisterBusinessStepSixFragment getInstance(){
        if(instance == null){
            instance = new RegisterBusinessStepSixFragment();
        }
        return instance;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentStepSixBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
        categories = getResources().getStringArray(R.array.bussiness_category);
        categoriesSelected = new ArrayList<>();
        listCategories = (ListView) binding.listCategories;
        listCategories.setAdapter(new AdapterListCategories(getContext(), categories, categoriesSelected, localBroadcastManager, this));
        bt = binding.btContinueRegisterStepSix;
        checkCategories();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_STEP, 7);
                intent.putExtra("BUSINESS_CATEGORIES", categoriesSelected);
                localBroadcastManager.sendBroadcast(intent);

            }
        });
        return view;
    }

    public void checkCategories(){
        if(categoriesSelected.size() == 0) {
            bt.setEnabled(false);
        } else {
            bt.setEnabled(true);
        }
    }

}