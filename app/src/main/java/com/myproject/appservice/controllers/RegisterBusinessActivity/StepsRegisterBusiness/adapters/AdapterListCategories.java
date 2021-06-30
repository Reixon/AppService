package com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.myproject.appservice.R;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepSixFragment;

import java.util.ArrayList;

public class AdapterListCategories extends BaseAdapter{

    private String [] categories;
    private boolean [] selected;
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<String> categoriesSelected;
    private static final String LOG ="LOG";
    private RegisterBusinessStepSixFragment fragment;

    public AdapterListCategories(Context context, String [] categories,
                                 ArrayList<String>categoriesSelected,
                                 LocalBroadcastManager localBroadcastManager,
                                 RegisterBusinessStepSixFragment fragment) {
        this.context = context;
        this.categories = categories;
        this.categoriesSelected = categoriesSelected;
        this.selected = new boolean[categories.length];
        this.fragment = fragment;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Object getItem(int position) {
        return categories[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        try {
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.adapter_category_business, null);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                holder.checkBox.setText(categories[position]);
                holder.checkBox.setChecked(selected[position]);
                holder.checkBox.setTag(position);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (int) buttonView.getTag();
                    if(isChecked){
                        categoriesSelected.add(categories[position]);
                    } else {
                        categoriesSelected.remove(categories[position]);
                    }
                    fragment.checkCategories();
                }
            });

            convertView.setBackgroundColor(Color.WHITE);
        }   catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
