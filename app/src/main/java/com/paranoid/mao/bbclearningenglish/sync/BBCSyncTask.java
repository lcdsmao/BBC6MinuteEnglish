package com.paranoid.mao.bbclearningenglish.sync;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.paranoid.mao.bbclearningenglish.singleton.MyApp;
import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.data.BBCPreference;
import com.paranoid.mao.bbclearningenglish.utilities.NotificationUtility;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncTask {

    synchronized public static void syncArticle(Context context,
                                                @NonNull Uri uriWithTimeStamp,
                                                @NonNull String articleHref) {
        BBCArticleRequest request = new BBCArticleRequest(Request.Method.GET, articleHref
                , uriWithTimeStamp, context);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApp.getRequestQueue(context).add(request);

    }

    synchronized public static void syncCategoryList(final Context context, final String category) {
        String url = BBCCategory.sCategoryUrlMap.get(category);
        Log.v("Tag", url);
        BBCContentListRequest request = new BBCContentListRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BBCSyncUtility.sIsContentListSyncComplete = true;
                        if (!TextUtils.isEmpty(response)) {
                            NotificationUtility.showNewContentNotification(context, response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        BBCSyncUtility.sIsContentListSyncComplete = true;
                    }
                }, context);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApp.getRequestQueue(context).add(request);
        BBCPreference.setLastUpdateTime(context, category, System.currentTimeMillis());
    }
}
