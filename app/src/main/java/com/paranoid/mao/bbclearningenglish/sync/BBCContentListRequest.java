package com.paranoid.mao.bbclearningenglish.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.data.BBCPreference;
import com.paranoid.mao.bbclearningenglish.utilities.BBCHtmlUtility;
import com.paranoid.mao.bbclearningenglish.utilities.NotificationUtility;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;

/**
 * Created by Paranoid on 17/8/7.
 */

public class BBCContentListRequest extends StringRequest {

    private static final int MAX_NUM = 200;

    private String mCategory;
    private Context mContext;
    private boolean mIsNew;
    private String mContentTitle;

    public BBCContentListRequest(int method, String url, Response.Listener<String> listener,
                                 Response.ErrorListener errorListener, Context context) {
        super(method, url, listener, errorListener);
        mCategory = BBCCategory.sCategoryUrlMap.get(url);
        mContext = context;
        mIsNew = false;
    }

    @Override
    protected void deliverResponse(String response) {
        String deliverContent = mIsNew?
                NotificationUtility.createContent(mCategory, mContentTitle) : "";
        super.deliverResponse(deliverContent);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String htmlString;
        try {
            htmlString =
                    new String(response.data, "UTF-8");
            Elements contentList = BBCHtmlUtility.getContentsList(htmlString);

            if (contentList == null || contentList.size() == 0) return super.parseNetworkResponse(response);

            int max = Math.min(MAX_NUM, contentList.size());
            ContentResolver contentResolver = mContext.getContentResolver();
            for (int i = 0; i < max; i++) {
                Element content = contentList.get(i);
                ContentValues contentValues = getContentValues(content, mCategory);
                contentResolver.insert(
                        DatabaseContract.BBCLearningEnglishEntry.CONTENT_URI,
                        contentValues);
            }

            long newestContentTime = BBCHtmlUtility.getTimeStamp(contentList.get(0));
            mIsNew = newestContentTime > BBCPreference.getLastUpdateTime(mContext, mCategory);
            mContentTitle = BBCHtmlUtility.getTitle(contentList.get(0));
            BBCPreference.setLastUpdateTime(mContext, mCategory, System.currentTimeMillis());

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
        return Response.success(htmlString, HttpHeaderParser.parseCacheHeaders(response));
    }

    private static ContentValues getContentValues(Element content, String filter) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_TITLE,
                BBCHtmlUtility.getTitle(content));
        contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_TIME,
                BBCHtmlUtility.getTime(content));
        contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_DESCRIPTION,
                BBCHtmlUtility.getDescription(content));
        contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_HREF,
                BBCHtmlUtility.getArticleHref(content));
        contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP,
                BBCHtmlUtility.getTimeStamp(content));
        contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_THUMBNAIL_HREF,
                BBCHtmlUtility.getImageHref(content));
        contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY,
                filter);
        return contentValues;
    }
}
