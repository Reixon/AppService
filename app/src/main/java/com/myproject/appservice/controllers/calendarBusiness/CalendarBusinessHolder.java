package com.myproject.appservice.controllers.calendarBusiness;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myproject.appservice.R;

import java.time.LocalDate;
import java.util.ArrayList;


public class CalendarBusinessHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private final ArrayList<LocalDate> days;
    public final View parentView;
    public final TextView dayOfMonth;
    private final WeekCalendarBusinessAdapter.OnItemListener onItemListener;

    public CalendarBusinessHolder(View itemView, WeekCalendarBusinessAdapter.OnItemListener listener, ArrayList<LocalDate> days){
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = (TextView) itemView.findViewById(R.id.cellDay);
        onItemListener = listener;
        itemView.setOnClickListener(this);
        this.days = days;
    }


    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
    }
}
