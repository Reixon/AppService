package com.myproject.appservice.controllers.viewMainCustomer.ConsultBookingDetail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.viewMainCustomer.BookingActivity.BookingServiceActivity;
import com.myproject.appservice.databinding.ActivityConsultBookingDetailBinding;
import com.myproject.appservice.models.Booking;
import com.myproject.appservice.models.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ConsultBooking extends AppCompatActivity {

    private ArrayList<Service> services;
    private TextView title;
    private View mProgressView;
    private ActivityConsultBookingDetailBinding binding;
    private Booking booking;
    private RecyclerView listService;
    private ImageButton btBack;
    private Button btCancel, btChange;
    private SupportMapFragment mapFragment;
    private AdapterConsultBooking adapterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultBookingDetailBinding.inflate(getLayoutInflater());
        mProgressView = binding.progressCircular;
        listService = binding.listServices;
        btBack = binding.backArrow;
        btCancel = binding.btCancel;
        btChange = binding.btChange;
        title = binding.titleTimeBooking;
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        services = new ArrayList<>();
        View view = binding.getRoot();
        setContentView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgress(true);
        String idBooking = (String) Objects.requireNonNull(getIntent().getExtras()).get("Booking");

        assert idBooking != null;
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.idUser)
                .collection("Booking")
                .document(idBooking);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                booking = task.getResult().toObject(Booking.class);
                assert booking != null;
                services = booking.getServices();
                Date d = new Date(booking.getTimestamp().toDate().getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd MMMM yyyy", Locale.forLanguageTag("es-ES"));
                title.setText(dateFormat.format(d).toUpperCase());
                if (mapFragment != null) {
                    mapFragment.getMapAsync(callback);
                }
                if (!booking.isDone()) {
                    btCancel.setVisibility(View.VISIBLE);
                    btChange.setVisibility(View.VISIBLE);
                } else {
                    btCancel.setVisibility(View.GONE);
                    btChange.setVisibility(View.GONE);
                }
            }
            initialListeners();
        });
    }

    private void initialListeners() {
        listService.setHasFixedSize(true);
        listService.setLayoutManager(new LinearLayoutManager(this));
        adapterService = new AdapterConsultBooking(this, services);
        listService.setAdapter(adapterService);

        btChange.setOnClickListener(v -> {
            Intent intent = new Intent(ConsultBooking.this, BookingServiceActivity.class);
            Booking booking = new Booking();
            booking.setId(this.booking.getId());
            booking.setBusiness(this.booking.getBusiness());
            booking.setIdBusiness(this.booking.getIdBusiness());
            booking.setLatitude(this.booking.getLatitude());
            booking.setLongitude(this.booking.getLongitude());
            booking.setServices(services);
            booking.setIdEmployee(this.booking.getIdEmployee());
            booking.setCustomer(Common.nameUser);
            booking.setEmailCustomer(Common.emailUser);
            booking.setSlot(this.booking.getSlot());
            intent.putExtra("Booking", booking);
            startActivity(intent);
        });

        btCancel.setOnClickListener(v -> {

        });

        btBack.setOnClickListener(v -> finish());
        showProgress(false);
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        RelativeLayout linearLayout = binding.generalLayout;
        linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        linearLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            if (booking.getLatitude() != null && booking.getLongitude() != null) {
                googleMap.clear();
                LatLng point = new LatLng(booking.getLatitude(), booking.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(point));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 19));
            }
        }
    };

}