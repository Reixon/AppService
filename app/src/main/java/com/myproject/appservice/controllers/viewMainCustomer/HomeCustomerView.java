package com.myproject.appservice.controllers.viewMainCustomer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myproject.appservice.Common;
import com.myproject.appservice.controllers.viewMainCustomer.Interface.IBookingInfoLoadListener;
import com.myproject.appservice.controllers.viewMainCustomer.ServiceSearchActivity.ServiceSearchActivity;
import com.myproject.appservice.databinding.FragmentHomeCustomerViewBinding;
import com.myproject.appservice.models.Booking;
import com.myproject.appservice.models.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeCustomerView extends Fragment implements IBookingInfoLoadListener {

    private ArrayList<Booking> bookings;
    private IBookingInfoLoadListener iBookingInfoLoadListener;
    private Timestamp toDayTimeStamp;
    private Calendar calendar;

    private CardView cardView;
    private TextView namBusiness;
    private TextView service;
    private TextView month;
    private TextView day;
    private TextView time;

    public HomeCustomerView() {
        // Required empty public constructor
        bookings = new ArrayList<>();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.MINUTE, 0);
        toDayTimeStamp = new Timestamp(calendar.getTime());
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBookings();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDataBooking();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentHomeCustomerViewBinding binding = FragmentHomeCustomerViewBinding.inflate(inflater, container, false);
        cardView = binding.bookingNotification;
        namBusiness = binding.txtNameBusiness;
        service = binding.txtService;
        month = binding.month;
        day = binding.day;
        time = binding.hour;
        HorizontalScrollView scroll = binding.includeMarketing.hsv;


        iBookingInfoLoadListener = this;

        EditText textSearch = binding.searchService.txtSearchService;
        textSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                Intent i = new Intent(getContext(), ServiceSearchActivity.class);
                startActivity(i);
                textSearch.clearFocus();
            }
        });
        textSearch.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), ServiceSearchActivity.class);
            startActivity(i);
            textSearch.clearFocus();
        });
        cardView.setOnClickListener(v -> {

        });
        return binding.getRoot();
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        cardView.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(Booking booking) {
        namBusiness.setText(booking.getBusiness());
        StringBuilder serviceInfo = new StringBuilder();
        for(Service service : booking.getServices()){
            serviceInfo.append(service.getName()).append(" ");
        }
        service.setText(serviceInfo);
        String timeInfo = booking.getTime().split(" at")[0];
        time.setText(timeInfo.split(" - ")[0]);
        String dayInfo = booking.getTime().split(" at")[1].split("/")[0];
        day.setText(dayInfo);
        Calendar calendar = Calendar.getInstance();
        Date d = booking.getTimestamp().toDate();
        calendar.setTime(d);
        String nameMonth = new SimpleDateFormat("MMMM").format(calendar.getTime()).toUpperCase();
        month.setText(nameMonth);
        cardView.setVisibility(View.VISIBLE);

    }


    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateDataBooking(){
        CollectionReference docRefPast = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.idUser)
                .collection("Booking");

        docRefPast.whereLessThanOrEqualTo("timestamp", toDayTimeStamp)
            .whereEqualTo("done", false)
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for (int i = 0 ; i< task.getResult().getDocuments().size(); i++){
                        task.getResult().getDocuments().get(i).getReference().update("done", true);
                    }
                }
                CollectionReference docRef = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(Common.idUser)
                        .collection("Booking");

                docRef.whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                    .limit(1).addSnapshotListener((value, e) -> {
                    assert value != null;
                    if(!value.getDocuments().isEmpty()){
                            Booking booking = value.getDocuments().get(0).toObject(Booking.class);
                            iBookingInfoLoadListener.onBookingInfoLoadSuccess(booking);
                        } else {
                            iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                        }
                    });
            });
    }



    private void loadBookings(){
        CollectionReference docRef = (CollectionReference) FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.idUser)
                .collection("Booking");

        docRef.whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(!task.getResult().isEmpty()){
                            Booking booking = task.getResult().getDocuments().get(0).toObject(Booking.class);
                            iBookingInfoLoadListener.onBookingInfoLoadSuccess(booking);
                        } else {
                            iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
            }
        });
    }
}