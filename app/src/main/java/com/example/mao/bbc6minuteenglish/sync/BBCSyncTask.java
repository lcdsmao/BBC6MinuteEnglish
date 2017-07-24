package com.example.mao.bbc6minuteenglish.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.utilities.BBCHtmlUtility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncTask {

    synchronized public static void syncArticle(Context context,
                                                @NonNull Uri uriWithTimeStamp,
                                                @NonNull String articleHref) {

        Document document = null;
        try {
            document = Jsoup.connect(articleHref).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (document == null) return;
        Log.v(uriWithTimeStamp.toString(), "Document not null");
        String article = BBCHtmlUtility.getArticleHtml(document);
        ContentValues contentValues = new ContentValues();
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_ARTICLE,
                article);
        context.getContentResolver().update(uriWithTimeStamp, contentValues, null, null);
    }
}
