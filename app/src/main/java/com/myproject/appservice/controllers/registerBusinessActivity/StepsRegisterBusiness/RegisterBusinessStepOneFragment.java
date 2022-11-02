package com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.textfield.TextInputEditText;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.databinding.FragmentStepOneBinding;

import java.util.Objects;


public class RegisterBusinessStepOneFragment extends Fragment {

    private final String TAG = "STEP1-REGISTER";
    private TextInputEditText txtNameOwner, txtEmail, txtPassword;
    private Button btContinue;
    private LocalBroadcastManager localBroadcastManager;
    private boolean cancelName, cancelEmail, cancelPassword;
    @SuppressLint("StaticFieldLeak")
    private static RegisterBusinessStepOneFragment instance;

    public static RegisterBusinessStepOneFragment getInstance(){
        if(instance == null){
            instance = new RegisterBusinessStepOneFragment();
        }
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        com.myproject.appservice.databinding.FragmentStepOneBinding binding = FragmentStepOneBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        cancelName = true;
        cancelEmail = true;
        cancelPassword = true;
        localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
        txtNameOwner = binding.txtNameOwnerRegBusiness;
        txtEmail = binding.txtEmailRegBusiness;
        txtPassword = binding.txtPasswordRegBusiness;
        btContinue = binding.btContinueRegisterStepOne;
        txtNameOwner.addTextChangedListener(new TextWatcher() {
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
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cancelEmail = TextUtils.isEmpty(s);
                enableButton();
            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cancelPassword = TextUtils.isEmpty(s);
                enableButton();
            }
        });

        btContinue.setOnClickListener(v -> checkFields());
        return view;
    }

    private void enableButton(){
        if(!cancelEmail && !cancelName && !cancelPassword){
            btContinue.setEnabled(true);
        }
    }

    private void checkFields() {

        // Reset errors.
        txtNameOwner.setError(null);
        txtEmail.setError(null);
        txtPassword.setError(null);

        // Store values at the time of the login attempt.
        String name = Objects.requireNonNull(txtNameOwner.getText()).toString();
        String email = Objects.requireNonNull(txtEmail.getText()).toString();
        String password = Objects.requireNonNull(txtPassword.getText()).toString();

        boolean cancel = false;

        if (!Common.isNameValid(name)) {
            txtNameOwner.setError(getString(R.string.msg_name_isRequired));
            cancel = true;
        }
        // Check for a valid password, if the result entered one.
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError(getString(R.string.msg_password_isRequired));
            cancel = true;
        } else if(!Common.isPasswordValid(password)) {
            txtPassword.setError(getString(R.string.msg_error_invalid_password));
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.msg_email_isRequired));
            cancel = true;
        } else if (!Common.isEmailValid(email)) {
            txtEmail.setError(getString(R.string.msg_error_invalid_email));
            cancel = true;
        }

        if (!cancel) {
            nextStep();
        }
    }

    private void nextStep(){
        Log.i(TAG, "SECOND STEP");
        Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
        intent.putExtra(Common.KEY_STEP, 0);
        intent.putExtra("NAME", Objects.requireNonNull(txtNameOwner.getText()).toString());
        intent.putExtra("EMAIL", Objects.requireNonNull(txtEmail.getText()).toString());
        intent.putExtra("PASSWORD", Objects.requireNonNull(txtPassword.getText()).toString());
        localBroadcastManager.sendBroadcast(intent);
    }
}