package com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepFiveFragment;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepFourDetailsAddressFragment;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepFourFragment;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepFourMapAddressFragment;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepOneFragment;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepSevenFragment;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepSixFragment;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepThreeFragment;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.RegisterBusinessStepTwoFragment;
import com.myproject.appservice.controllers.RegisterBusinessActivity.StepsRegisterBusiness.ServiceRegisterViewFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return RegisterBusinessStepOneFragment.getInstance();
            case 1:
                return RegisterBusinessStepTwoFragment.getInstance();
            case 2:
                return RegisterBusinessStepThreeFragment.getInstance();
            case 3:
                return RegisterBusinessStepFourFragment.getInstance();
            case 4:
                return RegisterBusinessStepFourDetailsAddressFragment.getInstance();
            case 5:
                return RegisterBusinessStepFourMapAddressFragment.getInstance();
            case 6:
                return RegisterBusinessStepFiveFragment.getInstance();
            case 7:
                return RegisterBusinessStepSixFragment.getInstance();
            case 8:
                return RegisterBusinessStepSevenFragment.getInstance();
            case 9:
                return ServiceRegisterViewFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 10;
    }
}
