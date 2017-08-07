package com.example.mao.BBCLearningEnglish.sync;

import com.example.mao.BBCLearningEnglish.data.BBCPreference;
import com.example.mao.BBCLearningEnglish.utilities.NotificationUtility;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by MAO on 8/2/2017.
 */

public class ScheduleSyncJobService extends JobService {

    public static final String TAG = ScheduleSyncJobService.class.getName();

    @Override
    public boolean onStartJob(JobParameters job) {
//        if (BBCPreference.isUpdateNeed(this)) {
//            boolean isNewContent = BBCSyncTask.syncContentList(this);
//            if (isNewContent) {
//                NotificationUtility.showNewContentNotification(this);
//            }
 //       }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
