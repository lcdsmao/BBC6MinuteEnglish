package com.example.mao.bbc6minuteenglish;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Paranoid on 17/7/31.
 */

public class ArticlePagerAdapter extends FragmentPagerAdapter {

    private String[] articleContents = new String[3];

    private String[] mViewPagerTitle;

    public ArticlePagerAdapter(FragmentManager fm, Context context){
        super(fm);
        mViewPagerTitle = new String[]{
                context.getString(R.string.article_question),
                context.getString(R.string.article_vocabulary),
                context.getString(R.string.article_transcript)
        };
    }

    public void setArticleContents(String[] strs) {
        articleContents = strs;
    }

    @Override
    public Fragment getItem(int position) {
        return ArticleHolderFragment.newInstance(articleContents[position]);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mViewPagerTitle[position];
    }
}
