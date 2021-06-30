package com.myproject.appservice.controllers.ViewMainCustomer.ConsultBookingDetail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.ViewMainCustomer.ListViewBookingCustomer;
import com.myproject.appservice.controllers.ViewMainCustomer.ServiceSearchActivity.ServiceSearchActivity;
import com.myproject.appservice.controllers.ViewMainCustomer.ViewBusiness.ConsultBusinessView;
import com.myproject.appservice.databinding.ActivityConsultBookingDetailBinding;
import com.myproject.appservice.models.Booking;
import com.myproject.appservice.models.Service;
import java.util.ArrayList;

public class ConsultBookingDetail extends AppCompatActivity {

    private ArrayList<Service> services;
    private View mProgressView;
    private ActivityConsultBookingDetailBinding binding;
    private AdapterListViewService adapterService;
    private Booking booking;
    private RecyclerView listService;
    private ImageButton btBack;
    private Button btCancel, btChange;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultBookingDetailBinding.inflate(getLayoutInflater());
        mProgressView = binding.progressCircular;
        listService = binding.listServices;
        btBack = binding.backArrow;
        btCancel = binding.btCancel;
        btChange = binding.btChange;
        mapFragment =  ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        services = new ArrayList<>();
        View view = binding.getRoot();
        setContentView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgress(true);
        String idBooking = (String) getIntent().getExtras().get("Booking");

        DocumentReference docRef = (DocumentReference) FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.idUser)
                .collection("Booking")
                .document(idBooking);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    booking=task.getResult().toObject(Booking.class);
                    services = booking.getServices();
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(callback);
                    }
                    if(!booking.isDone()){
                        btCancel.setVisibility(View.VISIBLE);
                        btChange.setVisibility(View.VISIBLE);
                    } else {
                        btCancel.setVisibility(View.GONE);
                        btChange.setVisibility(View.GONE);
                    }
                } else {

                }
                initialListeners();
                showProgress(false);
            }
        });
    }

    private void initialListeners(){
        listService.setHasFixedSize(true);
        listService.setLayoutManager(new LinearLayoutManager(this));
        adapterService = new AdapterListViewService(this, services);
        listService.setAdapter(adapterService);

        btChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
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

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if(booking.getLatitude() != null && booking.getLongitude()!=null) {
                googleMap.clear();
                LatLng point = new LatLng(booking.getLatitude(), booking.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(point));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,19));
            }
        }
    };

}