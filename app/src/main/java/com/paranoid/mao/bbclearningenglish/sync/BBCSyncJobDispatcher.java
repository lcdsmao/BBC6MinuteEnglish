package com.paranoid.mao.bbclearningenglish.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.paranoid.mao.bbclearningenglish.data.BBCPreference;

import java.util.concurrent.TimeUnit;

/**
 * Created by MAO on 8/2/2017.
 */

public class BBCSyncJobDispatcher {

    private static final int JOB_ID = 151515;

    private static final long TRIGGER_INTERVAL = TimeUnit.DAYS.toMillis(1);

    private static void buildScheduleSync(Context context) {
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,
                new ComponentName(context, BBCSyncJobService.class));

        builder.setPeriodic(TRIGGER_INTERVAL);
        builder.setPersisted(true);
        builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS,
                JobInfo.BACKOFF_POLICY_EXPONENTIAL);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(builder.build());
    }

    private static void cancelScheduleSync(Context context) {
        JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancelAll();
    }

    public static void dispatcherScheduleSync(Context context) {
        if (BBCPreference.getNotificationSwitch(context)) {
            buildScheduleSync(context);
        } else {
            cancelScheduleSync(context);
        }
    }
}
