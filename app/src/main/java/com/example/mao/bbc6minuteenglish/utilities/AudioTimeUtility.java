package com.example.mao.bbc6minuteenglish.utilities;

/**
 * Created by MAO on 7/31/2017.
 */

public class AudioTimeUtility {

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
}
