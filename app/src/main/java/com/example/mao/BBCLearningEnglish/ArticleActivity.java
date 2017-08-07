package com.example.mao.BBCLearningEnglish;

import android.content.ComponentName;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mao.BBCLearningEnglish.data.BBCContentContract;
import com.example.mao.BBCLearningEnglish.sync.BBCSyncUtility;
import com.example.mao.BBCLearningEnglish.utilities.TimeUtility;
import com.example.mao.BBCLearningEnglish.utilities.BBCHtmlUtility;

public class ArticleActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener,
        SeekBar.OnSeekBarChangeListener{

    private static final String TAG = ArticleActivity.class.getName();

    private static final int ARTICLE_LOADER_ID = 123;

    private static final String SERVICE_STATE_KEY = "service_state";

    private static final String[] PROJECTION = {
            BBCContentContract.BBCLearningEnglishEntry.COLUMN_TITLE,
            BBCContentContract.BBCLearningEnglishEntry.COLUMN_ARTICLE,
            BBCContentContract.BBCLearningEnglishEntry.COLUMN_MP3_HREF,
            BBCContentContract.BBCLearningEnglishEntry.COLUMN_CATEGORY
    };

    private static final int TITLE_INDEX = 0;
    private static final int ARTICLE_INDEX = 1;
    private static final int AUDIO_HREF_INDEX = 2;
    private static final int CATEGORY_INDEX = 3;

    private final static int REFRESH_TIME_INTERVAL = 500;

    private AudioPlayService mAudioService;
    private boolean mBond = false;

    private Uri mUriWithTimeStamp;

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

    private Handler mPlayerHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBond) {
                showAudioLoading();
                updateSeekBar();
                updatePlayButton();
            }
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
        BBCSyncUtility.articleInitialize(this, mUriWithTimeStamp);

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
        ImageView ForwardButton = (ImageView) findViewById(R.id.iv_forward);
        ForwardButton.setOnClickListener(this);
        ImageView ReplayButton = (ImageView) findViewById(R.id.iv_replay);
        ReplayButton.setOnClickListener(this);
        mCurrentTimeText = (TextView) findViewById(R.id.tv_current);
        mDurationTimeText = (TextView) findViewById(R.id.tv_duration);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uriWithTimeStamp = getIntent().getData();
        Log.v(TAG, "Create Loader");
        return new CursorLoader(
                this,
                uriWithTimeStamp,
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "Load finished");

        if(!data.moveToFirst()) return;

        String article = data.getString(ARTICLE_INDEX);
        String title = data.getString(TITLE_INDEX);
        String audioHref = data.getString(AUDIO_HREF_INDEX);
        String category = data.getString(CATEGORY_INDEX);
        getSupportActionBar().setTitle(title);

        if (!TextUtils.isEmpty(article)) {
            mArticleAdapter.setArticleSections(BBCHtmlUtility.getArticleSection(this, article, category));
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerHandler.removeCallbacks(mRunnable);
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
                    .putExtra(BBCContentContract.BBCLearningEnglishEntry.COLUMN_MP3_HREF, audioHref);
            startService(playerIntent);
            bindService(playerIntent, mConnection, BIND_AUTO_CREATE);
        }
    }

    private void updateSeekBar(){
        if (mBond && mAudioService.isPrepared()) {
            mAudioSeekBar.setEnabled(true);
            mAudioSeekBar.setMax(mAudioService.getDuration());
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                mAudioSeekBar.setProgress(mAudioService.getCurrentPosition(), true);
            } else {
                mAudioSeekBar.setProgress(mAudioService.getCurrentPosition());
            }
        } else {
            mAudioSeekBar.setEnabled(false);
        }
    }

    private void updatePlayButton() {
        if (mBond && mAudioService.isPlaying()) {
            mPlayButton.setImageResource(R.drawable.ic_pause);
        } else {
            mPlayButton.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    private void showAudioLoading() {
        if (!mBond) return;
        boolean isCached =
                (mAudioService.getCurrentPosition() - mAudioService.getCachedProgress()) < 1000;
        if (mAudioService.isPrepared()
                && isCached) {
            mAudioLoading.setVisibility(View.INVISIBLE);
            mPlayButton.setVisibility(View.VISIBLE);
        } else {
            mAudioLoading.setVisibility(View.VISIBLE);
            mPlayButton.setVisibility(View.INVISIBLE);
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
        switch (id) {
            case R.id.iv_play_pause:
                if (mBond && mAudioService.isPrepared()) {
                    mAudioService.controlPlayStatus();
                }
                break;
            case R.id.iv_forward:
                if (mBond && mAudioService.isPrepared()) {
                    int newPosition = mAudioService.getCurrentPosition() + 5000;
                    mAudioService.controlSeekPosition(newPosition);
                }
                break;
            case R.id.iv_replay:
                if (mBond && mAudioService.isPrepared()) {
                    int newPosition = mAudioService.getCurrentPosition() - 5000;
                    mAudioService.controlSeekPosition(newPosition);
                }
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

}
