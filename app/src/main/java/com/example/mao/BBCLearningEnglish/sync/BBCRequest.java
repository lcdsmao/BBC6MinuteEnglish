package com.example.mao.BBCLearningEnglish.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.mao.BBCLearningEnglish.data.BBCContentContract;
import com.example.mao.BBCLearningEnglish.data.BBCPreference;
import com.example.mao.BBCLearningEnglish.utilities.BBCHtmlUtility;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;

/**
 * Created by Paranoid on 17/8/7.
 */

public class BBCRequest extends StringRequest {

    private static final String TAG = BBCRequest.class.getSimpleName();

    private String mCategory;
    private Context mContext;

    public BBCRequest(int method, String url, Response.Listener<String> listener,
                      Response.ErrorListener errorListener, Context context) {
        super(method, url, listener, errorListener);
        mCategory = BBCHtmlUtility.sCategoryMap.get(url);
        mContext = context;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String htmlString =
                    new String(response.data, "UTF-8");
            Elements contentList = BBCHtmlUtility.getContentsList(htmlString);
            if (contentList == null) return super.parseNetworkResponse(response);
            int max = BBCPreference.getPreferenceMaxHistory(mContext);
            int maxHistory = Math.min(max, contentList.size());

            ContentResolver contentResolver = mContext.getContentResolver();
            for (int i = 0; i < maxHistory; i++) {
                Element content = contentList.get(i);
                ContentValues contentValues = getContentValues(content, mCategory);
                Log.v(TAG, mCategory);
                contentResolver.insert(
                        BBCContentContract.BBCLearningEnglishEntry.CONTENT_URI,
                        contentValues);
            }

            contentResolver.delete(BBCContentContract.BBCLearningEnglishEntry.CONTENT_URI,
                    BBCContentContract.BBCLearningEnglishEntry.getMaxHistoryWhere(maxHistory, mCategory),
                    null);
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
        return super.parseNetworkResponse(response);
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
}
