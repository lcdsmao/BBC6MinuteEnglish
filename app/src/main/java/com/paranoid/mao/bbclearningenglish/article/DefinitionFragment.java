package com.paranoid.mao.bbclearningenglish.article;

import android.app.Dialog;
import android.content.ContentValues;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
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
    implements View.OnClickListener{

    private static final String WORD_KEY = "word";

    private TextView mDefinitionView;
    private TextView mWordView;
    private TextView mSymbolView;
    private ImageView mAddView;

    private String mWord;
    private String mPronUrl;

    private MediaPlayer mMediaPlayer;

    private BottomSheetBehavior mBehavior;

    public static DefinitionFragment newInstance(String word) {
        Bundle args = new Bundle();
        args.putString(WORD_KEY, word);
        DefinitionFragment fagFragment = new DefinitionFragment();
        fagFragment.setArguments(args);
        return fagFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.fragment_definition, null);
        mWord = getArguments().getString(WORD_KEY);

        mDefinitionView = view.findViewById(R.id.tv_definition);
        mWordView = view.findViewById(R.id.tv_word);
        mSymbolView = view.findViewById(R.id.tv_symbol);
        mAddView = view.findViewById(R.id.iv_add);
        mWordView.setText(mWord);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());

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
                        mAddView.setOnClickListener(DefinitionFragment.this);
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
                    Toast.makeText(getContext(),
                            getString(R.string.added_to_word_book), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
