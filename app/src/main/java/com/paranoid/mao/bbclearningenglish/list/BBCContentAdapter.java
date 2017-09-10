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
import com.squareup.picasso.Picasso;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCContentAdapter extends
        RecyclerView.Adapter<BBCContentAdapter.BBCContentAdapterViewHolder> {

    // Projection for Showing data
    public static final String[] PROJECTION = {
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_TITLE,
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_TIME,
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_DESCRIPTION,
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_TIMESTAMP,
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_THUMBNAIL_HREF,
            DatabaseContract.BBCLearningEnglishEntry.COLUMN_CATEGORY
    };

    public static final int TITLE_INDEX = 0;
    public static final int TIME_INDEX = 1;
    public static final int DESCRIPTION_INDEX = 2;
    public static final int TIMESTAMP_INDEX = 3;
    public static final int THUMBNAIL_INDEX = 4;
    public static final int CATEGORY_INDEX = 5;

    private Cursor mCursor;
    private Context mContext;
    private OnListItemClickListener mOnClickListener;

    public BBCContentAdapter(Context context, OnListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    public interface OnListItemClickListener{
        void onClickItem(String path);
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

        // Set cursor to position
        mCursor.moveToPosition(position);

        // Title set
        holder.mTitleTextView.setText(mCursor.getString(TITLE_INDEX));

        // Time set
        holder.mTimeTextView.setText(mCursor.getString(TIME_INDEX));

        // Description set
        holder.mDescriptionTextView.setText(mCursor.getString(DESCRIPTION_INDEX));

        // Use picasso to load image
        Picasso.with(mContext)
                .load(mCursor.getString(THUMBNAIL_INDEX))
                .resizeDimen(R.dimen.list_item_img_width, R.dimen.list_item_img_height)
                .centerCrop()
                .placeholder(R.drawable.image_place_holder)
                .into(holder.mThumbnailImageView);

        String tag = mCursor.getString(TIMESTAMP_INDEX)
                + "/"
                + DatabaseContract.PATH_CATEGORY
                + "/"
                + mCursor.getString(CATEGORY_INDEX);
        holder.itemView.setTag(tag);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for BBC content adapter
     */
    public class BBCContentAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private ImageView mThumbnailImageView;
        private TextView mTitleTextView;
        private TextView mTimeTextView;
        private TextView mDescriptionTextView;

        public BBCContentAdapterViewHolder (View view) {
            super(view);
            mThumbnailImageView = (ImageView) view.findViewById(R.id.iv_thumbnail);
            mTitleTextView = (TextView) view.findViewById(R.id.tv_title);
            mTimeTextView = (TextView) view.findViewById(R.id.tv_time);
            mDescriptionTextView = (TextView) view.findViewById(R.id.tv_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mCursor.moveToPosition(position);
            String path = mCursor.getString(TIMESTAMP_INDEX)
                    + "/"
                    + DatabaseContract.PATH_CATEGORY
                    + "/"
                    + mCursor.getString(CATEGORY_INDEX);
            mOnClickListener.onClickItem(path);
        }
    }
}
