package com.example.mao.bbc6minuteenglish.sync;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.utilities.BBCContentUtility;
import com.example.mao.bbc6minuteenglish.utilities.BBCHtmlUtility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncTask {

    synchronized public static void syncArticle(Context context,
                                                @NonNull Uri uriWithTimeStamp,
                                                @NonNull String articleHref) {

        Document document = BBCHtmlUtility.getArticleDocument(articleHref);
        ContentValues contentValuesArticle = BBCContentUtility.getContentValuesArticle(document);
        context.getContentResolver().update(uriWithTimeStamp, contentValuesArticle, null, null);
    }

    synchronized public static void syncContentList(Context context) {
        Elements contentList = BBCHtmlUtility.getContentsList();
        if (contentList == null) return;
        final int max = 20;
        int maxLength = Math.min(max, contentList.size());
        ContentResolver contentResolver = context.getContentResolver();

        for (int i = 0; i < maxLength; i++) {
            Element content = contentList.get(i);
            long timeStamp = BBCHtmlUtility.getTimeStamp(content);
            Uri uriWithTimStamp = ContentUris.withAppendedId(
                    BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                    timeStamp);
            Cursor queryCursor = contentResolver.query(
                    uriWithTimStamp,
                    null,
                    null,
                    null,
                    null);
            if (queryCursor == null || queryCursor.moveToFirst()) continue;
            ContentValues contentValues = BBCContentUtility.getContentValues(content);
            contentResolver.insert(BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                    contentValues);
            queryCursor.close();
        }

        Cursor cursor = contentResolver.query(
                BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                null,
                null,
                null,
                BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP);
        if (cursor != null && cursor.getCount() > maxLength) {
            final String where = BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP
                    + " NOT IN "
                    + " (SELECT "
                    + BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP
                    + " FROM "
                    + BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME
                    + " ORDER BY "
                    + BBCContentContract.BBC6MinuteEnglishEntry.SORT_ORDER
                    + " LIMIT "
                    + maxLength
                    + ")";
            contentResolver.delete(BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                    where,
                    null);
        }
        cursor.close();
    }
}
