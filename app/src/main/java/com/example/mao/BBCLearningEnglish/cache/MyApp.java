package com.example.mao.BBCLearningEnglish.cache;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by MAO on 8/1/2017.
 */

public class MyApp extends Application {

    private static final String TAG = MyApp.class.getSimpleName();

    private HttpProxyCacheServer mProxy;
    private RequestQueue mRequestQueue;
    private static boolean sActivityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "on Create");
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApp app = (MyApp) context.getApplicationContext();
        return app.mProxy == null ? (app.mProxy = app.newProxy(context)) : app.mProxy;
    }

    public static RequestQueue getRequestQueue(Context context) {
        MyApp app = (MyApp) context.getApplicationContext();
        return app.mRequestQueue == null? (app.mRequestQueue = app.newRequestQueue(context)) : app.mRequestQueue;
    }

    @NonNull
    private RequestQueue newRequestQueue(Context context) {
        return Volley.newRequestQueue(context);
    }

    @NonNull
    private HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .fileNameGenerator(new AudioFileNameGenerator())
                .build();
    }

    public static boolean isActivityVisible() {
        return sActivityVisible;
    }

    public static void activityResumed() {
        sActivityVisible = true;
    }

    public static void activityPaused() {
        sActivityVisible = false;
    }
}
