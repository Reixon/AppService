package com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.myproject.appservice.Common;
import com.myproject.appservice.databinding.FragmentStepThreeBinding;

import javax.annotation.Nullable;


public class RegisterBusinessStepThreeFragment extends Fragment {

    private final String TAG = "STEP3-REGISTER";
    private LocalBroadcastManager localBroadcastManager;
    private static RegisterBusinessStepThreeFragment instance;
    private int businessKind;

    public static RegisterBusinessStepThreeFragment getInstance(){
        if(instance == null){
            instance = new RegisterBusinessStepThreeFragment();
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
        FragmentStepThreeBinding binding = FragmentStepThreeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
        RadioButton establishment = (RadioButton) binding.radioButtonOnlyEstablishment;
        RadioButton delivery = (RadioButton) binding.radioButtonOnlyDelivery;
        RadioButton both = (RadioButton) binding.radioButtonEstablishmentAndDelivery;
        RadioGroup radioGroup = (RadioGroup) binding.groupRadioButton;
        Button btContinue = (Button) binding.btContinueRegisterStepThree;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                businessKind = -1;
                if(checkedId == establishment.getId()){
                    businessKind = 1;
                } else if(checkedId == delivery.getId()){
                    businessKind = 2;
                } else {
                    businessKind = 3;
                }
                btContinue.setEnabled(true);
            }
        });

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });

        return view;
    }



    private void nextStep(){
        Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
        intent.putExtra(Common.KEY_STEP, 2);
        intent.putExtra("BUSINESS_KIND", businessKind);
        localBroadcastManager.sendBroadcast(intent);
    }
}