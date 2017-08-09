package com.paranoid.mao.bbclearningenglish.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.paranoid.mao.bbclearningenglish.data.BBCContentContract;
import com.paranoid.mao.bbclearningenglish.utilities.BBCHtmlUtility;

import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;

/**
 * Created by Paranoid on 17/8/7.
 */

public class BBCArticleRequest extends StringRequest {

    private Context mContext;
    private Uri mUri;

    public BBCArticleRequest(int method, String url, Uri uri, Context context) {
        super(method, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        mContext = context;
        mUri = uri;

    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String articleHtml;
        try {
            articleHtml = new String(response.data, "UTF-8");
            Document document = BBCHtmlUtility.getArticleDocument(articleHtml);
            ContentValues contentValuesArticle = getContentValuesArticle(document);
            mContext.getContentResolver().update(mUri, contentValuesArticle, null, null);
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
        return Response.success(articleHtml, HttpHeaderParser.parseCacheHeaders(response));
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
