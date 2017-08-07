package com.example.mao.BBCLearningEnglish.cache;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by MAO on 8/1/2017.
 */

public class App extends Application {

    private HttpProxyCacheServer proxy;
    private RequestQueue requestQueue;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy(context)) : app.proxy;
    }

    public static RequestQueue getRequestQueue(Context context) {
        App app = (App) context.getApplicationContext();
        return app.requestQueue == null? (app.requestQueue = app.newRequestQueue(context)) : app.requestQueue;
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
}
