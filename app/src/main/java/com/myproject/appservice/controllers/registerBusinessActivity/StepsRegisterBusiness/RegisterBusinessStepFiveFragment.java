package com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.appservice.R;
import com.myproject.appservice.controllers.registerBusinessActivity.StepsRegisterBusiness.adapters.AdapterListSchedule;
import com.myproject.appservice.Common;
import com.myproject.appservice.databinding.FragmentStepFiveBinding;
import com.myproject.appservice.models.Schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


public class RegisterBusinessStepFiveFragment extends Fragment {

    private final String TAG = "STEP5-REGISTER";
    private List<Schedule> schedules;
    private Button bt;

    public static RegisterBusinessStepFiveFragment getInstance(){
        return new RegisterBusinessStepFiveFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentStepFiveBinding binding = FragmentStepFiveBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
        RecyclerView listView = binding.listSchedule;
        bt = binding.btContinueRegisterStepFive;
        loadData();
        enableButton();
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(new AdapterListSchedule(getContext(), schedules));
        bt.setOnClickListener(v -> {
            Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
            intent.putExtra(Common.KEY_STEP, 6);
            intent.putExtra("SCHEDULE", (Serializable) schedules);
            localBroadcastManager.sendBroadcast(intent);
        });
        return view;
    }

    private void loadData(){
        String [] days = getResources().getStringArray(R.array.array_days_week);
        schedules = new ArrayList<Schedule>();
        for(int i = 0; i < days.length ; i++){
            Schedule schedule = new Schedule(i, days[i]);
            schedule.setOpened(i <= 4);
            schedules.add(schedule);
        }
    }

    private boolean checkAllSchedules(){
        for(int i=0; i< schedules.size(); i++){
            if(schedules.get(i).isOpened()){
                return true;
            }
        }
        return false;
    }

    public void enableButton(){
        bt.setEnabled(checkAllSchedules());
    }



}