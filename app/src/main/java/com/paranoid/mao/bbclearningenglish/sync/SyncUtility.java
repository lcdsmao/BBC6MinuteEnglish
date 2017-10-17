package com.paranoid.mao.bbclearningenglish.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

/**
 * Created by MAO on 7/24/2017.
 */

public class SyncUtility {

    public static boolean sIsContentListSyncComplete = true;

    synchronized public static void articleInitialize(final Context context,
                                                      @NonNull final Uri uriWithTimeStamp) {

        final String projection[] = {DatabaseContract.BBCLearningEnglishEntry.COLUMN_ARTICLE,
                DatabaseContract.BBCLearningEnglishEntry.COLUMN_HREF};
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
        intentToSyncImmediately.putExtra(DatabaseContract.BBCLearningEnglishEntry.COLUMN_HREF,
                articleHref);
        context.startService(intentToSyncImmediately);
    }

    private static void startContentLisSyncByCategory(@NonNull final Context context, final String category) {
        Intent intentToSyncImmediately = new Intent(context, BBCSyncContentListIntentService.class)
                .putExtra(DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY, category);
        context.startService(intentToSyncImmediately);
    }

    synchronized public static void wordBookInitialize(final Context context,
                                                      @NonNull final Uri uriWithID) {

        final String projection[] = {
                DatabaseContract.VocabularyEntry.COLUMN_VOCAB,
                DatabaseContract.VocabularyEntry.COLUMN_MEAN};
        final int VOCAB_INDEX = 0;
        final int MEAN_INDEX = 1;

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = context.getContentResolver().query(uriWithID,
                        projection,
                        null,
                        null,
                        null);
                if (cursor == null) return;
                cursor.moveToFirst();
                String vocab = cursor.getString(VOCAB_INDEX);
                String mean = cursor.getString(MEAN_INDEX);

                if (TextUtils.isEmpty(mean)) {
                    startVocabularySync(context, uriWithID, vocab);
                }

                cursor.close();
            }
        });

        checkForEmpty.start();

    }

    private static void startVocabularySync(final Context context, Uri uriWithID, String vocab){
        Intent intent = new Intent(context, SyncVocabularyIntentService.class)
                .setData(uriWithID)
                .putExtra(DatabaseContract.VocabularyEntry.COLUMN_VOCAB, vocab);
        context.startService(intent);
    }
}
