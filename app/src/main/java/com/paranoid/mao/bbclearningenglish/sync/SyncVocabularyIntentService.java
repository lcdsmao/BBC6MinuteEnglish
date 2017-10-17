package com.paranoid.mao.bbclearningenglish.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

public class SyncVocabularyIntentService extends IntentService {

    public SyncVocabularyIntentService() {
        super("SyncVocabularyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Uri uriWithId = intent.getData();
            String vocab = intent.getStringExtra(DatabaseContract.VocabularyEntry.COLUMN_VOCAB);
            SyncTask.syncVocab(this, uriWithId, vocab);
        }
    }
}
