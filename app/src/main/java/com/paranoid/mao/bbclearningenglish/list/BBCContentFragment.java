package com.paranoid.mao.bbclearningenglish.list;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.BBCCategory;
import com.paranoid.mao.bbclearningenglish.data.BBCPreference;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.sync.SyncUtility;
import com.paranoid.mao.bbclearningenglish.utilities.NetworkUtility;

import java.lang.ref.WeakReference;

public class BBCContentFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_CATEGORY = "category";
    private static final int BBC_CONTENT_LOADER_ID = 1;

    private String mCategory;

    private BBCContentAdapter mBBCContentAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mContentRecycleView;

    private final Handler syncHandler = new SyncHandler(this);

    private static class SyncHandler extends Handler {
        private final WeakReference<BBCContentFragment> mFragment;

        SyncHandler(BBCContentFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            BBCContentFragment fragment = mFragment.get();
            if (fragment != null && fragment.isAdded()) {
                fragment.mSwipeContainer.setRefreshing(false);
                String textMsg;
                switch (msg.arg1) {
                    case 1:
                        textMsg = fragment.getString(R.string.list_sync_successful_msg);
                        if (fragment.mCategory.equals(BBCCategory.getItemIdCategory(msg.arg2))) {
                            Toast.makeText(fragment.getContext(),
                                    textMsg,
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        textMsg = fragment.getString(R.string.error_message);
                        Toast.makeText(fragment.getContext(),
                                textMsg,
                                Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

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
        mSwipeContainer = view.findViewById(R.id.srl_content_container);
        mSwipeContainer.setOnRefreshListener(this);
        mSwipeContainer.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.accent));

        /*Set the recycler view*/
        mBBCContentAdapter = new BBCContentAdapter(getContext(),
                new OnBBCItemClickListener(getContext()));
        mContentRecycleView = view.findViewById(R.id.rv_content_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mContentRecycleView.setLayoutManager(manager);
        mContentRecycleView.setAdapter(mBBCContentAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        mContentRecycleView.addItemDecoration(dividerItemDecoration);
        /*Set the recycler view complete*/

        getLoaderManager().initLoader(BBC_CONTENT_LOADER_ID, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DatabaseContract.BBCLearningEnglishEntry.CONTENT_CATEGORY_URI.buildUpon()
                .appendPath(mCategory).build();
        if (BBCPreference.isUpdateNeed(getContext(), mCategory) &&
                NetworkUtility.isConnected(getContext())) {
            mSwipeContainer.setRefreshing(true);
            SyncUtility.startContentListSyncByCategory(getContext(), mCategory, syncHandler);
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
        getActivity().setTitle(BBCCategory.getCategoryStringRecourse(mCategory));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBBCContentAdapter.swapCursor(null);
    }

    @Override
    public void onRefresh() {
        SyncUtility.startContentListSyncByCategory(getContext(), mCategory, syncHandler);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void swapCategory(String category) {
        mCategory = category;
        mSwipeContainer.setRefreshing(false);
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
        if (savedInstanceState != null) {
            mCategory = savedInstanceState.getString(KEY_CATEGORY);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
