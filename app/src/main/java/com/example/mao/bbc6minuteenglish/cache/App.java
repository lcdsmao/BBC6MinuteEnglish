package com.example.mao.bbc6minuteenglish.cache;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by MAO on 8/1/2017.
 */

public class App extends Application {

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy(context)) : app.proxy;
    }

    @NonNull
    private HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .fileNameGenerator(new AudioFileNameGenerator())
                .build();
    }
}
