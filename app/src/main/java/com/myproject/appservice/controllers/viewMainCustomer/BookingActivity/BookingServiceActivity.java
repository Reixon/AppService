package com.myproject.appservice.controllers.viewMainCustomer.BookingActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
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
import com.myproject.appservice.controllers.viewMainBusiness.calendarBusiness.CalendarUtils;
import com.myproject.appservice.controllers.viewMainCustomer.SearchService.ListServiceActivity;
import com.myproject.appservice.controllers.viewMainCustomer.ViewBusiness.ConsultBusinessActivity;
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

public class BookingServiceActivity extends AppCompatActivity implements DefaultLifecycleObserver {

    private static final String TAG = "BOOKING_SERVICE_ACTIVITY";
    private ActivityConsultServiceViewBinding binding;
    private Calendar calendar;
    private List<Date> dates = new ArrayList<>();
    private Business business;
    private String nameBusiness;
    private ArrayList<String> schedulesBooking;
    private BookingServiceListAdapter adapterService;
    private ArrayList<ToggleButton> btScheduleList;
    private ArrayList<Service> services;

    private ImageButton backArrow;
    private CalendarView calendarView;
    private RecyclerView listView;
    private ImageButton btAddService;
    private TextView textAddService;
    private Button btDoBooking;
    private View mProgressView;
    private int dayOfWeek;
    private ActivityResultLauncher<Intent> getAddService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultServiceViewBinding.inflate(getLayoutInflater());
        backArrow = binding.backArrow;
        TextView title = binding.title;
        calendarView = binding.viewPagerGrid;
        HorizontalScrollView horizontalScrollView = binding.horizontalScroll;
        listView = binding.listServices;
        btAddService = binding.btAddService;
        textAddService = binding.addServiceTxt;
        btDoBooking = binding.btBooking;
        mProgressView = binding.progressCircular;

        View view = binding.getRoot();
        setContentView(view);
        business = (Business) Objects.requireNonNull(getIntent().getExtras()).get("Business");
        Service service = (Service) getIntent().getExtras().get("Service");
        nameBusiness = business.getNameBusiness();
        services = new ArrayList<>();
        services.add(service);
        adapterService = new BookingServiceListAdapter(this, business.getId(), services);
        calendar = Calendar.getInstance( TimeZone.getTimeZone("GMT+02:00"));
        System.out.println(calendar);

        showProgress(true);
        initialDataSchedule();
        initialListeners();

        getAddService = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    try {
                        if (result.getResultCode() == RESULT_OK && null != result.getData()) {
                            Bundle bundle = result.getData().getExtras();
                            assert bundle != null;
                            Service serviceResult = (Service) bundle.get("Service");
                            adapterService.addService(serviceResult);
                            adapterService.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    /*
    private boolean checkCurrentHourHasService(ArrayList<String> schedulesDay){
        String[] range = schedulesDay.get(schedulesDay.size()-1).split(" - ");
        int afterHour = Integer.parseInt(range[1].split(":")[0]);
        return (afterHour > calendar.get(Calendar.HOUR_OF_DAY));
    }*/

    private void initialDataSchedule() {
        dayOfWeek = CalendarUtils.sortDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        String[] days = getResources().getStringArray(R.array.array_days_week);
        Task<QuerySnapshot> schedulesRef = FirebaseFirestore.getInstance()
                .collection("Businesses")
                .document(business.getId())
                .collection("Schedules")
                .whereEqualTo("day", days[dayOfWeek])
                .whereEqualTo("opened", true)
                .get();
        schedulesRef.addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    schedulesBooking = new ArrayList<>();
                    Schedule schedules = document.toObject(Schedule.class);
                    ArrayList<String> divideSchedule = validateRangeHours(schedules.getSchedulesDay());
                    if (schedules.getSchedulesDay() != null && divideSchedule.size() > 0) {
                        calendarView.setMinDate(calendar.getTimeInMillis());
                        decomposeSchedule(divideSchedule);
                    } else {
                        Log.d(TAG, "next day " + calendar.getTime());
                        calendar.add(Calendar.DATE, 1);
                        calendarView.setMinDate(calendar.getTimeInMillis());
                        initialDataSchedule();
                    }
                    System.out.println(new Date(calendarView.getDate()));
                }
            } else {
                calendar.add(Calendar.DATE, 1);
                calendarView.setDate(calendar.getTime().getTime());
                initialDataSchedule();
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isPastDay() {

        TimeZone tz = TimeZone.getTimeZone("GMT+01:00");
        Calendar today = Calendar.getInstance(tz);

        String sDate = calendar.get(Calendar.YEAR) + "-" +
                calendar.get(Calendar.MONTH) + "-" +
                calendar.get(Calendar.DAY_OF_MONTH);

        String tDate = today.get(Calendar.YEAR) + "-" +
                today.get(Calendar.MONTH) + "-" +
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

        return com;
    }

    private void loadSchedule() {
        dayOfWeek = CalendarUtils.sortDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        String[] days = getResources().getStringArray(R.array.array_days_week);
        // Comprobar si el d??a del calendario es igual o superior al d??a actual
        if (!isPastDay()) {
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
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            schedulesBooking = new ArrayList<>();
                            Schedule schedules = document.toObject(Schedule.class);
                            ArrayList<String> divideSchedule = validateRangeHours(schedules.getSchedulesDay());
                            if (schedules.getSchedulesDay() != null && divideSchedule.size() > 0) {
                                decomposeSchedule(divideSchedule);
                            } else {
                                loadScheduleInterfaceNoAvailable();
                            }
                        }
                    } else {
                        loadScheduleInterfaceNoAvailable();
                    }
                }
            });

        } else {
            loadScheduleInterfaceNoAvailable();
        }
    }

    private int loadTimeService() {
        int sumTime = 0;
        for (int i = 0; i < services.size(); i++) {
            String time = services.get(i).getTime().split("m")[0];
            sumTime += Integer.parseInt(time);
        }
        return sumTime;
    }

    private ArrayList<String> validateRangeHours(ArrayList<String> schedulesBooking) {
        ArrayList<String> newScheduleDay = new ArrayList<>();
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));

        int sumTimeService = loadTimeService();
//        SimpleDateFormat sdfMadrid = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//        sdfMadrid.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
//        System.out.printf("Hora Madrid:\t\t %s\n", sdfMadrid.format(calendar.getTime()));
//        System.out.printf("Hora Madrid:\t\t %s\n", sdfMadrid.format(now.getTime()));

        float hour = now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0f;
        if (CalendarUtils.isSameDay(calendar, now)) {
            if (hour < Integer.parseInt(schedulesBooking.get(schedulesBooking.size() - 1).split(" - ")[1].split(":")[0])) {
                for (int i = 0; i < schedulesBooking.size(); i++) {
                    String[] range = schedulesBooking.get(i).split(" - ");
                    int afterMinute = Integer.parseInt(range[1].split(":")[1]);
                    int beforeHour = Integer.parseInt(range[0].split(":")[0]);
                    int afterHour = Integer.parseInt(range[1].split(":")[0]);
                    float timeBefore = Float.parseFloat(range[0].split(":")[0]) +
                            (float) afterMinute / 60.0f;
                    while(hour > timeBefore){
                        timeBefore+=0.25;
                    }
                    beforeHour = Math.round(timeBefore);
                    float timeAfter = Float.parseFloat(range[1].split(":")[0]) +
                            Float.parseFloat(range[1].split(":")[1]) / 60.0f;
                    if (hour <= timeBefore && hour < timeAfter) {
                        int timeCheck = (afterHour - beforeHour) * sumTimeService + afterMinute / 15;
                        for (int x = 0; x < timeCheck; x++) {
                            float compareHour = beforeHour + (x * (0.25f * sumTimeService));
                            if (hour <= compareHour) {
                                int minutes = (int) (x * (0.25f * sumTimeService) - Math.ceil(x * (0.25f * sumTimeService))) * 60;
                                String minuteTxt = minutes == 0 ? "00" : Integer.toString(minutes);
                                int firstHour = (int) Math.ceil(compareHour);
                                newScheduleDay.add(firstHour + ":" + minuteTxt + " - " + range[1]);
                                break;
                            }
                        }
                    }
                }
            }
            return newScheduleDay;
        }
        return schedulesBooking;
    }

    private void decomposeSchedule(ArrayList<String> schedules) {
        for (int i = 0; i < schedules.size(); i++) {
            //cargar los horarios del d??a
            String[] range = schedules.get(i).split(" - ");
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
            if (range[1].split(":")[1].equals("00")) {
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


    private void generateArraySchedule(int beforeHour, int beforeMinute, int res) {
        String bH = Integer.toString(beforeHour);
        String bM = Integer.toString(beforeMinute);
        if (beforeHour == 0) {
            bH = "00";
        } else if(beforeHour > 0 && beforeHour < 10){
            bH = "0"+bH;
        }
        if (beforeMinute == 0) {
            bM = "00";
        } else if(beforeMinute > 0 && beforeHour < 10) {
            bM = "0"+bM;
        }
        int minutes = 0;
        int hours = beforeHour;
        schedulesBooking.add(bH + ":" + bM);
        for (int x = 1; x < res; x++) {
            if (x % 4 == 0 && x > 0) {
                hours += 1;
                bH = hours + "";
            }
            if (x > 0) {
                minutes += 15;
                bM = minutes + "";
            }
            if (x % 4 == 0 && x > 0) {
                bM = "00";
                minutes = 0;
            }
            schedulesBooking.add(bH + ":" + bM);
        }

    }

//    private void generateArraySchedule(int beforeHour, int beforeMinute, float res, int sumTimeService) {
//        String bH = Integer.toString(beforeHour);
//        String bM = Integer.toString(beforeMinute);
//        if (beforeHour == 0) {
//            bH = "00";
//        }
//        if (beforeMinute == 0) {
//            bM = "00";
//        }
//        int minutes = 0;
//        int hours = beforeHour;
//        schedulesBooking.add(bH + ":" + bM);
//        for (int x = 1; x < res; x++) {
//            if (x % (60/sumTimeService) == 0) {
//                hours += 1;
//                bH = hours + "";
//            }
//            minutes += sumTimeService;
//            bM = minutes + "";
//            if (x % (60/sumTimeService) == 0) {
//                bM = "00";
//                minutes = 0;
//            }
//            schedulesBooking.add(bH + ":" + bM);
//        }
//
//    }

    private void loadScheduleInterfaceNoAvailable() {
        HorizontalScrollView horizontalScrollView = binding.horizontalScroll;
        horizontalScrollView.setBackgroundColor(Color.GRAY);
        LinearLayout linearLayout = binding.layoutHours;
        linearLayout.removeAllViews();
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(18, 18, 18, 18);
        params.setMarginStart(33);
        TextView txt = new TextView(this);
        txt.setTextColor(Color.WHITE);
        txt.setText(getResources().getString(R.string.info_schedule_no_available));
        txt.setLayoutParams(params);
        linearLayout.addView(txt);
        btDoBooking.setEnabled(false);
        btAddService.setEnabled(false);
        textAddService.setEnabled(false);
        Button findNearEnableDate = new Button(this);
        findNearEnableDate.setTextSize(13);
        findNearEnableDate.setText(getResources().getString(R.string.txt_find_first_enable_data));
        findNearEnableDate.setGravity(Gravity.CENTER);
        findNearEnableDate.setLayoutParams(params);
        linearLayout.addView(findNearEnableDate);


        findNearEnableDate.setOnClickListener(v -> {

        });

    }

    private void loadScheduleInterface() {
        LinearLayout linearLayout = binding.layoutHours;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.removeAllViews();
        HorizontalScrollView horizontalScrollView = binding.horizontalScroll;
        horizontalScrollView.setBackgroundColor(Color.WHITE);
        btScheduleList = new ArrayList<>();
        for (int i = 0; i < schedulesBooking.size(); i++) {

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
            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                clickSchedule(isChecked, buttonView);
            });
            linearLayout.addView(button);
            btDoBooking.setEnabled(true);
            btAddService.setEnabled(true);
            textAddService.setEnabled(true);
            showProgress(false);
        }
        if (btScheduleList.size() > 0) {
            btScheduleList.get(0).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_selected_button, getTheme()));
            btScheduleList.get(0).setChecked(true);
        }
    }

    private void clickSchedule(boolean isChecked, Button buttonView){
        if (isChecked) {
            int position = (int) buttonView.getId();
            Common.slotSchedule = position;
            int totalMinute = Integer.parseInt(schedulesBooking.get(position).split(":")[1]);
            int totalHour = Integer.parseInt(schedulesBooking.get(position).split(":")[0]);

            String txtHour = "", txtMinute = "";
            String[] time = null;

            for (int i1 = 0; i1 < services.size(); i1++) {
                if (services.get(i1).getTime().contains(":")) {
                    time = services.get(i1).getTime().split(":");
                    if (Integer.parseInt(time[1]) < 60) {
                        totalMinute += Integer.parseInt(time[1]);
                    } else {
                        totalMinute = 0;
                        totalHour += 1;
                    }
                    totalHour += Integer.parseInt(time[1]);
                } else if (services.get(i1).getTime().contains("m")) {
                    time = services.get(i1).getTime().split("m");
                    if ((Integer.parseInt(time[0]) + totalMinute) < 60) {
                        totalMinute += Integer.parseInt(time[0]);
                    } else {
                        totalMinute = (Integer.parseInt(time[0]) + totalMinute) - 60;
                        totalHour += 1;
                    }
                } else if (services.get(i1).getTime().contains("h")) {
                    time = services.get(i1).getTime().split("h");
                    totalHour += Integer.parseInt(time[0]);
                }
            }
            if (totalHour <= 9) {
                txtHour = "0" + totalHour;
            } else {
                txtHour = totalHour + "";
            }
            if (totalMinute < 9) {
                txtMinute = "0" + totalMinute;
            } else {
                txtMinute = totalMinute + "";
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String untilTime = txtHour + ":" + txtMinute;
            Common.rangeHours = schedulesBooking.get(position) + " - " + untilTime;

            Common.timeSchedule = Common.rangeHours +
                    " at " +
                    simpleDateFormat.format(calendar.getTime());
            Common.dateSchedule = simpleDateFormat.format(calendar.getTime());

            for (int i1 = 0; i1 < btScheduleList.size(); i1++) {
                if (buttonView.getId() != i1) {
                    btScheduleList.get(i1).setChecked(false);
                }
            }
            buttonView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_selected_button, getTheme()));
            adapterService.notifyDataSetChanged();
        } else {
            buttonView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.border_button, getTheme()));
        }
    }

    private void removeBookingService() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        CollectionReference serviceRef = FirebaseFirestore.getInstance()
                .collection("Businesses")
                .document(business.getId())
                .collection("Booking")
                .document(Common.idEmployee)//ID DEL EMPLEADO
                .collection(simpleDateFormat.format(calendar.getTime()));
        serviceRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Booking booking = document.toObject(Booking.class);
                            String[] date = booking.getTime().split(" at")[0].split(" - ");
                            int forPosition = schedulesBooking.indexOf(date[0]);
                            int ultPosition = schedulesBooking.indexOf(date[1]);
                            ArrayList copy = schedulesBooking;

                            for (int i = forPosition; i <= ultPosition; i++) {
                                copy.remove(forPosition);
                            }
                            schedulesBooking = copy;
                        }
                    }
                    loadScheduleInterface();
                });
    }


    private void initialListeners() {
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapterService);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            loadSchedule();
            Log.i("LOG", "dia " + dayOfMonth + " mes " + month + " a??o " + year +
                    " calendar A??O " + calendar.get(Calendar.YEAR) + " MES " + calendar.get(Calendar.MONTH) + " dia " + calendar.get(Calendar.DAY_OF_MONTH));
        });

        btAddService.setOnClickListener(v -> clickAddService());



        textAddService.setOnClickListener(v -> clickAddService());

        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ConsultBusinessActivity.class);
            startActivity(intent
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
        });

        btDoBooking.setOnClickListener(v -> {
            doBooking();
        });
    }

    private void doBooking(){
        String[] convertTime = Common.rangeHours.split(" - ");
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
        bookingRef.set(booking).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addToUserBooking(booking);
            } else {
                Toast.makeText(BookingServiceActivity.this, R.string.msg_error_do_booking,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToUserBooking(Booking booking) {
        DocumentReference bookingRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.idUser)
                .collection("Booking")
                .document(booking.getId());
        bookingRef.set(booking).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(BookingServiceActivity.this, ViewMainCustomer.class);
                startActivity(intent
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                Toast.makeText(BookingServiceActivity.this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BookingServiceActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    private void clickAddService() {
        Intent intent = new Intent(BookingServiceActivity.this, ListServiceActivity.class);
        intent.putExtra("Business", business.getId());
        getAddService.launch(intent);
    }


}