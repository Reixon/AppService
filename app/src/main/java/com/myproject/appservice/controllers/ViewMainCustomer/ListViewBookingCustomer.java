package com.myproject.appservice.controllers.ViewMainCustomer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.ViewMainCustomer.ConsultBookingDetail.ConsultBookingDetail;
import com.myproject.appservice.databinding.FragmentListViewBookingCustomerBinding;
import com.myproject.appservice.models.Booking;
import com.myproject.appservice.models.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListViewBookingCustomer extends Fragment{

    private ArrayList<Booking> bookings;
    private RecyclerView listBooking;
    private AdapterListViewBooking adapter;
    private View mProgressView;
    private FragmentListViewBookingCustomerBinding binding;

    public ListViewBookingCustomer() {
        bookings = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        showProgress(true);

        FirebaseFirestore.getInstance().collection("Users")
        .document(Common.idUser).collection("Booking")
        .orderBy("timestamp", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(!task.getResult().isEmpty()){
                            bookings = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                Booking booking = queryDocumentSnapshot.toObject(Booking.class);
                                bookings.add(booking);
                            }
                            adapter = new AdapterListViewBooking(getContext(), bookings);
                            listBooking.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            showProgress(false);
                        }
                    }
                }).addOnFailureListener(e -> {

                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentListViewBookingCustomerBinding.inflate(inflater, container, false);
        mProgressView = binding.progressCircular;
        listBooking = binding.listServices;
        listBooking.setHasFixedSize(true);
        listBooking.setLayoutManager(new LinearLayoutManager(this.requireContext().getApplicationContext()));

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

    public class AdapterListViewBooking extends RecyclerView.Adapter<AdapterListViewBooking.ViewHolder>{

        private ArrayList<Booking> bookings;
        private final Context context;

        public AdapterListViewBooking(Context context, ArrayList<Booking> bookings) {
            this.context = context;
            this.bookings = bookings;
        }

        @NonNull
        @Override
        public AdapterListViewBooking.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_booking_customer, parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterListViewBooking.ViewHolder holder, int position) {
            StringBuilder services = new StringBuilder();
            for (int i = 0; i< bookings.get(position).getServices().size(); i++){
                if(i>0){
                    services.append(",");
                }
                services.append(bookings.get(position).getServices().get(i).getName());
            }
            holder.txtService.setText(services.toString());
            holder.txtNameBusiness.setText(bookings.get(position).getBusiness());
            holder.txtAddress.setText(bookings.get(position).getAddress());
            StringBuilder serviceInfo = new StringBuilder();
            Booking booking = bookings.get(position);
            for(Service service : booking.getServices()){
                serviceInfo.append(service.getName()).append(" ");
            }
            holder.txtService.setText(serviceInfo);
            String timeInfo = booking.getTime().split(" at")[0];
            holder.txtTime.setText(timeInfo.split(" - ")[0]);
            String dayInfo = booking.getTime().split(" at")[1].split("/")[0];
            holder.txtDay.setText(dayInfo);
            String date = booking.getTime();
            Calendar calendar = Calendar.getInstance();
            Date d = booking.getTimestamp().toDate();
            calendar.setTime(d);
            @SuppressLint("SimpleDateFormat") String nameMonth = new SimpleDateFormat("MMMM").format(calendar.getTime()).toUpperCase();
            holder.txtMonth.setText(nameMonth);
            holder.btBooking.setTag(position);
            holder.cardView.setTag(position);
            Calendar today = Calendar.getInstance();
            if(calendar.getTime().getTime() > today.getTime().getTime()){
                holder.btBooking.setVisibility(View.VISIBLE);
            } else {
                holder.btBooking.setVisibility(View.GONE);
            }
            holder.btBooking.setOnClickListener(v -> {

            });
            holder.cardView.setOnClickListener(v -> {
                int position1 = (int) v.getTag();
                Intent intent = new Intent(context, ConsultBookingDetail.class);
                intent.putExtra("Booking", bookings.get(position1).getId());
                intent.putExtra("Business", bookings.get(position1).getIdBusiness());
                context.startActivity(intent);
            });

        }

        @Override
        public int getItemCount() {
            return this.bookings.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtNameBusiness, txtService, txtAddress, txtMonth, txtDay, txtTime;
            private Button btBooking;
            private CardView cardView;

            public ViewHolder(@NonNull View convertView) {
                super(convertView);
                cardView = convertView.findViewById(R.id.booking_notification);
                txtNameBusiness = convertView.findViewById(R.id.txt_nameBusiness);
                txtService = convertView.findViewById(R.id.txt_service);
                txtAddress = convertView.findViewById(R.id.txt_address);
                txtMonth = convertView.findViewById(R.id.month);
                txtDay = convertView.findViewById(R.id.day);
                txtTime = convertView.findViewById(R.id.time);
                btBooking = convertView.findViewById(R.id.bt_booking);
                convertView.setBackgroundColor(Color.WHITE);
            }
        }
    }

}