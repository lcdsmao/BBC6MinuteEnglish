package com.paranoid.mao.bbclearningenglish.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by MAO on 7/31/2017.
 */

public class TimeUtility {

    public static String getDisplayTime(int milliseconds) {
        int minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        String timeString;
        if (minutes < 10) {
            timeString = "0" + String.valueOf(minutes);
        } else {
            timeString = String.valueOf(minutes);
        }
        timeString += ":";
        if (seconds < 10) {
            timeString += "0" + String.valueOf(seconds);
        } else {
            timeString += String.valueOf(seconds);
        }
        return timeString;
    }

    public static long getTimeStamp(String timeFromBBC) {
        long timestamp = -1;
        try {
            DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            timestamp = format.parse(timeFromBBC).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }
}
