package com.paranoid.mao.bbclearningenglish.data;

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
import java.util.List;

/**
 * Created by MAO on 7/18/2017.
 */

public class BBCContentProvider extends ContentProvider {

    // Match code for uri matcher
    private static final int BBC_CODE = 100;
    private static final int BBC_FILTER_CODE = 101;
    private static final int BBC_FILTER_TIMESTAMP_CODE = 102;
    private static final int BBC_FAVOURITE_CODE = 103;
    private static final int VOCABULARY_CODE = 104;
    private static final int VOCABULARY_ID_CODE = 105;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Helper for create database
    private BBCDbHelper mBBCDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_BBC, BBC_CODE);
        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.PATH_BBC + "/" + DatabaseContract.PATH_CATEGORY + "/*", BBC_FILTER_CODE);
        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.PATH_BBC + "/#/" + DatabaseContract.PATH_CATEGORY + "/*", BBC_FILTER_TIMESTAMP_CODE);
        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.PATH_BBC + "/" + DatabaseContract.PATH_FAVOURITE, BBC_FAVOURITE_CODE);
        uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_VOCABULARY, VOCABULARY_CODE);
        uriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.PATH_VOCABULARY + "/#", VOCABULARY_ID_CODE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mBBCDbHelper = new BBCDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase bbcDatabase = mBBCDbHelper.getReadableDatabase();
        int code = sUriMatcher.match(uri);
        Cursor cursor;
        String filter;
        String timeStamp;
        switch (code) {
            case BBC_CODE:
                cursor = bbcDatabase.query(DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BBC_FILTER_CODE:
                filter = uri.getLastPathSegment();
                selection = DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY + "=?";
                cursor = bbcDatabase.query(DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{filter},
                        null,
                        null,
                        sortOrder);
                break;
            case BBC_FILTER_TIMESTAMP_CODE:
                List<String> pathSegments = uri.getPathSegments();
                filter = pathSegments.get(3);
                timeStamp = pathSegments.get(1);
                selection = DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY + "=?"
                        + " AND "
                        + DatabaseContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP + "=?";
                cursor = bbcDatabase.query(DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{filter, timeStamp},
                        null,
                        null,
                        null);
                break;
            case BBC_FAVOURITE_CODE:
                selection = DatabaseContract.BBCLearningEnglishEntry.COLUMN_FAVOURITES + ">0";
                cursor = bbcDatabase.query(DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case VOCABULARY_CODE:
                cursor = bbcDatabase.query(DatabaseContract.VocabularyEntry.TABLE_NAME,
                        projection,
                        selection,
                        null,
                        null,
                        null,
                        sortOrder);
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
        SQLiteDatabase bbcDatabase = mBBCDbHelper.getWritableDatabase();
        int code = sUriMatcher.match(uri);
        long id = -1;
        Uri returnedUri = null;
        switch (code) {
            case BBC_CODE:
                id = bbcDatabase.insertWithOnConflict(DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnedUri = ContentUris.withAppendedId(
                            DatabaseContract.BBCLearningEnglishEntry.CONTENT_URI,
                            id);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case VOCABULARY_CODE:
                id = bbcDatabase.insert(DatabaseContract.VocabularyEntry.TABLE_NAME,
                        null,
                        values);
                if (id > 0) {
                    returnedUri = ContentUris.withAppendedId(
                            DatabaseContract.VocabularyEntry.CONTENT_URI,
                            id);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new SQLException("Insert failed!");
        }
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase bbcDatabase = mBBCDbHelper.getWritableDatabase();
        int n = -1;
        int code = sUriMatcher.match(uri);
        String filter;
        String timeStamp;
        switch (code) {
            case BBC_CODE:
                n = bbcDatabase.delete(DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case BBC_FILTER_TIMESTAMP_CODE:
                List<String> pathSegments = uri.getPathSegments();
                filter = pathSegments.get(3);
                timeStamp = pathSegments.get(1);
                selection = DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY + "=?"
                        + " AND "
                        + DatabaseContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP + "=?";
                n = bbcDatabase.delete(DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME,
                        selection,
                        new String[]{filter, timeStamp});
                break;
            case VOCABULARY_CODE:
                n = bbcDatabase.delete(DatabaseContract.VocabularyEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case VOCABULARY_ID_CODE:
                long id = ContentUris.parseId(uri);
                selection = DatabaseContract.VocabularyEntry._ID + "=?";
                n = bbcDatabase.delete(DatabaseContract.VocabularyEntry.TABLE_NAME,
                        selection,
                        new String[]{String.valueOf(id)});
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
        SQLiteDatabase bbcDatabase = mBBCDbHelper.getWritableDatabase();
        int n = -1;
        int code = sUriMatcher.match(uri);
        String filter;
        String timeStamp;
        switch (code) {
            case BBC_CODE:
                n = bbcDatabase.update(DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case BBC_FILTER_CODE:
                filter = uri.getLastPathSegment();
                selection = DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY + "=?";
                n = bbcDatabase.update(DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME,
                        values,
                        selection,
                        new String[]{filter});
                break;
            case BBC_FILTER_TIMESTAMP_CODE:
                List<String> pathSegments = uri.getPathSegments();
                filter = pathSegments.get(3);
                timeStamp = pathSegments.get(1);
                selection = DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY + "=?"
                        + " AND "
                        + DatabaseContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP + "=?";
                n = bbcDatabase.update(DatabaseContract.BBCLearningEnglishEntry.TABLE_NAME,
                        values,
                        selection,
                        new String[]{filter, timeStamp});
                break;
            default:
                throw new SQLException("Update failed!");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return n;
    }
}
