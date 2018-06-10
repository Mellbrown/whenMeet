package com.g2utools.whenmeet.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.g2utools.whenmeet.R;

/**
 * Created by mlyg2 on 2018-02-24.
 */

public class SimpleTitleViewHolder extends RecyclerView.ViewHolder{

    public TextView title;
    public View itemView;

    public SimpleTitleViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        title = itemView.findViewById(R.id.title);
    }
}
