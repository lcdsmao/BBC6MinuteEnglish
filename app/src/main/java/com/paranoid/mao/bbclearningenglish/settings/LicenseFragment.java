package com.paranoid.mao.bbclearningenglish.settings;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.webkit.WebView;

import com.paranoid.mao.bbclearningenglish.R;

public class LicenseFragment extends DialogFragment {


    public static LicenseFragment newInstance() {
        return new LicenseFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        WebView webView = (WebView) LayoutInflater.from(
                getActivity()).inflate(R.layout.fragment_webview, null);
        webView.loadUrl("file:///android_asset/Licenses.html");
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.setting_licenses_title)
                .setView(webView)
                .setPositiveButton(android.R.string.ok, null)
                .create();

    }
}
