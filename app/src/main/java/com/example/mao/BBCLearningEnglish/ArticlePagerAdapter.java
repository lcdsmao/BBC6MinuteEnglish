package com.example.mao.BBCLearningEnglish;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mao.BBCLearningEnglish.data.BBCArticleSection;

import java.util.ArrayList;

/**
 * Created by Paranoid on 17/7/31.
 */

public class ArticlePagerAdapter extends FragmentPagerAdapter {

    private ArrayList<BBCArticleSection> articleSections;

    public ArticlePagerAdapter(FragmentManager fm){
        super(fm);
    }

    public void setArticleSections(ArrayList<BBCArticleSection> articleSections) {
        this.articleSections = articleSections;
    }

    @Override
    public Fragment getItem(int position) {
        return ArticleHolderFragment.newInstance(articleSections.get(position).getArticle());
    }

    @Override
    public int getCount() {
        return articleSections.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return articleSections.get(position).getTitle();
    }
}
