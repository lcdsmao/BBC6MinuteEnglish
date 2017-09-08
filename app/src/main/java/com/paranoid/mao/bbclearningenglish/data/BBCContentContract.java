package com.paranoid.mao.bbclearningenglish.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCContentContract {

    // URI String
    public static final String AUTHORITY = "com.paranoid.mao.bbclearningenglish";
    public static final String PATH_BBC = "bbc";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_FAVOURITE = "favourite";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class BBCLearningEnglishEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_BBC).build();

        public static final Uri CONTENT_CATEGORY_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendEncodedPath(PATH_BBC)
                        .appendEncodedPath(PATH_CATEGORY).build();

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
        public static final String COLUMN_FAVOURITES = "favourites";

        public static final String SORT_ORDER =
                BBCLearningEnglishEntry.COLUMN_TIMESTAMP + " DESC";

    }
}
