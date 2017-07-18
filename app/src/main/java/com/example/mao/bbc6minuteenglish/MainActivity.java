package com.example.mao.bbc6minuteenglish;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.utilities.BBCHtmlUtility;
import com.example.mao.bbc6minuteenglish.utilities.DbBitmapUtility;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MainActivity.class.getName();

    private static final int MAX_NUMBER_OF_CONTENTS = 20;

    private static final int BBC_CONTENT_LOADER_ID = 1;
    private static final int BBC_CONTENT_UPDATE_LOADER_ID = 2;

    private BBCContentAdapter mBBCContentAdapter;
    private RecyclerView mContentRecycleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_content_list);

        /*Set the recycler view*/
        mContentRecycleView = (RecyclerView) findViewById(R.id.rv_content_list);

        mBBCContentAdapter = new BBCContentAdapter(this);
        mContentRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mContentRecycleView.setAdapter(mBBCContentAdapter);
        /*Set the recycler view complete*/

        getLoaderManager().initLoader(BBC_CONTENT_LOADER_ID, null, this);
    }

    /* Set Loader */
    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(BBC_CONTENT_LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "On create loader");
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mBBCData;

            final String[] projections = {
                    BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TITLE,
                    BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME,
                    BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_DESCRIPTION,
                    BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_THUMBNAIL
            };

            @Override
            protected void onStartLoading() {
                if (mBBCData != null) {
                    deliverResult(mBBCData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                Cursor cursor = getContentResolver().query(
                        BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                        projections,
                        null,
                        null,
                        BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME + " DESC");

                if (getId() == BBC_CONTENT_UPDATE_LOADER_ID || cursor.getCount() == 0) {
                    updateDatabase();
                    cursor = getContentResolver().query(
                            BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                            projections,
                            null,
                            null,
                            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME + " DESC");
                }
                return cursor;
            }

            @Override
            public void deliverResult(Cursor data) {
                mBBCData = data;
                super.deliverResult(mBBCData);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "On load finish");
        mBBCContentAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBBCContentAdapter.swapCursor(null);
    }
    /* Set Loader complete*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            Log.v(TAG, "Refresh");
            getLoaderManager().restartLoader(BBC_CONTENT_UPDATE_LOADER_ID, null, this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Need to modify
    private void updateDatabase(){
        Log.v(TAG, "Update");
        Elements contentList = BBCHtmlUtility.getContentsList();

        for (int i = 0; i < MAX_NUMBER_OF_CONTENTS; i++) {
            try {
                Element content = contentList.get(i);
                ContentValues newValues = setContentValues(content);
                Cursor cursor = getContentResolver().query(
                        BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                        null,
                        BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP + " = "
                                + BBCHtmlUtility.getTimeStamp(content),
                        null, null);
                if (cursor.getCount() > 0) {
                    return;
                }
                getContentResolver().insert(BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                        newValues);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
//        Cursor queryCursor = getContentResolver().query(
//                BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
//                null, null, null, null);
//        int sub = queryCursor.getCount() - MAX_NUMBER_OF_CONTENTS;
//        if (sub > 0) {
//            getContentResolver().delete(
//                    BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
//                    BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME ""
//            )
//        }
    }

    private ContentValues setContentValues(Element content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TITLE,
                BBCHtmlUtility.getTitle(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME,
                BBCHtmlUtility.getTime(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_DESCRIPTION,
                BBCHtmlUtility.getDescription(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_HREF,
                BBCHtmlUtility.getArticleHref(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP,
                BBCHtmlUtility.getTimeStamp(content));
        String imgHref = BBCHtmlUtility.getImageHref(content);
        Bitmap bitmap = DbBitmapUtility.getBitmapFromURL(imgHref);
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_THUMBNAIL,
                DbBitmapUtility.getBytes(bitmap));
        return contentValues;
    }



}
