package com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness.adapters.AdapterListAddress;
import com.myproject.appservice.Common;
import com.myproject.appservice.databinding.FragmentStepFourBinding;
import com.myproject.appservice.models.MPlace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class RegisterBusinessStepFourFragment extends Fragment {

    private final String TAG = "STEP3-REGISTER";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocalBroadcastManager localBroadcastManager;
    private ArrayList<MPlace> addressList;
    private static RegisterBusinessStepFourFragment instance;
    private final int AUTOCOMPLETE_REQUEST_CODE = 101;
    private AdapterListAddress adapterListAddress;
    private EditText editText;

    public static RegisterBusinessStepFourFragment getInstance(){
        if(instance == null){
            instance = new RegisterBusinessStepFourFragment();
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
        FragmentStepFourBinding binding = FragmentStepFourBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
        ListView listAddress = (ListView) binding.listAdresses;
        editText = (EditText) binding.txtSearchService;
        Places.initialize(getContext(), Common.KEY);
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(getLocation, new IntentFilter(Common.SEND_LATITUDE_LONGITUDE));
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                searchAddress(b, view);
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddress(true, v);
            }
        });

        addressList = new ArrayList<>();
        adapterListAddress = new AdapterListAddress(view.getContext(), addressList);
        listAddress.setAdapter(adapterListAddress);

        listAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_STEP, 3);
                intent.putExtra("STREET", addressList.get(position).getStreet());
                intent.putExtra("NUMBER", addressList.get(position).getNumber());
                intent.putExtra("POSTAL_CODE", addressList.get(position).getPostalCode());
                intent.putExtra("CITY", addressList.get(position).getCity());
                intent.putExtra("AUTOCOMPLETE", false);
                localBroadcastManager.sendBroadcast(intent);
            }
        });

        return view;
    }

    private void searchAddress(boolean b, View view){
        if(b){
            List<Place.Field> fieldList = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.ADDRESS,
                    Place.Field.ADDRESS_COMPONENTS,
                    Place.Field.LAT_LNG
            );

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fieldList).
                    setTypeFilter(TypeFilter.ADDRESS).setCountry("es")
                    .build(view.getContext());

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                AddressComponents d = place.getAddressComponents();
                String street="", number = "", postalCode = "", city = "";
                Double latitude, longitude;
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                for(int i = 0; i< d.asList().size(); i++) {
                    if (d.asList().get(i).getTypes().contains("route")) {
                        street = d.asList().get(i).getName();
                    } else if(d.asList().get(i).getTypes().contains("locality")){
                        city = d.asList().get(i).getName();
                    } else if(d.asList().get(i).getTypes().contains("postal_code")){
                        postalCode = d.asList().get(i).getName();
                    } else if(d.asList().get(i).getTypes().contains("street_number")){
                        number+= d.asList().get(i).getName();
                    }
                }

                Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
                intent.putExtra("STREET", street);
                intent.putExtra("NUMBER", number);
                intent.putExtra("POSTAL_CODE", postalCode);
                intent.putExtra("CITY", city);
                intent.putExtra("LONGITUDE", longitude);
                intent.putExtra("LATITUDE", latitude);
                intent.putExtra("AUTOCOMPLETE", true);
                intent.putExtra(Common.KEY_STEP, 3);
                localBroadcastManager.sendBroadcast(intent);

                editText.clearFocus();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private BroadcastReceiver getLocation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(addressList.size() == 0) {
                Double latitude = intent.getExtras().getDouble("Latitude");
                Double longitude = intent.getExtras().getDouble("Longitude");
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    Address address = addresses.get(0);
                    MPlace mPlace = new MPlace(address.getThoroughfare(), address.getSubThoroughfare(), address.getPostalCode(), address.getLocality());
                    mPlace.setLatitude(latitude);
                    mPlace.setLongitude(longitude);
                    mPlace.setAutoLocalice(true);
                    addressList.add(mPlace);
                    adapterListAddress.setAddresses(addressList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}