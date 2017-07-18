package com.example.mao.bbc6minuteenglish.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCContentDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bbc6minute.db";

    private static final int DATABASE_VERSION = 10;

    public BBCContentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "
                + BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME + "("
                + BBCContentContract.BBC6MinuteEnglishEntry._ID + " INTEGER PRIMARY KEY,"
                + BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME + " TEXT NOT NULL,"
                + BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL UNIQUE,"
                + BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL,"
                + BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_HREF + " TEXT NOT NULL,"


                // For test, not null disable
                // TODO: set type to not null
                + BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_MP3_HREF + " TEXT,"
                + BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_ARTICLE + " TEXT,"
                + BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_THUMBNAIL + " BLOB"
                + ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS "
                + BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME;
        db.execSQL(dropTable);
        onCreate(db);
    }
}
