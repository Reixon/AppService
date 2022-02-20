package com.myproject.appservice.controllers.ViewMainCustomer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.appservice.R;
import com.myproject.appservice.databinding.FragmentListViewProfilCustomerBinding;
import com.myproject.appservice.models.Booking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfilView extends Fragment{

    private List<Integer> settings;
    private RecyclerView listSettings;
    private AdapterListView adapter;
    private View mProgressView;
    private FragmentListViewProfilCustomerBinding binding;

    public ProfilView() {
        settings = Collections.singletonList(R.array.settings);
    }

    @Override
    public void onStart() {
        super.onStart();
        showProgress(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentListViewProfilCustomerBinding.inflate(inflater, container, false);
        mProgressView = binding.progressCircular;
        listSettings = binding.listServices;
        listSettings.setHasFixedSize(true);
        listSettings.setLayoutManager(new LinearLayoutManager(this.requireContext().getApplicationContext()));

        return binding.getRoot();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        LinearLayout linearLayout = binding.layout;
        linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        linearLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public class AdapterListView extends RecyclerView.Adapter<AdapterListView.ViewHolder>{

        private final Context context;
        private ArrayList<String> settings;

        public AdapterListView(Context context, ArrayList<Booking> bookings) {
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_profit, parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textItem.setText(settings.get(position));
        }

        @Override
        public int getItemCount() {
            return settings.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView textItem;


            public ViewHolder(@NonNull View convertView) {
                super(convertView);
                textItem = convertView.findViewById(R.id.textItem);
                convertView.setBackgroundColor(Color.WHITE);
            }
        }
    }

}