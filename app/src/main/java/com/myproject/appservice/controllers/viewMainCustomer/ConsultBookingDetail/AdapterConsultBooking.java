package com.myproject.appservice.controllers.viewMainCustomer.ConsultBookingDetail;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myproject.appservice.R;
import com.myproject.appservice.models.Service;

import java.util.ArrayList;

public class AdapterConsultBooking extends RecyclerView.Adapter<AdapterConsultBooking.ViewHolder>{

    private ArrayList<Service> services;
    private final Context context;

    public AdapterConsultBooking(Context context, ArrayList<Service> services) {
        this.context = context;
        this.services = services;
    }

    @NonNull
    @Override
    public AdapterConsultBooking.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_service_booking, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterConsultBooking.ViewHolder holder, int position) {
        holder.txtService.setText(services.get(position).getName());
        double price = services.get(position).getPrice();
        String txtPrice = price + "€";
        holder.txtCost.setText(txtPrice);
        holder.txtTime.setText(services.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return this.services.size();
    }

    public void addService(Service serviceResult) {
        services.add(serviceResult);
        notifyItemInserted(services.size()-1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtService;
        private final TextView txtCost;
        private final TextView txtTime;
        private FloatingActionButton addFloatingButton;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            txtService = convertView.findViewById(R.id.txt_name_service);
            txtCost = convertView.findViewById(R.id.txtCost);
            txtTime = convertView.findViewById(R.id.time);
            addFloatingButton = convertView.findViewById(R.id.addFloatingButton);
            addFloatingButton.setVisibility(View.GONE);
            convertView.setBackgroundColor(Color.WHITE);
        }
    }
}
