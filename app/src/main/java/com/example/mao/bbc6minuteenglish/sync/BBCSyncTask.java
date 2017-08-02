package com.example.mao.bbc6minuteenglish.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.data.PreferenceUtility;
import com.example.mao.bbc6minuteenglish.utilities.BBCContentUtility;
import com.example.mao.bbc6minuteenglish.utilities.BBCHtmlUtility;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncTask {

    private final static String TAG = BBCSyncTask.class.getName();

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
        int max = PreferenceUtility.getPreferenceMaxHistory(context);
        int maxHistory = Math.min(max, contentList.size());
        ContentResolver contentResolver = context.getContentResolver();

        for (int i = 0; i < maxHistory; i++) {
            Element content = contentList.get(i);
            ContentValues contentValues = BBCContentUtility.getContentValues(content);
            try {
                contentResolver.insert(
                        BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                        contentValues);
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        contentResolver.delete(BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                BBCContentContract.BBC6MinuteEnglishEntry.getMaxHistoryWhere(maxHistory),
                null);
        BBCSyncUtility.sIsContentListSyncComplete = true;
        PreferenceUtility.setLastUpdateTime(context, System.currentTimeMillis());
    }
}
