package com.myproject.appservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myproject.appservice.controllers.login.LoginActivity;
import com.myproject.appservice.controllers.viewMainBusiness.ViewMainBusinessActivity;
import com.myproject.appservice.controllers.viewMainCustomer.ViewMainCustomer;

public class MainActivity extends AppCompatActivity {

    private  FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
//        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);
//
//        TextView nameApplication = findViewById(R.id.textView);
//        ImageView logo = findViewById(R.id.imageView);
//
//        nameApplication.setAnimation(animation2);
//        logo.setAnimation(animation);

//        new Handler().postDelayed(() -> {
//            mAuth = FirebaseAuth.getInstance();
//            if(mAuth.getCurrentUser() != null){
//                loginUser();
//            } else {
//                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent
//                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
//                finish();
//            }
//        }, 2500);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            loginUser();
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
        }
    }

    private void loginUser() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference users = database.collection("Users").document(mAuth.getCurrentUser().getUid());
        users.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.get("idBusiness") != null) {
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
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Authentication failed.",
                Toast.LENGTH_SHORT).show());
    }
}