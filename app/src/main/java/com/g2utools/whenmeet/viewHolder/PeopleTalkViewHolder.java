package com.g2utools.whenmeet.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.model.TalkItemData;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by mlyg2 on 2018-02-12.
 */

public class PeopleTalkViewHolder extends TalkViewHolder {

    public RoundedImageView thumbnail;
    public TextView nickname;
    public TextView talk;
    public TextView date;
    public View itemView;

    public PeopleTalkViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        thumbnail = itemView.findViewById(R.id.thumnail);
        nickname = itemView.findViewById(R.id.nickname);
        talk = itemView.findViewById(R.id.talk);
        date = itemView.findViewById(R.id.date);
    }

    public void dataBind(TalkItemData data){
//        if (data.getThumbnail() != null) {
//            thumbnail.setImageBitmap(data.getThumbnail());
//        }else{
//            thumbnail.setImageResource(R.drawable.ic_person_black_24dp);
//        }
        data.setThumbnail(itemView.getContext(),thumbnail);
        nickname.setText(data.nickname);
        talk.setText(data.talk);
        date.setText(data.getStringDate());
    }
}
