package com.example.mao.BBCLearningEnglish.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mao.BBCLearningEnglish.cache.App;
import com.example.mao.BBCLearningEnglish.data.BBCContentContract;
import com.example.mao.BBCLearningEnglish.data.BBCPreference;
import com.example.mao.BBCLearningEnglish.utilities.BBCHtmlUtility;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.CountDownLatch;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncTask {

    private final static String TAG = BBCSyncTask.class.getSimpleName();

    synchronized public static void syncArticle(Context context,
                                                @NonNull Uri uriWithTimeStamp,
                                                @NonNull String articleHref) {

        Document document = BBCHtmlUtility.getArticleDocument(articleHref);
        ContentValues contentValuesArticle = getContentValuesArticle(document);
        context.getContentResolver().update(uriWithTimeStamp, contentValuesArticle, null, null);
    }

    synchronized public static void syncContentList(Context context) {
        syncCategoryList(context, BBCHtmlUtility.BBC_LINGO_HACK_URL);
        syncCategoryList(context, BBCHtmlUtility.BBC_ENGLISH_AT_UNIVERSITY_URL);
        syncCategoryList(context, BBCHtmlUtility.BBC_ENGLISH_AT_WORK_URL);
        syncCategoryList(context, BBCHtmlUtility.BBC_THE_ENGLISH_WE_SPEAK_URL);
        syncCategoryList(context, BBCHtmlUtility.BBC_NEWS_REPORT_URL);
        syncCategoryList(context, BBCHtmlUtility.BBC_6_MINUTE_ENGLISH_URL);

        BBCSyncUtility.sIsContentListSyncComplete = true;
        BBCPreference.setLastUpdateTime(context, System.currentTimeMillis());
    }

    synchronized private static void syncCategoryList(final Context context, final String url) {
        BBCRequest bbcRequest = new BBCRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "Volley Error");
                    }
                }, context);
        bbcRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        App.getRequestQueue(context).add(bbcRequest);
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
