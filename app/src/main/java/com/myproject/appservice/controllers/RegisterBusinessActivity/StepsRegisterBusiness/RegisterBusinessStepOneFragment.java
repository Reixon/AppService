package com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.databinding.FragmentStepOneBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterBusinessStepOneFragment extends Fragment {

    private final String TAG = "STEP1-REGISTER";
    private TextInputEditText txtNameOwner, txtEmail, txtPassword;
    private Button btContinue;
    private LocalBroadcastManager localBroadcastManager;
    private boolean cancelName, cancelEmail, cancelPassword;

    private FragmentStepOneBinding binding;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentStepOneBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        cancelName = true;
        cancelEmail = true;
        cancelPassword = true;
        localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
        txtNameOwner = (TextInputEditText) binding.txtNameOwnerRegBusiness;
        txtEmail = (TextInputEditText) binding.txtEmailRegBusiness;
        txtPassword = (TextInputEditText) binding.txtPasswordRegBusiness;
        btContinue = (Button) binding.btContinueRegisterStepOne;
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

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });
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
        String name = txtNameOwner.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        boolean cancel = false;

        if (!isNameValid(name)) {
            txtNameOwner.setError(getString(R.string.msg_name_isRequired));
            cancel = true;
        }
        // Check for a valid password, if the result entered one.
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError(getString(R.string.msg_password_isRequired));
            cancel = true;
        } else if(!isPasswordValid(password)) {
            txtPassword.setError(getString(R.string.msg_error_invalid_password));
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.msg_email_isRequired));
            cancel = true;
        } else if (!isEmailValid(email)) {
            txtEmail.setError(getString(R.string.msg_error_invalid_email));
            cancel = true;
        }

        if (!cancel) {
            nextStep();
        }
    }

    private void nextStep(){
        Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
        intent.putExtra(Common.KEY_STEP, 0);
        intent.putExtra("NAME", txtNameOwner.getText().toString());
        intent.putExtra("EMAIL", txtEmail.getText().toString());
        intent.putExtra("PASSWORD", txtPassword.getText().toString());
        localBroadcastManager.sendBroadcast(intent);
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isNameValid(String name) {
        return name.length() >= 4;
    }

    private boolean isPasswordValid(String password) {
        String expression = "^[\\w\\.-]{6,20}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }


}