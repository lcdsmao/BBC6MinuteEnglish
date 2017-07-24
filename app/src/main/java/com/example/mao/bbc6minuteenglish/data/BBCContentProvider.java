package com.example.mao.bbc6minuteenglish.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by MAO on 7/18/2017.
 */

public class BBCContentProvider extends ContentProvider {

    // Match code for uri matcher
    private static final int CONTENT_CODE = 100;
    private static final int CONTENT_WITH_TIMESTAMP_CODE = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Helper for create database
    private BBCContentDbHelper mDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(BBCContentContract.AUTHORITY, BBCContentContract.PATH_BBC, CONTENT_CODE);
        uriMatcher.addURI(BBCContentContract.AUTHORITY,
                BBCContentContract.PATH_BBC + "/#", CONTENT_WITH_TIMESTAMP_CODE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new BBCContentDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        int code = sUriMatcher.match(uri);
        Cursor cursor;
        switch (code) {
            case CONTENT_CODE :
                cursor = sqLiteDatabase.query(BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CONTENT_WITH_TIMESTAMP_CODE:
                long timeStamp = ContentUris.parseId(uri);
                cursor = sqLiteDatabase.query(BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME,
                        projection,
                        BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP + "=?",
                        new String[]{String.valueOf(timeStamp)},
                        null,
                        null,
                        null);
                break;
            default:
                throw new SQLException("Query failed!");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        int code = sUriMatcher.match(uri);
        long id = -1;
        Uri returnedUri;
        switch (code) {
            case CONTENT_CODE:
                id = sqLiteDatabase.insert(BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME,
                        null,
                        values);
                if (id > 0) {
                    returnedUri = ContentUris.withAppendedId(
                            BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                            id);
                } else {
                    throw new SQLException("Insert failed!");
                }
                break;
            default:
                throw new SQLException("Insert failed!");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnedUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        int code = sUriMatcher.match(uri);
        int n = 0;
        if (values.length == 0) {
            return n;
        }
        switch (code) {
            case CONTENT_CODE :
                for (ContentValues contentValues : values) {
                    long id = sqLiteDatabase.insert(BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME,
                            null,
                            contentValues);
                    if (id > 0) {
                       n++;
                    }
                }
                break;
            default:
                throw new SQLException("Bulk insert failed");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return n;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        int n = -1;
        int code = sUriMatcher.match(uri);
        switch (code) {
            case CONTENT_CODE :
                n = sqLiteDatabase.delete(BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CONTENT_WITH_TIMESTAMP_CODE:
                long timeStamp = ContentUris.parseId(uri);
                n = sqLiteDatabase.delete(BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME,
                        BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP + "=?",
                        new String[]{String.valueOf(timeStamp)});
                break;
            default:
                throw new SQLException("Delete failed!");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return n;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        int n = -1;
        int code = sUriMatcher.match(uri);
        switch (code) {
            case CONTENT_CODE :
                n = sqLiteDatabase.update(BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CONTENT_WITH_TIMESTAMP_CODE:
                long timeStamp = ContentUris.parseId(uri);
                n = sqLiteDatabase.update(BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME,
                        values,
                        BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP + "=?",
                        new String[]{String.valueOf(timeStamp)});
                break;
            default:
                throw new SQLException("Update failed!");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return n;
    }
}
