package com.g2utools.whenmeet.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.g2utools.whenmeet.util.NullCheck;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mlyg2 on 2018-02-12.
 */

@IgnoreExtraProperties
public class TalkItemData implements Comparable<TalkItemData>{
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yy/MM/dd hh:mm");

    public String uid;
    public String nickname;
    public String talk;
    public Long timestmap;

    public TalkItemData(){ }

    @Exclude
    public String getStringDate(){
        return mDateFormat.format(new Date(timestmap));
    }
    @Exclude
    public String getStringDate(String pattern){
        return new SimpleDateFormat(pattern).format(new Date(timestmap));
    }
    @Exclude
    public Date getDate(){
        return new Date(timestmap);
    }
    @Exclude
    public void setThumbnail(Context context, ImageView taget){
        Glide.with(context).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReference("profiles/"+uid +".jpg")).into(taget);
    }


    @Override
    public boolean equals(@NonNull Object obj) {
        if(obj instanceof TalkItemData){
            TalkItemData data = ((TalkItemData) obj);
            return NullCheck.equal(uid, data.uid) &&
                    NullCheck.equal(nickname, data.nickname) &&
                    NullCheck.equal(talk, data.talk) &&
                    NullCheck.equal(timestmap, data.timestmap);
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull TalkItemData talkItemData) {
        return timestmap.compareTo(talkItemData.timestmap);
    }
}
