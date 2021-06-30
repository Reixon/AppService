package com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.myproject.appservice.R;
import com.myproject.appservice.Common;
import com.myproject.appservice.databinding.FragmentViewServiceBinding;
import com.myproject.appservice.models.Service;

import java.util.Objects;

import javax.annotation.Nullable;

public class ServiceRegisterViewFragment extends Fragment {

    private final String TAG = "STEP8-REGISTER";
    private LocalBroadcastManager localBroadcastManager;
    private FragmentViewServiceBinding binding;
    private static ServiceRegisterViewFragment instance;
    private View view;
    private TextInputEditText txtNameService, txtPrice, txtTypePrice;
    private TextView txtDuration;
    private Button bt_save, bt_cancel;
    private float price;
    private boolean editable, enablePrice, enableName;
    private int position;

    public static ServiceRegisterViewFragment getInstance(){
        if(instance == null){
            instance = new ServiceRegisterViewFragment();
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
        binding = FragmentViewServiceBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        localBroadcastManager = LocalBroadcastManager.getInstance(view.getContext());
        localBroadcastManager.registerReceiver(getDataService, new IntentFilter(Common.EDIT_DATA_SERVICE));
        txtNameService = (TextInputEditText) binding.txtNameServiceView;
        txtDuration = (TextView) binding.txtDuration;
        bt_save = (Button) binding.btSave;
        bt_cancel = (Button) binding.btCancel;
        txtPrice = (TextInputEditText) binding.txtPrice;
        editable = false;
    //    txtTypePrice = (TextInputEditText) binding.txtTypePrice;
        defaultValue();
        txtDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                String [] txt = textView.getText().toString().split(":");

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.adapter_timepicker_service);
                NumberPicker hourPicker = bottomSheetDialog.findViewById(R.id.duration_hour);
                NumberPicker minutePicker = bottomSheetDialog.findViewById(R.id.duration_minute);
                String [] hours = getResources().getStringArray(R.array.duration_hours);
                String [] minute = getResources().getStringArray(R.array.duration_minute);
                hourPicker.setDisplayedValues(hours);
                hourPicker.setMaxValue(11);
                hourPicker.setMinValue(0);
                minutePicker.setMaxValue(11);
                minutePicker.setMinValue(0);
                minutePicker.setDisplayedValues(minute);
                String hourValue = "";
                if(txt.length > 1) {
                    int positionMin = Integer.parseInt(txt[1].split("m")[0])/5;
                    String positionH = txt[0].split("h")[0];
                    hourValue = hours[Integer.parseInt(positionH)].split("h")[0];
                    hourPicker.setValue(Integer.parseInt(hourValue));
                    minutePicker.setValue(positionMin);
                } else {
                    int positionMin = Integer.parseInt(txt[0].split("m")[0])/5;
                    minutePicker.setValue(positionMin);
                    hourPicker.setValue(0);
                }

                bt_save = bottomSheetDialog.findViewById(R.id.bt_save);
                bt_save.setOnClickListener(v1 -> {
                    String time = "";
                    if(hourPicker.getValue() > 0) {
                        time = hours[hourPicker.getValue()] + ":" + minute[minutePicker.getValue()];
                    } else {
                        time = minute[minutePicker.getValue()];
                    }
                    txtDuration.setText(time);
                    bottomSheetDialog.cancel();
                });
                bt_cancel = bottomSheetDialog.findViewById(R.id.bt_cancel);
                bt_cancel.setOnClickListener(v12 -> bottomSheetDialog.cancel());
                bottomSheetDialog.show();
            }
        });
        bt_save.setEnabled(false);
        bt_save.setOnClickListener(v -> save());

        bt_cancel.setOnClickListener(v -> cancel());

        txtNameService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableName = !s.toString().equals("") && s.toString().length() > 4;

                bt_save.setEnabled(enablePrice && enableName);
            }
        });

        txtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enablePrice = !TextUtils.isEmpty(Objects.requireNonNull(txtPrice.getText()).toString());

                bt_save.setEnabled(enablePrice && enableName);
            }
        });
        return view;
    }

    private void cancel(){
        Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
        intent.putExtra("BT_CANCEL", true);
        intent.putExtra(Common.KEY_STEP, 8);
        defaultValue();
        localBroadcastManager.sendBroadcast(intent);
    }

    private void save(){
        Intent intent = new Intent(Common.ENABLE_BUTTON_NEXT);
        intent.putExtra(Common.KEY_STEP, 8);
        intent.putExtra("BT_SAVE", true);
        intent.putExtra("EDIT", editable);
        if(editable){
            position = intent.getExtras().getInt("POSITION");
        }
        intent.putExtra("NAME_SERVICE", txtNameService.getText().toString());
        String clean = txtPrice.getText().toString().replaceAll("[€]","");
        price = Float.parseFloat(clean);
        intent.putExtra("PRICE", price);
        intent.putExtra("DURATION", txtDuration.getText().toString());
        defaultValue();
        localBroadcastManager.sendBroadcast(intent);
    }

    private void defaultValue(){
        txtNameService.setText("");
        txtPrice.setText("");
        txtDuration.setText("30m");
    }

    private BroadcastReceiver getDataService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Service service = (Service) Objects.requireNonNull(intent.getExtras()).get("SERVICE_ITEM");
            position = intent.getExtras().getInt("POSITION");
            if(service != null){
                editable = true;
                txtNameService.setText(service.getName());
                txtDuration.setText(service.getTime());
                String stringPrice = Float.toString(service.getPrice());
                txtPrice.setText(stringPrice+"€");
            } else {
                editable = false;
            }

        }
    };


}
