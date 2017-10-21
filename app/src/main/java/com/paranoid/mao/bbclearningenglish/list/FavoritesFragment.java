package com.paranoid.mao.bbclearningenglish.list;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
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
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

public class FavoritesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {

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
        ItemTouchHelper.SimpleCallback swipeToDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
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

                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 2.5f;
                    height = height * 0.48f;

                    RectF background;
                    RectF icon;

                    if (dX > 0) {
                        background = new RectF((float) itemView.getLeft(),
                                (float) itemView.getTop(),
                                dX,
                                (float) itemView.getBottom());
                        icon = new RectF(
                                dX - 2 * width,
                                (float) itemView.getTop() + height,
                                dX - width,
                                (float) itemView.getBottom() - height);
                    } else {
                        background = new RectF(
                                (float) itemView.getRight() + dX,
                                (float) itemView.getTop(),
                                (float) itemView.getRight(),
                                (float) itemView.getBottom());
                        icon = new RectF(
                                (float) itemView.getRight() + dX + width,
                                (float) itemView.getTop() + height,
                                (float) itemView.getRight() + dX + 2 * width,
                                (float) itemView.getBottom() - height);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.custom_favourite);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bbc_content_list, container, false);
        mSwipeContainer = view.findViewById(R.id.srl_content_container);
        mSwipeContainer.setOnRefreshListener(this);
        mSwipeContainer.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.accent));

        /*Set the recycler view*/
        mBBCContentAdapter = new BBCContentAdapter(getContext(),
                new OnBBCItemClickListener(getContext()));
        RecyclerView contentRecycleView = view.findViewById(R.id.rv_content_list);
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
