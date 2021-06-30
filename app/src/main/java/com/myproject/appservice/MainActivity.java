package com.myproject.appservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.myproject.appservice.controllers.Login.LoginActivity;
import com.myproject.appservice.controllers.ViewMainBusiness.ViewMainBusiness;
import com.myproject.appservice.controllers.ViewMainCustomer.ViewMainCustomer;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore database;
    private  FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        TextView nameApplication = findViewById(R.id.textView);
        ImageView logo = findViewById(R.id.imageView);

        nameApplication.setAnimation(animation2);
        logo.setAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser() != null){
                    loginUser();
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                }
            }
        }, 2500);
    }

    private void loginUser() {
        database = FirebaseFirestore.getInstance();
        DocumentReference users = database.collection("Users").document(mAuth.getCurrentUser().getUid());
        users.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("idBusiness") != null) {
                    Common.idBusiness = (String) documentSnapshot.get("idBusiness");
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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}