package com.myproject.appservice.controllers.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.registerBusinessActivity.RegisterBusinessActivity;
import com.myproject.appservice.controllers.registerCustomer.RegisterCustomerActivity;
import com.myproject.appservice.controllers.viewMainBusiness.ViewMainBusinessActivity;
import com.myproject.appservice.controllers.viewMainCustomer.ViewMainCustomer;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private FirebaseAuth mAuth;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        mProgressView = (ProgressBar) findViewById(R.id.progress_circular);
        Button bt_register = (Button) findViewById(R.id.bt_register);
        Button bt_login = findViewById(R.id.bt_login);

        bt_login.setOnClickListener(view -> {
            showProgress(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            EditText focused = txtEmail.isFocused() ? txtEmail : txtPassword;
            imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
            checkUser();
        });
        bt_register.setOnClickListener(view -> createAccount());
        mAuth = FirebaseAuth.getInstance();

        /*if(mAuth.getCurrentUser() != null){
            loginUser();
        }*/
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void createAccount(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.message_register_business_or_customer);
        builder.setPositiveButton(R.string.message_customer, (dialog, id) -> {
            Intent intent = new Intent(LoginActivity.this, RegisterCustomerActivity.class);
            startActivity(intent);
        });
        builder.setNegativeButton(R.string.message_business, (dialog, id) -> {
            Intent intent = new Intent(LoginActivity.this, RegisterBusinessActivity.class);
            startActivity(intent);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void checkUser(){

        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, R.string.msg_email_isRequired, Toast.LENGTH_LONG).show();
            return ;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, R.string.msg_password_isRequired, Toast.LENGTH_LONG).show();
            return ;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                loginUser();
            } else {
                showProgress(false);
                Toast.makeText(LoginActivity.this, R.string.msg_error_authentication,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loginUser(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference users = database.collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        users.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.get("idBusiness")!=null) {
                Common.idUser = (String) documentSnapshot.get("idUser");
                Common.idBusiness = (String) documentSnapshot.get("idBusiness");
                Intent intent = new Intent(getApplicationContext(), ViewMainBusinessActivity.class);
                startActivity(intent
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            } else {
                Common.idUser = (String) documentSnapshot.get("idUser");
                Common.nameUser = (String) documentSnapshot.get("userName");
                Common.emailUser = (String) documentSnapshot.get("email");
                Intent intent = new Intent(getApplicationContext(), ViewMainCustomer.class);
                startActivity(intent
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
            finish();
            showProgress(false);
        //    Toast.makeText(LoginActivity.this, "Bienvenido ",  Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            showProgress(false);
            Toast.makeText(LoginActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        LinearLayout relativeLayout = findViewById(R.id.generalLayout);
        relativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        relativeLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                relativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}