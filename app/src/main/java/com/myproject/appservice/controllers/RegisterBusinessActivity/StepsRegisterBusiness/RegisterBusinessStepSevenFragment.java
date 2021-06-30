package com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.adapters.AdapterListBusinessService;
import com.myproject.appservice.Common;
import com.myproject.appservice.databinding.FragmentStepSevenBinding;
import com.myproject.appservice.models.Service;

import java.util.ArrayList;
import java.util.Objects;

import javax.annotation.Nullable;


public class RegisterBusinessStepSevenFragment extends Fragment {

    private final String TAG = "STEP8-REGISTER";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocalBroadcastManager localBroadcastManager;
    private FragmentStepSevenBinding binding;
    private static RegisterBusinessStepSevenFragment instance;
    private View view;
    private ArrayList<Service> arrayService;
    private AdapterListBusinessService adapter;
    private TextView textView;
    private ListView listView;
    private Button btAdd, btContinue;

    public static RegisterBusinessStepSevenFragment getInstance(){
        if(instance == null){
            instance = new RegisterBusinessStepSevenFragment();
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
        binding = FragmentStepSevenBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
        localBroadcastManager.registerReceiver(getDataService, new IntentFilter(Common.SEND_DATA_SERVICE));
        arrayService = new ArrayList<>();
        btAdd = (Button) binding.btAddService;
        btContinue = (Button) binding.btContinueRegisterStepSix;
        listView = (ListView) binding.listServiceAddService;
        textView = (TextView) binding.info;
        adapter = new AdapterListBusinessService(getContext(), arrayService, this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendDataService(arrayService.size(), false);
            }
        });
        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });

        enableButton();
        return view;
    }

    private void nextStep(){
        Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
        intent.putExtra(Common.KEY_STEP, 9);
        intent.putExtra("ARRAY_SERVICES", arrayService);
        localBroadcastManager.sendBroadcast(intent);
    }

    public void enableButton(){
        if(arrayService.size() == 0 ){
            this.btContinue.setEnabled(false);
        } else {
            this.btContinue.setEnabled(true);
        }
    }

    public void sendDataService(int position, boolean editService){
        Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
        intent.putExtra(Common.KEY_STEP, 8);
        if(editService) {
            intent.putExtra("ITEM_SERVICE", (Parcelable) arrayService.get(position));
            intent.putExtra("POSITION", position);
        }
        intent.putExtra("EDIT", editService);
        localBroadcastManager.sendBroadcast(intent);
    }

    private BroadcastReceiver getDataService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Service service = (Service) Objects.requireNonNull(intent.getExtras()).get("SERVICE_ITEM");
            boolean edi = intent.getExtras().getBoolean("EDIT");
            int position = intent.getExtras().getInt("POSITION");
            if(edi){
                arrayService.remove(position);
                arrayService.add(position, service);
            } else {
                arrayService.add(service);
            }
            adapter.setServices(arrayService);
            adapter.notifyDataSetChanged();
            if(arrayService.size() > 0){
                textView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
            enableButton();
        }
    };

}