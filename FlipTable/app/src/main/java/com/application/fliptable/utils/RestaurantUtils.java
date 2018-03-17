package com.application.fliptable.utils;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yanchummar on 12/31/17.
 */

public class RestaurantUtils {

    private int currentInternetTime = -1;
    private String todayString = null, todayFullString = null;

    public static String CLOSED_NOW = "Closed Now", CLOSING_SOON = "Closing Soon",
            BOOKINGS_CLOSED = "Bookings Closed", OPEN_NOW = "Open Now", OPENING_SOON="Opening Soon";

    public RestaurantUtils(String dateString){
        InternetTime internetTime = new InternetTime(dateString);
        if (internetTime.getMinute() < 10){
            currentInternetTime = Integer.parseInt(String.valueOf(internetTime.getHour()).concat("0").concat(String.valueOf(internetTime.getMinute())));
        }else {
            currentInternetTime = Integer.parseInt(String.valueOf(internetTime.getHour()).concat(String.valueOf(internetTime.getMinute())));
        }
        todayString = String.valueOf(DateFormat.format("EEE", internetTime.getDate())).toLowerCase();
        todayFullString = String.valueOf(DateFormat.format("EEEE", internetTime.getDate()));
    }

    public static int getImage(Context context, String imageName) {
        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        return drawableResourceId;
    }

    public String determineOpenStatus(boolean openBoolean, String openingTime, String closingTime) {
        // Off Day calculator
        if (openingTime == null || closingTime == null || !openingTime.contains(":")){
            return "Closed on " +todayFullString+"s";
        }
        if (openBoolean) {
            int startTime, endTime;
            int currentTime;
            String openStatus;
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("Hmm", Locale.ENGLISH);
            if (currentInternetTime != -1){
                currentTime = currentInternetTime;
            }else {
                currentTime = Integer.parseInt(sdf.format(cal.getTime()));
            }
            Log.i("D", "currentTime:" + currentTime);

            try {
                SimpleDateFormat hrFormat = new SimpleDateFormat("H:mm", Locale.ENGLISH);
                SimpleDateFormat smallHrFormat = new SimpleDateFormat("Hmm", Locale.ENGLISH);
                Date dateObj = hrFormat.parse(openingTime);
                Date dateObjEnd = hrFormat.parse(closingTime);
                startTime = Integer.parseInt(smallHrFormat.format(dateObj));
                endTime = Integer.parseInt(smallHrFormat.format(dateObjEnd));
                Log.i("TSAdapter", "startTime and EndTime: " + startTime + " end: " + endTime);
            } catch (ParseException e) {
                //Exception Handling
                return null;
            }

            if (endTime < 1000 && !(currentTime > 2359)){
                endTime += 2400;
            }

            if (currentTime < startTime || currentTime > endTime || currentTime == endTime) {
                openStatus = CLOSED_NOW;
            } else {
                openStatus = OPEN_NOW;
            }

            int bookingsClosedDecisionTime, closingSoonTime, openingSoonTime;
            String endTimeMinutes = String.valueOf(endTime).substring(2);
            if (Integer.parseInt(endTimeMinutes) >= 50) {
                closingSoonTime = endTime - 50;
            } else {
                closingSoonTime = endTime - 90;
            }
            if (Integer.parseInt(endTimeMinutes) >= 20){
                bookingsClosedDecisionTime = endTime - 20;
                openingSoonTime = startTime - 20;
            } else {
                bookingsClosedDecisionTime = endTime - 60;
                openingSoonTime = startTime - 60;
            }

            if (currentTime >= openingSoonTime && currentTime < startTime){
                openStatus = OPENING_SOON;
            }

            if (currentTime >= closingSoonTime) {
                openStatus = CLOSING_SOON;
            }

            if (currentTime >= endTime) {
                openStatus = CLOSED_NOW;
            }

            Log.i("RestaurantUtils", "bookingsClosingDecisionTime:"+bookingsClosedDecisionTime+ " currentTime:"+currentTime+" endtime:"+endTime+" closingSoonTime:"+closingSoonTime);

            return openStatus;
        } else {
            return "Bookings Closed";
        }
    }

    public static String simplifyTime(String time){
        String[] simpleTimeArray = time.split(":");
        String timeInDay = simpleTimeArray[2];
        String simpleTime;

        if (simpleTimeArray[1].matches("00")){
            simpleTime = simpleTimeArray[0] + timeInDay;
        }else{
            simpleTime = simpleTimeArray[0]+":"+simpleTimeArray[1]+ " " + timeInDay;
        }

        return simpleTime;
    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
