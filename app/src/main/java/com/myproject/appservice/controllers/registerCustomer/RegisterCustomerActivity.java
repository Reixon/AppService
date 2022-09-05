package com.myproject.appservice.controllers.registerCustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.viewMainCustomer.ViewMainCustomer;
import com.myproject.appservice.models.User;

public class RegisterCustomerActivity extends AppCompatActivity {

    private Button btRegistrarse;
    private EditText txtEmail, txtPassword, txtNameCustomer;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);
        txtNameCustomer = (EditText) findViewById(R.id.txtNameCustomer);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        btRegistrarse = (Button) findViewById(R.id.bt_register);
        progressBar = findViewById(R.id.progress_circular);

        fAuth = FirebaseAuth.getInstance();

        btRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameUser = txtNameCustomer.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if(TextUtils.isEmpty(nameUser)){
                    txtNameCustomer.setError(getResources().getString(R.string.msg_name_isRequired));
                }

                if(TextUtils.isEmpty(email)){
                    txtEmail.setError(getResources().getString(R.string.msg_email_isRequired));
                }

                if(TextUtils.isEmpty(password)){
                    txtPassword.setError(getResources().getString(R.string.msg_password_isRequired));
                }

                if(password.length() < 6 ){
                    txtPassword.setError(getResources().getString(R.string.msg_error_invalid_password));
                }
                progressBar.setVisibility(View.VISIBLE);
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String userId = fAuth.getCurrentUser().getUid();
                            DocumentReference users = FirebaseFirestore.getInstance()
                                    .collection("Users")
                                    .document(userId);
                            User user = new User(nameUser, email);
                            user.setIdUser(userId);
                            users.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterCustomerActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), ViewMainCustomer.class);
                                    startActivity(intent
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterCustomerActivity.this,
                                            getResources().getString(R.string.msg_error_communication_data_no_update),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(RegisterCustomerActivity.this,
                                    getResources().getString(R.string.msg_error_communication_data_no_update),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}