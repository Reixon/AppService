package com.myproject.appservice;


import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.myproject.appservice.models.Schedule;
import com.myproject.appservice.models.Service;
import com.myproject.appservice.models.User;

import java.util.ArrayList;

public class Common {
    public static final String IS_LOGIN = "IsLogin";
    public static User currentUser;
    public static final String KEY = "AIzaSyAncFbRvSGXIg4V0Sjtj1_x0C_ghEPSkIw";
    public static int step = 0;
    public static int lastStep = 0;

    /** Data Register **/
    public static final String ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_STEP = "KEY_STEP";
    public static final String SEND_LATITUDE_LONGITUDE= "SEND_LATITUDE_LONGITUDE";
    public static final String SEND_ADDRESS= "SEND_ADDRESS";
    public static final String SEND_ADDRESS_MAP = "SEND_ADDRESS_MAP";
    public static final String EDIT_DATA_SERVICE = "EDIT_DATA_SERVICE";
    public static final String SEND_DATA_SERVICE = "SET_DATA_SERVICE";
    public static String name;
    public static String email;
    public static String password;
    public static String nameBusiness;
    public static String phone;
    public static int kindBusiness;
    public static String street;
    public static String number;
    public static String postalCode;
    public static String city;
    public static double latitude;
    public static double longitude;
    public static ArrayList<String> categories;
    public static ArrayList<Service> services;
    public static ArrayList<Schedule> schedules;
    public static String [] hours;


    /** USER**/
    public static String idUser;
    public static String emailUser;
    public static String nameUser;
    public static int slotSchedule;
    public static String timeSchedule;
    public static String dateSchedule;
    public static String rangeHours;
    public static String idEmployee;


    /** BUSINESS**/
    public static String idBusiness;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
