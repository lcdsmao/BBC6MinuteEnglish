package com.example.mao.BBCLearningEnglish.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.mao.BBCLearningEnglish.cache.App;
import com.example.mao.BBCLearningEnglish.data.BBCContentContract;
import com.example.mao.BBCLearningEnglish.data.BBCPreference;
import com.example.mao.BBCLearningEnglish.utilities.BBCHtmlUtility;

import org.jsoup.nodes.Document;

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

//    synchronized public static void syncContentList(Context context, String category) {
//
//    }

    synchronized public static void syncCategoryList(Context context, String category) {
        String url = BBCHtmlUtility.sCategoryMap.get(category);
        BBCRequest bbcRequest = new BBCRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!TextUtils.isEmpty(response)) BBCSyncUtility.sIsContentListSyncComplete = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        BBCSyncUtility.sIsContentListSyncComplete = true;
                        Log.v(TAG, "Volley Error");
                    }
                }, context);
        bbcRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        App.getRequestQueue(context).add(bbcRequest);
        BBCPreference.setLastUpdateTime(context, category);
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
