package com.example.mao.bbc6minuteenglish.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;

/**
 * Created by MAO on 7/24/2017.
 */

public class BBCSyncIntentService extends IntentService {

    public BBCSyncIntentService() {
        super(BBCSyncIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Uri uriWithStamp = intent.getData();
        String articleHref = intent.getStringExtra(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_HREF);
        Log.v(BBCSyncIntentService.class.getName(), "On handle intent");
        BBCSyncTask.syncArticle(this, uriWithStamp, articleHref);
    }
}
