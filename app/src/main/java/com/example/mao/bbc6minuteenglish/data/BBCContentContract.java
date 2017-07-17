package com.example.mao.bbc6minuteenglish.data;

import android.provider.BaseColumns;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCContentContract {

    public static class BBC6MinuteEnglishEntry implements BaseColumns{

        public static final String TABLE_NAME = "bbc_6_minute_english";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_MP3_HREF = "mp3";
        public static final String COLUMN_HREF = "href";
        public static final String COLUMN_ARTICLE = "article";
        public static final String COLUMN_THUMBNAIL = "thumbnail";

    }
}
