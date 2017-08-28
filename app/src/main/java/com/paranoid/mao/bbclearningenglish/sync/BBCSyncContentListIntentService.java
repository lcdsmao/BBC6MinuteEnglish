package com.paranoid.mao.bbclearningenglish.sync;

import android.app.IntentService;
import android.content.Intent;

import com.paranoid.mao.bbclearningenglish.data.BBCContentContract;

/**
 * Created by MAO on 7/26/2017.
 */

public class BBCSyncContentListIntentService extends IntentService {

    public BBCSyncContentListIntentService() {
        super(BBCSyncContentListIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra(BBCContentContract.BBCLearningEnglishEntry.COLUMN_CATEGORY)) {
            String category = intent.getStringExtra(
                    BBCContentContract.BBCLearningEnglishEntry.COLUMN_CATEGORY);
            BBCSyncTask.syncCategoryList(this, category);
        }

    }
}