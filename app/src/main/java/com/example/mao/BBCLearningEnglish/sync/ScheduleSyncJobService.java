package com.example.mao.BBCLearningEnglish.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mao.BBCLearningEnglish.data.BBCCategory;
import com.example.mao.BBCLearningEnglish.data.BBCPreference;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by MAO on 8/2/2017.
 */

public class ScheduleSyncJobService extends JobService {

    public static final String TAG = ScheduleSyncJobService.class.getSimpleName();

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
        Log.v(TAG, "On start Job");
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
