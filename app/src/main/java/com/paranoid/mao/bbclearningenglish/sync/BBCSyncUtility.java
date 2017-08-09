package com.paranoid.mao.bbclearningenglish.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.paranoid.mao.bbclearningenglish.data.BBCContentContract;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncUtility {

    public static boolean sIsContentListSyncComplete = true;

    synchronized public static void articleInitialize(final Context context,
                                                      @NonNull final Uri uriWithTimeStamp) {

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

    synchronized public static void contentListSync(final Context context, final String category){
        sIsContentListSyncComplete = false;
        startContentLisSyncByCategory(context, category);
    }

    private static void startArticleSync(@NonNull final Context context,
                                        final Uri uriWithTimeStamp,
                                        final String articleHref) {
        Intent intentToSyncImmediately = new Intent(context, BBCSyncArticleIntentService.class);
        intentToSyncImmediately.setData(uriWithTimeStamp);
        intentToSyncImmediately.putExtra(BBCContentContract.BBCLearningEnglishEntry.COLUMN_HREF,
                articleHref);
        context.startService(intentToSyncImmediately);
    }

    private static void startContentLisSyncByCategory(@NonNull final Context context, final String category) {
        Intent intentToSyncImmediately = new Intent(context, BBCSyncContentListIntentService.class)
                .putExtra(BBCContentContract.BBCLearningEnglishEntry.COLUMN_CATEGORY, category);
        context.startService(intentToSyncImmediately);
    }

}
