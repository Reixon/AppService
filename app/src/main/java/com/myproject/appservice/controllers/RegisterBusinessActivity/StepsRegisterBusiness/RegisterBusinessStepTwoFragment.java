package com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.textfield.TextInputEditText;
import com.myproject.appservice.R;
import com.myproject.appservice.Common;
import com.myproject.appservice.databinding.FragmentStepTwoBinding;

import javax.annotation.Nullable;


public class RegisterBusinessStepTwoFragment extends Fragment {

    private final String TAG = "STEP2-REGISTER";
    private boolean cancelName, cancelPhone;
    private TextInputEditText txtBusinessName, txtPhone;
    private LocalBroadcastManager localBroadcastManager;
    private FragmentStepTwoBinding binding;
    private static RegisterBusinessStepTwoFragment instance;
    private Button btContinue;

    public static RegisterBusinessStepTwoFragment getInstance(){
        if(instance == null){
            instance = new RegisterBusinessStepTwoFragment();
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
        binding = FragmentStepTwoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        cancelName = true;
        cancelPhone = true;
        localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
        txtBusinessName = (TextInputEditText) binding.txtNameBusiness;
        txtPhone = (TextInputEditText) binding.txtMobileBusiness;
        btContinue = (Button) binding.btContinueRegisterStepTwo;

        txtBusinessName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cancelName = TextUtils.isEmpty(s);
                enableButton();
            }
        });
        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cancelPhone = TextUtils.isEmpty(s);
                enableButton();
            }
        });

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });
        return view;
    }

    private void enableButton(){
        if(!cancelPhone && !cancelName){
            btContinue.setEnabled(true);
        }
    }

    public void checkFields() {

        // Reset errors.
        txtBusinessName.setError(null);
        txtPhone.setError(null);

        // Store values at the time of the login attempt.
        String businessName = txtBusinessName.getText().toString();
        String phone = txtPhone.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid phone, if the result entered one.
        if (TextUtils.isEmpty(phone)) {
            txtPhone.setError(getString(R.string.msg_business_phone_isRequired));
            cancel = true;
        } else if(phone.length()< 6 || phone.length() >13) {
            txtPhone.setError(getString(R.string.msg_business_phone_format));
            cancel = true;
        }

        // Check for a valid name business.
        if (TextUtils.isEmpty(businessName)) {
            txtBusinessName.setError(getString(R.string.msg_business_name_isRequired));
            cancel = true;
        }

        if (!cancel) {
            nextStep();
        }
    }

    private void nextStep(){
        Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
        intent.putExtra(Common.KEY_STEP, 1);
        intent.putExtra("BUSINESS_NAME", txtBusinessName.getText().toString());
        intent.putExtra("BUSINESS_PHONE", txtPhone.getText().toString());
        localBroadcastManager.sendBroadcast(intent);
    }
}