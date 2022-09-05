package com.myproject.appservice.controllers.viewMainCustomer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.login.LoginActivity;
import com.myproject.appservice.databinding.FragmentListViewProfileCustomerBinding;

import java.util.Arrays;
import java.util.List;

public class ProfileView extends Fragment{

    private RecyclerView listSettings;
    private View mProgressView;
    private FragmentListViewProfileCustomerBinding binding;

    public ProfileView() {

    }

    @Override
    public void onStart() {
        super.onStart();
        showProgress(true);
        List<String> settings = Arrays.asList(getResources().getStringArray(R.array.settings));
        AdapterListView adapter = new AdapterListView(getContext(), settings);
        listSettings.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        showProgress(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentListViewProfileCustomerBinding.inflate(inflater, container, false);
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

    public static class AdapterListView extends RecyclerView.Adapter<AdapterListView.ViewHolder>{

        private final Context context;
        private final  List<String> settings;

        public AdapterListView(Context context, List<String> settings) {
            this.context = context;
            this.settings = settings;
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
            holder.cardView.setTag(position);
            holder.cardView.setOnClickListener(v -> {
                int position1 = (int) v.getTag();
                switch (position1){
                    case 0: //CERRAR SESIÓN
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setMessage(R.string.message_logout).setPositiveButton(R.string.msg_confirmation,
                                (dialog, which) -> logout()).setNegativeButton(R.string.msg_negation, null);
                        AlertDialog alert1 = alert.create();
                        alert1.show();
                        break;
                }
            });
        }

        private void logout(){
            FirebaseAuth.getInstance().signOut();
            context.startActivity(new Intent(context, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            Activity activity = (Activity) context;
            activity.finish();
        }

        @Override
        public int getItemCount() {
            return settings.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView textItem;
            private CardView cardView;

            public ViewHolder(@NonNull View convertView) {
                super(convertView);
                cardView = convertView.findViewById(R.id.item_setting);
                textItem = convertView.findViewById(R.id.textItem);
                convertView.setBackgroundColor(Color.WHITE);
            }
        }
    }

}