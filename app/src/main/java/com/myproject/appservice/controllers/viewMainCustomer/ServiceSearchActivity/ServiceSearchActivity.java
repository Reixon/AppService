package com.myproject.appservice.controllers.viewMainCustomer.ServiceSearchActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.viewMainCustomer.ViewBusiness.ConsultBusinessView;
import com.myproject.appservice.models.Business;

import java.util.ArrayList;

public class ServiceSearchActivity extends AppCompatActivity {

    private ListView list;
    private AdapterListBusiness adapterListBusiness;
    private EditText txt_edit;
    private ArrayList<Business> arrayBusiness;
    private Double latitude, longitude;

    private FirebaseFirestore db;
    private double radius = 20;
    private final String TAG = "SERVICE_SEARCH";
    private GeoFire geoFire;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final int MY_PERMISSIONS = 100;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_search);
        list = (ListView) findViewById(R.id.listView);
        txt_edit = (EditText) findViewById(R.id.txt_search_service);

        arrayBusiness = new ArrayList<>();
        adapterListBusiness = new AdapterListBusiness(getApplicationContext(), arrayBusiness);
        list.setAdapter(adapterListBusiness);

        requestPermissionsLocalization();

        // Initialize db reference
        db = FirebaseFirestore.getInstance();
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("geoFire"));
        initializeListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
            getLocation();

        }
    }

    private void showExplanation(String title, String message, final int permissionRequestCode){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(title);
        TextView txt = new TextView(this);
        txt.setText(message);
        builder.setView(txt);
        builder.setPositiveButton(R.string.btOk, (dialog, which) ->
            ActivityCompat.requestPermissions(ServiceSearchActivity.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode));
        AlertDialog dialog = builder.create();
        dialog.show();
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ServiceSearchActivity.this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                getGeoLocation(latitude, longitude);
            }
        });
    }

    private void getGeoLocation(double latitude, double longitude){
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(
                latitude, longitude), radius);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.i(TAG, "entered "+key+" - Geo "+location.latitude+", "+location.longitude);
            //    distanceCalculated(location);
                DocumentReference businessRef = FirebaseFirestore.getInstance()
                        .collection("Businesses").document(key);
                businessRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Business b = document.toObject(Business.class);
                        if(b!=null) {
                            arrayBusiness.add(b);
                            adapterListBusiness.notifyDataSetChanged();
                        }
                    }
                });
            }
            @Override
            public void onKeyExited(String key) {
                Log.i("KEY_ENTERED", "exited "+key);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    arrayBusiness.removeIf(obj -> obj.getId().equals(key));
                    adapterListBusiness.notifyDataSetChanged();
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.i(TAG, "moved  "+key+" - Geo "+location.latitude+", "+location.longitude);
            }

            @Override
            public void onGeoQueryReady() {
                //     Log.i(TAG, "ready ");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.i(TAG, "error ");
            }
        });

    }

/*
    private void distanceCalculated(GeoLocation location){
        double mLat = getIntent().getExtras().getDouble("latitude");
        double mLong = getIntent().getExtras().getDouble("longitude");

        double phi1 = location.latitude * (Math.PI/180);
        double phi2 = mLat * (Math.PI/180);

        double delta1 = (mLat-location.longitude)*(Math.PI/180);
        double delta2 = (mLong-location.longitude)*(Math.PI/180);

        double cal1 = Math.sin(delta1/2)*Math.sin(delta1/2) +
                (Math.cos(phi1)*Math.cos(phi2)*Math.sin(delta2/2)*Math.sin(delta2/2));

        double cal2 = 2 * Math.atan2((Math.sqrt(cal1)), (Math.sqrt(1-cal1)));
        double radEarth = 6.3781* (Math.pow(10.0,6.0));
        double distance = radEarth*cal2;
    }
*/

    public void initializeListeners(){

        adapterListBusiness.getFilter().filter(txt_edit.getText().toString());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ServiceSearchActivity.this, ConsultBusinessView.class);
                intent.putExtra("Business", arrayBusiness.get(position));
                startActivity(intent);
            }
        });

        txt_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, " text change");
                adapterListBusiness.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "after text change");
            }
        });

        txt_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == KeyEvent.KEYCODE_ENTER)&& event.getAction()== KeyEvent.ACTION_DOWN){
                    adapterListBusiness.getFilter().filter(txt_edit.getText());
                }
                return false;
            }
        });

    }
}