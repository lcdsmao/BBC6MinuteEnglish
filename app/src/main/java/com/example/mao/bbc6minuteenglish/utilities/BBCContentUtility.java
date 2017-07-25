package com.example.mao.bbc6minuteenglish.utilities;

import android.content.ContentValues;
import android.graphics.Bitmap;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by MAO on 7/25/2017.
 */

public class BBCContentUtility {

    public static ContentValues getContentValues(Element content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TITLE,
                BBCHtmlUtility.getTitle(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME,
                BBCHtmlUtility.getTime(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_DESCRIPTION,
                BBCHtmlUtility.getDescription(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_HREF,
                BBCHtmlUtility.getArticleHref(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP,
                BBCHtmlUtility.getTimeStamp(content));
        String imgHref = BBCHtmlUtility.getImageHref(content);
        Bitmap bitmap = DbBitmapUtility.getBitmapFromURL(imgHref);
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_THUMBNAIL,
                DbBitmapUtility.getBytes(bitmap));
        return contentValues;
    }

    public static ContentValues getContentValuesArticle(Document document){
        String article = BBCHtmlUtility.getArticleHtml(document);
        ContentValues contentValues = new ContentValues();
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_ARTICLE,
                article);
        return contentValues;
    }

}
