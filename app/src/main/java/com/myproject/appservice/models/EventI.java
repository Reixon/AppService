package com.myproject.appservice.models;

import com.myproject.appservice.controllers.calendarBusiness.TimeLineView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventI {

    private final String name;
    private final Float startIndex;
    private final Float endIndex;
    private final String availability;

    public EventI(Event event, Integer start) {
        this.name = event.name + " - " + event.service;
        startIndex = getHourIndex(event.startTime, start);
        endIndex = getHourIndex(event.endTime, start);

        availability = ""+ new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(event.startTime * 1000))
                +" - "+ new SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(new Date(event.endTime * 1000));
    }

    public String getName() {
        return name;
    }

    public String getAvailability() {
        return availability;
    }

    public Float getStartIndex() {
        return startIndex;
    }

    public Float getEndIndex() {
        return endIndex;
    }

    private Float getHourIndex(Long time, Integer start ) {

        SimpleDateFormat formatter = new SimpleDateFormat("HH mm", Locale.forLanguageTag("es-ES"));
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(formatter.format(new Date(time*1000)));

        DateFormat df = new SimpleDateFormat("dd:MM:yy HH:mm:ss", Locale.forLanguageTag("es-ES"));
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(df.format(new Date(time*1000)));

        String[] hm = formatter.format(new Date(time * 1000)).split(" ");
        float h = Float.parseFloat(hm[0]);
        int m = Integer.parseInt(hm[1]);
        h = h + m/60f - start;
        if (h < 0) h += TimeLineView.TOTAL;
        return h;
    }
}
