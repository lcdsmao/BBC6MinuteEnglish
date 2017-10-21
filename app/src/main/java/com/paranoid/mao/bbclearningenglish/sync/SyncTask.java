package com.paranoid.mao.bbclearningenglish.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.paranoid.mao.bbclearningenglish.data.BBCPreference;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.data.VocabularyDefinition;
import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.utilities.BBCHtmlUtility;
import com.paranoid.mao.bbclearningenglish.utilities.NotificationUtility;
import com.paranoid.mao.bbclearningenglish.utilities.WordReferenceUtility;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by MAO on 7/24/2017.
 */

public class SyncTask {


    synchronized static void syncArticle (Context context,
                                                @NonNull Uri uriWithTimeStamp,
                                                @NonNull String articleHref) {

        try {
            String response = request(articleHref);
            if (response == null) return;
            Document document = BBCHtmlUtility.getArticleDocument(response);
            String article = BBCHtmlUtility.getArticleHtml(document);
            String audioHref = BBCHtmlUtility.getMp3Href(document);

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_ARTICLE,
                    article);
            contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_MP3_HREF,
                    audioHref);

            context.getContentResolver().update(uriWithTimeStamp,
                    contentValues,
                    null,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    synchronized static void syncCategoryList(Context context, String category) {
        String url = BBCCategory.getCategoryUrl(category);
        if (url == null) return;
        int MAX_NUM = 200;
        try {
            String response = request(url);
            if (response == null) return;
            Elements contentList = BBCHtmlUtility.getContentsList(response);
            if (contentList == null) return;

            int max = Math.min(MAX_NUM, contentList.size());
            ContentResolver contentResolver = context.getContentResolver();
            for (int i = 0; i < max; i++) {
                Element content = contentList.get(i);
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
                        category);
                contentResolver.insert(
                        DatabaseContract.BBCLearningEnglishEntry.CONTENT_URI,
                        contentValues);
            }

            long newestContentTime = BBCHtmlUtility.getTimeStamp(contentList.get(0));
            Log.v("time", "" + newestContentTime + "" + BBCPreference.getLastUpdateTime(context, category));
            boolean isNew = newestContentTime > BBCPreference.getLastUpdateTime(context, category);
            if (isNew) {
                NotificationUtility.showNewContentNotification(context,
                        category,
                        BBCHtmlUtility.getTitle(contentList.get(0)));
            }
            BBCPreference.setLastUpdateTime(context, category, System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SyncUtility.sIsContentListSyncComplete = true;
        }

    }

    synchronized static void syncVocab(Context context,
                                              Uri uriWithID,
                                              String word) {

        String url = WordReferenceUtility.getWordUrl(word);
        try {
            String response = request(url);
            VocabularyDefinition vocab = WordReferenceUtility.getVocab(response, word);

            String symbol = vocab.getSymbol();
            String definition = vocab.getDefinition();
            String audioHref = vocab.getAudioHref();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_VOCAB, word);
            contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_MEAN, definition);
            contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_SYMBOL, symbol);
            contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_AUDIO_HREF, audioHref);
            context.getContentResolver().update(uriWithID, contentValues, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String request(String url) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String responseBody;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException();
            responseBody = response.body().string();
        }
        return responseBody;
    }
}
