package com.paranoid.mao.bbclearningenglish.article;

import android.content.ContentValues;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.data.VocabularyDefinition;
import com.paranoid.mao.bbclearningenglish.singleton.MyApp;
import com.paranoid.mao.bbclearningenglish.sync.WordReferenceRequest;

import java.io.IOException;

/**
 * Created by Paranoid on 17/9/10.
 */

public class DefinitionFragment extends BottomSheetDialogFragment
        implements View.OnClickListener {

    private static final String TAG = DefinitionFragment.class.getSimpleName();
    private static final String WORD_KEY = "word";

    private TextView mDefinitionView;
    private TextView mWordView;
    private TextView mSymbolView;
    private ImageView mAddView;
    private ImageView mPronunciationView;

    private String mWord;
    private String mPronUrl;

    private MediaPlayer mMediaPlayer;

    public static DefinitionFragment newInstance(String word) {
        Bundle args = new Bundle();
        args.putString(WORD_KEY, word);
        DefinitionFragment fagFragment = new DefinitionFragment();
        fagFragment.setArguments(args);
        return fagFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_definition, container);
        mWord = getArguments().getString(WORD_KEY);

        mDefinitionView = view.findViewById(R.id.tv_definition);
        mWordView = view.findViewById(R.id.tv_word);
        mSymbolView = view.findViewById(R.id.tv_symbol);
        mAddView = view.findViewById(R.id.iv_add);
        mWordView.setText(mWord);
        mPronunciationView = view.findViewById(R.id.iv_pronunciation);

        getDefinition(mWord);
        return view;
    }

    private void getDefinition(String word) {
        WordReferenceRequest request = new WordReferenceRequest(word,
                new Response.Listener<VocabularyDefinition>() {
                    @Override
                    public void onResponse(VocabularyDefinition response) {
                        mWordView.setText(response.getWord());
                        mSymbolView.setText(response.getSymbol());
                        mDefinitionView.setText(response.getDefinition());
                        mAddView.setOnClickListener(DefinitionFragment.this);
                        mPronUrl = response.getAudioHref();
                        if (!TextUtils.isEmpty(mPronUrl)) {
                            mPronunciationView.setVisibility(View.VISIBLE);
                            mPronunciationView.setOnClickListener(DefinitionFragment.this);
                            prepareMedia();
                        } else {
                            mPronunciationView.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mDefinitionView.setText(getString(R.string.error_message));
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(TAG);
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
                    Toast.makeText(getContext(),
                            getString(R.string.added_to_word_book), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onStop() {
        super.onStop();
        MyApp.getRequestQueue(getContext()).cancelAll(TAG);
    }
}
