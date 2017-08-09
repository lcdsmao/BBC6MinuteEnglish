package com.paranoid.mao.bbclearningenglish.singleton;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.danikula.videocache.HttpProxyCacheServer;
import com.paranoid.mao.bbclearningenglish.data.AudioFileNameGenerator;

/**
 * Created by MAO on 8/1/2017.
 */

public class MyApp extends Application {

    private HttpProxyCacheServer mProxy;
    private RequestQueue mRequestQueue;
    private static boolean sActivityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
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
