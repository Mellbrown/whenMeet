package com.g2utools.whenmeet.viewHolder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.model.ChatItemData;

/**
 * Created by mlyg2 on 2018-02-11.
 */

public class ChatViewHolder extends RecyclerView.ViewHolder{

    public TextView title;
    public TextView date;
    public TextView chatting;
    public TextView noticeCount;
    public TextView memeberCount;
    public CardView noticeContainer;

    public ChatViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);
        chatting = itemView.findViewById(R.id.chatting);
        noticeCount = itemView.findViewById(R.id.notice);
        memeberCount = itemView.findViewById(R.id.member);
        noticeContainer = itemView.findViewById(R.id.notice_container);
    }
    
    public void dataBind(ChatItemData data){
        title.setText(data.title);
        date.setText(data.getDate());
        chatting.setText(data.chatting);
        noticeCount.setText(data.noticeCount.toString());
        memeberCount.setText(data.memberCount.toString());
        if(data.noticeCount == 0){
            noticeContainer.setVisibility(View.INVISIBLE);
        }else{
            noticeContainer.setVisibility(View.VISIBLE);
        }
    }
}
