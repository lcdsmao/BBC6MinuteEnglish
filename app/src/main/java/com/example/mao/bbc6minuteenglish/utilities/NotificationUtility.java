package com.example.mao.bbc6minuteenglish.utilities;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.mao.bbc6minuteenglish.ArticleActivity;
import com.example.mao.bbc6minuteenglish.AudioPlayService;
import com.example.mao.bbc6minuteenglish.R;
import com.example.mao.bbc6minuteenglish.data.BBCContentContract;

/**
 * Created by MAO on 7/28/2017.
 */

public class NotificationUtility {

    private static final String[] PROJECTION = {
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TITLE,
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_DESCRIPTION
    };

    private static final int TITLE_INDEX = 0;
    private static final int DESCRIPTION_INDEX = 1;

    @Nullable
    public static Notification buildAudioServiceNotification(Context context,
                                                             Uri uriWithTimeStamp,
                                                             String action) {

        Cursor queryCursor = context.getContentResolver().query(uriWithTimeStamp,
                PROJECTION,
                null,
                null,
                null);
        if (queryCursor == null || !queryCursor.moveToFirst()) return null;
        String title = queryCursor.getString(TITLE_INDEX);
        String description = queryCursor.getString(DESCRIPTION_INDEX);
        queryCursor.close();

        Intent intentActivity = new Intent(context, ArticleActivity.class)
                .setData(uriWithTimeStamp);
        PendingIntent pendingIntentActivity =
                PendingIntent.getActivity(context, 0, intentActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentService = new Intent(context, AudioPlayService.class)
                .setAction(action);
        PendingIntent pendingIntentService = PendingIntent.getService(context, 0, intentService, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setShowWhen(false)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntentActivity)
                .addAction(getDrawable(action), getActionName(context, action), pendingIntentService)
                .setSmallIcon(R.drawable.ic_headset)
                .build();
        return notification;
    }

    private static int getDrawable(String action) {
        switch (action) {
            case AudioPlayService.ACTION_PLAY:
                return R.drawable.ic_play_arrow;
            case AudioPlayService.ACTION_PAUSE:
                return R.drawable.ic_pause;
            default:
                return R.drawable.ic_play_arrow;
        }
    }

    private static String getActionName(Context context, String action){
        switch (action) {
            case AudioPlayService.ACTION_PLAY:
                return context.getString(R.string.play);
            case AudioPlayService.ACTION_PAUSE:
                return context.getString(R.string.pause);
            default:
                return context.getString(R.string.play);
        }
    }
}
