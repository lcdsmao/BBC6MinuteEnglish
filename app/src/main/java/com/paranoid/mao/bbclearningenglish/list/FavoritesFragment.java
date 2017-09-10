package com.paranoid.mao.bbclearningenglish.list;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.data.BBCPreference;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.sync.BBCSyncUtility;

public class FavoritesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener{

    private static final int FAVORITES_LOADER_ID = 1;

    private BBCContentAdapter mBBCContentAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private ItemTouchHelper mSwipeToDeleteHelper;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ItemTouchHelper.SimpleCallback swipeToDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String path = (String) viewHolder.itemView.getTag();
                Uri uri = DatabaseContract.BBCLearningEnglishEntry.CONTENT_URI
                        .buildUpon()
                        .appendEncodedPath(path)
                        .build();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_FAVOURITES, 0);
                getContext().getContentResolver().update(uri, contentValues, null, null);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    p.setColor(ContextCompat.getColor(getContext(), R.color.red));
                    float left = itemView.getLeft();
                    float top = itemView.getTop();
                    float bottom = itemView.getBottom();
                    c.drawRect(left, top, dX, bottom, p);

                    p.setColor(ContextCompat.getColor(getContext(), R.color.background));
                    float line_left = dX - 164;
                    float line_right = dX - 100;
                    float line_top = (top + bottom) / 2 + 6;
                    float line_bottom = (top + bottom) / 2 - 6;
                    c.drawRect(line_left, line_top, line_right, line_bottom, p);

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        mSwipeToDeleteHelper = new ItemTouchHelper(swipeToDelete);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bbc_content_list, container, false);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srl_content_container);
        mSwipeContainer.setOnRefreshListener(this);
        mSwipeContainer.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.accent));

        /*Set the recycler view*/
        mBBCContentAdapter = new BBCContentAdapter(getContext(),
                new OnBBCItemClickListener(getContext()));
        RecyclerView contentRecycleView = (RecyclerView) view.findViewById(R.id.rv_content_list);
        contentRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentRecycleView.setAdapter(mBBCContentAdapter);
        /*Set the recycler view complete*/

        mSwipeToDeleteHelper.attachToRecyclerView(contentRecycleView);

        getLoaderManager().initLoader(FAVORITES_LOADER_ID, new Bundle(), this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSwipeContainer.setRefreshing(true);
        Uri uri = DatabaseContract.BBCLearningEnglishEntry.CONTENT_URI;
        String selection = DatabaseContract.BBCLearningEnglishEntry.COLUMN_FAVOURITES + ">0";
        String sortOrder = DatabaseContract.BBCLearningEnglishEntry.FAVOURITE_SORT_ORDER;
        return new CursorLoader(
                getContext(),
                uri,
                BBCContentAdapter.PROJECTION,
                selection,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBBCContentAdapter.swapCursor(data);
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBBCContentAdapter.swapCursor(null);
    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, this);
    }
}
