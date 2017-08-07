package com.example.mao.BBCLearningEnglish.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCContentContract {

    // URI String
    public static final String AUTHORITY = "com.example.mao.BBCLearningEnglish";
    public static final String PATH_BBC = "bbc";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class BBCLearningEnglishEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_BBC).build();

        public static final String TABLE_NAME = "bbc";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TIMESTAMP = "timeStamp";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_MP3_HREF = "mp3";
        public static final String COLUMN_HREF = "href";
        public static final String COLUMN_ARTICLE = "article";
        public static final String COLUMN_THUMBNAIL_HREF = "thumbnail";
        public static final String COLUMN_CATEGORY = "category";

        public static final String CATEGORY_6_MINUTE_ENGLISH = "6Minute";
        public static final String CATEGORY_THE_ENGLISH_WE_SPEAK = "weSpeak";
        public static final String CATEGORY_NEWS_REPORT = "newsReport";
        public static final String CATEGORY_ENGLISH_AT_WORK = "atWork";
        public static final String CATEGORY_ENGLISH_AT_UNIVERSITY = "atUniversity";
        public static final String CATEGORY_LINGO_HACK = "lingoHack";

        public static final String SORT_ORDER =
                BBCLearningEnglishEntry.COLUMN_TIMESTAMP + " DESC";

        public static String getMaxHistoryWhere(int maxHistory, String filter) {
            return COLUMN_TIMESTAMP
                    + " NOT IN "
                    + " (SELECT "
                    + COLUMN_TIMESTAMP
                    + " FROM "
                    + TABLE_NAME
                    + " WHERE "
                    + COLUMN_CATEGORY + " = '" + filter + "'"
                    + " ORDER BY "
                    + SORT_ORDER
                    + " LIMIT "
                    + maxHistory
                    + ") AND "
                    + COLUMN_CATEGORY + " = '" + filter + "'";
        }
    }
}
