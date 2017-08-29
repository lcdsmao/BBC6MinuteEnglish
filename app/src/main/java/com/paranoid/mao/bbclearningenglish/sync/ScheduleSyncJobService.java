package com.paranoid.mao.bbclearningenglish.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.data.BBCPreference;


/**
 * Created by MAO on 8/2/2017.
 */

public class ScheduleSyncJobService extends JobService {

    private static final String[] ALL_CATEGORY = {
            BBCCategory.CATEGORY_6_MINUTE_ENGLISH,
            BBCCategory.CATEGORY_ENGLISH_AT_UNIVERSITY,
            BBCCategory.CATEGORY_LINGO_HACK,
            BBCCategory.CATEGORY_NEWS_REPORT,
            BBCCategory.CATEGORY_THE_ENGLISH_WE_SPEAK
    };

    private AsyncTask<Void, Void, Void> mSyncTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mSyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (String category: ALL_CATEGORY) {
                    if (BBCPreference.isUpdateNeed(getApplicationContext(), category)) {
                        BBCSyncTask.syncCategoryList(getApplicationContext(), category);
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
