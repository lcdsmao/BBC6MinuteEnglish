package com.paranoid.mao.bbclearningenglish.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Paranoid on 17/9/9.
 */

public class VocabularyDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vocabulary.db";

    private static final int DATABASE_VERSION = 1;

    public VocabularyDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE "
                + DatabaseContract.VocabularyEntry.TABLE_NAME + " ("
                + DatabaseContract.VocabularyEntry._ID + " INTEGER PRIMARY KEY,"
                + DatabaseContract.VocabularyEntry.COLUMN_VOCAB + " TEXT NOT NULL,"
                + DatabaseContract.VocabularyEntry.COLUMN_MEAN + " TEXT,"
                + DatabaseContract.VocabularyEntry.COLUMN_SYMBOL + " TEXT,"
                + DatabaseContract.VocabularyEntry.COLUMN_AUDIO_HREF + " TEXT"
                //+ DatabaseContract.VocabularyEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL"
                + ");";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropTable = "DROP TABLE IF EXISTS "
                + DatabaseContract.VocabularyEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(dropTable);
        onCreate(sqLiteDatabase);
    }
}
