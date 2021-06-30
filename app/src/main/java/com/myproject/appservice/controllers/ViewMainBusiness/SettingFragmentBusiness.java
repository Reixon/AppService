package com.myproject.appservice.controllers.ViewMainBusiness;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.myproject.appservice.R;

public class SettingFragmentBusiness extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}