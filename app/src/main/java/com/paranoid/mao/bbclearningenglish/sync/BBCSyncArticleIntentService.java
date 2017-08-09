package com.paranoid.mao.bbclearningenglish.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import com.paranoid.mao.bbclearningenglish.data.BBCContentContract;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncArticleIntentService extends IntentService {

    public BBCSyncArticleIntentService() {
        super(BBCSyncArticleIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri uriWithStamp = intent.getData();
        String articleHref = intent.getStringExtra(BBCContentContract.BBCLearningEnglishEntry.COLUMN_HREF);
        BBCSyncTask.syncArticle(this, uriWithStamp, articleHref);
    }
}
