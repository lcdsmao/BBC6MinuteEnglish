package com.paranoid.mao.bbclearningenglish.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncArticleIntentService extends IntentService {

    public BBCSyncArticleIntentService() {
        super(BBCSyncArticleIntentService.class.getName());
    }

    private void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri uriWithStamp = intent.getData();
        String articleHref = intent.getStringExtra(DatabaseContract.BBCLearningEnglishEntry.COLUMN_HREF);
        boolean isSuccessful = SyncTask.syncArticle(this, uriWithStamp, articleHref);
        if (!isSuccessful) showToast(getString(R.string.error_message));
    }
}
