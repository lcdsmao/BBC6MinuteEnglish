package com.paranoid.mao.bbclearningenglish.data;

import android.app.Dialog;
import android.content.ContentValues;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.singleton.MyApp;
import com.paranoid.mao.bbclearningenglish.sync.WordReferenceRequest;

import java.io.IOException;

/**
 * Created by Paranoid on 17/9/10.
 */

public class DefinitionFragment extends BottomSheetDialogFragment
    implements View.OnClickListener{

    public enum Mode {
        ARTICLE_MODE,
        WORDBOOK_MODE
    }

    private static final String MODE_KEY = "mode";
    private static final String WORD_KEY = "word";

    private Mode mMode;

    private TextView mDefinitionView;
    private TextView mWordView;
    private TextView mSymbolView;
    private ImageView mAddView;
    private ImageView mPronView;

    private String mWord;
    private String mPronUrl;

    private MediaPlayer mMediaPlayer;

    public static DefinitionFragment newInstance(String word, Mode mode) {
        Bundle args = new Bundle();
        args.putString(WORD_KEY, word);
        args.putSerializable(MODE_KEY, mode);
        DefinitionFragment fagFragment = new DefinitionFragment();
        fagFragment.setArguments(args);
        return fagFragment;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.fragment_definition, null);
        mWord = getArguments().getString(WORD_KEY);
        mMode = (Mode) getArguments().get(MODE_KEY);

        mDefinitionView = view.findViewById(R.id.tv_definition);
        mWordView = view.findViewById(R.id.tv_word);
        mSymbolView = view.findViewById(R.id.tv_symbol);
        mAddView = view.findViewById(R.id.iv_add);
        mPronView = view.findViewById(R.id.iv_pronunciation);
        mWordView.setText(mWord);

        dialog.setContentView(view);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        getDefinition(mWord);
    }

    private void getDefinition(String word) {
        WordReferenceRequest request = new WordReferenceRequest(word,
                new Response.Listener<VocabularyDefinition>() {
                    @Override
                    public void onResponse(VocabularyDefinition response) {
                        mWordView.setText(response.getWord());
                        mSymbolView.setText(response.getSymbol());
                        mDefinitionView.setText(response.getDefinition());
                        mPronUrl = response.getAudioHref();
                        if (mMode == Mode.ARTICLE_MODE) {
                            mAddView.setVisibility(View.VISIBLE);
                            mAddView.setOnClickListener(DefinitionFragment.this);
                            mPronView.setVisibility(View.GONE);
                        } else if (mMode == Mode.WORDBOOK_MODE && !TextUtils.isEmpty(mPronUrl)){
                            mAddView.setVisibility(View.GONE);
                            mPronView.setVisibility(View.VISIBLE);
                            mPronView.setOnClickListener(DefinitionFragment.this);
                            prepareMedia();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         mDefinitionView.setText(getString(R.string.default_definition));
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApp.getRequestQueue(getContext()).add(request);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_add:
                if (mWord != null && mWord.length() > 0 && mWord.length() < 20) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_VOCAB, mWord);
                    getContext().getContentResolver().insert(
                            DatabaseContract.VocabularyEntry.CONTENT_URI,
                            contentValues
                    );
                }
                break;
            case R.id.iv_pronunciation:
                 if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                }
                break;
            default:
                break;
        }
    }

    private void prepareMedia(){
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(mPronUrl);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
