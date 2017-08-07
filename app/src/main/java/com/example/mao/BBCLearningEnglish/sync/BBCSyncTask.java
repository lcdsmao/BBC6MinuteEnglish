package com.example.mao.BBCLearningEnglish.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mao.BBCLearningEnglish.data.BBCContentContract;
import com.example.mao.BBCLearningEnglish.data.BBCPreference;
import com.example.mao.BBCLearningEnglish.utilities.BBCHtmlUtility;

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
        ContentValues contentValuesArticle = getContentValuesArticle(document);
        context.getContentResolver().update(uriWithTimeStamp, contentValuesArticle, null, null);
    }

    synchronized public static void syncContentList(Context context) {

        syncCategoryList(context, BBCHtmlUtility.BBC_6_MINUTE_ENGLISH_URL);
        syncCategoryList(context, BBCHtmlUtility.BBC_NEWS_REPORT_URL);
        syncCategoryList(context, BBCHtmlUtility.BBC_THE_ENGLISH_WE_SPEAK_URL);
        syncCategoryList(context, BBCHtmlUtility.BBC_ENGLISH_AT_WORK_URL);
        syncCategoryList(context, BBCHtmlUtility.BBC_ENGLISH_AT_UNIVERSITY_URL);
        syncCategoryList(context, BBCHtmlUtility.BBC_LINGO_HACK_URL);

        BBCSyncUtility.sIsContentListSyncComplete = true;
        BBCPreference.setLastUpdateTime(context, System.currentTimeMillis());
    }

    private static void syncCategoryList(Context context, String url) {
        Elements contentList = BBCHtmlUtility.getContentsList(url);
        String filter = BBCHtmlUtility.getCategory(url);

        int max = BBCPreference.getPreferenceMaxHistory(context);
        int maxHistory = Math.min(max, contentList.size());

        ContentResolver contentResolver = context.getContentResolver();
        for (int i = 0; i < maxHistory; i++) {
            Element content = contentList.get(i);
            ContentValues contentValues = getContentValues(content, filter);
            Log.v(TAG, filter);
            try {
                contentResolver.insert(
                        BBCContentContract.BBCLearningEnglishEntry.CONTENT_URI,
                        contentValues);
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        contentResolver.delete(BBCContentContract.BBCLearningEnglishEntry.CONTENT_URI,
                BBCContentContract.BBCLearningEnglishEntry.getMaxHistoryWhere(maxHistory, filter),
                null);
    }

    private static ContentValues getContentValues(Element content, String filter) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BBCContentContract.BBCLearningEnglishEntry.COLUMN_TITLE,
                BBCHtmlUtility.getTitle(content));
        contentValues.put(BBCContentContract.BBCLearningEnglishEntry.COLUMN_TIME,
                BBCHtmlUtility.getTime(content));
        contentValues.put(BBCContentContract.BBCLearningEnglishEntry.COLUMN_DESCRIPTION,
                BBCHtmlUtility.getDescription(content));
        contentValues.put(BBCContentContract.BBCLearningEnglishEntry.COLUMN_HREF,
                BBCHtmlUtility.getArticleHref(content));
        contentValues.put(BBCContentContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP,
                BBCHtmlUtility.getTimeStamp(content));
        contentValues.put(BBCContentContract.BBCLearningEnglishEntry.COLUMN_THUMBNAIL_HREF,
                BBCHtmlUtility.getImageHref(content));
        contentValues.put(BBCContentContract.BBCLearningEnglishEntry.COLUMN_CATEGORY,
                filter);
        return contentValues;
    }

    private static ContentValues getContentValuesArticle(Document document){
        String article = BBCHtmlUtility.getArticleHtml(document);
        String audioHref = BBCHtmlUtility.getMp3Href(document);
        ContentValues contentValues = new ContentValues();
        contentValues.put(BBCContentContract.BBCLearningEnglishEntry.COLUMN_ARTICLE,
                article);
        contentValues.put(BBCContentContract.BBCLearningEnglishEntry.COLUMN_MP3_HREF,
                audioHref);
        return contentValues;
    }
}
