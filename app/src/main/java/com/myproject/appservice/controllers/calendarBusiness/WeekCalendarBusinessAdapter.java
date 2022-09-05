package com.myproject.appservice.controllers.calendarBusiness;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.appservice.R;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class WeekCalendarBusinessAdapter extends RecyclerView.Adapter<CalendarBusinessHolder>{

    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;
    private final Context context;

    public WeekCalendarBusinessAdapter(Context context, ArrayList<LocalDate> days, OnItemListener onItemListener){
        this.context = context;
        this.onItemListener = onItemListener;
        this.days = days;
    }

    @NonNull
    @Override
    public CalendarBusinessHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_item_calendar_business, parent,false);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) parent.getHeight();
        return new CalendarBusinessHolder(view, onItemListener, days);
    }

    @Override
    public void onBindViewHolder(@NonNull final CalendarBusinessHolder holder, int position) {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Madrid"));
        final LocalDate date = days.get(position);
        if(date != null){
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if (date.equals(CalendarUtils.selectedDate)){
                Drawable circle = ContextCompat.getDrawable(context, R.drawable.ic_circle_white_8dp);
                Drawable drawableWrapper = DrawableCompat.wrap(circle);
                GradientDrawable gradientDrawable = (GradientDrawable) drawableWrapper.mutate();
                if(CalendarUtils.selectedDate.equals(today)) {
                    gradientDrawable.setColor(Color.RED);
                } else {
                    gradientDrawable.setColor(Color.BLACK);
                }
                holder.dayOfMonth.setBackground(gradientDrawable);
                holder.dayOfMonth.setTextColor(context.getResources().getColor(R.color.white));
            }else {
                if(date.equals(today)){
                    holder.dayOfMonth.setTextColor(Color.RED);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface OnItemListener
    {
        void onItemClick(int position, LocalDate date);
    }
}
