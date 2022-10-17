package com.myproject.appservice.controllers.viewMainCustomer.ViewBusiness;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.viewMainCustomer.BookingActivity.BookingServiceActivity;
import com.myproject.appservice.models.Business;
import com.myproject.appservice.models.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BusinessServiceListAdapter extends RecyclerView.Adapter<BusinessServiceListAdapter.ViewHolder> implements Filterable {

    private ArrayList<Service> services;
    private final ArrayList<Service> mStringFilterList;
    private final HashMap<String, String> keyWords;
    private BusinessFilter businessFilter;
    private final Context context;
    private Business business;

    public BusinessServiceListAdapter(Context context, Business business, ArrayList<Service> services) {
        this.context = context;
        this.services = services;
        this.business = business;
        mStringFilterList = services;
        keyWords = new HashMap<>();
        DatabaseReference keyWord = FirebaseDatabase.getInstance().getReference().child("KeyWords");

        keyWord.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    keyWords.put(snapshot.getKey(), Objects.requireNonNull(snapshot.getValue()).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public BusinessServiceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_service_businessview, parent,false);
        return new BusinessServiceListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessServiceListAdapter.ViewHolder holder, int position) {
        holder.txt_nameBusiness.setText(services.get(position).getName());
        holder.btBooking.setTag(position);
        float price = services.get(position).getPrice();
        String txtPrice = price + "€";
        holder.txtCost.setText(txtPrice);
        holder.txtTime.setText(services.get(position).getTime());
        holder.txt_nameBusiness.setTextColor(Color.BLACK);

        holder.btBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                Intent intent = new Intent(context, BookingServiceActivity.class);
                intent.putExtra("Service", services.get(position));
                intent.putExtra("Business", business);
                context.startActivity(intent);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_nameBusiness;
        private TextView txtCost;
        private TextView txtTime;
        private Button btBooking;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            txt_nameBusiness = convertView.findViewById(R.id.txt_name_service);
            txtCost = convertView.findViewById(R.id.txtCost);
            txtTime = convertView.findViewById(R.id.time);
            btBooking = convertView.findViewById(R.id.btBooking);
            convertView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    @Override
    public Filter getFilter() {
        if(businessFilter == null) {
            businessFilter = new BusinessFilter();
        }
        return businessFilter;
    }

    private class BusinessFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint!= null && constraint.length()>0){
                ArrayList<Service> filterList=new ArrayList<>();
                String keyWordString = "";

                for(int i=0;i<mStringFilterList.size();i++){
                    String data  = keyWords.get(mStringFilterList.get(i).getName().toUpperCase());
                    if(data != null && !TextUtils.isEmpty(data) && data.contains(constraint.toString().toUpperCase())){
                        keyWordString = keyWords.get(mStringFilterList.get(i).getName());
                    }
                    if((mStringFilterList.get(i).getName().toUpperCase())
                        .contains(constraint.toString().toUpperCase())
                        || (!TextUtils.isEmpty(keyWordString) && Objects.requireNonNull(keyWordString)
                            .contains(constraint.toString().toUpperCase()))) {
                        Service service = new Service();
                        service.setName(mStringFilterList.get(i).getName());
                        service.setTime(mStringFilterList.get(i).getTime());
                        service.setPrice(mStringFilterList.get(i).getPrice());
                        filterList.add(service);
                    }
                }
                results.count=filterList.size();
                results.values=filterList;
            }else{
                results.count=mStringFilterList.size();
                results.values=mStringFilterList;
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            services = (ArrayList<Service>) results.values;
            notifyDataSetChanged();
        }

    }

}
