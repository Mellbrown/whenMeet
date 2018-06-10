package com.g2utools.whenmeet.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mlyg2 on 2018-02-11.
 */

@IgnoreExtraProperties
public class ChatItemData implements Comparable<ChatItemData>, Serializable {

    public String title;
    public String chatting;
    public Long noticeCount;
    public Long memberCount;
    public Long timestamp;

    public String refChatRoom;

    public ChatItemData(){}

    @Exclude
    public String getDate() {
        return new SimpleDateFormat("yy/MM/dd hh:mm").format(new Date(timestamp));
    }

    @Override
    public int compareTo(@NonNull ChatItemData data) {
        return timestamp.compareTo(data.timestamp);
    }
}
