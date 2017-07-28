package com.example.mao.bbc6minuteenglish.utilities;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

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
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME
    };

    private static final int TITLE_INDEX = 0;
    private static final int TIME_INDEX = 1;

    @Nullable
    public static Notification buildAudioServiceNotification(Context context, Uri uriWithTimeStamp) {

        Cursor queryCursor = context.getContentResolver().query(uriWithTimeStamp,
                PROJECTION,
                null,
                null,
                null);
        if (queryCursor == null || !queryCursor.moveToFirst()) return null;
        String title = queryCursor.getString(TITLE_INDEX);
        String time = queryCursor.getString(TIME_INDEX);
        queryCursor.close();

        Intent intent = new Intent(context, ArticleActivity.class)
                .setData(uriWithTimeStamp);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(time)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_headset)
                .build();
        return notification;
    }

    private static NotificationCompat.Action createPlayAction(Context context) {
        Intent intent = new Intent(context, AudioPlayService.class)
                .setAction(AudioPlayService.ACTION_PLAY);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action(
                R.drawable.ic_play_arrow,
                context.getString(R.string.play),
                pendingIntent);
        return action;
    }

    private static NotificationCompat.Action createPauseAction(Context context) {
        Intent intent = new Intent(context, AudioPlayService.class)
                .setAction(AudioPlayService.ACTION_PAUSE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action(
                R.drawable.ic_pause,
                context.getString(R.string.pause),
                pendingIntent);
        return action;
    }
}
