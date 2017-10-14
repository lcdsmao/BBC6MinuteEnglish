package com.paranoid.mao.bbclearningenglish.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.paranoid.mao.bbclearningenglish.article.ArticleActivity;
import com.paranoid.mao.bbclearningenglish.article.AudioPlayService;
import com.paranoid.mao.bbclearningenglish.list.MainActivity;
import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.singleton.MyApp;
import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

/**
 * Created by MAO on 7/28/2017.
 */

public class NotificationUtility {

    private static final String[] PROJECTION = {
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_TITLE,
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_DESCRIPTION
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

        Notification notification = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setShowWhen(false)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntentActivity)
                .addAction(createAction(context, AudioPlayService.ACTION_REPLAY))
                .addAction(createAction(context, action))
                .addAction(createAction(context, AudioPlayService.ACTION_FORWARD))
                .setSmallIcon(R.drawable.ic_headset)
                .setStyle(new android.support.v7.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0,1,2))
                .build();
        return notification;
    }

    private static int getDrawable(String action) {
        switch (action) {
            case AudioPlayService.ACTION_PLAY:
                return R.drawable.ic_play_arrow;
            case AudioPlayService.ACTION_PAUSE:
                return R.drawable.ic_pause;
            case AudioPlayService.ACTION_FORWARD:
                return R.drawable.ic_forward_5;
            case AudioPlayService.ACTION_REPLAY:
                return R.drawable.ic_replay_5;
            default:
                return R.drawable.ic_play_arrow;
        }
    }

    @NonNull
    private static String getActionName(Context context, String action){
        switch (action) {
            case AudioPlayService.ACTION_PLAY:
                return context.getString(R.string.play);
            case AudioPlayService.ACTION_PAUSE:
                return context.getString(R.string.pause);
            case AudioPlayService.ACTION_FORWARD:
                return context.getString(R.string.forward);
            case AudioPlayService.ACTION_REPLAY:
                return context.getString(R.string.replay);
            default:
                return context.getString(R.string.play);
        }
    }

    private static NotificationCompat.Action createAction(Context context, String action) {
        Intent intentService = new Intent(context, AudioPlayService.class)
                .setAction(action);
        PendingIntent pendingIntentService = PendingIntent.getService(context, 0, intentService, 0);
        return new NotificationCompat.Action(
                getDrawable(action), getActionName(context, action), pendingIntentService);
    }

    public static void showNewContentNotification(Context context, String newContent) {
        if (MyApp.isActivityVisible()) return;
        String[] content = splitContent(newContent);
        String contentTitle = context.getString(R.string.notification_new_content) + " "
                + context.getString(BBCCategory.sCategoryStringResourceMap.get(content[0]));
        Intent intent = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY, content[0]);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_headset)
                .setContentTitle(contentTitle)
                .setContentText(content[1])
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(12345, notification);
    }

    public static String createContent(String category, String title) {
        return category + "$" + title;
    }

    private static String[] splitContent(String content) {
        return content.split("\\$");
    }
}
