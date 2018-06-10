package com.g2utools.whenmeet.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.g2utools.whenmeet.util.NullCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by mlyg2 on 2018-02-10.
 */

@IgnoreExtraProperties
public class PeopleItemData implements Comparable<PeopleItemData>, Serializable{


    public String uid;
    public String nickname;
    public String email;


    public PeopleItemData(){}

    @Exclude
    public void setThumbnail(Context context, ImageView taget){
        Glide.with(context).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReference("profiles/"+uid +".jpg")).into(taget);
    }

    @Override
    public boolean equals(@NonNull Object obj) {
        if(obj instanceof PeopleItemData){
            PeopleItemData data = (PeopleItemData) obj;
            return NullCheck.equal(nickname,data.nickname) &&
                    NullCheck.equal(email,data.email) &&
                    NullCheck.equal(uid, data.uid);
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull PeopleItemData data) {
        int com = NullCheck.comapareTo(nickname,data.nickname);
        if(com == 0){
            int com2 = NullCheck.comapareTo(email, data.email);
            if(com2 == 0){
                return NullCheck.comapareTo(uid,data.uid);
            }
            return com2;
        }
        return com;
    }
}
