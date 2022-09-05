package com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.myproject.appservice.R;
import com.myproject.appservice.Common;
import com.myproject.appservice.databinding.FragmentStepFourMapAddressBinding;

import javax.annotation.Nullable;


public class RegisterBusinessStepFourMapAddressFragment extends Fragment {

    private TextView txt_address_inf;
    private FragmentStepFourMapAddressBinding binding;
    private LocalBroadcastManager localBroadcastManager;
    private static RegisterBusinessStepFourMapAddressFragment instance;
    private Double latitude, longitude;
    private View view;
    private SupportMapFragment mapFragment;
    private Button btContinue;
    public static RegisterBusinessStepFourMapAddressFragment getInstance(){
        if(instance == null){
            instance = new RegisterBusinessStepFourMapAddressFragment();
        }
        return instance;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentStepFourMapAddressBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
        localBroadcastManager.registerReceiver(getAddressMap, new IntentFilter(Common.SEND_ADDRESS_MAP));
        txt_address_inf = (TextView) binding.txtAddressInf;
        mapFragment =  ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        btContinue = (Button) binding.btContinueRegisterStepFourMapDetail;
        btContinue.setEnabled(true);
        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_STEP, 5);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
        return view;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if(latitude != null && longitude!=null) {
                googleMap.clear();
                LatLng point = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(point));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));
            }
        }
    };


    private BroadcastReceiver getAddressMap = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            latitude = intent.getExtras().getDouble("LATITUDE");
            longitude = intent.getExtras().getDouble("LONGITUDE");
            String txtAddress= intent.getExtras().getString("STREET", Common.street);
            String txtNumber = intent.getExtras().getString("NUMBER", Common.number);
            String txtPostalCode = intent.getExtras().getString("POSTAL_CODE", Common.postalCode);
            String txtCity = intent.getExtras().getString("CITY", Common.city);
            Resources res = getResources();
            String resume = String.format(res.getString(R.string.text_resume_places), txtAddress,
                    txtNumber, txtPostalCode, txtCity);
            txt_address_inf.setText(resume);
            if (mapFragment != null) {
                mapFragment.getMapAsync(callback);
            }

        }
    };


}