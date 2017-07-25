package com.example.mao.bbc6minuteenglish;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.sync.BBCSyncUtility;

public class ArticleActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = ArticleActivity.class.getName();

    private static final int ARTICLE_LOADER_ID = 123;

    private TextView mArticleTextView;
    private ProgressBar mArticleLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mArticleTextView = (TextView) findViewById(R.id.tv_article);
        mArticleLoading = (ProgressBar) findViewById(R.id.pb_article_load);

        // Show progress bar and hide article
        showLoading();

        // Check database if the article is null or not
        Uri uriWithTimeStamp = getIntent().getData();
        BBCSyncUtility.articleInitialize(this, uriWithTimeStamp);

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
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "Load finished");

        if(!data.moveToFirst()) return;

        int indexArticle = data.getColumnIndex(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_ARTICLE);
        int indexTitle = data.getColumnIndex(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TITLE);
        String article = data.getString(indexArticle);
        String title = data.getString(indexTitle);
        getSupportActionBar().setTitle(title);

        if (!TextUtils.isEmpty(article)) {
            mArticleTextView.setText(Html.fromHtml(article));
            showArticle();
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

}
