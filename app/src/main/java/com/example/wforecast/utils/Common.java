package com.example.wforecast.utils;

import android.annotation.SuppressLint;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    private static final String TAG = "Common";

    public static String API_KEY_GOOGLE_MAPS = "AIzaSyBQSU6kjhqdvHB_Io1Ox8CZAO9wxs4Lt8U";
    public static String API_KEY_2 = "5036a7df940464b000fe94604b96b465";
    public static Location CURRENT_LOCATION;
    public static String UNITS = "imperial";

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String x = format.format(date);
        return x;
    }


}
