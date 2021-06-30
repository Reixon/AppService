package com.myproject.appservice.controllers.ViewMainBusiness;

import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myproject.appservice.R;
import com.myproject.appservice.databinding.FragmentCalendarBusinessBinding;


public class CalendarBusiness extends Fragment {

    private FragmentCalendarBusinessBinding binding;

    public CalendarBusiness() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentCalendarBusinessBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

  /*
        WeekCalendar weekCalendar = (WeekCalendar) binding.weekCalendar;
        weekCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(DateTime dateTime) {

            }

        });*/

   /*     WeekView mWeekView = (WeekView) binding.revolvingWeekview;
        mWeekView.setWeekViewLoader(new WeekView.WeekViewLoader() {

            @Override
            public List<? extends WeekViewEvent> onWeekViewLoad() {
                List<WeekViewEvent> events = new ArrayList<>();
                // Add some events
                return events;
            }
        });*/


        return view;
    }
}