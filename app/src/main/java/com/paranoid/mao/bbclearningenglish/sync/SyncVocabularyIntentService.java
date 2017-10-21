package com.paranoid.mao.bbclearningenglish.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

public class SyncVocabularyIntentService extends IntentService {

    public SyncVocabularyIntentService() {
        super("SyncVocabularyIntentService");
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
        if (intent != null) {
            Uri uriWithId = intent.getData();
            String vocab = intent.getStringExtra(DatabaseContract.VocabularyEntry.COLUMN_VOCAB);
            boolean isSucceed = SyncTask.syncVocab(this, uriWithId, vocab);
            if (!isSucceed) showToast(getString(R.string.error_message));
        }
    }
}
