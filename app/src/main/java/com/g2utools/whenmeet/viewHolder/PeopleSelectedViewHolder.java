package com.g2utools.whenmeet.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.model.PeopleItemData;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by mlyg2 on 2018-02-11.
 */

public class PeopleSelectedViewHolder extends RecyclerView.ViewHolder {

    public View itemView;
    public RoundedImageView thumbnail;
    public TextView nickname;

    public PeopleSelectedViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        thumbnail = itemView.findViewById(R.id.thumnail);
        nickname = itemView.findViewById(R.id.nickname);
    }

    public void dataBind(PeopleItemData data){
        nickname.setText(data.nickname);
//        if (data.getThumbnail() != null) {
//            thumbnail.setImageBitmap(data.getThumbnail());
//        }else{
//            thumbnail.setImageResource(R.drawable.ic_person_black_24dp);
//        }
        data.setThumbnail(itemView.getContext(),thumbnail);
    }
}
