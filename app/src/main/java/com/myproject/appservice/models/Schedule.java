package com.myproject.appservice.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Schedule implements Serializable {

    private int id;
    private String day;
    private ArrayList<String> schedulesDay;
    private boolean opened;

    public Schedule(){

    }

    public Schedule(int id, String day) {
        this.id = id;
        this.day = day;
        this.schedulesDay = new ArrayList<>();
        this.schedulesDay.add("09:00 - 14:00");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<String> getSchedulesDay() {
        if(opened)
        {
            return  schedulesDay;
        }
        else{
            return null;
        }
    }

    public void setSchedulesDay(ArrayList<String> schedulesDay) {
        this.schedulesDay = schedulesDay;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

}
