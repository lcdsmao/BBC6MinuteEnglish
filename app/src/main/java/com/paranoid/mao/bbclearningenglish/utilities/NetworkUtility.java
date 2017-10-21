package com.paranoid.mao.bbclearningenglish.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Paranoid on 17/10/21.
 */

public class NetworkUtility {

    public static String request(String url, Context context) throws Exception {

        boolean isConnected = isConnected(context);
        if (!isConnected) throw new IOException();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String responseBody;

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException();
        responseBody = response.body().string();

        return responseBody;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
