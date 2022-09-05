package com.myproject.appservice.controllers.viewMainCustomer.BookingActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myproject.appservice.Common;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.calendarBusiness.CalendarUtils;
import com.myproject.appservice.controllers.viewMainCustomer.ViewBusiness.ConsultBusinessView;
import com.myproject.appservice.controllers.viewMainCustomer.ViewMainCustomer;
import com.myproject.appservice.databinding.ActivityConsultServiceViewBinding;
import com.myproject.appservice.models.Booking;
import com.myproject.appservice.models.Business;
import com.myproject.appservice.models.Schedule;
import com.myproject.appservice.models.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class BookingServiceActivity extends AppCompatActivity {

    private  ActivityConsultServiceViewBinding binding;
    private Calendar calendar;
    private List<Date> dates = new ArrayList<>();
    private Business business;
    private String nameBusiness;
    private ArrayList<String> schedulesBooking;
    private BookingServiceListAdapter adapterService;
    private ArrayList<ToggleButton> btScheduleList;
    private ArrayList<Service> services;

    private ImageButton backArrow;
    private TextView title;
    private CalendarView calendarView;
    private HorizontalScrollView horizontalScrollView;
    private RecyclerView listView;
    private ImageButton btAddService;
    private TextView textAddService;
    private Button btDoBooking;
    private View mProgressView;
    private  int dayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultServiceViewBinding.inflate(getLayoutInflater());
        backArrow = binding.backArrow;
        title = binding.title;
        calendarView = binding.viewPagerGrid;
        horizontalScrollView = binding.horizontalScroll;
        listView = binding.listServices;
        btAddService = binding.btAddService;
        textAddService = binding.addServiceTxt;
        btDoBooking = binding.btBooking;
        mProgressView = binding.circularProgress;
        View view = binding.getRoot();
        setContentView(view);
        business = (Business) Objects.requireNonNull(getIntent().getExtras()).get("Business");
        Service service = (Service) getIntent().getExtras().get("Service");
        nameBusiness = business.getNameBusiness();
        services = new ArrayList<>();
        services.add(service);
        adapterService = new BookingServiceListAdapter(this, business.getId(), services);
        TimeZone tz = TimeZone.getTimeZone("GMT+01:00");
        calendar = Calendar.getInstance(tz);

        dayOfWeek = CalendarUtils.sortDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));

        initialDataSchedule();
        initialListeners();
    }

    /*
    private boolean checkCurrentHourHasService(ArrayList<String> schedulesDay){
        String[] range = schedulesDay.get(schedulesDay.size()-1).split(" - ");
        int afterHour = Integer.parseInt(range[1].split(":")[0]);
        return (afterHour > calendar.get(Calendar.HOUR_OF_DAY));
    }*/

    private void initialDataSchedule(){
        showProgress(true);
        String [] days = getResources().getStringArray(R.array.array_days_week);
        Task<QuerySnapshot> schedulesRef = FirebaseFirestore.getInstance()
            .collection("Businesses")
            .document(business.getId())
            .collection("Schedules")
            .whereEqualTo("day", days[dayOfWeek])
            .get();
        schedulesRef.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().size() > 0){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", document.getId() + " => " + document.getData());
                        schedulesBooking = new ArrayList<>();
                        Schedule schedules = document.toObject(Schedule.class);
                        if (schedules.getSchedulesDay() != null) {
                            calendarView.setMinDate(calendar.getTimeInMillis());
                            descomposeSchedule(schedules);
                        } else {
                            calendar.add(Calendar.DATE, 1);
                            calendarView.setDate(calendar.getTimeInMillis(), true, true);
                            calendarView.setMinDate(calendar.getTimeInMillis());
                            initialDataSchedule();
                        }
                    }
                } else {
                    calendar.add(Calendar.DATE, 1);
                    calendarView.setDate(calendar.getTime().getTime());
                    initialDataSchedule();
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isPastDay(){

        TimeZone tz = TimeZone.getTimeZone("GMT+01:00");
        Calendar today = Calendar.getInstance(tz);

        String sDate =  calendar.get(Calendar.YEAR)+"-"+
                calendar.get(Calendar.MONTH)+"-"+
                calendar.get(Calendar.DAY_OF_MONTH);

        String tDate =  today.get(Calendar.YEAR)+"-"+
                today.get(Calendar.MONTH)+"-"+
                today.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

        Date dToday = null;
        Date dSelect = null;
        boolean com = false;
        try {
            dSelect = sdformat.parse(sDate);
            dToday = sdformat.parse(tDate);

            com = dSelect.before(dToday);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  com;
    }

    private void loadSchedule(){
        String [] days = getResources().getStringArray(R.array.array_days_week);
        // Comprobar si el día del calendario es igual o superior al día actual
        if(!isPastDay()) {
            Task<QuerySnapshot> schedulesRef = FirebaseFirestore.getInstance()
                .collection("Businesses")
                .document(business.getId())
                .collection("Schedules")
                .whereEqualTo("opened", true)
                .whereEqualTo("day", days[dayOfWeek])
                .get();
            schedulesRef.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("TAG", document.getId() + " => " + document.getData());
                            schedulesBooking = new ArrayList<>();
                            Schedule schedules = document.toObject(Schedule.class);
                            if (schedules.getSchedulesDay() != null) {
                                descomposeSchedule(schedules);
                            } else {
                                loadScheduleInterfaceNoAvailable();
                            }
                        }
                    } else {
                        loadScheduleInterfaceNoAvailable();;
                    }
                }
            });

        } else {
            loadScheduleInterfaceNoAvailable();
        }
    }

    private void descomposeSchedule(Schedule schedules){
        for (int i = 0; i < schedules.getSchedulesDay().size(); i++) {
            //cargar los horarios del día
            String[] range = schedules.getSchedulesDay().get(i).split(" - ");
            int beforeHour = Integer.parseInt(range[0].split(":")[0]);
            int beforeMinute;
            if (range[0].split(":")[1].contains("00")) {
                beforeMinute = 0;
            } else {
                String x = range[0].split(":")[1];
                beforeMinute = Integer.parseInt(x);
            }
            int afterHour = Integer.parseInt(range[1].split(":")[0]);
            int afterMinute;
            if (range[0].split(":")[1].equals("00")) {
                afterMinute = 0;
            } else {
                String x = range[1].split(":")[1];
                afterMinute = Integer.parseInt(x);
            }
            int resM = afterMinute / 15;
            int res = (afterHour - beforeHour) * 4 + resM;
            generateArraySchedule(beforeHour, beforeMinute, res);
        }
        removeBookingService();
    }

    private void loadScheduleInterfaceNoAvailable(){
        LinearLayout linearLayout = binding.layoutHours;
        linearLayout.removeAllViews();
        TextView txt = new TextView(this);
        txt.setText(getResources().getString(R.string.info_schedule_no_available));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(18,18,18,18);
        linearLayout.addView(txt);
    }

    private void loadScheduleInterface(){
        LinearLayout linearLayout = binding.layoutHours;
        linearLayout.removeAllViews();
        btScheduleList = new ArrayList<>();
        for(int i = 0; i < schedulesBooking.size(); i++) {

            ToggleButton button = new ToggleButton(this);
            button.setTextOff(schedulesBooking.get(i));
            button.setTextOn(schedulesBooking.get(i));
            button.setText(schedulesBooking.get(i));
            button.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_button, getTheme()));
            button.setId(i);
            btScheduleList.add(button);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(18, 18, 18, 18);
            button.setLayoutParams(params);
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        int position = (int) buttonView.getId();
                        Common.slotSchedule = position;
                        int totalMinute = Integer.parseInt(schedulesBooking.get(position).split(":")[1]);
                        int totalHour = Integer.parseInt(schedulesBooking.get(position).split(":")[0]);

                        String txtHour = "", txtMinute ="";
                        String[] time = null;

                        for(int i = 0; i < services.size(); i++){
                            if(services.get(i).getTime().contains(":")) {
                                time = services.get(i).getTime().split(":");
                                if (Integer.parseInt(time[1]) < 60) {
                                    totalMinute += Integer.parseInt(time[1]);
                                } else {
                                    totalMinute = 0;
                                    totalHour += 1;
                                }
                                totalHour += Integer.parseInt(time[1]);
                            } else if(services.get(i).getTime().contains("m")){
                                time = services.get(i).getTime().split("m");
                                if ((Integer.parseInt(time[0])+ totalMinute ) < 60) {
                                    totalMinute += Integer.parseInt(time[0]);
                                } else {
                                    totalMinute = (Integer.parseInt(time[0]) + totalMinute) - 60;
                                    totalHour += 1;
                                }
                            } else if(services.get(i).getTime().contains("h")){
                                time = services.get(i).getTime().split("h");
                                totalHour +=Integer.parseInt(time[0]);
                            }
                        }
                        if(totalHour <= 9){
                            txtHour= "0"+totalHour;
                        } else {
                            txtHour= totalHour+"";
                        }
                        if(totalMinute < 9){
                            txtMinute ="0"+totalMinute;
                        } else {
                            txtMinute = totalMinute+"";
                        }
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String untilTime = txtHour + ":" + txtMinute;
                        Common.rangeHours = schedulesBooking.get(position) + " - " + untilTime;

                        Common.timeSchedule = Common.rangeHours +
                                " at " +
                                simpleDateFormat.format(calendar.getTime());
                        Common.dateSchedule = simpleDateFormat.format(calendar.getTime());

                        for(int i = 0 ; i< btScheduleList.size(); i++){
                            if(buttonView.getId() != i) {
                                btScheduleList.get(i).setChecked(false);
                            }
                        }
                        buttonView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_selected_button, getTheme()));
                        adapterService.notifyDataSetChanged();
                    } else {
                        buttonView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_button, getTheme()));
                    }
                    showProgress(false);
                }
            });
            linearLayout.addView(button);
        }
        btScheduleList.get(0).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_selected_button, getTheme()));
        btScheduleList.get(0).setChecked(true);
    }

    private void removeBookingService(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        CollectionReference serviceRef = FirebaseFirestore.getInstance()
            .collection("Businesses")
            .document(business.getId())
            .collection("Booking")
            .document(Common.idEmployee)//ID DEL EMPLEADO
            .collection(simpleDateFormat.format(calendar.getTime()));
        serviceRef.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Booking booking = document.toObject(Booking.class);
                            String[] date = booking.getTime().split(" at")[0].split(" - ");
                            int forPosition = schedulesBooking.indexOf(date[0]);
                            int ultPosition = schedulesBooking.indexOf(date[1]);
                            for(int i = forPosition; i <= ultPosition; i++){
                                schedulesBooking.remove(forPosition);
                            }
                        }
                    }
                    loadScheduleInterface();
                }
            });
    }

    private void generateArraySchedule(int beforeHour, int beforeMinute, int res){
        String bH = Integer.toString(beforeHour);
        String bM = Integer.toString(beforeMinute);
        if(beforeHour == 0){
            bH = "00";
        }
        if(beforeMinute == 0){
            bM = "00";
        }
        int minutes = 0;
        int hours = beforeHour;
        schedulesBooking.add(bH + ":" +bM );
        for(int x = 1; x <= res; x++){
            if( x % 4 == 0 && x > 0){
                hours += 1;
                bH = hours+"";
            }
            if(x > 0){
                minutes += 15;
                bM = minutes+"";
            }
            if( x % 4 == 0 && x > 0){
                bM = "00";
                minutes = 0;
            }
            schedulesBooking.add(bH + ":" +bM );
        }

    }

    private void initialListeners(){
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapterService);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                loadSchedule();
                Log.i("LOG", "dia "+dayOfMonth+" mes " + month + " año " +year +
                        " calendar AÑO "+calendar.get(Calendar.YEAR) + " MES " + calendar.get(Calendar.MONTH) + " dia "+calendar.get(Calendar.DAY_OF_MONTH));
            }
        });

        btAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAddService();
            }
        });

        textAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAddService();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConsultBusinessView.class);
                startActivity(intent
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
        });

        btDoBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String [] convertTime = Common.rangeHours.split(" - ");
                String[] startTimeConvert = convertTime[0].split(":");
                int startHourInt = Integer.parseInt(startTimeConvert[0]);
                int startMinInt = Integer.parseInt(startTimeConvert[1]);

                Calendar bookingDateWithourHouse = Calendar.getInstance();
            //    bookingDateWithourHouse.setTimeInMillis(calendar.getTimeInMillis());
                bookingDateWithourHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
                bookingDateWithourHouse.set(Calendar.MINUTE, startMinInt);

                Timestamp timestamp = new Timestamp(bookingDateWithourHouse.getTime());

                // DO BOOKING
                Booking booking = new Booking();
                booking.setTimestamp(timestamp);
                booking.setCustomer(Common.nameUser);
                booking.setEmailCustomer(Common.emailUser);
                booking.setTime(Common.timeSchedule);
                booking.setIdBusiness(business.getId());
                booking.setBusiness(nameBusiness);
                booking.setServices(services);
                booking.setLatitude(business.getLatitude());
                booking.setLongitude(business.getLongitude());
                booking.setSlot(Common.slotSchedule);
                booking.setDone(false);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");

                DocumentReference bookingRef = FirebaseFirestore.getInstance()
                    .collection("Businesses")
                    .document(business.getId())
                    .collection("Booking")
                    .document(Common.idEmployee)//ID DEL EMPLEADO
                    .collection(simpleDateFormat.format(calendar.getTime()))
                    .document();
                booking.setId(bookingRef.getId());
                bookingRef.set(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        addToUserBooking(booking);
                    }
                });

            }
        });
    }

    private void addToUserBooking(Booking booking) {
        DocumentReference bookingRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.idUser)
                .collection("Booking")
                .document(booking.getId());
        bookingRef.set(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(BookingServiceActivity.this, ViewMainCustomer.class);
                startActivity(intent
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                Toast.makeText(BookingServiceActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        RelativeLayout relativeLayout = binding.generalLayout;
        relativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        relativeLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                relativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void clickAddService(){

    }
}