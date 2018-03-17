package com.application.fliptable.business.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yanchummar on 1/5/18.
 */

public class InternetTime {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private Date mDate;

    private static final String DATE_FORMAT = "H:mm dd-MM-yyyy";

    public InternetTime(String dateString){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date date = dateFormat.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            // Initializing
            year = Integer.parseInt(DateFormat.format("yyyy", cal).toString());
            month = Integer.parseInt(DateFormat.format("MM", cal).toString());
            day = Integer.parseInt(DateFormat.format("dd", cal).toString());
            hour = Integer.parseInt(DateFormat.format("H", cal).toString());
            minute = Integer.parseInt(DateFormat.format("mm", cal).toString());
            mDate = date;
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public Date getDate() {
        return mDate;
    }
}
