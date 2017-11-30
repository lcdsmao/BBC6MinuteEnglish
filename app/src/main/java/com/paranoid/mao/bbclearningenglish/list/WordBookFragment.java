package com.paranoid.mao.bbclearningenglish.list;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.sync.SyncUtility;

import java.io.IOException;

/**
 * Created by Paranoid on 17/9/10.
 */

public class WordBookFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        WordBookAdapter.OnItemClickListener {

    private static final int WORD_BOOK_LOADER_ID = 64236;

    private WordBookAdapter mAdapter;
    private ItemTouchHelper mSwipeToDeleteHelper;
    private MediaPlayer mMediaPlayer;
    private String mCurrentAudioHref = "";

    public WordBookFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ItemTouchHelper.SimpleCallback swipeToDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final String vocabulary = ((TextView) viewHolder.itemView.findViewById(R.id.tv_vocabulary)).getText().toString();
                final long id = (long) viewHolder.itemView.getTag();

                mAdapter.clearDeletedExpandedPosition(position);
                viewHolder.itemView.setAlpha(1.0f);
                Uri uri = DatabaseContract.VocabularyEntry.CONTENT_URI
                        .buildUpon()
                        .appendEncodedPath(String.valueOf(id))
                        .build();
                getContext().getContentResolver().delete(uri, null, null);
                Snackbar.make(viewHolder.itemView, R.string.vocabulary_deleted, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DatabaseContract.VocabularyEntry.COLUMN_VOCAB, vocabulary);
                                contentValues.put(DatabaseContract.VocabularyEntry._ID, id);
                                getContext().getContentResolver().
                                        insert(DatabaseContract.VocabularyEntry.CONTENT_URI, contentValues);
                            }
                        }).show();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;
                    itemView.setAlpha(1.0f - Math.abs(dX) / itemView.getWidth());

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        mSwipeToDeleteHelper = new ItemTouchHelper(swipeToDelete);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(R.string.custom_word_book);

        View view = inflater.inflate(R.layout.fragent_word_book_list, container, false);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.rv_word_book_list);
        WordItemAnimator animator = new WordItemAnimator();
        recyclerView.setItemAnimator(animator);
        mAdapter = new WordBookAdapter(getContext(), recyclerView, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(manager);
        mSwipeToDeleteHelper.attachToRecyclerView(recyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

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
                DatabaseContract.VocabularyEntry._ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void OnPronunciationClick(String audioHref) {
        if (TextUtils.isEmpty(audioHref)) {
            Toast.makeText(getContext(), R.string.no_audio, Toast.LENGTH_SHORT).show();
        } else if (mCurrentAudioHref.equals(audioHref) && mMediaPlayer != null) {
            mMediaPlayer.start();
        } else {
            prepareMedia(audioHref);
            mCurrentAudioHref = audioHref;
        }
    }

    @Override
    public void OnDetailClick(long id) {
        Uri uri = DatabaseContract.VocabularyEntry.CONTENT_URI
                .buildUpon()
                .appendEncodedPath(String.valueOf(id))
                .build();
        SyncUtility.wordBookInitialize(getContext(), uri);
    }

    private void prepareMedia(String audioHref) {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(audioHref);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (IOException e) {
            mMediaPlayer.release();
            mMediaPlayer = null;
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

    static class WordItemAnimator extends DefaultItemAnimator {

        @Override
        public boolean animateMove(
                RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            return false;
        }
    }
}
