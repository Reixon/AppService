package com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.adapters;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myproject.appservice.R;
import com.myproject.appservice.models.Business;
import com.myproject.appservice.models.MPlace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterListAddress extends BaseAdapter{

    private List<MPlace> addresses;
    private LayoutInflater inflater;
    private Context context;

    public AdapterListAddress(Context context, List<MPlace> addresses) {
        this.context = context;
        this.addresses = addresses;
        inflater = (LayoutInflater.from(context));
    }

    public void setAddresses(List<MPlace> items){
        this.addresses = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public Object getItem(int position) {
        return addresses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        LinearLayout titleItem;
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
                convertView = mInflater.inflate(R.layout.adapter_item_address, null);
                holder.titleItem = (LinearLayout) convertView.findViewById(R.id.titleItem);
                if(!addresses.get(position).isAutoLocalice()){
                    holder.titleItem.setVisibility(View.INVISIBLE);
                }
                holder.txt_address = (TextView) convertView.findViewById(R.id.txt_detail_address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txt_address.setText(addresses.get(position).toString());

            convertView.setBackgroundColor(Color.WHITE);
        }   catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
