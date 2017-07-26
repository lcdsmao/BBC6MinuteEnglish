package com.example.mao.bbc6minuteenglish.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCContentContract {

    // URI String
    public static final String AUTHORITY = "com.example.mao.bbc6minuteenglish";
    public static final String PATH_BBC = "bbc";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class BBC6MinuteEnglishEntry implements BaseColumns{

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

        public static final String SORT_ORDER =
                BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP + " DESC";
    }
}
