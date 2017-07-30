package com.example.mao.bbc6minuteenglish;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Paranoid on 17/7/31.
 */

public class ArticleHolderFragment extends Fragment {

    private static final String ARTICLE_KEY = "article_text";

    public ArticleHolderFragment() {
    }

    public static ArticleHolderFragment newInstance(String str) {

        Bundle args = new Bundle();

        ArticleHolderFragment fragment = new ArticleHolderFragment();
        args.putString(ARTICLE_KEY, str);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ArticleHolderFragment fragment = new ArticleHolderFragment();
        View rootView = inflater.inflate(R.layout.fragment_article, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.tv_article);
        textView.setText(getArguments().getString(ARTICLE_KEY));
        return rootView;
    }
}
