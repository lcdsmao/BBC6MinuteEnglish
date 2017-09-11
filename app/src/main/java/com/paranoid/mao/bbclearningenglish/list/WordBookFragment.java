package com.paranoid.mao.bbclearningenglish.list;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paranoid.mao.bbclearningenglish.data.DefinitionFragment;
import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

/**
 * Created by Paranoid on 17/9/10.
 */

public class WordBookFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        WordBookAdapter.OnListItemClickListener{

    private static final int WORD_BOOK_LOADER_ID = 64236;

    private WordBookAdapter mAdapter;
    private ItemTouchHelper mSwipeToDeleteHelper;

    public WordBookFragment(){};

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
                long ID = (long) viewHolder.itemView.getTag();
                Uri uri = DatabaseContract.VocabularyEntry.CONTENT_URI
                        .buildUpon()
                        .appendEncodedPath(String.valueOf(ID))
                        .build();
                getContext().getContentResolver().delete(uri, null, null);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();

                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 2.5f;
                    height = height * 0.48f;

                    RectF background;
                    RectF icon;

                    if(dX > 0){
                        background = new RectF((float) itemView.getLeft(),
                                (float) itemView.getTop(),
                                dX,
                                (float) itemView.getBottom());
                        icon = new RectF(
                                dX - 2*width ,
                                (float) itemView.getTop() + height,
                                dX - width,
                                (float)itemView.getBottom() - height);
                    } else {
                        background = new RectF(
                                (float) itemView.getRight() + dX,
                                (float) itemView.getTop(),
                                (float) itemView.getRight(),
                                (float) itemView.getBottom());
                        icon = new RectF(
                                (float) itemView.getRight() + dX + width ,
                                (float) itemView.getTop() + height,
                                (float) itemView.getRight() + dX + 2*width,
                                (float)itemView.getBottom() - height);
                    }

                    p.setColor(ContextCompat.getColor(getContext(), R.color.red));
                    c.drawRect(background, p);
                    p.setColor(ContextCompat.getColor(getContext(), R.color.icons));
                    c.drawRect(icon, p);

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

        mAdapter = new WordBookAdapter(getContext(), this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.rv_word_book_list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(manager);
        mSwipeToDeleteHelper.attachToRecyclerView(recyclerView);

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

    @Override
    public void onClickItem(String word) {
        DefinitionFragment fragment = DefinitionFragment.newInstance(word, DefinitionFragment.Mode.WORDBOOK_MODE);
        fragment.show(getFragmentManager(), "Definition Fragment");
    }
}
