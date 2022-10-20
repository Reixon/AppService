package com.myproject.appservice.controllers.viewMainCustomer.SearchCompany;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myproject.appservice.R;
import com.myproject.appservice.models.Business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AdapterSearchCompany extends BaseAdapter implements Filterable {

    private ArrayList<Business> businesses;
    private ArrayList<Business> mStringFilterList;
    private HashMap<String, String> keyWords;
    private BusinessFilter businessFilter;
    private LayoutInflater inflater;
    private Context context;
    private DatabaseReference keyWord;

    public AdapterSearchCompany(Context context, ArrayList<Business> businesses) {
        this.context = context;
        this.businesses = businesses;
        mStringFilterList = businesses;
        inflater = (LayoutInflater.from(context));
        keyWords = new HashMap<>();
        keyWord  = FirebaseDatabase.getInstance().getReference().child("KeyWords");

        keyWord.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    keyWords.put(snapshot.getKey(), snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getCount() {
        return businesses.size();
    }

    @Override
    public Object getItem(int position) {
        return businesses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView txt_nameBusiness;
        TextView txt_address;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        try {
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.adapter_item_business, null);
                holder.txt_nameBusiness = (TextView) convertView.findViewById(R.id.txt_nameBusiness);
                holder.txt_address = (TextView) convertView.findViewById(R.id.txt_address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txt_nameBusiness.setText(businesses.get(position).getNameBusiness());
            holder.txt_address.setText(businesses.get(position).getAddress());
            convertView.setBackgroundColor(Color.WHITE);
            holder.txt_nameBusiness.setTextColor(Color.BLACK);
        }   catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(businessFilter == null){
            businessFilter = new BusinessFilter();
        }
        return businessFilter;
    }

    private class BusinessFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint!= null && constraint.length()>0){
                ArrayList<Business> filterList=new ArrayList<Business>();
                String keyWordString = "";

                for(int i=0;i<mStringFilterList.size();i++){
                    for(int x = 0; x < mStringFilterList.get(i).getCategories().size(); x++){
                        String data  = keyWords.get(mStringFilterList.get(i).getCategories().get(x).toUpperCase());
                        if(data != null && !TextUtils.isEmpty(data) && data.contains(constraint.toString().toUpperCase())){
                            keyWordString = keyWords.get(mStringFilterList.get(i).getCategories().get(x));
                        }
                    }
                    if((mStringFilterList.get(i).getNameBusiness().toUpperCase())
                        .contains(constraint.toString().toUpperCase())
                        || (!TextUtils.isEmpty(keyWordString) && Objects.requireNonNull(keyWordString)
                            .contains(constraint.toString().toUpperCase()))) {
                        Business business = new Business();
                        business.setNameBusiness(mStringFilterList.get(i).getNameBusiness());
                        business.setId(mStringFilterList.get(i).getId());
                        business.setAddress(mStringFilterList.get(i).getAddress());
                        filterList.add(business);
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
               businesses = (ArrayList<Business>) results.values;
               notifyDataSetChanged();
        }

    }

}
