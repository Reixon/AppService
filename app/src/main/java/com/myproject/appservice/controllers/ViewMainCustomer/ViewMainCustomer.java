package com.myproject.appservice.controllers.ViewMainCustomer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

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

        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager()
                .findFragmentById(R.id.main_fragment_customer);
        assert navHostFragment != null;
        NavigationUI.setupWithNavController(bottomNavigationView,
                navHostFragment.getNavController());
    }
}