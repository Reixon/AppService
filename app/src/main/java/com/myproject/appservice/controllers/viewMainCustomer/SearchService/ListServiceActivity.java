package com.myproject.appservice.controllers.viewMainCustomer.SearchService;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myproject.appservice.R;
import com.myproject.appservice.databinding.ActivitySearchServiceBinding;
import com.myproject.appservice.models.Service;

import java.util.ArrayList;
import java.util.Objects;

public class ListServiceActivity extends AppCompatActivity {

    private TextView title;
    private RecyclerView listview;
    private ActivitySearchServiceBinding binding;
    private ImageView btBack;
    private View mProgressView;
    private ArrayList<Service> services;
    private String idBusiness;
    private AdapterListService adapter;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        title = binding.getRoot().findViewById(R.id.txt_search_service);
        listview = binding.listView;
        btBack = binding.btBack;
        mProgressView = binding.circularProgress;
        idBusiness = (String) Objects.requireNonNull(getIntent().getExtras()).get("Business");
        listview.setHasFixedSize(true);
        listview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        services = new ArrayList<>();
        adapter = new AdapterListService(services,this);
        listview.setAdapter(adapter);
        loadData();
    }

    private void loadData(){
        showProgress(true);
        Task<QuerySnapshot> schedulesRef = FirebaseFirestore.getInstance()
                .collection("Businesses")
                .document(idBusiness)
                .collection("Service")
                .get();
        schedulesRef.addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getDocuments().size() > 0){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("TAG", document.getId() + " => " + document.getData());
                    Service service = document.toObject(Service.class);
                    services.add(service);
                }
            }
            showProgress(false);

        });
    }

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
