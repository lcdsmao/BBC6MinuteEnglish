package com.example.mao.BBCLearningEnglish.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mao.BBCLearningEnglish.data.BBCContentContract;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncUtility {

    protected static boolean sIsContentListSyncComplete = true;

    synchronized public static void articleInitialize(final Context context,
                                                      @NonNull final Uri uriWithTimeStamp) {

        Log.v(uriWithTimeStamp.toString(), "Start initialize");
        final String projection[] = {BBCContentContract.BBCLearningEnglishEntry.COLUMN_ARTICLE,
                BBCContentContract.BBCLearningEnglishEntry.COLUMN_HREF};
        final int ARTICLE_INDEX = 0;
        final int ARTICLE_HREF_INDEX = 1;

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = context.getContentResolver().query(uriWithTimeStamp,
                        projection,
                        null,
                        null,
                        null);
                if (cursor == null) return;
                cursor.moveToFirst();

                String article = cursor.getString(ARTICLE_INDEX);
                String articleHref = cursor.getString(ARTICLE_HREF_INDEX);

                if (article == null || article.length() == 0) {
                    startArticleSync(context, uriWithTimeStamp, articleHref);
                }

                cursor.close();
            }
        });

        checkForEmpty.start();

    }

    public static boolean isIsContentListSyncComplete() {
        return sIsContentListSyncComplete;
    }

    synchronized public static void contentListSync(final Context context){
        sIsContentListSyncComplete = false;
        startContentLisSync(context);
    }

    private static void startArticleSync(@NonNull final Context context,
                                        final Uri uriWithTimeStamp,
                                        final String articleHref) {
        Log.v(uriWithTimeStamp.toString(), "Start article sync");
        Intent intentToSyncImmediately = new Intent(context, BBCSyncArticleIntentService.class);
        intentToSyncImmediately.setData(uriWithTimeStamp);
        intentToSyncImmediately.putExtra(BBCContentContract.BBCLearningEnglishEntry.COLUMN_HREF,
                articleHref);
        context.startService(intentToSyncImmediately);
    }

    private static void startContentLisSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, BBCSyncContentListIntentService.class);
        context.startService(intentToSyncImmediately);
    }

}
