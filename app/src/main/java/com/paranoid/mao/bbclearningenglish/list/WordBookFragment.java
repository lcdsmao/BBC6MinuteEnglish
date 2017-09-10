package com.paranoid.mao.bbclearningenglish.list;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

/**
 * Created by Paranoid on 17/9/10.
 */

public class WordBookFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int WORD_BOOK_LOADER_ID = 64236;

    private WordBookAdapter mAdapter;

    public WordBookFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragent_word_book_list, container, false);

        mAdapter = new WordBookAdapter(getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.rv_word_book_list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(manager);

        getLoaderManager().initLoader(WORD_BOOK_LOADER_ID, null, this);

        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                DatabaseContract.VocabularyEntry.CONTENT_URI,
                WordBookAdapter.PROJECTION,
                null,
                null,
                DatabaseContract.VocabularyEntry._ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
