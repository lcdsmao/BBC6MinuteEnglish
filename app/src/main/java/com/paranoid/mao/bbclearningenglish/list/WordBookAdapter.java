package com.paranoid.mao.bbclearningenglish.list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.data.VocabularyDefinition;
import com.paranoid.mao.bbclearningenglish.singleton.MyApp;
import com.paranoid.mao.bbclearningenglish.sync.WordReferenceRequest;
import com.paranoid.mao.bbclearningenglish.utilities.ExpansionAnimatorUtility;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private Context mContext;
    private Cursor mCursor;
    private Set<Integer> mExpendedSet;

    private OnItemClickListener mListener;

    public WordBookAdapter(Context context, OnItemClickListener listener) {
        mContext = context;
        mListener = listener;
        mExpendedSet = new HashSet<>();
    }

    interface OnItemClickListener{
        void OnPronunciationClick(String audioHref);
        void OnDetailClick(long id);
    }

    @Override
    public VocabularyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.vocabulary_list_item, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VocabularyViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        final long id = mCursor.getInt(ID_INDEX);
        holder.itemView.setTag(id);

        holder.mVocabularyTextView.setText(mCursor.getString(VOCAB_INDEX));
        holder.mSymbolTextView.setText(mCursor.getString(SYMBOL_INDEX));
        holder.mDefinitionTextView.setText(mCursor.getString(MEAN_INDEX));
        holder.mAudioHref = mCursor.getString(AUDIO_HREF_INDEX);
        holder.mProgressBar.setVisibility(
                TextUtils.isEmpty(mCursor.getString(MEAN_INDEX))? View.VISIBLE:View.GONE);
        holder.mPronunciationImageView.setVisibility(
                TextUtils.isEmpty(holder.mAudioHref)? View.GONE:View.VISIBLE);

        holder.mVocabularyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curPosition = holder.getAdapterPosition();
                if (mExpendedSet.contains(curPosition)) {
                    holder.itemView.setActivated(false);
                    ExpansionAnimatorUtility.animateClose(holder.mDetailView);
                    mExpendedSet.remove(curPosition);
                } else {
                    holder.itemView.setActivated(true);
                    ExpansionAnimatorUtility.animateOpen(holder.mDetailView);
                    mExpendedSet.add(curPosition);
                }
                mListener.OnDetailClick(id);
            }
        });

        boolean isExpended = mExpendedSet.contains(position);
        holder.mDetailView.setVisibility(isExpended? View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpended);
    }

    @Override
    public int getItemCount() {
        return mCursor == null? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    class VocabularyViewHolder extends RecyclerView.ViewHolder {

        TextView mVocabularyTextView;
        ViewGroup mDetailView;
        TextView mSymbolTextView;
        TextView mDefinitionTextView;
        ProgressBar mProgressBar;
        ImageView mPronunciationImageView;
        String mAudioHref;

        VocabularyViewHolder(View itemView) {
            super(itemView);
            mVocabularyTextView = itemView.findViewById(R.id.tv_vocabulary);
            mDetailView = itemView.findViewById(R.id.detail_container);
            mSymbolTextView = itemView.findViewById(R.id.tv_detail_symbol);
            mDefinitionTextView = itemView.findViewById(R.id.tv_detail_definition);
            mProgressBar = itemView.findViewById(R.id.pb_detail);
            mPronunciationImageView = itemView.findViewById(R.id.iv_detail_pronunciation);

            mPronunciationImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnPronunciationClick(mAudioHref);
                }
            });
        }
    }
}
