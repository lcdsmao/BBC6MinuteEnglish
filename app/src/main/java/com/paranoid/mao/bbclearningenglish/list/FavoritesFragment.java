package com.paranoid.mao.bbclearningenglish.list;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
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
                viewHolder.itemView.setAlpha(1.0f);
                String path = (String) viewHolder.itemView.getTag();
                final Uri uri = DatabaseContract.BBCLearningEnglishEntry.CONTENT_URI
                        .buildUpon()
                        .appendEncodedPath(path)
                        .build();
                final ContentValues contentValues = new ContentValues();
                final long favouritesTimes = getFavouriteTime(uri);
                contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_FAVOURITES, 0);
                getContext().getContentResolver().update(uri, contentValues, null, null);
                Snackbar.make(viewHolder.itemView, R.string.favourites_deleted, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                contentValues.put(DatabaseContract.BBCLearningEnglishEntry.COLUMN_FAVOURITES, favouritesTimes);
                                getContext().getContentResolver().update(uri, contentValues, null, null);
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

    private long getFavouriteTime(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri,
                new String[]{DatabaseContract.BBCLearningEnglishEntry.COLUMN_FAVOURITES},
                null,
                null,
                null);
        long favouritesTime = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            favouritesTime = cursor.getLong(0);
            cursor.close();
        }
        return favouritesTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.custom_favourites);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bbc_content_list, container, false);
        mSwipeContainer = view.findViewById(R.id.srl_content_container);
        mSwipeContainer.setOnRefreshListener(this);
        mSwipeContainer.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.accent));

        /*Set the recycler view*/
        mBBCContentAdapter = new BBCContentAdapter(getContext(),
                new OnBBCItemClickListener(getContext()));
        RecyclerView contentRecycleView = view.findViewById(R.id.rv_content_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        contentRecycleView.setLayoutManager(manager);
        contentRecycleView.setAdapter(mBBCContentAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        contentRecycleView.addItemDecoration(dividerItemDecoration);

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
