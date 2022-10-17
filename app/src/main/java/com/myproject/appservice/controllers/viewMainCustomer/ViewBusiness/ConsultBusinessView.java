package com.myproject.appservice.controllers.viewMainCustomer.ViewBusiness;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myproject.appservice.Common;
import com.myproject.appservice.controllers.viewMainCustomer.ServiceSearchActivity.ServiceSearchActivity;
import com.myproject.appservice.databinding.ActivityConsultBusinessViewBinding;
import com.myproject.appservice.models.Business;
import com.myproject.appservice.models.Service;

import java.util.ArrayList;

public class ConsultBusinessView extends AppCompatActivity {

    private  ActivityConsultBusinessViewBinding binding;
    private Business business;
    private ArrayList<Service> services;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultBusinessViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mProgressView = binding.progressCircular;
        business = (Business) getIntent().getSerializableExtra("Business");
        assert business != null;
        Common.idEmployee = business.getIdUser();
        CollectionReference serviceRef = FirebaseFirestore.getInstance().
                collection("Businesses")
                .document(business.getId())
                .collection("Service");
        services = new ArrayList<>();
        showProgress(true);
        serviceRef.get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Service s = document.toObject(Service.class);
                        s.setId(document.getId());
                        services.add(s);
                    }
                    initialListeners();
                }
                showProgress(false);
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

        back.setOnClickListener(v -> {
            Intent intent = new Intent(ConsultBusinessView.this, ServiceSearchActivity.class);
            startActivity(intent
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        RelativeLayout relativeLayout = binding.generalLayout;
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