package com.myproject.appservice.controllers.ViewMainCustomer.ViewBusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myproject.appservice.Common;
import com.myproject.appservice.controllers.Login.LoginActivity;
import com.myproject.appservice.controllers.RegisterBusinessActivity.RegisterBusinessActivity;
import com.myproject.appservice.controllers.ViewMainCustomer.ServiceSearchActivity.ServiceSearchActivity;
import com.myproject.appservice.databinding.ActivityConsultBusinessViewBinding;
import com.myproject.appservice.models.Business;
import com.myproject.appservice.models.Service;

import java.util.ArrayList;

public class ConsultBusinessView extends AppCompatActivity {

    private  ActivityConsultBusinessViewBinding binding;
    private Business business;
    private ArrayList<Service> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultBusinessViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        business = (Business) getIntent().getSerializableExtra("Business");
        assert business != null;
        Common.idEmployee = business.getIdUser();
        CollectionReference serviceRef = FirebaseFirestore.getInstance().
                collection("Businesses")
                .document(business.getId())
                .collection("Service");
        services = new ArrayList<>();
        serviceRef.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service s = document.toObject(Service.class);
                            s.setId(document.getId());
                            services.add(s);
                        }
                        initialListeners();
                    }
                }
            });

    }

    private void initialListeners(){
        ImageView imageView = binding.imageBusiness;
        TextView nameBusiness = binding.txtNameBusiness;
        TextView txt_address = binding.txtAddress;
        EditText textSearchService = binding.txtSearchService;
        RecyclerView listView = binding.listService;
        ImageButton back = binding.backArrow;
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));
        BusinessServiceListAdapter adapter = new BusinessServiceListAdapter(this, business, services);
        listView.setAdapter(adapter);
        nameBusiness.setText(business.getNameBusiness());
        txt_address.setText(business.getAddress());
        textSearchService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsultBusinessView.this, ServiceSearchActivity.class);
                startActivity(intent
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
        });

        textSearchService.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == KeyEvent.KEYCODE_ENTER)&& event.getAction()== KeyEvent.ACTION_DOWN){
                    adapter.getFilter().filter(textSearchService.getText());
                }
                return false;
            }
        });
    }
}