package com.g2utools.whenmeet.util;

import com.g2utools.whenmeet.model.ChatItemData;
import com.g2utools.whenmeet.model.PeopleItemData;
import com.g2utools.whenmeet.model.TalkItemData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by mlyg2 on 2018-03-09.
 */

public class MessageSender implements Serializable {

    private String mChatRoomID;
    private String mRefChatRoom;
    private Map<String, PeopleItemData> mAttender;

    public MessageSender(String chatRoomID, String refChatRoom, Map<String, PeopleItemData> attender){
        mChatRoomID = chatRoomID;
        mRefChatRoom = refChatRoom;
        mAttender = attender;
    }

    public void SendMessage(final String message){
        final long time = new Date().getTime();

        //톡방에 톡 넣기
        TalkItemData data = new TalkItemData();
        data.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        data.nickname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        data.talk = message;
        data.timestmap = time;

        FirebaseDatabase.getInstance().getReference(mRefChatRoom+"/talkes").push().setValue(data);
        //상대방 알림 넣어주기
        FirebaseDatabase.getInstance().getReference("private").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                for (String uid : mAttender.keySet()){
                    MutableData chat = mutableData.child(uid).child("chat").child(mChatRoomID);
                    ChatItemData o = chat.getValue(ChatItemData.class);
                    if( o == null){
                        return Transaction.success(mutableData);
                    }
                    o.chatting = FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + " : " + message;
                    o.noticeCount++;
                    o.timestamp = time;
                    chat.setValue(o);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}
