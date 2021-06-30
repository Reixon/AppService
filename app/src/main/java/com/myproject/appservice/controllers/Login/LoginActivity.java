package com.myproject.appservice.controllers.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.RegisterBusinessActivity.RegisterBusinessActivity;
import com.myproject.appservice.controllers.RegisterCustomer.RegisterCustomerActivity;
import com.myproject.appservice.controllers.ViewMainBusiness.ViewMainBusiness;
import com.myproject.appservice.controllers.ViewMainCustomer.ViewMainCustomer;
import com.myproject.appservice.models.User;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private Button bt_login, bt_register;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        mProgressView = (ProgressBar) findViewById(R.id.progress_circular);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = findViewById(R.id.bt_login);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser();
            }
        });
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
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
        builder.setPositiveButton(R.string.message_customer, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(LoginActivity.this, RegisterCustomerActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.message_business, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(LoginActivity.this, RegisterBusinessActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void checkUser(){

        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return ;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Se debe ingresar un password", Toast.LENGTH_LONG).show();
            return ;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    loginUser();
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void loginUser(){
        database = FirebaseFirestore.getInstance();
        DocumentReference users = database.collection("Users").document(mAuth.getCurrentUser().getUid());
        users.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("idBusiness")!=null) {
                    Intent intent = new Intent(getApplicationContext(), ViewMainBusiness.class);
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
                Toast.makeText(LoginActivity.this, "Bienvenido ",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
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