package com.paranoid.mao.bbclearningenglish.article;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.paranoid.mao.bbclearningenglish.singleton.MyApp;
import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.sync.SyncUtility;
import com.paranoid.mao.bbclearningenglish.utilities.TimeUtility;
import com.paranoid.mao.bbclearningenglish.utilities.BBCHtmlUtility;

public class ArticleActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener,
        SeekBar.OnSeekBarChangeListener{

    private static final int ARTICLE_LOADER_ID = 123;

    private static final String SERVICE_STATE_KEY = "service_state";

    private static final String[] PROJECTION = {
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_TITLE,
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_ARTICLE,
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_MP3_HREF,
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_FAVOURITES
    };

    private static final int TITLE_INDEX = 0;
    private static final int ARTICLE_INDEX = 1;
    private static final int AUDIO_HREF_INDEX = 2;
    private static final int FAVOURITES_INDEX = 3;

    private final static int REFRESH_TIME_INTERVAL = 500;

    private AudioPlayService mAudioService;
    private boolean mBond = false;

    private Uri mUriWithTimeStamp;
    private boolean mIsFavorite = false;
    private boolean mIsFavoriteChanged = false;

    private ArticlePagerAdapter mArticleAdapter;

    private ProgressBar mArticleLoading;
    private ImageView mPlayButton;
    private SeekBar mAudioSeekBar;
    private ProgressBar mAudioLoading;
    private ViewPager mArticleViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private TextView mCurrentTimeText;
    private TextView mDurationTimeText;
    private ImageView mForwardButton;
    private ImageView mReplayButton;

    private Handler mPlayerHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateAudioLoadingUI();
            updateSeekBarUI();
            updatePlayerControlButtonUI();
            mPlayerHandler.postDelayed(this, REFRESH_TIME_INTERVAL);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        // Bind bew by id
        viewBind();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mArticleAdapter = new ArticlePagerAdapter(getSupportFragmentManager());

        // Show progress bar and hide article
        showArticleLoading();

        // Check database if the article is null or not
        mUriWithTimeStamp = getIntent().getData();

        getSupportLoaderManager().initLoader(ARTICLE_LOADER_ID, null, this);
    }

    private void viewBind() {
        mArticleLoading = (ProgressBar) findViewById(R.id.pb_article_load);
        mPlayButton = (ImageView) findViewById(R.id.iv_play_pause);
        mPlayButton.setOnClickListener(this);
        mAudioSeekBar = (SeekBar) findViewById(R.id.sb_play_bar);
        mAudioSeekBar.setOnSeekBarChangeListener(this);
        mAudioSeekBar.setEnabled(false);
        mAudioLoading = (ProgressBar) findViewById(R.id.pb_audio_load);
        mTabLayout = (TabLayout) findViewById(R.id.tabbar);
        mArticleViewPager = (ViewPager) findViewById(R.id.view_pager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mForwardButton = (ImageView) findViewById(R.id.iv_forward);
        mForwardButton.setOnClickListener(this);
        mReplayButton = (ImageView) findViewById(R.id.iv_replay);
        mReplayButton.setOnClickListener(this);
        mCurrentTimeText = (TextView) findViewById(R.id.tv_current);
        mDurationTimeText = (TextView) findViewById(R.id.tv_duration);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_favourite){
            mIsFavoriteChanged = true;
            mIsFavorite = !item.isChecked();
            item.setChecked(mIsFavorite);
            item.setIcon(mIsFavorite ? R.drawable.ic_favorite : R.drawable.ic_not_favorite);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SyncUtility.articleInitialize(this, mUriWithTimeStamp);
        return new CursorLoader(
                this,
                mUriWithTimeStamp,
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()) return;

        String article = data.getString(ARTICLE_INDEX);
        String title = data.getString(TITLE_INDEX);
        String audioHref = data.getString(AUDIO_HREF_INDEX);
        mIsFavorite = data.getLong(FAVOURITES_INDEX) > 0;

        getSupportActionBar().setTitle(title);

        if (!TextUtils.isEmpty(article)) {
            mArticleAdapter.setArticleSections(BBCHtmlUtility.getArticleSection(this, article));
            mArticleViewPager.setAdapter(mArticleAdapter);
            mTabLayout.setupWithViewPager(mArticleViewPager);
            showArticle();
        }

        if (!TextUtils.isEmpty(audioHref)) {
            prepareAudioService(audioHref);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showArticleLoading() {
        mArticleViewPager.setVisibility(View.INVISIBLE);
        mArticleLoading.setVisibility(View.VISIBLE);
    }

    private void showArticle() {
        mArticleLoading.setVisibility(View.INVISIBLE);
        mArticleViewPager.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SERVICE_STATE_KEY, mBond);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBond = savedInstanceState.getBoolean(SERVICE_STATE_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayerHandler.postDelayed(mRunnable, REFRESH_TIME_INTERVAL);
        MyApp.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerHandler.removeCallbacks(mRunnable);
        MyApp.activityPaused();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mIsFavoriteChanged) return;
        long favouriteTime = mIsFavorite ? System.currentTimeMillis() : 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(
                DatabaseContract.BBCLearningEnglishEntry.COLUMN_FAVOURITES, favouriteTime);
        getContentResolver().update(
                mUriWithTimeStamp,
                contentValues,
                null,
                null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBond) {
            unbindService(mConnection);
            mAudioService.stopSelf();
        }
    }

    private void prepareAudioService(String audioHref) {
        //Check is service is active
        if (!mBond) {
            Intent playerIntent = new Intent(this, AudioPlayService.class)
                    .setData(mUriWithTimeStamp)
                    .setAction(AudioPlayService.ACTION_INITIALIZE)
                    .putExtra(DatabaseContract.BBCLearningEnglishEntry.COLUMN_MP3_HREF, audioHref);
            startService(playerIntent);
            bindService(playerIntent, mConnection, BIND_AUTO_CREATE);
        }
    }

    private void updateSeekBarUI(){
        if (mBond && mAudioService.isPrepared()) {
            mAudioSeekBar.setEnabled(true);
            mAudioSeekBar.setMax(mAudioService.getDuration());
            mAudioSeekBar.setSecondaryProgress(mAudioService.getCachedProgress());
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                mAudioSeekBar.setProgress(mAudioService.getCurrentPosition(), false);
            } else {
                mAudioSeekBar.setProgress(mAudioService.getCurrentPosition());
            }
        } else {
            mAudioSeekBar.setEnabled(false);
        }
    }

    private void updatePlayerControlButtonUI() {
        if (mBond && mAudioService.isPrepared()) {
            mPlayButton.setEnabled(true);
            mReplayButton.setEnabled(true);
            mForwardButton.setEnabled(true);
            if (mAudioService.isPlaying()) {
                mPlayButton.setImageResource(R.drawable.ic_pause);
            } else {
                mPlayButton.setImageResource(R.drawable.ic_play_arrow);
            }
        } else {
            mPlayButton.setEnabled(false);
            mReplayButton.setEnabled(false);
            mForwardButton.setEnabled(false);
        }
    }

    private void updateAudioLoadingUI() {
        if (mBond && mAudioService.isPrepared()) {
            if (mAudioService.getCurrentPosition() - mAudioService.getCachedProgress() < 1000) {
                mAudioLoading.setVisibility(View.INVISIBLE);
                mPlayButton.setVisibility(View.VISIBLE);
            } else {
                mAudioLoading.setVisibility(View.VISIBLE);
                mPlayButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AudioPlayService.LocalBinder binder = (AudioPlayService.LocalBinder) service;
            mAudioService = binder.getService();
            mBond = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBond = false;
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int newPosition;
        switch (id) {
            case R.id.iv_play_pause:
                mAudioService.controlPlayStatus();
                break;
            case R.id.iv_forward:
                // 5 seconds
                newPosition = mAudioService.getCurrentPosition() + 5000;
                mAudioService.controlSeekPosition(newPosition);
                break;
            case R.id.iv_replay:
                // 5 seconds
                newPosition = mAudioService.getCurrentPosition() - 5000;
                mAudioService.controlSeekPosition(newPosition);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Here to update the time of audio
        if (mBond) {
            mCurrentTimeText.setText(TimeUtility.getDisplayTime(progress));
            mDurationTimeText.setText(TimeUtility.getDisplayTime(mAudioService.getDuration()));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mPlayerHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mBond) {
            mAudioService.controlSeekPosition(seekBar.getProgress());
        }
        mPlayerHandler.postDelayed(mRunnable, REFRESH_TIME_INTERVAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_menu, menu);
        MenuItem favouriteMenu = menu.findItem(R.id.menu_favourite);
        favouriteMenu.setChecked(mIsFavorite);
        favouriteMenu.setIcon(mIsFavorite ? R.drawable.ic_favorite : R.drawable.ic_not_favorite);
        return true;
    }
}
