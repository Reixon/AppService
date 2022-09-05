package com.myproject.appservice.controllers.viewMainCustomer.ConsultBookingDetail;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.appservice.R;
import com.myproject.appservice.models.Service;

import java.util.ArrayList;

public class AdapterListViewService extends RecyclerView.Adapter<AdapterListViewService.ViewHolder>{

    private ArrayList<Service> services;
    private final Context context;

    public AdapterListViewService(Context context, ArrayList<Service> services) {
        this.context = context;
        this.services = services;
    }

    @NonNull
    @Override
    public AdapterListViewService.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_service_booking, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListViewService.ViewHolder holder, int position) {
        holder.txtService.setText(services.get(position).getName());
        float price = services.get(position).getPrice();
        String txtPrice = price + "€";
        holder.txtCost.setText(txtPrice);
        holder.txtTime.setText(services.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return this.services.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtService, txtCost, txtTime;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            txtService = convertView.findViewById(R.id.txt_name_service);
            txtCost = convertView.findViewById(R.id.txtCost);
            txtTime = convertView.findViewById(R.id.time);
            convertView.setBackgroundColor(Color.WHITE);
        }
    }
}
