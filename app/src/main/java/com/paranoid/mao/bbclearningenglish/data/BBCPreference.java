package com.paranoid.mao.bbclearningenglish.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.paranoid.mao.bbclearningenglish.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by MAO on 8/1/2017.
 */

public class BBCPreference {

    public static boolean getNotificationSwitch(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String notificationKey = context.getString(R.string.setting_notification_key);
        return sharedPreferences.getBoolean(notificationKey, true);
    }

    public static void setLastUpdateTime(Context context, String category, long time) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastUpdateKey = category + context.getString(R.string.pref_last_update_time_key);
        editor.putLong(lastUpdateKey, time);
        editor.apply();
    }

    public static long getLastUpdateTime(Context context, String category) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lastUpdateKey = category + context.getString(R.string.pref_last_update_time_key);
        return sharedPreferences.getLong(lastUpdateKey, 0);
    }

    public static boolean isUpdateNeed(Context context, String category) {
        long currentTime = System.currentTimeMillis();
        long lastUpdateTime = getLastUpdateTime(context, category);
        return TimeUnit.MILLISECONDS.toDays(currentTime - lastUpdateTime) >= 2;
    }

}
