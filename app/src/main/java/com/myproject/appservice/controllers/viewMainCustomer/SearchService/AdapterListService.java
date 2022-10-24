package com.myproject.appservice.controllers.viewMainCustomer.SearchService;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.appservice.R;
import com.myproject.appservice.controllers.viewMainCustomer.BookingActivity.BookingServiceActivity;
import com.myproject.appservice.models.Service;

import java.util.ArrayList;

public class AdapterListService extends RecyclerView.Adapter<AdapterListService.ViewHolder> {

    private final ArrayList<Service> services;
    private final ListServiceActivity context;

    public AdapterListService(ArrayList<Service> services, ListServiceActivity context) {
        this.services = services;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterListService.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_search_service, parent,false);
        return new AdapterListService.ViewHolder(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListService.ViewHolder holder, int position) {
        holder.txtNameService.setText(services.get(position).getName());
        holder.itemView.setTag(position);
        holder.btAdd.setTag(position);
        holder.txtCost.setText(String.format(String.valueOf(R.string.format_float), services.get(position).getPrice()));
        holder.txtTime.setText(services.get(position).getTime());

        holder.itemView.setOnClickListener( v -> {
            int positionX = (Integer) v.getTag();
            clickItem(positionX);
        });
    }

    @Override
    public int getItemCount() {
        return this.services.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void clickItem(int position){

        Intent intent = new Intent(context, BookingServiceActivity.class);
        intent.putExtra("Service", services.get(position));
        context.setResult(-1, intent);
        context.finish();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtNameService;
        private final TextView txtCost;
        private final TextView txtTime;
        private final Button btAdd;
        private AdapterListService adapter;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            txtNameService = convertView.findViewById(R.id.txt_name_service);
            txtCost = convertView.findViewById(R.id.txt_cost);
            txtTime = convertView.findViewById(R.id.txt_duration);
            btAdd = convertView.findViewById(R.id.bt_add);
            btAdd.setOnClickListener(v -> {
                int positionX = (Integer) v.getTag();
                adapter.clickItem(positionX);
            });
        }

        public ViewHolder linkAdapter(AdapterListService bookingServiceListAdapter) {
            this.adapter = bookingServiceListAdapter;
            return this;
        }
    }

}
