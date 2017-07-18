package com.example.mao.bbc6minuteenglish;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.data.BBCContentContract.BBC6MinuteEnglishEntry;
import com.example.mao.bbc6minuteenglish.utilities.DbBitmapUtility;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCContentAdapter extends
        RecyclerView.Adapter<BBCContentAdapter.BBCContentAdapterViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public BBCContentAdapter(Context context) {
        mContext = context;
    }

    @Override
    public BBCContentAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int list_item_id = R.layout.content_list_item;

        View view = LayoutInflater.from(context).inflate(list_item_id, parent, false);
        return new BBCContentAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BBCContentAdapterViewHolder holder, int position) {

        int titleIndex = mCursor.getColumnIndex(BBC6MinuteEnglishEntry.COLUMN_TITLE);
        int timeIndex = mCursor.getColumnIndex(BBC6MinuteEnglishEntry.COLUMN_TIME);
        int desIndex = mCursor.getColumnIndex(BBC6MinuteEnglishEntry.COLUMN_DESCRIPTION);
        int imgIndex = mCursor.getColumnIndex(BBC6MinuteEnglishEntry.COLUMN_THUMBNAIL);

        // Set cursor to position
        mCursor.moveToPosition(position);

        // Title set
        holder.mTitleTextView.setText(mCursor.getString(titleIndex));

        // Time set
        holder.mTimeTextView.setText(mCursor.getString(timeIndex));

        // Description set
        holder.mDescriptionTextView.setText(mCursor.getString(desIndex));

        // TODO: Thumbnail Set
        // For test, use local img
        Bitmap thumbnail = DbBitmapUtility.getImage(mCursor.getBlob(imgIndex));
        holder.mThumbnailImageView.setImageBitmap(thumbnail);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    /**
     * ViewHolder for BBC content adapter
     */
    public class BBCContentAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView mThumbnailImageView;
        TextView mTitleTextView;
        TextView mTimeTextView;
        TextView mDescriptionTextView;

        public BBCContentAdapterViewHolder (View view) {
            super(view);
            mThumbnailImageView = (ImageView) view.findViewById(R.id.iv_thumbnail);
            mTitleTextView = (TextView) view.findViewById(R.id.tv_title);
            mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
            mDescriptionTextView = (TextView) view.findViewById(R.id.tv_description);
        }
    }
}
