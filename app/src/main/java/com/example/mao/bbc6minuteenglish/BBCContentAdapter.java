package com.example.mao.bbc6minuteenglish;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCContentAdapter extends
        RecyclerView.Adapter<BBCContentAdapter.BBCContentAdapterViewHolder> {

    @Override
    public BBCContentAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int list_item_id = R.layout.content_list_item;

        View view = LayoutInflater.from(context).inflate(list_item_id, parent, false);
        return new BBCContentAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BBCContentAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * ViewHolder for BBC content adapter
     */
    public class BBCContentAdapterViewHolder extends RecyclerView.ViewHolder {

        public BBCContentAdapterViewHolder (View view) {
            super(view);
        }
    }
}
