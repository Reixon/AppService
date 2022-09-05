package com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.myproject.appservice.R;
import com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepSevenFragment;
import com.myproject.appservice.models.Service;

import java.util.List;

public class AdapterListBusinessService extends BaseAdapter{

    private List<Service> services;
    private LayoutInflater inflater;
    private Context context;
    private final RegisterBusinessStepSevenFragment registerBusinessStepSevenFragment;

    public AdapterListBusinessService(Context context, List<Service> services, RegisterBusinessStepSevenFragment registerBusinessStepSevenFragment) {
        this.context = context;
        this.services = services;
        this.registerBusinessStepSevenFragment = registerBusinessStepSevenFragment;
        inflater = (LayoutInflater.from(context));
    }

    public void setServices(List<Service> items){
        this.services = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int position) {
        return services.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageButton deleteBtn;
        TextView txt_name;
        TextView txt_duration;
        TextView txt_cost;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        try {
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.adapter_item_service, null);
                holder.deleteBtn = (ImageButton) convertView.findViewById(R.id.bt_name_service);
                holder.deleteBtn.setTag(position);
                holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name_service);
                holder.txt_duration = (TextView) convertView.findViewById(R.id.txt_duration);
                holder.txt_cost = (TextView) convertView.findViewById(R.id.txt_cost);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txt_name.setText(services.get(position).getName());
            holder.txt_duration.setText(services.get(position).getTime());
            String precio = Float.toString(services.get(position).getPrice());
            holder.txt_cost.setText(precio+"€");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerBusinessStepSevenFragment.sendDataService(position, true);
                }
            });
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton deleteBt = (ImageButton) v;
                    int position = (int) deleteBt.getTag();
                    services.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Borrado", Toast.LENGTH_SHORT).show();
                }
            });
            convertView.setBackgroundColor(Color.WHITE);
        }   catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
