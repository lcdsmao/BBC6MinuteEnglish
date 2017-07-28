package com.example.mao.bbc6minuteenglish;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.sync.BBCSyncUtility;

public class ArticleActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = ArticleActivity.class.getName();

    private static final int ARTICLE_LOADER_ID = 123;

    private static final String SERVICE_STATE_KEY = "service_state";

    private static final String[] PROJECTION = {
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TITLE,
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_ARTICLE,
            BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_MP3_HREF
    };

    private static final int TITLE_INDEX = 0;
    private static final int ARTICLE_INDEX = 1;
    private static final int AUDIO_HREF_INDEX = 2;

    private AudioPlayService mAudioService;
    private boolean mBond = false;

    private Uri mUriWithTimeStamp;

    private TextView mArticleTextView;
    private ProgressBar mArticleLoading;
    private ImageView mPlayButton;
    private SeekBar mAudioSeekBar;

    private Handler mPlayerHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBond) {
                mAudioSeekBar.setMax(mAudioService.getDuration());
                mAudioSeekBar.setProgress(mAudioService.getCurrentPosition());
            }
            mPlayerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mArticleTextView = (TextView) findViewById(R.id.tv_article);
        mArticleLoading = (ProgressBar) findViewById(R.id.pb_article_load);
        mPlayButton = (ImageView) findViewById(R.id.iv_play_control);
        mAudioSeekBar = (SeekBar) findViewById(R.id.sb_play_bar);

        // Show progress bar and hide article
        showLoading();

        // Check database if the article is null or not
        mUriWithTimeStamp = getIntent().getData();
        BBCSyncUtility.articleInitialize(this, mUriWithTimeStamp);

        getSupportLoaderManager().initLoader(ARTICLE_LOADER_ID, null, this);
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
        getSupportActionBar().setTitle(title);

        if (!TextUtils.isEmpty(article)) {
            mArticleTextView.setText(Html.fromHtml(article));
            showArticle();
        }

        if (!TextUtils.isEmpty(audioHref)) {
            playAudio(audioHref);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showLoading() {
        mArticleTextView.setVisibility(View.INVISIBLE);
        mArticleLoading.setVisibility(View.VISIBLE);
    }

    private void showArticle() {
        mArticleLoading.setVisibility(View.INVISIBLE);
        mArticleTextView.setVisibility(View.VISIBLE);
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
        mPlayerHandler.postDelayed(mRunnable, 1000);
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

    private void playAudio(String audioHref) {
        //Check is service is active
        if (!mBond) {
            Intent playerIntent = new Intent(this, AudioPlayService.class);
            playerIntent.setData(mUriWithTimeStamp);
            playerIntent.putExtra(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_MP3_HREF,
                    audioHref);
            startService(playerIntent);
            bindService(playerIntent, mConnection, BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
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

            Toast.makeText(ArticleActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBond = false;
        }
    };
}
