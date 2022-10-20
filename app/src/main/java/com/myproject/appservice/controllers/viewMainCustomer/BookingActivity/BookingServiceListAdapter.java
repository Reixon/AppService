package com.myproject.appservice.controllers.viewMainCustomer.BookingActivity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.models.Service;

import java.util.ArrayList;

public class BookingServiceListAdapter extends RecyclerView.Adapter<BookingServiceListAdapter.ViewHolder> {

    private final ArrayList<Service> services;
    private String idBusiness;
    private Context context;

    public BookingServiceListAdapter(Context context, String idBusiness, ArrayList<Service> services) {
        this.services = services;
        this.idBusiness = idBusiness;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingServiceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_service_booking, parent,false);
        return new BookingServiceListAdapter.ViewHolder(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingServiceListAdapter.ViewHolder holder, int position) {
        holder.txt_nameBusiness.setText(services.get(position).getName());
        float price = services.get(position).getPrice();
        String txtPrice = price + "€";
        holder.txtCost.setText(txtPrice);
        holder.txt_nameBusiness.setTextColor(Color.BLACK);
        holder.txtTime.setText(Common.rangeHours);

        if(getItemCount()>1){
            holder.deleteService.setVisibility(View.VISIBLE);
        }else {
            holder.deleteService.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.services.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addService(Service service) {
        services.add(service);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_nameBusiness;
        private TextView txtCost;
        private TextView txtTime;
        private FloatingActionButton deleteService;
        private BookingServiceListAdapter adapter;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            txt_nameBusiness = convertView.findViewById(R.id.txt_name_service);
            txtCost = convertView.findViewById(R.id.txtCost);
            txtTime = convertView.findViewById(R.id.time);
            deleteService = convertView.findViewById(R.id.addFloatingButton);
            convertView.setBackgroundColor(Color.WHITE);

            deleteService.setOnClickListener(v ->{
                adapter.services.remove(getAbsoluteAdapterPosition());
                adapter.notifyItemChanged(getAbsoluteAdapterPosition());
            });
        }

        public ViewHolder linkAdapter(BookingServiceListAdapter bookingServiceListAdapter) {
            this.adapter = bookingServiceListAdapter;
            return this;
        }
    }

}
