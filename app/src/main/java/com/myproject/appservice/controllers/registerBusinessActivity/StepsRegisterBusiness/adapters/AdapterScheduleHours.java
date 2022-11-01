package com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.myproject.appservice.R;
import com.myproject.appservice.Common;
import com.myproject.appservice.models.Schedule;

import java.util.List;

public class AdapterScheduleHours extends RecyclerView.Adapter<AdapterScheduleHours.ViewHolder> {

    private final Context context;
    private final List<Schedule> scheduleList;
    private final int weekPosition;
    private final AdapterListSchedule adapterListSchedule;

    AdapterScheduleHours(Context context, List<Schedule> scheduleList, final int positionScheduleDays,
                         AdapterListSchedule adapterListSchedule){
        this.context = context;
        this.scheduleList = scheduleList;
        this.weekPosition = positionScheduleDays;
        this.adapterListSchedule = adapterListSchedule;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_schedule_day_business, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterScheduleHours.ViewHolder holder, final int position) {
        holder.txtOpeningHours.setTag(position);
        holder.actionBtn.setTag(position);

        if(!adapterListSchedule.getAddSchedule(weekPosition) && scheduleList.get(weekPosition).getSchedulesDay().size() == 1){
            adapterListSchedule.setAddSchedule(weekPosition,true);
        }

        if (scheduleList.get(weekPosition).isOpened()) {
            holder.txtOpeningHours.setText(scheduleList.get(weekPosition).getSchedulesDay().get(position));
            if(adapterListSchedule.getAddSchedule(weekPosition) && position == 0){
                holder.actionBtn.setVisibility(View.VISIBLE);
            } else {
                holder.actionBtn.setVisibility(View.INVISIBLE);
            }
            if(position > 0){
                holder.actionBtn.setVisibility(View.VISIBLE);
            }
        } else {
            holder.txtOpeningHours.setText(context.getString(R.string.closed));

        }

        if(position > 0){
            holder.actionBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_remove_24));
        } else {
            holder.actionBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_add_24));
        }

        /*  LISTENERS */
        holder.txtOpeningHours.setOnClickListener(v -> {
            TextView textView = (TextView) v;
            final int posicion =  (int) v.getTag();
            //    Log.i(LOG, "click");
            int maxValue = Common.hours.length - 1;
            int minValue = 0;
            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(R.layout.adapter_timepicker_schedule);
            NumberPicker numberPicker = dialog.findViewById(R.id.forHour);
            assert numberPicker != null;
            numberPicker.setDisplayedValues(null);
            numberPicker.setMaxValue(maxValue);
            numberPicker.setMinValue(minValue);
            numberPicker.setDisplayedValues(Common.hours);

            NumberPicker numberPicker1 = dialog.findViewById(R.id.untilHour);
            assert numberPicker1 != null;
            numberPicker1.setDisplayedValues(null);
            numberPicker1.setMaxValue(maxValue);
            numberPicker1.setMinValue(minValue);
            numberPicker1.setDisplayedValues(Common.hours);

            String[] txt = textView.getText().toString().split(context.getResources().getString(R.string.separateSchedule));

            String[] forHour = txt[0].split(":");
            int positionH = Integer.parseInt(forHour[0]) * 12;
            int positionT = (Integer.parseInt(forHour[1]) / 5) + positionH;
            numberPicker.setValue(positionT);
            String[] untilHour = txt[1].split(":");
            int positionHUntil = Integer.parseInt(untilHour[0]) * 12;
            int positionUntil = (Integer.parseInt(untilHour[1]) / 5) + positionHUntil;
            numberPicker1.setValue(positionUntil);
            dialog.show();
            Button accept = dialog.findViewById(R.id.btOk_TimePicker);
            assert accept != null;
            accept.setOnClickListener(v1 -> {
                Resources res = context.getResources();
                String resume = String.format(res.getString(R.string.text_schedule_format),
                        Common.hours[numberPicker.getValue()],
                        Common.hours[numberPicker1.getValue()]);
                textView.setText(resume);
                scheduleList.get(weekPosition).getSchedulesDay().set(posicion, resume);
                dialog.cancel();
            });
            Button cancel = dialog.findViewById(R.id.btCancel_TimePicker);
            assert cancel != null;
            cancel.setOnClickListener(v12 -> dialog.cancel());

        });
        holder.actionBtn.setOnClickListener(v -> {
            final int positionActionBtn = (int) v.getTag();
            if(positionActionBtn == 0){
                if (scheduleList.get(weekPosition).isOpened()) {
                    if (scheduleList.get(weekPosition).getSchedulesDay().size() == 1) {
                        scheduleList.get(weekPosition).getSchedulesDay().add(context.getResources().getString(R.string.afternoonSchedule));
                        notifyItemInserted(1);
                    } else if (scheduleList.get(weekPosition).getSchedulesDay().size() == 2) {
                        scheduleList.get(weekPosition).getSchedulesDay().add(context.getResources().getString(R.string.eveningSchedule));
                        notifyItemInserted(2);
                    } else if (scheduleList.get(weekPosition).getSchedulesDay().size() == 3) {
                        scheduleList.get(weekPosition).getSchedulesDay().add(context.getResources().getString(R.string.nightSchedule));
                        notifyItemInserted(3);
                    }
                    if (adapterListSchedule.getAddSchedule(weekPosition) && scheduleList.get(weekPosition).getSchedulesDay().size() < 4) {
                        holder.actionBtn.setVisibility(View.VISIBLE);
                    } else {
                        holder.actionBtn.setVisibility(View.INVISIBLE);
                        adapterListSchedule.setAddSchedule(weekPosition, false);
                    }
                    notifyItemChanged(0);
                }
            } else {
                scheduleList.get(weekPosition).getSchedulesDay().remove(positionActionBtn);
                notifyItemRemoved(positionActionBtn);
            }

        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.get(weekPosition).getSchedulesDay().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtOpeningHours;
        private final ImageButton actionBtn;

        public ViewHolder(@NonNull View context) {
            super(context);
            txtOpeningHours = context.findViewById(R.id.txtOpeningHours);
            actionBtn = context.findViewById(R.id.pluBt);
        }
    }


}
