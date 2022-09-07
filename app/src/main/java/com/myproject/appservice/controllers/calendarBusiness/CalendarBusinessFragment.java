package com.myproject.appservice.controllers.calendarBusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myproject.appservice.controllers.calendarBusiness.bookingBusinessDetail.ActivityNewBooking;
import com.myproject.appservice.databinding.FragmentCalendarBusinessBinding;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarBusinessFragment extends Fragment implements WeekCalendarBusinessAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ExtendedFloatingActionButton todayFloating;
    private LocalDate today;
    private FragmentCalendarBusinessBinding binding;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        today = LocalDate.now(ZoneId.of("Europe/Madrid"));
        CalendarUtils.selectedDate = today;

        binding = FragmentCalendarBusinessBinding.inflate(inflater, container, false);
        monthYearText = binding.titleCalendar;
        calendarRecyclerView = binding.daysRecyclerView;
        todayFloating = binding.todayFloatingButton;
        FloatingActionButton addBooking = binding.addFloatingButton;

        setWeekView();
        addBooking.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ActivityNewBooking.class);
            requireContext().startActivity(intent);
        });

        todayFloating.setOnClickListener(v -> {
            updateCalendar(today);
            todayFloating.setVisibility(View.GONE);
        });

        return binding.getRoot();
    }

    private void setWeekView(){

        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate);

        WeekCalendarBusinessAdapter calendarBusinessAdapter = new WeekCalendarBusinessAdapter(getContext(), days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarBusinessAdapter);

//        EventAdapter eventAdapter = new EventAdapter(getContext(), days, this);
//        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        eventsRecyclerView.setAdapter(eventAdapter);
//        eventsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM");
        return formatter.format(date);
    }

    @Override
    public void onItemClick(int position,  LocalDate date) {
        if(!today.equals(date)){ todayFloating.setVisibility(View.VISIBLE); }
        else { todayFloating.setVisibility(View.GONE); }
        updateCalendar(date);
    }

    private void updateCalendar(LocalDate date){
        CalendarUtils.selectedDate = date;
        setWeekView();
        binding.timelineView.loadData();
    }
}