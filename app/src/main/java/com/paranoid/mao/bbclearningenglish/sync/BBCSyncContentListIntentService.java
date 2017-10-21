package com.paranoid.mao.bbclearningenglish.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

/**
 * Created by MAO on 7/26/2017.
 */

public class BBCSyncContentListIntentService extends IntentService {

    public BBCSyncContentListIntentService() {
        super(BBCSyncContentListIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String category = bundle.getString(
                    DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY);
            if (category == null) return;
            boolean isSuccessful = SyncTask.syncCategoryList(this, category);

            Messenger messenger = (Messenger) bundle.get(SyncUtility.MESSENGER_KEY);
            if (messenger == null) return;
            Message msg = Message.obtain();
            msg.arg1 = isSuccessful ? 1 : 0;
            msg.arg2 = BBCCategory.getCategoryItemId(category);
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }
}
