package com.paranoid.mao.bbclearningenglish.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

import java.util.List;

import static com.paranoid.mao.bbclearningenglish.utilities.AnimatorUtility.getFastOutSlowInInterpolator;

/**
 * Created by Paranoid on 17/9/10.
 */

public class WordBookAdapter extends RecyclerView.Adapter<WordBookAdapter.VocabularyViewHolder> {

    public static final String[] PROJECTION = {
            DatabaseContract.VocabularyEntry.COLUMN_VOCAB,
            DatabaseContract.VocabularyEntry._ID,
            DatabaseContract.VocabularyEntry.COLUMN_MEAN,
            DatabaseContract.VocabularyEntry.COLUMN_SYMBOL,
            DatabaseContract.VocabularyEntry.COLUMN_AUDIO_HREF
    };

    private static final int VOCAB_INDEX = 0;
    private static final int ID_INDEX = 1;
    private static final int MEAN_INDEX = 2;
    private static final int SYMBOL_INDEX = 3;
    private static final int AUDIO_HREF_INDEX = 4;

    private static final int EXPAND = 0x1;
    private static final int COLLAPSE = 0x2;

    private Context mContext;
    private Cursor mCursor;
    private RecyclerView mWordList;
    private int mExpandedPosition = RecyclerView.NO_POSITION;
    private final Transition mExpandCollapse;


    private OnItemClickListener mListener;

    public WordBookAdapter(Context context,
                           RecyclerView wordList,
                           OnItemClickListener listener) {
        mContext = context;
        mListener = listener;
        mWordList = wordList;

        mExpandCollapse = new AutoTransition();
        mExpandCollapse.setDuration(120);
        mExpandCollapse.setInterpolator(getFastOutSlowInInterpolator(mContext));
    }

    interface OnItemClickListener {
        void OnPronunciationClick(String audioHref);
        void OnDetailClick(long id);
    }

    @Override
    public VocabularyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VocabularyViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.vocabulary_list_item, parent, false));
    }

    @Override
    public void onViewDetachedFromWindow(VocabularyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onBindViewHolder(final VocabularyViewHolder holder, int position) {

        boolean isExpanded = position == mExpandedPosition;

        mCursor.moveToPosition(position);
        final long id = mCursor.getInt(ID_INDEX);
        holder.itemView.setTag(id);

        holder.mVocabularyTextView.setText(mCursor.getString(VOCAB_INDEX));
        holder.mSymbolTextView.setText(mCursor.getString(SYMBOL_INDEX));
        holder.mDefinitionTextView.setText(mCursor.getString(MEAN_INDEX));
        holder.mAudioHref = mCursor.getString(AUDIO_HREF_INDEX);

        // set visibility
        holder.itemView.setActivated(isExpanded);
        holder.mProgressBar.setVisibility(
                TextUtils.isEmpty(mCursor.getString(MEAN_INDEX)) && isExpanded ? View.VISIBLE : View.GONE);
        holder.mSymbolTextView.setVisibility(
                !TextUtils.isEmpty(mCursor.getString(SYMBOL_INDEX)) && isExpanded? View.VISIBLE : View.GONE);
        holder.mDefinitionTextView.setVisibility(
                !TextUtils.isEmpty(mCursor.getString(MEAN_INDEX)) && isExpanded? View.VISIBLE : View.GONE);

        holder.mVocabularyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                TransitionManager.beginDelayedTransition(mWordList, mExpandCollapse);

                // collapse currently expanded items
                if (RecyclerView.NO_POSITION != mExpandedPosition) {
                    notifyItemChanged(mExpandedPosition, COLLAPSE);
                }

                // expand this item
                if (mExpandedPosition != position) {
                    mExpandedPosition = position;
                    notifyItemChanged(position, EXPAND);
                } else {
                    mExpandedPosition = RecyclerView.NO_POSITION;
                }

                mListener.OnDetailClick(id);
            }
        });
    }

    @Override
    public void onBindViewHolder(VocabularyViewHolder holder, int position, List<Object> payloads) {
        if (payloads.contains(COLLAPSE) || payloads.contains(EXPAND)) {
            boolean isExpanded = position == mExpandedPosition;
            mCursor.moveToPosition(position);
            // set visibility
            holder.itemView.setActivated(isExpanded);
            holder.mProgressBar.setVisibility(
                    TextUtils.isEmpty(mCursor.getString(MEAN_INDEX)) && isExpanded ? View.VISIBLE : View.GONE);
            holder.mSymbolTextView.setVisibility(
                    !TextUtils.isEmpty(mCursor.getString(SYMBOL_INDEX)) && isExpanded? View.VISIBLE : View.GONE);
            holder.mDefinitionTextView.setVisibility(
                    !TextUtils.isEmpty(mCursor.getString(MEAN_INDEX)) && isExpanded? View.VISIBLE : View.GONE);
        } else {
            onBindViewHolder(holder, position);
        }
    }

    public void clearDeletedExpandedPosition(int deletedPosition) {
        if (mExpandedPosition == deletedPosition) {
            mExpandedPosition = RecyclerView.NO_POSITION;
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    class VocabularyViewHolder extends RecyclerView.ViewHolder {

        TextView mVocabularyTextView;
        TextView mSymbolTextView;
        TextView mDefinitionTextView;
        ViewGroup mVocabularyContainer;
        ProgressBar mProgressBar;
        String mAudioHref;

        VocabularyViewHolder(View itemView) {
            super(itemView);
            mVocabularyTextView = itemView.findViewById(R.id.tv_vocabulary);
            mSymbolTextView = itemView.findViewById(R.id.tv_detail_symbol);
            mDefinitionTextView = itemView.findViewById(R.id.tv_detail_definition);
            mProgressBar = itemView.findViewById(R.id.pb_detail);
            mVocabularyContainer = itemView.findViewById(R.id.vocabulary_container);

            mSymbolTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnPronunciationClick(mAudioHref);
                }
            });
        }
    }
}
