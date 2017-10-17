package com.paranoid.mao.bbclearningenglish.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.data.VocabularyDefinition;
import com.paranoid.mao.bbclearningenglish.singleton.MyApp;
import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.utilities.NotificationUtility;

/**
 * Created by MAO on 7/24/2017.
 */

public class SyncTask {

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
        if (!BBCCategory.sCategoryUrlMap.containsKey(category)) {
            SyncUtility.sIsContentListSyncComplete = true;
            return;
        }
        String url = BBCCategory.sCategoryUrlMap.get(category);
        BBCContentListRequest request = new BBCContentListRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SyncUtility.sIsContentListSyncComplete = true;
                        if (!TextUtils.isEmpty(response)) {
                            NotificationUtility.showNewContentNotification(context, response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        SyncUtility.sIsContentListSyncComplete = true;
                    }
                }, context);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApp.getRequestQueue(context).add(request);
    }

    synchronized public static void syncVocab(final Context context, final Uri uriWithID, final String vocab) {
        WordReferenceRequest request = new WordReferenceRequest(vocab, new Response.Listener<VocabularyDefinition>() {
            @Override
            public void onResponse(VocabularyDefinition response) {
                String symbol = response.getSymbol();
                String definition = response.getDefinition();
                String audioHref = response.getAudioHref();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_VOCAB, vocab);
                contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_MEAN, definition);
                contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_SYMBOL, symbol);
                contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_AUDIO_HREF, audioHref);
                Log.v("SyncV", symbol + definition);
                context.getContentResolver().update(uriWithID, contentValues, null, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, context.getString(R.string.default_definition),
                        Toast.LENGTH_SHORT).show();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApp.getRequestQueue(context).add(request);
    }
}
