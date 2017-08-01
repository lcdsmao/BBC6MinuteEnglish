package com.example.mao.bbc6minuteenglish.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.mao.bbc6minuteenglish.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by MAO on 8/1/2017.
 */

public class PreferenceUtility {

    public static int getPreferenceMaxHistory(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String maxHistoryKey = context.getString(R.string.setting_history_key);
        return Integer.parseInt(sharedPreferences.getString(maxHistoryKey,
                context.getString(R.string.setting_history_less)));
    }

    public static boolean getNotificationSwitch(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String notificationKey = context.getString(R.string.setting_notification_key);
        return sharedPreferences.getBoolean(notificationKey, true);
    }

    public static void setLastUpdateTime(Context context, long time) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastUpdateKey = context.getString(R.string.pref_last_update_time_key);
        editor.putLong(lastUpdateKey, time);
        editor.apply();
    }

    private static long getLastUpdateTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lastUpdateKey = context.getString(R.string.pref_last_update_time_key);
        return sharedPreferences.getLong(lastUpdateKey, 0);
    }

    public static boolean isUpdateNeed(Context context) {
        long currentTime = System.currentTimeMillis();
        long lastUpdateTime = getLastUpdateTime(context);
        return  TimeUnit.MILLISECONDS.toDays(currentTime - lastUpdateTime) > 2;
    }

}
