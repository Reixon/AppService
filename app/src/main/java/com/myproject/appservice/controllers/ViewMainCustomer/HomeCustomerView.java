package com.myproject.appservice.controllers.ViewMainCustomer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.ViewMainCustomer.Interface.IBookingInfoLoadListener;
import com.myproject.appservice.controllers.ViewMainCustomer.ServiceSearchActivity.ServiceSearchActivity;
import com.myproject.appservice.databinding.FragmentHomeCustomerViewBinding;
import com.myproject.appservice.models.Booking;
import com.myproject.appservice.models.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentHomeCustomerViewBinding binding = FragmentHomeCustomerViewBinding.inflate(inflater, container, false);
        cardView = binding.bookingNotification;
        namBusiness = binding.txtNameBusiness;
        service = binding.txtService;
        month = binding.month;
        day = binding.day;
        time = binding.hour;
        iBookingInfoLoadListener = this;

        EditText textSearch = (EditText) binding.searchService.txtSearchService;
        textSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Intent i = new Intent(getContext(), ServiceSearchActivity.class);
                    startActivity(i);
                    textSearch.clearFocus();
                }
            }
        });
        textSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ServiceSearchActivity.class);
                startActivity(i);
                textSearch.clearFocus();
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
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
        CollectionReference docRefPast = (CollectionReference) FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.idUser)
                .collection("Booking");

        docRefPast.whereLessThanOrEqualTo("timestamp", toDayTimeStamp)
                .whereEqualTo("done", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (int i = 0 ; i< task.getResult().getDocuments().size(); i++){
                                task.getResult().getDocuments().get(i).getReference().update("done", true);
                            }
                        }
                        CollectionReference docRef = (CollectionReference) FirebaseFirestore.getInstance()
                                .collection("Users")
                                .document(Common.idUser)
                                .collection("Booking");

                        docRef.whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                            .limit(1)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if(!value.getDocuments().isEmpty()){
                                        Booking booking = value.getDocuments().get(0).toObject(Booking.class);
                                        iBookingInfoLoadListener.onBookingInfoLoadSuccess(booking);
                                    } else {
                                        iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                                    }
                                }
                            });
                    }
                });
    }

 /*   private void loadBookings(){
        CollectionReference docRef = (CollectionReference) FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.idUser)
                .collection("Booking");

        docRef.whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                Booking booking = task.getResult().getDocuments().get(0).toObject(Booking.class);
                                iBookingInfoLoadListener.onBookingInfoLoadSuccess(booking);
                            } else {
                                iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
            }
        });
    }*/
}