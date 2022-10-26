package com.myproject.appservice.controllers.viewMainCustomer.SearchCompany;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.viewMainCustomer.ViewBusiness.ConsultBusinessActivity;
import com.myproject.appservice.databinding.ActivityServiceSearchBusinessBinding;
import com.myproject.appservice.models.Business;

import java.util.ArrayList;

public class SearchCompanyActivity extends AppCompatActivity {

    private ActivityServiceSearchBusinessBinding binding;
    private static final int DEFAULT_UPDATE_INTERVAL = 5;
    private static final int ERROR_DIALOG = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;

    private ListView list;
    private AdapterSearchCompany adapterListBusiness;
    private EditText txt_edit;
    private ArrayList<Business> arrayBusiness;
    private Double latitude, longitude;
    private View mProgressView;

    private FirebaseFirestore db;
    private double radius = 20;
    private final String TAG = "SERVICE_SEARCH";
    private GeoFire geoFire;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final int MY_PERMISSIONS = 100;
    private final int ERROR_DIALOG_REQUEST = 901;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean isPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceSearchBusinessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = binding.getRoot().findViewById(R.id.listView);
        txt_edit = binding.txtSearchService;
        mProgressView = binding.circularProgress;

        arrayBusiness = new ArrayList<>();
        adapterListBusiness = new AdapterSearchCompany(getApplicationContext(), arrayBusiness);
        list.setAdapter(adapterListBusiness);

        showProgress(true);


        // Initialize db reference
        db = FirebaseFirestore.getInstance();
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("geoFire"));
        initializeListeners();

        if (!isPermissionGranted) {
            if (isGPSEnable()) {
                requestPermissionsLocalization();
            }
        }

    }

    private boolean isGPSEnable() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.i(TAG, "enableGPS: mLocationPermissionGranted " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            locationRequest = LocationRequest.create()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)
                    .setFastestInterval(10000 / 2);

//            AlertDialog alertDialog = new AlertDialog.Builder(this)
//                    .setTitle("GPS Permission")
//                    .setMessage("GPS is required for this app to work. Please enable GPS")
//                    .setPositiveButton("Yes", ((dialogInterface, i)-> {
//
//                    }));
            LocationSettingsRequest.Builder locationBuilder = new LocationSettingsRequest.Builder();

            locationBuilder.addLocationRequest(locationRequest);
            locationBuilder.setAlwaysShow(true);

            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationBuilder.build());
            task.addOnSuccessListener(v -> {
                if (isPermissionGranted) {
                    Log.i(TAG, "enableGPS: mLocationPermissionGranted " + true);
                    getLocation();
                }
            });
            task.addOnFailureListener(v -> {
                if (v instanceof ResolvableApiException) {
                    Log.i(TAG, "enableGPS: " + false);
                    ResolvableApiException resolvableApiException = (ResolvableApiException) v;
                    try {
                        resolvableApiException.startResolutionForResult(SearchCompanyActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: ");

        if (requestCode == REQUEST_CHECK_SETTINGS) {
                Log.i(TAG, "GPS_PROVIDER ON");
                requestPermissionsLocalization();
        }
    }

    private void requestPermissionsLocalization() {
        locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(10000 / 2);
        Log.i(TAG, "PermissionsLocalization IN");
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if ((shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))) {
                String title = getResources().getString(R.string.title_use_location);
                String message = getResources().getString(R.string.explication_permissions);
                showExplanation(title, message, MY_PERMISSIONS);
            } else {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS);
            }
        } else {
            Log.i(TAG, "checkSelfPermission GRANTED");
            getLocation();
        }
    }

    private void showExplanation(String title, String message, final int permissionRequestCode) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(title);
        TextView txt = new TextView(this);
        txt.setText(message);
        builder.setView(txt);
        builder.setPositiveButton(R.string.btOk, (dialog, which) ->
                ActivityCompat.requestPermissions(SearchCompanyActivity.this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "RequestPermissionsResult TRUE");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        Log.i(TAG, "IN LOCATION");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    showProgress(false);
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        Log.i(TAG, "LOCATION REMOVE");
                    }
                }

            }
        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SearchCompanyActivity.this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            Log.i(TAG, "LOCATION GEOLOCATION " + location);
            if (location != null) {
                Log.i(TAG, "LOCATION true");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                getGeoLocation(latitude, longitude);
            }
        });
        Log.i(TAG, "END LOCATION");
    }


//    private void enableGPS(LocationRequest locationRequest) {
//        LocationSettingsRequest.Builder locationBuilder = new LocationSettingsRequest.Builder();
//
//        locationBuilder.addLocationRequest(locationRequest);
//        locationBuilder.setAlwaysShow(true);
//
//        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationBuilder.build());
//        task.addOnSuccessListener(v ->{
//            if(mLocationPermissionGranted){
//                Log.i(TAG, "enableGPS: mLocationPermissionGranted "+ true);
//                getLocation();
//            }
//        });
//        task.addOnFailureListener(v -> {
//            if (v instanceof ResolvableApiException) {
//                Log.i(TAG, "enableGPS: "+ false);
//                ResolvableApiException resolvableApiException = (ResolvableApiException) v;
//                try {
//                    resolvableApiException.startResolutionForResult(SearchCompanyActivity.this, REQUEST_CHECK_SETTINGS);
//                } catch (IntentSender.SendIntentException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    private void getGeoLocation(double latitude, double longitude) {
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(
                latitude, longitude), radius);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.i(TAG, "entered " + key + " - Geo " + location.latitude + ", " + location.longitude);
                //    distanceCalculated(location);

                DocumentReference businessRef = FirebaseFirestore.getInstance()
                        .collection("Businesses").document(key);
                businessRef.get().addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    Business b = document.toObject(Business.class);
                    if (b != null) {
                        arrayBusiness.add(b);
                        adapterListBusiness.notifyDataSetChanged();
                    }
                });
                showProgress(false);
            }

            @Override
            public void onKeyExited(String key) {
                Log.i("KEY_ENTERED", "exited " + key);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    arrayBusiness.removeIf(obj -> obj.getId().equals(key));
                    adapterListBusiness.notifyDataSetChanged();
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.i(TAG, "moved  " + key + " - Geo " + location.latitude + ", " + location.longitude);
            }

            @Override
            public void onGeoQueryReady() {
                Log.i(TAG, "ready ");
                showProgress(false);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.i(TAG, "error ");
                showProgress(false);
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

    public void initializeListeners() {

        adapterListBusiness.getFilter().filter(txt_edit.getText().toString());

        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(SearchCompanyActivity.this, ConsultBusinessActivity.class);
            intent.putExtra("Business", arrayBusiness.get(position));
            startActivity(intent);
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
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && event.getAction() == KeyEvent.ACTION_DOWN) {
                    adapterListBusiness.getFilter().filter(txt_edit.getText());
                }
                return false;
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
}