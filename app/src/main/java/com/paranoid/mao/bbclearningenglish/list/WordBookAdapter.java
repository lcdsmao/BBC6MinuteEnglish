package com.paranoid.mao.bbclearningenglish.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;

/**
 * Created by Paranoid on 17/9/10.
 */

public class WordBookAdapter extends RecyclerView.Adapter<WordBookAdapter.VocabularyViewHolder> {

    public static final String[] PROJECTION = {
            DatabaseContract.VocabularyEntry.COLUMN_VOCAB,
            DatabaseContract.VocabularyEntry._ID
    };

    private static final int VOCAB_INDEX = 0;
    private static final int ID_INDEX = 1;

    private Context mContext;
    private Cursor mCursor;

    private OnListItemClickListener mClickListener;

    public WordBookAdapter(Context context, OnListItemClickListener listener) {
        mContext = context;
        mClickListener = listener;
    }

    public interface OnListItemClickListener{
        void onClickItem(String word);
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

        long ID = mCursor.getInt(ID_INDEX);
        holder.itemView.setTag(ID);
    }

    @Override
    public int getItemCount() {
        return mCursor == null? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public class VocabularyViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        TextView mVocabularyTextView;

        public VocabularyViewHolder(View itemView) {
            super(itemView);
            mVocabularyTextView = itemView.findViewById(R.id.tv_vocabulary);
            ImageView search = itemView.findViewById(R.id.iv_search);
            search.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String word = mVocabularyTextView.getText().toString();
            mClickListener.onClickItem(word);
        }
    }
}
