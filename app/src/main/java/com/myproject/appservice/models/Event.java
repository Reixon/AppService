package com.myproject.appservice.models;

public class Event {
    public String service;
    public String name;
    public Long startTime;
    public Long endTime;

    public Event(String orgy, String service, Integer i, Integer i1) {
        this.name = orgy;
        this.service = service;
        startTime = i.longValue();
        endTime = i1.longValue();
    }
}

