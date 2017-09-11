package com.paranoid.mao.bbclearningenglish.list;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.data.BBCPreference;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.sync.BBCSyncUtility;

public class BBCContentFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener{

    private static final String KEY_CATEGORY = "category";
    private static final int BBC_CONTENT_LOADER_ID = 1;

    private String mCategory;

    private BBCContentAdapter mBBCContentAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mContentRecycleView;

    public BBCContentFragment() {
        // Required empty public constructor
    }

    public static BBCContentFragment newInstance(String category) {
        BBCContentFragment fragment = new BBCContentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(KEY_CATEGORY);
        }
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
        mContentRecycleView = (RecyclerView) view.findViewById(R.id.rv_content_list);
        mContentRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentRecycleView.setAdapter(mBBCContentAdapter);
        /*Set the recycler view complete*/

        getLoaderManager().initLoader(BBC_CONTENT_LOADER_ID, new Bundle(), this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSwipeContainer.setRefreshing(true);
        Uri uri = DatabaseContract.BBCLearningEnglishEntry.CONTENT_CATEGORY_URI.buildUpon()
                .appendPath(mCategory).build();
        if (BBCPreference.isUpdateNeed(getContext(), mCategory)) {
            BBCSyncUtility.contentListSync(getContext(), mCategory);
        }
        String sortOrder = DatabaseContract.BBCLearningEnglishEntry.NORMAL_SORT_ORDER;
        return new CursorLoader(
                getContext(),
                uri,
                BBCContentAdapter.PROJECTION,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mBBCContentAdapter.swapCursor(data);
        getActivity().setTitle(BBCCategory.sCategoryStringResourceMap.get(mCategory));
        if (BBCSyncUtility.sIsContentListSyncComplete) {
            mSwipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBBCContentAdapter.swapCursor(null);
    }

    @Override
    public void onRefresh() {
        BBCSyncUtility.contentListSync(getContext(), mCategory);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!BBCSyncUtility.sIsContentListSyncComplete) {
            mSwipeContainer.setRefreshing(true);
        }
    }

    public void swapCategory(String category) {
        mCategory = category;
        getLoaderManager().restartLoader(BBC_CONTENT_LOADER_ID, null, this);
        mContentRecycleView.scrollToPosition(0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CATEGORY, mCategory);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null ) {
            mCategory = savedInstanceState.getString(KEY_CATEGORY);
        }
    }
}
