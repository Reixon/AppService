package com.myproject.appservice.controllers.viewMainBusiness;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.myproject.appservice.R;
import com.myproject.appservice.controllers.calendarBusiness.CalendarBusinessFragment;

public class ViewMainBusinessActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_business_view);
        setUpNavigation();
    }

    public void setUpNavigation(){
        bottomNavigationView =findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener (item -> {
            if(R.id.bookings ==  item.getItemId()){
                showSelectedFragment(new CalendarBusinessFragment());
            } else if(R.id.customer ==  item.getItemId()){
                showSelectedFragment(new CustomerView());
            } else if(R.id.settingFragmentBusiness ==  item.getItemId()) {
                showSelectedFragment(new ProfileViewBusiness());
            }
            return true;
        });

    }

    private void showSelectedFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_business, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
}