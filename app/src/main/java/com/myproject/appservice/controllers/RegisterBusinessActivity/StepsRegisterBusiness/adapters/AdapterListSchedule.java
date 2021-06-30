package com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.appservice.R;
import com.myproject.appservice.models.Schedule;

import java.util.Arrays;
import java.util.List;

public class AdapterListSchedule extends RecyclerView.Adapter<AdapterListSchedule.ViewHolder> {

    private Context context;
    private List<Schedule> scheduleList;
    private LayoutInflater layoutInflater;
    private Boolean [] addSchedule ;

    public AdapterListSchedule(Context context, List<Schedule> schedules) {
        this.context = context;
        this.scheduleList = schedules;
        layoutInflater = LayoutInflater.from(context);
        addSchedule = new Boolean[7];
        Arrays.fill(addSchedule, true);
    }

    public void setAddSchedule(int position, boolean value){
        addSchedule[position] = value;
    }

    public boolean getAddSchedule(int position){
        return addSchedule[position];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_schedule_business, parent,false);
        return new AdapterListSchedule.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterListSchedule.ViewHolder holder, final int position) {
        holder.checkBox.setChecked(scheduleList.get(position).isOpened());
        holder.checkBox.setText(scheduleList.get(position).getDay());
        holder.checkBox.setTag(position);
        holder.listView.setAdapter(new AdapterScheduleHours(context, scheduleList, position, this));
        holder.listView.setLayoutManager(new LinearLayoutManager(context));
        holder.listView.getLayoutManager().scrollToPosition(1);

        if(scheduleList.get(position).isOpened()){
            holder.listView.setVisibility(View.VISIBLE);
            holder.txtClosed.setVisibility(View.GONE);
        } else {
            holder.listView.setVisibility(View.GONE);
            holder.txtClosed.setVisibility(View.VISIBLE);
        }

        holder.checkBox.setOnClickListener(view -> {
           CheckBox c = (CheckBox) view;
           int pos = (int) c.getTag();
           scheduleList.get(pos).setOpened(c.isChecked());
           notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private TextView txtClosed;
        private RecyclerView listView;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            txtClosed = (TextView) convertView.findViewById(R.id.txtClose);
            listView = (RecyclerView) convertView.findViewById(R.id.list_scheduleDay);
        }
    }
}
