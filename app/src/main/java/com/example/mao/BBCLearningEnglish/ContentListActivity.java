package com.example.mao.BBCLearningEnglish;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mao.BBCLearningEnglish.data.BBCArticleSection;
import com.example.mao.BBCLearningEnglish.data.BBCContentContract;
import com.example.mao.BBCLearningEnglish.data.BBCPreference;
import com.example.mao.BBCLearningEnglish.sync.BBCSyncUtility;
import com.example.mao.BBCLearningEnglish.sync.JobDispatcher;

public class ContentListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, BBCContentAdapter.OnListItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, SharedPreferences.OnSharedPreferenceChangeListener,
        NavigationView.OnNavigationItemSelectedListener{

    public static final String TAG = ContentListActivity.class.getSimpleName();

    private static final int BBC_CONTENT_LOADER_ID = 1;

    private static final String TITLE_STATE_KEY = "title";

    private BBCContentAdapter mBBCContentAdapter;
    private SwipeRefreshLayout mSwipeContainer;

    // Projection for Showing data
    public static final String[] PROJECTION = {
            BBCContentContract.BBCLearningEnglishEntry.COLUMN_TITLE,
            BBCContentContract.BBCLearningEnglishEntry.COLUMN_TIME,
            BBCContentContract.BBCLearningEnglishEntry.COLUMN_DESCRIPTION,
            BBCContentContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP,
            BBCContentContract.BBCLearningEnglishEntry.COLUMN_THUMBNAIL_HREF,
            BBCContentContract.BBCLearningEnglishEntry.COLUMN_CATEGORY
    };

    public static final int TITLE_INDEX = 0;
    public static final int TIME_INDEX = 1;
    public static final int DESCRIPTION_INDEX = 2;
    public static final int TIMESTAMP_INDEX = 3;
    public static final int THUMBNAIL_INDEX = 4;
    public static final int CATEGORY_INDEX = 5;

    private String mCurrentCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.category_6_minute_english);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        mCurrentCategory = BBCContentContract.BBCLearningEnglishEntry.CATEGORY_6_MINUTE_ENGLISH;

        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.srl_content_container);
        mSwipeContainer.setOnRefreshListener(this);
        mSwipeContainer.setColorSchemeColors(ContextCompat.getColor(this, R.color.accent));

        /*Set the recycler view*/
        mBBCContentAdapter = new BBCContentAdapter(this, this);
        final RecyclerView contentRecycleView = (RecyclerView) findViewById(R.id.rv_content_list);
        contentRecycleView.setLayoutManager(new LinearLayoutManager(this));
        contentRecycleView.setAdapter(mBBCContentAdapter);
        /*Set the recycler view complete*/

        Log.v(TAG, "On create");
        getSupportLoaderManager().initLoader(BBC_CONTENT_LOADER_ID, new Bundle(), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "On start");
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "On resume");
        if (!BBCSyncUtility.sIsContentListSyncComplete) {
            mSwipeContainer.setRefreshing(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "On stop");
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_refresh:
                mSwipeContainer.setRefreshing(true);
                BBCSyncUtility.contentListSync(this, mCurrentCategory);
                return true;
            case R.id.menu_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClickItem(String path) {
        Log.v(TAG, "Path = " + path);
        Intent intent = new Intent(this, ArticleActivity.class);
        Uri uri = BBCContentContract.BBCLearningEnglishEntry.CONTENT_URI
                .buildUpon()
                .appendEncodedPath(path)
                .build();
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "On create loader");
        mSwipeContainer.setRefreshing(true);
        Uri uri = BBCContentContract.BBCLearningEnglishEntry.CONTENT_CATEGORY_URI.buildUpon()
                .appendPath(mCurrentCategory).build();
        if (BBCPreference.isUpdateNeed(this, mCurrentCategory)) {
            BBCSyncUtility.contentListSync(this, mCurrentCategory);
        }
        return new CursorLoader(
                this,
                uri,
                PROJECTION,
                null,
                null,
                BBCContentContract.BBCLearningEnglishEntry.SORT_ORDER
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "On load finish");
        mBBCContentAdapter.swapCursor(data);
        if (BBCSyncUtility.sIsContentListSyncComplete) {
            mSwipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBBCContentAdapter.swapCursor(null);
    }

    @Override
    public void onRefresh() {
        BBCSyncUtility.contentListSync(this, mCurrentCategory);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // For future use
        Log.v(TAG, key);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        ActionBar actionBar = getSupportActionBar();
        switch (id) {
            case R.id.category_six:
                actionBar.setTitle(R.string.category_6_minute_english);
                mCurrentCategory = BBCContentContract.BBCLearningEnglishEntry.CATEGORY_6_MINUTE_ENGLISH;
                getSupportLoaderManager().restartLoader(BBC_CONTENT_LOADER_ID, null, this);
                break;
            case R.id.category_we_speak:
                actionBar.setTitle(R.string.category_the_english_we_speak);
                mCurrentCategory = BBCContentContract.BBCLearningEnglishEntry.CATEGORY_THE_ENGLISH_WE_SPEAK;
                getSupportLoaderManager().restartLoader(BBC_CONTENT_LOADER_ID, null, this);
                break;
            case R.id.category_news_report:
                actionBar.setTitle(R.string.category_news_report);
                mCurrentCategory = BBCContentContract.BBCLearningEnglishEntry.CATEGORY_NEWS_REPORT;
                getSupportLoaderManager().restartLoader(BBC_CONTENT_LOADER_ID, null, this);
                break;
            case R.id.category_lingo_hack:
                actionBar.setTitle(R.string.category_lingo_hack);
                mCurrentCategory = BBCContentContract.BBCLearningEnglishEntry.CATEGORY_LINGO_HACK;
                getSupportLoaderManager().restartLoader(BBC_CONTENT_LOADER_ID, null, this);
                break;
            case R.id.category_university:
                actionBar.setTitle(R.string.category_english_at_university);
                mCurrentCategory = BBCContentContract.BBCLearningEnglishEntry.CATEGORY_ENGLISH_AT_UNIVERSITY;
                getSupportLoaderManager().restartLoader(BBC_CONTENT_LOADER_ID, null, this);
                break;
            case R.id.drawer_rating:
                break;
            case R.id.drawer_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TITLE_STATE_KEY, getSupportActionBar().getTitle().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getSupportActionBar().setTitle(savedInstanceState.getString(TITLE_STATE_KEY));
    }

}
