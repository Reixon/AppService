package com.myproject.appservice.controllers.calendarBusiness;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarUtils {

    public static LocalDate selectedDate;

    static ArrayList<LocalDate> daysInWeekArray(LocalDate date) {
        ArrayList<LocalDate> days = new ArrayList<>();

        LocalDate current = sundayForDate(date);
        LocalDate endDate = current.plusWeeks(1);

        while (current.isBefore(endDate)){
            days.add(current);
            current = current.plusDays(1);
        }

        return days;
    }

    private static LocalDate sundayForDate(LocalDate current){
        LocalDate oneWeekAgo = current.minusWeeks(1);
        while (current.isAfter(oneWeekAgo)){
            if (current.getDayOfWeek() == DayOfWeek.MONDAY){
                return current;
            }
            current = current.minusDays(1);
        }
        return null;
    }

    /**
     * Checks if two times are on the same day.
     * @param dayOne The first day.
     * @param dayTwo The second day.
     * @return Whether the times are on the same day.
     */
    public static boolean isSameDay(Calendar dayOne, Calendar dayTwo) {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Returns a calendar instance at the start of this day
     * @return the calendar instance
     */
    public static Calendar today(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today;
    }

    public static int sortDayOfWeek(int dayOfWeek){
        // 1 Sunday, 2 Monday, 3 Tuesday, 4 Wednesday, 5 thursday, 6 friday, 7 saturday
        switch (dayOfWeek){
            case 1:
                dayOfWeek = 6;
                break;
            case 2:
                dayOfWeek = 0;
                break;
            case 3:
                dayOfWeek = 1;
                break;
            case 4:
                dayOfWeek = 2;
                break;
            case 5:
                dayOfWeek = 3;
                break;
            case 6:
                dayOfWeek = 4;
                break;
            case 7:
                dayOfWeek = 5;
                break;
        }
        return dayOfWeek;
    }
}
