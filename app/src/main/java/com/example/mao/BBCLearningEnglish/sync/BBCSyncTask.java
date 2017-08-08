package com.example.mao.BBCLearningEnglish.sync;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.mao.BBCLearningEnglish.singleton.MyApp;
import com.example.mao.BBCLearningEnglish.data.BBCCategory;
import com.example.mao.BBCLearningEnglish.data.BBCPreference;
import com.example.mao.BBCLearningEnglish.utilities.NotificationUtility;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncTask {

    private final static String TAG = BBCSyncTask.class.getSimpleName();

    synchronized public static void syncArticle(Context context,
                                                @NonNull Uri uriWithTimeStamp,
                                                @NonNull String articleHref) {
        BBCArticleRequest request = new BBCArticleRequest(Request.Method.GET, articleHref
                , uriWithTimeStamp, context);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApp.getRequestQueue(context).add(request);

    }

//    synchronized public static void syncContentList(Context context, String category) {
//
//    }

    synchronized public static void syncCategoryList(final Context context, final String category) {
        String url = BBCCategory.sCategoryUrlMap.get(category);
        BBCContentListRequest request = new BBCContentListRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BBCSyncUtility.sIsContentListSyncComplete = true;
                        Log.v(TAG, "Response:" + response);
                        if (!TextUtils.isEmpty(response)) {
                            NotificationUtility.showNewContentNotification(context, response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        BBCSyncUtility.sIsContentListSyncComplete = true;
                        Log.v(TAG, "Volley Error");
                    }
                }, context);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApp.getRequestQueue(context).add(request);
        BBCPreference.setLastUpdateTime(context, category, System.currentTimeMillis());
    }
}
