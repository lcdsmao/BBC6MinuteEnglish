package com.paranoid.mao.bbclearningenglish.sync;

import android.content.Context;

import com.paranoid.mao.bbclearningenglish.data.BBCPreference;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by MAO on 8/2/2017.
 */

public class BBCSyncJobDispatcher {

    private static final int TRIGGER_INTERVAL = (int) TimeUnit.DAYS.toSeconds(2);
    private static final int TRIGGER_WINDOWS = (int) TimeUnit.HOURS.toSeconds(6);

    private static void buildScheduleSync(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job syncJob = dispatcher.newJobBuilder()
                .setService(ScheduleSyncJobService.class)
                .setTag(ScheduleSyncJobService.TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setTrigger(Trigger.executionWindow(TRIGGER_INTERVAL,
                        TRIGGER_INTERVAL + TRIGGER_WINDOWS))
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)
                .build();
        dispatcher.schedule(syncJob);
    }

    private static void cancelScheduleSync(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancel(ScheduleSyncJobService.TAG);
    }

    public static void dispatcherScheduleSync(Context context) {
        if (BBCPreference.getNotificationSwitch(context)) {
            buildScheduleSync(context);
        } else {
            cancelScheduleSync(context);
        }
    }
}
