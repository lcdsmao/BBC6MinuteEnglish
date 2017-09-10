package com.paranoid.mao.bbclearningenglish.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

/**
 * Created by Paranoid on 17/9/10.
 */

public class WordBookAdapter extends RecyclerView.Adapter<WordBookAdapter.VocabularyViewHolder> {

    public static final String[] PROJECTION = {
            DatabaseContract.VocabularyEntry.COLUMN_VOCAB
    };

    public static final int VOCAB_INDEX = 0;

    private Context mContext;
    private Cursor mCursor;

    public WordBookAdapter(Context context) {
        mContext = context;
    }

    @Override
    public VocabularyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.vocabulary_list_item, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VocabularyViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String vocabulary = mCursor.getString(VOCAB_INDEX);

        holder.mVocabularyTextView.setText(vocabulary);
    }

    @Override
    public int getItemCount() {
        return mCursor == null? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public class VocabularyViewHolder extends RecyclerView.ViewHolder{

        TextView mVocabularyTextView;

        public VocabularyViewHolder(View itemView) {
            super(itemView);
            mVocabularyTextView = itemView.findViewById(R.id.tv_vocabulary);
        }
    }
}
