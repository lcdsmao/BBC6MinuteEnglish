package com.example.mao.BBCLearningEnglish.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCContentDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bbcLearningEnglish.db";

    private static final int DATABASE_VERSION = 17;

    public BBCContentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "
                + BBCContentContract.BBCLearningEnglishEntry.TABLE_NAME + "("
                + BBCContentContract.BBCLearningEnglishEntry._ID + " INTEGER PRIMARY KEY,"
                + BBCContentContract.BBCLearningEnglishEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + BBCContentContract.BBCLearningEnglishEntry.COLUMN_TIME + " TEXT NOT NULL,"
                + BBCContentContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL,"
                + BBCContentContract.BBCLearningEnglishEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL,"
                + BBCContentContract.BBCLearningEnglishEntry.COLUMN_HREF + " TEXT NOT NULL,"
                + BBCContentContract.BBCLearningEnglishEntry.COLUMN_MP3_HREF + " TEXT,"
                + BBCContentContract.BBCLearningEnglishEntry.COLUMN_ARTICLE + " TEXT,"
                + BBCContentContract.BBCLearningEnglishEntry.COLUMN_THUMBNAIL_HREF + " TEXT NOT NULL,"
                + BBCContentContract.BBCLearningEnglishEntry.COLUMN_CATEGORY + " TEXT NOT NULL,"
                + " UNIQUE (" + BBCContentContract.BBCLearningEnglishEntry.COLUMN_CATEGORY
                + ", "
                + BBCContentContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP
                + " ) ON CONFLICT IGNORE);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS "
                + BBCContentContract.BBCLearningEnglishEntry.TABLE_NAME;
        db.execSQL(dropTable);
        onCreate(db);
    }
}