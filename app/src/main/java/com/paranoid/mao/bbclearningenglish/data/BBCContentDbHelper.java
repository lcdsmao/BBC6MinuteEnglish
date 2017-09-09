package com.paranoid.mao.bbclearningenglish.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCContentDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bbcLearningEnglish.db";

    private static final int DATABASE_VERSION = 21;

    public BBCContentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "
                + DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME + "("
                + DatabaseContract.BBCLearningEnglishEntry._ID + " INTEGER PRIMARY KEY,"
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_TIME + " TEXT NOT NULL,"
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL,"
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL,"
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_HREF + " TEXT NOT NULL,"
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_MP3_HREF + " TEXT,"
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_ARTICLE + " TEXT,"
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_THUMBNAIL_HREF + " TEXT NOT NULL,"
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY + " TEXT NOT NULL,"
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_FAVOURITES + " INTEGER,"
                + " UNIQUE (" + DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY
                + ", "
                + DatabaseContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP
                + " ) ON CONFLICT IGNORE);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS "
                + DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME;
        db.execSQL(dropTable);
        onCreate(db);
    }
}
