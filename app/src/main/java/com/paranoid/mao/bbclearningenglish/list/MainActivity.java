package com.paranoid.mao.bbclearningenglish.list;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.paranoid.mao.bbclearningenglish.singleton.MyApp;
import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.settings.SettingActivity;
import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.sync.BBCSyncJobDispatcher;
import com.paranoid.mao.bbclearningenglish.sync.SyncTask;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    private static final String TITLE_STATE_KEY = "title";

    private static final String BBC_CONTENT_TAG = "bbc_content";
    private static final String FAVORITES_TAG = "favorites";
    private static final String WORD_BOOK_TAG = "wordbook";

    private DrawerLayout mDrawerLayout;

    private String mCurrentCategory = BBCCategory.CATEGORY_6_MINUTE_ENGLISH;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        NavigationView NavigationView = (NavigationView) findViewById(R.id.nav_view);
        NavigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        int id = R.id.category_six;
        if (intent.hasExtra(DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY)) {
            mCurrentCategory = intent.getStringExtra(DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY);
            id = BBCCategory.getCategoryItemId(mCurrentCategory);
        }
        NavigationView.setCheckedItem(id);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_list_container,
                    BBCContentFragment.newInstance(mCurrentCategory), BBC_CONTENT_TAG)
                    .commit();
        }

        BBCSyncJobDispatcher.dispatcherScheduleSync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        FragmentManager fm;
        FragmentTransaction ft;
        Fragment newFragment;
        switch (id) {
            case R.id.category_six:
            case R.id.category_we_speak:
            case R.id.category_news_report:
            case R.id.category_lingo_hack:
            case R.id.category_university:
                mCurrentCategory = BBCCategory.getItemIdCategory(id);
                fm = getSupportFragmentManager();
                BBCContentFragment bbcFm = (BBCContentFragment) fm.findFragmentByTag(BBC_CONTENT_TAG);
                if (bbcFm != null) {
                    bbcFm.swapCategory(mCurrentCategory);
                } else {
                    //ft.setCustomAnimations(android.R.anim., android.R.anim.slide_out_right);
                    newFragment = BBCContentFragment.newInstance(mCurrentCategory);
                    fm.beginTransaction().replace(R.id.content_list_container,
                            newFragment, BBC_CONTENT_TAG)
                            .commit();
                }
                break;
            case R.id.custom_favourites:
                ft = getSupportFragmentManager().beginTransaction();
                newFragment = new FavoritesFragment();
                ft.replace(R.id.content_list_container, newFragment, FAVORITES_TAG).commit();
                break;
            case R.id.custom_word_book:
                ft = getSupportFragmentManager().beginTransaction();
                newFragment = new WordBookFragment();
                ft.replace(R.id.content_list_container, newFragment, WORD_BOOK_TAG).commit();
                break;
            case R.id.drawer_rating:
                intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("market://details?id=com.paranoid.mao.bbclearningenglish");
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case R.id.drawer_setting:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                return false;
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

}
