package com.example.mao.bbc6minuteenglish;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;

public class ArticleActivity extends AppCompatActivity {

    private static final String TAG = ArticleActivity.class.getName();

    private TextView mArticleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mArticleTextView = (TextView) findViewById(R.id.tv_article);

        Uri uri = getIntent().getData();
        Log.v(TAG, uri.toString());
        Cursor cursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        int indexArticleHref = cursor.getColumnIndex(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_HREF);
        String article = cursor.getString(indexArticleHref);
        mArticleTextView.setText(Html.fromHtml(article));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
