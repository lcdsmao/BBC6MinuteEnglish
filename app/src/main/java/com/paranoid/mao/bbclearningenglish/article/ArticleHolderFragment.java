package com.paranoid.mao.bbclearningenglish.article;

import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

/**
 * Created by Paranoid on 17/7/31.
 */

public class ArticleHolderFragment extends Fragment{

    private static final String ARTICLE_KEY = "article_text";

    TextView mArticleText;

    public static ArticleHolderFragment newInstance(String str) {
        Bundle args = new Bundle();
        ArticleHolderFragment fragment = new ArticleHolderFragment();
        args.putString(ARTICLE_KEY, str);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article, container, false);
        mArticleText = rootView.findViewById(R.id.tv_article);
        String str = getArguments().getString(ARTICLE_KEY);
        if (Build.VERSION.SDK_INT >= 24) {
            mArticleText.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY));
        } else {
            mArticleText.setText(Html.fromHtml(str));
        }

        mArticleText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.select_text_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                menu.removeItem(android.R.id.selectAll);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                String word;
                actionMode.invalidate();
                switch (menuItem.getItemId()) {
                    case R.id.menu_word_book:
                        word = getSelectedText();
                        ContentValues contentValues = new ContentValues();
                        if (word != null && word.length() > 0 && word.length() < 20) {
                            contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_VOCAB, word);
                            getContext().getContentResolver().insert(
                                    DatabaseContract.VocabularyEntry.CONTENT_URI,
                                    contentValues
                            );
                            Toast.makeText(getContext(),
                                    getString(R.string.added_to_word_book), Toast.LENGTH_SHORT).show();
                        }
                        actionMode.finish();
                        break;
                    case R.id.menu_definition:
                        word = getSelectedText();
                        DefinitionFragment.newInstance(word).show(getFragmentManager(), "Definition Fragment");
                        actionMode.finish();
                        break;
                    default:

                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        return rootView;
    }

    private String getSelectedText() {
        String selectedText = "";
        if (mArticleText.isFocused()) {
            final int textStartIndex = mArticleText.getSelectionStart();
            final int textEndIndex = mArticleText.getSelectionEnd();

            int min = Math.max(0, Math.min(textStartIndex, textEndIndex));
            int max = Math.max(0, Math.max(textStartIndex, textEndIndex));
            selectedText = mArticleText.getText().subSequence(min, max).toString().trim();
        }
        return selectedText;
    }
}
