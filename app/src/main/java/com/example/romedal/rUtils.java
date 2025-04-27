package com.example.romedal;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import lecho.lib.hellocharts.model.PointValue;

public class rUtils {

    public static final String TAG = "r_TAG";

    public static int getCurrentYear() {

        int currentYear;
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Log.d(TAG, "Current year is " + currentYear);

        return currentYear;
    }

    public static int getCurrentMonth() {

        int currentMonth = 1;
        currentMonth += Calendar.getInstance().get(Calendar.MONTH);
        Log.d(TAG, "Current month is " + currentMonth);

        return currentMonth;
    }

    public static String getDayOfMonth(String month, String year) {
        int daysCount, date = 1;

        getCurrentYear();
        getCurrentMonth();

        DecimalFormat twoDigits = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int yearCalendar = Integer.parseInt(year);
        int monthCalendar = Integer.parseInt(month);
        calendar.set(yearCalendar, monthCalendar - 1, date);
        daysCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Log.d(TAG, "yearCalendar => " + yearCalendar);
        Log.d(TAG, "monthCalendar => " + monthCalendar);
        Log.d(TAG, "daysCount => " + daysCount);
        Log.d(TAG, "number of days in month: " + twoDigits.format(daysCount));

        return twoDigits.format(daysCount);
    }

    public static Map<PointValue, String> fillChart() {

        float r = 1.0F;
        Map<PointValue, String> points = new LinkedHashMap<>();

        for (int i = 0; i < 31; i++) {
            r += 0.1F;
            points.put(new PointValue(i, r), "");
        }

        return points;
    }

}
