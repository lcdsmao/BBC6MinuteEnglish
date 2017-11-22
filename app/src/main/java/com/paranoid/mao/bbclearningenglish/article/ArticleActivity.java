package com.paranoid.mao.bbclearningenglish.article;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.audio.AudioPlayerFragment;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.singleton.MyApp;
import com.paranoid.mao.bbclearningenglish.sync.SyncUtility;
import com.paranoid.mao.bbclearningenglish.utilities.BBCHtmlUtility;

public class ArticleActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ARTICLE_LOADER_ID = 123;
    private static final String AUDIO_PLAYER_FRAGMENT_TAG = "audioPlayerFragment";

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

    private Uri mUriWithTimeStamp;
    private boolean mIsFavorite = false;
    private boolean mIsFavoriteChanged = false;

    private ArticlePagerAdapter mArticleAdapter;

    private ProgressBar mArticleLoading;
    private ViewPager mArticleViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;



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

        if (getSupportFragmentManager().findFragmentByTag(AUDIO_PLAYER_FRAGMENT_TAG) == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.audio_player_container,
                    new AudioPlayerFragment(), AUDIO_PLAYER_FRAGMENT_TAG).commit();
        }


        getSupportLoaderManager().initLoader(ARTICLE_LOADER_ID, null, this);
    }

    private void viewBind() {
        mArticleLoading = (ProgressBar) findViewById(R.id.pb_article_load);
        mTabLayout = (TabLayout) findViewById(R.id.tabbar);
        mArticleViewPager = (ViewPager) findViewById(R.id.view_pager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_favourite) {
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
        if (!data.moveToFirst()) return;

        String article = data.getString(ARTICLE_INDEX);
        String title = data.getString(TITLE_INDEX);
        String audioHref = data.getString(AUDIO_HREF_INDEX);
        mIsFavorite = data.getLong(FAVOURITES_INDEX) > 0;

        setTitle(title);

        if (!TextUtils.isEmpty(article)) {
            mArticleAdapter.setArticleSections(BBCHtmlUtility.getArticleSection(this, article));
            mArticleViewPager.setAdapter(mArticleAdapter);
            mTabLayout.setupWithViewPager(mArticleViewPager);
            showArticle();
        }

        if (!TextUtils.isEmpty(audioHref)) {
            AudioPlayerFragment audioPlayerFragment = (AudioPlayerFragment)
                    getSupportFragmentManager().findFragmentByTag(AUDIO_PLAYER_FRAGMENT_TAG);
            audioPlayerFragment.prepareAudioService(audioHref, mUriWithTimeStamp);
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
    protected void onResume() {
        super.onResume();
        MyApp.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApp.activityPaused();
        if (isFinishing()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(getSupportFragmentManager().findFragmentByTag(AUDIO_PLAYER_FRAGMENT_TAG)).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsFavoriteChanged) {
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
