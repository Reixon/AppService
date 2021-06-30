package com.myproject.appservice.controllers.RegisterBusinessActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myproject.appservice.Common;
import com.myproject.appservice.controllers.Login.LoginActivity;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.adapters.ViewPagerAdapter;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.adapters.NonSwiperViewPager;
import com.myproject.appservice.controllers.ViewMainBusiness.ViewMainBusiness;
import com.myproject.appservice.models.Business;
import com.myproject.appservice.models.Schedule;
import com.myproject.appservice.models.Service;
import com.myproject.appservice.models.User;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RegisterBusinessActivity extends AppCompatActivity {

    private LocalBroadcastManager localBroadcastManager;
    private StepView stepView;
    private NonSwiperViewPager viewPager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Double latitude, longitude;
    private final int MY_PERMISSIONS = 100;
    private View mProgressView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business);
        mProgressView = (ProgressBar) findViewById(R.id.circularProgress);
        stepView = findViewById(R.id.step_view);
        viewPager = findViewById(R.id.view_pager);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.ENABLE_BUTTON_NEXT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setUpStepView();
        generateHours();
        mAuth = FirebaseAuth.getInstance();

        // View
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(10);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position >= 6){
                    position = -2;
                }
                stepView.go(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    }
                }
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(Common.step > 0){
                Common.step = Common.step - 1;
                viewPager.setCurrentItem(Common.step);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra(Common.KEY_STEP, 0);
            if(step == 0){
                Common.name = intent.getExtras().getString("NAME");
                Common.email = intent.getExtras().getString("EMAIL");
                Common.password = intent.getExtras().getString("PASSWORD");
            } else if(step == 1){
                Common.nameBusiness = intent.getExtras().getString("BUSINESS_NAME");
                Common.phone = intent.getExtras().getString("BUSINESS_PHONE");
            } else if(step == 2){
                Common.kindBusiness = Objects.requireNonNull(intent.getExtras()).getInt("BUSINESS_KIND");
                requestPermissionsLocalization();
            } else if(step == 3){ //LISTA DE DIRECCIONES
                Common.street = Objects.requireNonNull(intent.getExtras()).getString("STREET");
                Common.number = intent.getExtras().getString("NUMBER");
                Common.postalCode = intent.getExtras().getString("POSTAL_CODE");
                Common.city = intent.getExtras().getString("CITY");
                if(intent.getExtras().getBoolean("AUTOCOMPLETE")){
                    Common.latitude = intent.getExtras().getDouble("LATITUDE");
                    Common.longitude = intent.getExtras().getDouble("LONGITUDE");
                } else {
                    Common.latitude = latitude;
                    Common.longitude = longitude;
                }
                sendAddress();
            } else if(step == 4){
                Common.street = intent.getExtras().getString("STREET_EDIT");
                Common.number = intent.getExtras().getString("NUMBER_EDIT");
                Common.postalCode = intent.getExtras().getString("POSTAL_CODE_EDIT");
                Common.city = intent.getExtras().getString("CITY_EDIT");
                sendAddressMap();
            } else if(step == 6){
                Common.schedules = (ArrayList<Schedule>) intent.getExtras().getSerializable("SCHEDULE");
            } else if(step == 7){
                Common.categories = Objects.requireNonNull(intent.getExtras()).getStringArrayList("BUSINESS_CATEGORIES");
            } else if(step == 8){
                // PASA A LA PANTALLA 7
                boolean edit = Objects.requireNonNull(intent.getExtras()).getBoolean("EDIT");
                if (Objects.requireNonNull(intent.getExtras()).getBoolean("BT_SAVE")) {
                    String nameService = intent.getExtras().getString("NAME_SERVICE");
                    float price = intent.getExtras().getFloat("PRICE");
                    String duration = intent.getExtras().getString("DURATION");
                    Service service = new Service(nameService, duration, price);
                    int position = intent.getExtras().getInt("POSITION");
                    sendService(service, edit, position);
                } else {
                    // PASA A LA PANTALLA VIEWSERVICEFRAGMENT
                    Service service = intent.getExtras().getParcelable("ITEM_SERVICE");
                    int position = intent.getExtras().getInt("POSITION");
                    editService(service, position);
                }
            } else if(step == 9){
                Common.services = (ArrayList<Service>) intent.getExtras().getSerializable("ARRAY_SERVICES");
                //SAVE LOS DATA EN FIREBASE
                confirmRegister();
                Common.step--;
            }
            if(step == 8 &&
                    (Objects.requireNonNull(intent.getExtras()).getBoolean("BT_CANCEL")||
                    Objects.requireNonNull(intent.getExtras()).getBoolean("BT_SAVE"))){
                Common.step--;
            } else {
                Common.step++;
            }
            viewPager.setCurrentItem(Common.step);
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
        viewPager.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void confirmRegister(){
        showProgress(true);
        completeRegisterUser();
    }

    private void completeRegisterUser(){

        mAuth.createUserWithEmailAndPassword(Common.email, Common.password)
            .addOnSuccessListener(this, task -> {

                User user = new User(Common.name, Common.email);
                user.setIdUser(Objects.requireNonNull(task.getUser()).getUid());

                DocumentReference userDocument = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(user.getIdUser());

                DocumentReference businesses = FirebaseFirestore.getInstance()
                        .collection("Businesses")
                        .document();

                user.setIdBusiness(businesses.getId());

                userDocument.set(user).addOnFailureListener(e -> {
                    Toast.makeText(RegisterBusinessActivity.this,
                            getResources().getString(R.string.msg_error_communication_data_no_update),
                            Toast.LENGTH_SHORT).show();
                    showProgress(false);
                })
                .addOnSuccessListener(aVoid -> {
                    completeRegisterBusiness(userDocument.getId(), businesses);
                });
            })
            .addOnFailureListener(this, e -> {
                    Toast.makeText(RegisterBusinessActivity.this,
                            getResources().getString(R.string.msg_error_communication_user_update),
                            Toast.LENGTH_SHORT).show();
                showProgress(false);
            });


    }


    private void completeRegisterBusiness(final String idUser, DocumentReference businesses){

        Business business = new Business();
        business.setId(businesses.getId());
        business.setNameBusiness(Common.nameBusiness);
        business.setPhone(Common.phone);
        business.setCity(Common.city);
        business.setNumber(Common.number);
        business.setPostalCode(Common.postalCode);
        Resources res = getResources();
        String resume = String.format(res.getString(R.string.text_resume_places), Common.street,
                Common.number, Common.postalCode, Common.city);
        business.setAddress(resume);
        business.setStreet(Common.street);
        business.setLatitude(Common.latitude);
        business.setLongitude(Common.longitude);
        business.setKindBusiness(Common.kindBusiness);
        business.setCategories(Common.categories);
    /*    business.setSchedules(Common.schedules);
        business.setCategories(Common.categories);
        business.setServices(Common.services);*/

        business.setIdUser(idUser);

        businesses.set(business).addOnSuccessListener(aVoid1 -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geoFire");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(business.getId(), new GeoLocation(Common.latitude, Common.longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    showProgress(false);
                    if (error != null) {
                        deleteEmailUser(idUser, business.getId());
                        Intent intent = new Intent(RegisterBusinessActivity.this, LoginActivity.class);
                        startActivity(intent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        finish();
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        for(int i = 0; i < Common.schedules.size(); i++){
                            DocumentReference schedules = FirebaseFirestore.getInstance()
                                    .collection("Businesses")
                                    .document(business.getId())
                                    .collection("Schedules")
                                    .document(Integer.toString(i));
                            data.put("day", Common.schedules.get(i).getDay());
                            data.put("opened", Common.schedules.get(i).isOpened());
                            data.put("schedulesDay", Common.schedules.get(i).getSchedulesDay());
                            schedules.set(data);
                        }
                        Map<String, Object> dataService = new HashMap<>();
                        for(int x = 0; x < Common.services.size(); x++) {
                            DocumentReference servicesRef = FirebaseFirestore.getInstance()
                                    .collection("Businesses")
                                    .document(business.getId())
                                    .collection("Service")
                                    .document();
                            dataService.put("id", servicesRef.getId());
                            dataService.put("time", Common.services.get(x).getTime());
                            dataService.put("name", Common.services.get(x).getName());
                            dataService.put("price", Common.services.get(x).getPrice());
                            servicesRef.set(dataService);
                        }

                        Intent intent = new Intent(RegisterBusinessActivity.this, ViewMainBusiness.class);
                        startActivity(intent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        Toast.makeText(RegisterBusinessActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    }
                    finish();
                }
            });
        }).addOnFailureListener(this, e ->
                Toast.makeText(RegisterBusinessActivity.this,
                        getResources().getString(R.string.msg_error_communication_data_no_update),
                        Toast.LENGTH_SHORT).show());
    }

    private void deleteEmailUser(final String idUser, final String idBusiness){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DocumentReference userDocument = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(idUser);
                userDocument.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference businessDocument = FirebaseFirestore.getInstance()
                                .collection("Businesses")
                                .document(idBusiness);
                        businessDocument.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("geoFire");
                                GeoFire geoFire = new GeoFire(ref);
                                geoFire.removeLocation(idBusiness);
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void setUpStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Usuario");
        stepList.add("Negocio");
        stepList.add("¿Donde trabaja?");
        stepList.add("Dirección");
        stepList.add("Horario");
        stepList.add("Lugar de trabajo");
        stepList.add("Servicios");
        stepView.setSteps(stepList);
    }

    private void showExplanation(String title, String message, final int permissionRequestCode){
        final android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(this);
        builder.setTitle(title);
        TextView txt = new TextView(this);
        txt.setText(message);
        builder.setView(txt);
        builder.setPositiveButton(R.string.btOk, (dialog, which) ->
                ActivityCompat.requestPermissions(RegisterBusinessActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void requestPermissionsLocalization() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            if ((shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))) {
                String title = getResources().getString(R.string.title_use_location);
                String message = getResources().getString(R.string.explication_permissions);
                showExplanation(title, message, MY_PERMISSIONS);

            } else {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS);
            }
        } else {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(20 * 1000);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            };
                getLocation();
            }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Intent intent = new Intent(Common.SEND_LATITUDE_LONGITUDE);
                intent.putExtra("Latitude", latitude);
                intent.putExtra("Longitude", longitude);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    private void sendAddress(){
        Intent intent = new Intent(Common.SEND_ADDRESS);
        intent.putExtra("STREET", Common.street);
        intent.putExtra("NUMBER", Common.number);
        intent.putExtra("POSTAL_CODE", Common.postalCode);
        intent.putExtra("CITY", Common.city);
        intent.putExtra("LONGITUDE", Common.longitude);
        intent.putExtra("LATITUDE", Common.latitude);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void sendAddressMap(){
        Intent intent = new Intent(Common.SEND_ADDRESS_MAP);
        intent.putExtra("STREET", Common.street);
        intent.putExtra("NUMBER", Common.number);
        intent.putExtra("POSTAL_CODE", Common.postalCode);
        intent.putExtra("CITY", Common.city);
        intent.putExtra("LONGITUDE", Common.longitude);
        intent.putExtra("LATITUDE", Common.latitude);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void sendService(Service service, boolean edit, int position){
        Intent intent = new Intent(Common.SEND_DATA_SERVICE);
        intent.putExtra("SERVICE_ITEM", service);
        if(edit){
            intent.putExtra("POSITION", position);
        }
        intent.putExtra("EDIT", edit);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void editService(Service service, int position){
        Intent intent = new Intent(Common.EDIT_DATA_SERVICE);
        if(service != null) {
            intent.putExtra("SERVICE_ITEM", service);
            intent.putExtra("POSITION", position);
            intent.putExtra("EDIT", true);
        } else {
            intent.putExtra("EDIT", false);
        }
        localBroadcastManager.sendBroadcast(intent);
    }

    private void generateHours() {
        int x = 0;
        int y = 0;
        Common.hours = new String[(60/5)*24];
        for(int i = 0; i < 24; i++){
            for(x = x; x < (60/5)*(i+1); x++){
                String minute = Integer.toString(y*5);
                String minutes;
                if((y*5) < 10){
                    minutes = 0 + minute;
                } else{
                    minutes = minute;
                }
                String hour;
                if(i < 10){
                    hour = "0"+i+":"+minutes;
                } else {
                    hour = i+":"+minutes;
                }
                Common.hours[x]=hour;
                y++;
            }
            y = 0;
        }
    }




}