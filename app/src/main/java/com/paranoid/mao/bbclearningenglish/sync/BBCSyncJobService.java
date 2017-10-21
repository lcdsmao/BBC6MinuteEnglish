package com.paranoid.mao.bbclearningenglish.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.data.BBCPreference;
import com.paranoid.mao.bbclearningenglish.singleton.MyApp;


/**
 * Created by MAO on 8/2/2017.
 */

public class BBCSyncJobService extends JobService {

    private AsyncTask<Void, Void, Void> mSyncTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        if (MyApp.isActivityVisible()) return false;
        mSyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (String category : BBCCategory.ALL_CATEGORY) {
                    if (BBCPreference.isUpdateNeed(getApplicationContext(), category)) {
                        // only update one at a time
                        SyncTask.syncCategoryList(getApplicationContext(), category);
                        return null;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };
        mSyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mSyncTask != null) {
            mSyncTask.cancel(true);
        }
        return true;
    }
}
