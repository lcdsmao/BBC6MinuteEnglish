package com.example.mao.bbc6minuteenglish;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.sync.BBCSyncUtility;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, BBCContentAdapter.OnListItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    public static final String TAG = MainActivity.class.getName();

    private static final int MAX_NUMBER_OF_CONTENTS = 20;

    private static final int BBC_CONTENT_LOADER_ID = 1;

    private BBCContentAdapter mBBCContentAdapter;
    private RecyclerView mContentRecycleView;
    private SwipeRefreshLayout mSwipeContainer;

    // Projection for Showing data
    public static final String[] PROJECTION = {
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TITLE,
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME,
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_DESCRIPTION,
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP,
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_THUMBNAIL_HREF
    };

    public static final int TITLE_INDEX = 0;
    public static final int TIME_INDEX = 1;
    public static final int DESCRIPTION_INDEX = 2;
    public static final int TIMESTAMP_INDEX = 3;
    public static final int THUMBNAIL_INDEX = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_content_list);

        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.srl_content_container);
        mSwipeContainer.setOnRefreshListener(this);
        mSwipeContainer.setColorSchemeColors(ContextCompat.getColor(this, R.color.accent));

        /*Set the recycler view*/
        mContentRecycleView = (RecyclerView) findViewById(R.id.rv_content_list);

        mBBCContentAdapter = new BBCContentAdapter(this, this);
        mContentRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mContentRecycleView.setAdapter(mBBCContentAdapter);
        /*Set the recycler view complete*/

        getSupportLoaderManager().initLoader(BBC_CONTENT_LOADER_ID, null, this);
    }

    /* Set Loader */
    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(BBC_CONTENT_LOADER_ID, null, this);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickItem(long timeStamp) {
        Log.v(TAG, "Timestamp = " + timeStamp);
        Intent intent = new Intent(this, ArticleActivity.class);
        Uri uriWithTimeStamp = ContentUris.withAppendedId(
                BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                timeStamp);
        intent.setData(uriWithTimeStamp);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "On create loader");
        return new CursorLoader(
                this,
                BBCContentContract.BBC6MinuteEnglishEntry.CONTENT_URI,
                PROJECTION,
                null,
                null,
                BBCContentContract.BBC6MinuteEnglishEntry.SORT_ORDER
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "On load finish");
        mBBCContentAdapter.swapCursor(data);
        if (data != null && data.getCount() > 0) {
            mSwipeContainer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeContainer.setRefreshing(false);
                }
            }, 1000);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBBCContentAdapter.swapCursor(null);
    }

    @Override
    public void onRefresh() {
        BBCSyncUtility.contentListUpdate(this);
        getSupportLoaderManager().restartLoader(BBC_CONTENT_LOADER_ID, null, this);
    }
}
