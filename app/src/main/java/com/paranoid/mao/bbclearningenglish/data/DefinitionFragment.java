package com.paranoid.mao.bbclearningenglish.data;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.webkit.WebView;

import com.paranoid.mao.bbclearningenglish.R;

/**
 * Created by Paranoid on 17/9/10.
 */

public class DefinitionFragment extends DialogFragment {

    private static final String WORD_REFERENCE_URL = "http://www.wordreference.com/definition/";
    private static final String WORD_KEY = "word";


    public static DefinitionFragment newInstance(String word) {
        Bundle args = new Bundle();
        args.putString(WORD_KEY, word);
        DefinitionFragment fagFragment = new DefinitionFragment();
        fagFragment.setArguments(args);
        return fagFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        WebView webView = (WebView) LayoutInflater.from(
                getActivity()).inflate(R.layout.fragment_web_view, null);
        String url = WORD_REFERENCE_URL + getArguments().get(WORD_KEY);
        webView.loadUrl(url);
        return new AlertDialog.Builder(getActivity())
                .setView(webView)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
