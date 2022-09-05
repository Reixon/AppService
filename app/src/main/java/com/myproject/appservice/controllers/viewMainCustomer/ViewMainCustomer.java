package com.myproject.appservice.controllers.viewMainCustomer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.myproject.appservice.R;

public class ViewMainCustomer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_customer_view);
        setUpNavigation();
    }

    public void setUpNavigation(){
        BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener (item -> {
           if(R.id.home ==  item.getItemId()){
               showSelectedFragment(new HomeCustomerView());
           } else if(R.id.bookings ==  item.getItemId()){
               showSelectedFragment(new ListViewBookingCustomer());
           } else if(R.id.profile ==  item.getItemId()) {
               showSelectedFragment(new ProfileView());
           }
            return true;
        });

//        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager()
//                .findFragmentById(R.id.main_fragment_customer);
//        assert navHostFragment != null;
//        NavigationUI.setupWithNavController(bottomNavigationView,
//                navHostFragment.getNavController());
    }

    private void showSelectedFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_customer, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
}