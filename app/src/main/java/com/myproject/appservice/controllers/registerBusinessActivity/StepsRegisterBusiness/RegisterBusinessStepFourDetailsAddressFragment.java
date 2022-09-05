package com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.myproject.appservice.Common;
import com.myproject.appservice.databinding.FragmentStepFourDetailsAddressBinding;

import javax.annotation.Nullable;


public class RegisterBusinessStepFourDetailsAddressFragment extends Fragment {

    private TextInputEditText txtAddress, txtNumber, txtPostalCode, txtCity;

    private static RegisterBusinessStepFourDetailsAddressFragment instance;
    private View view;
    private LocalBroadcastManager localBroadcastManager;
    private boolean cancelAddress = false, cancelPostalCode = false, cancelCity = false;
    private Button btContinue;
    public static RegisterBusinessStepFourDetailsAddressFragment getInstance(){
        if(instance == null){
            instance = new RegisterBusinessStepFourDetailsAddressFragment();
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
        FragmentStepFourDetailsAddressBinding binding = FragmentStepFourDetailsAddressBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        txtAddress = (TextInputEditText) binding.txtStreet;
        txtCity = (TextInputEditText) binding.txtCity;
        txtPostalCode = (TextInputEditText) binding.txtPostalCode;
        txtNumber = (TextInputEditText) binding.txtNumber;
        btContinue = (Button) binding.btContinueRegisterStepFourDetail;
        localBroadcastManager = LocalBroadcastManager.getInstance(requireActivity());
        localBroadcastManager.registerReceiver(getAddress, new IntentFilter(Common.SEND_ADDRESS));
        txtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cancelAddress = TextUtils.isEmpty(s);
                enableButton();
            }
        });

        txtCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cancelCity = TextUtils.isEmpty(s);
                enableButton();
            }
        });

        txtPostalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cancelPostalCode = TextUtils.isEmpty(s);
                enableButton();
            }
        });
     /*   txtAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (view.getId() == txtAddress.getId() && !TextUtils.isEmpty(txtAddress.getText())) {
                    activateAddress = true;
                } else if (view.getId() == txtPostalCode.getId() && !TextUtils.isEmpty(txtPostalCode.getText())) {
                    activatePostalCode = true;
                } else if (view.getId() == txtCity.getId() && !TextUtils.isEmpty(txtCity.getText())) {
                    activateCity = true;
                }
                if (activateAddress && activateCity && activatePostalCode) {
                    Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
                    intent.putExtra(Common.KEY_STEP, 4);
                    intent.putExtra("STREET", txtAddress.getText());
                    intent.putExtra("NUMBER", txtNumber.getText());
                    intent.putExtra("POSTAL_CODE", txtPostalCode.getText());
                    intent.putExtra("CITY", txtCity.getText());
                    localBroadcastManager.sendBroadcast(intent);
                    return true;
                }
                return false;
            }
        });*/

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_STEP, 4);
                intent.putExtra("STREET_EDIT", txtAddress.getText().toString());
                intent.putExtra("NUMBER_EDIT", txtNumber.getText().toString());
                intent.putExtra("POSTAL_CODE_EDIT", txtPostalCode.getText().toString());
                intent.putExtra("CITY_EDIT", txtCity.getText().toString());
                localBroadcastManager.sendBroadcast(intent);
            }
        });

        return view;
    }

    private void enableButton(){
        if(!cancelAddress && !cancelCity && !cancelPostalCode){
            btContinue.setEnabled(true);
        }
    }

    private BroadcastReceiver getAddress = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            txtAddress.setText(intent.getExtras().getString("STREET", Common.street));
            txtNumber.setText(intent.getExtras().getString("NUMBER", Common.number));
            txtPostalCode.setText(intent.getExtras().getString("POSTAL_CODE", Common.postalCode));
            txtCity.setText(intent.getExtras().getString("CITY", Common.city));
        }
    };

}