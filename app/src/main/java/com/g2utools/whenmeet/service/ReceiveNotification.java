package com.g2utools.whenmeet.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.activity.main.ChatRoomActivity;
import com.g2utools.whenmeet.model.ChatItemData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class ReceiveNotification extends Service{
    public static final String P_REF_CHAT_ITEMS = "ref chat items";
    private String refChatItems;
    public static String refReadingChatItem;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) return START_STICKY;
        refChatItems = intent.getStringExtra(P_REF_CHAT_ITEMS);
        FirebaseDatabase.getInstance().getReference(refChatItems).addChildEventListener(chatItemListener);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        FirebaseDatabase.getInstance().getReference(refChatItems).removeEventListener(chatItemListener);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ChildEventListener chatItemListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String refChatItem = refChatItems+"/" + dataSnapshot.getKey();
            if(refReadingChatItem != null && (refChatItem).equals(refReadingChatItem)) return;

            ChatItemData data = dataSnapshot.getValue(ChatItemData.class);
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Intent intent = new Intent(ReceiveNotification.this, ChatRoomActivity.class);
            intent.putExtra(ChatRoomActivity.P_REF_CHAT_ITEM,refChatItem);
            intent.putExtra(ChatRoomActivity.P_REF_CHAT_ROOM,"chatroom/"+data.refChatRoom);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(ReceiveNotification.this)
                    .setContentTitle(data.title + " (" + data.noticeCount + ")")
                    .setContentText(data.chatting)
                    .setSmallIcon(R.drawable.ic_person_black_24dp)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(0,notification.build());
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
