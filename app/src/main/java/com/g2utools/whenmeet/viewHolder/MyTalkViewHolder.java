package com.g2utools.whenmeet.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.model.TalkItemData;

/**
 * Created by mlyg2 on 2018-02-12.
 */

public class MyTalkViewHolder extends TalkViewHolder {

    public TextView talk;
    public TextView date;
    public View itemView;

    public MyTalkViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        date = itemView.findViewById(R.id.date);
        talk = itemView.findViewById(R.id.talk);
    }

    public void dataBind(TalkItemData data){
        talk.setText(data.talk);
        date.setText(data.getStringDate());
    }
}
