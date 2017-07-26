package com.example.mao.bbc6minuteenglish.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by MAO on 7/26/2017.
 */

public class BBCSyncContentListIntentService extends IntentService {

    public BBCSyncContentListIntentService() {
        super(BBCSyncContentListIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        BBCSyncTask.syncContentList(this);
    }
}
