package com.paranoid.mao.bbclearningenglish.list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.paranoid.mao.bbclearningenglish.article.ArticleActivity;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

/**
 * Created by Paranoid on 17/9/10.
 */

public class OnBBCItemClickListener implements BBCContentAdapter.OnListItemClickListener {

    private Context mContext;

    public OnBBCItemClickListener(Context context) {
        mContext = context;
    }

    @Override
    public void onClickItem(String path) {
        Intent intent = new Intent(mContext, ArticleActivity.class);
        Uri uri = DatabaseContract.BBCLearningEnglishEntry.CONTENT_URI
                .buildUpon()
                .appendEncodedPath(path)
                .build();
        intent.setData(uri);
        mContext.startActivity(intent);
    }

}
