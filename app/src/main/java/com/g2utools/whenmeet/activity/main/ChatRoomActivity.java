package com.g2utools.whenmeet.activity.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.service.ReceiveNotification;
import com.g2utools.whenmeet.adapter.ChatListAdapter;
import com.g2utools.whenmeet.dialog.TextDialog;
import com.g2utools.whenmeet.model.ChatItemData;
import com.g2utools.whenmeet.model.PeopleItemData;
import com.g2utools.whenmeet.model.TalkItemData;
import com.g2utools.whenmeet.activity.sheet.SheetList;
import com.g2utools.whenmeet.util.MessageSender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity {
    public static final String P_REF_CHAT_ROOM = "chat room";
    public static final String P_REF_CHAT_ITEM = "chat item";

    private EditText mMessage;
    private FloatingActionButton mSend;

    private RecyclerView mTalkListView;
    private LinearLayoutManager mTalkListLayoutMgr;
    private ChatListAdapter mChatListAdpater;

    private String uid;
    private String refChatRoom;
    private String refChatItem;
    private Map<String, PeopleItemData> mAttender = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        //파라메터 설정
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        refChatRoom = getIntent().getStringExtra(P_REF_CHAT_ROOM);
        refChatItem = getIntent().getStringExtra(P_REF_CHAT_ITEM);
        //UI 연결
        mMessage = findViewById(R.id.message);
        mSend = findViewById(R.id.send);
        mSend.setOnClickListener(onClickSendListener); //아래에 멤버 변수로 선언되어 있음
        //리스트뷰 설정
        mTalkListView = findViewById(R.id.talkes);
        mTalkListLayoutMgr = new LinearLayoutManager(this);
        mChatListAdpater = new ChatListAdapter(uid);
        mTalkListView.setLayoutManager(mTalkListLayoutMgr);
        mTalkListView.setAdapter(mChatListAdpater);
    }

    private View.OnClickListener onClickSendListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //발송 준비
            final String message = mMessage.getText().toString();
            mMessage.setText("");
            if (message.equals("")) {
                return;
            }
            //발송
            MessageSender messageSender = new MessageSender(mChatRoomID, refChatRoom, mAttender);
            messageSender.SendMessage(message);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference(refChatRoom).addValueEventListener(mChatRoomListener);
        FirebaseDatabase.getInstance().getReference(refChatItem).addValueEventListener(mChatItemListener);
        ReceiveNotification.refReadingChatItem = refChatItem;
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference(refChatRoom).removeEventListener(mChatRoomListener);
        FirebaseDatabase.getInstance().getReference(refChatItem).removeEventListener(mChatItemListener);
        ReceiveNotification.refReadingChatItem = null;
    }


    private ValueEventListener mChatRoomListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mAttender.clear();
            for(DataSnapshot o : dataSnapshot.child("attender").getChildren()){
                PeopleItemData data = o.getValue(PeopleItemData.class);
                mAttender.put(data.uid, data);
            }

            mChatListAdpater.dataList.clear();
            for(DataSnapshot o : dataSnapshot.child("talkes").getChildren()){
                TalkItemData data = o.getValue(TalkItemData.class);
                mChatListAdpater.dataList.add(data);
            }
            mChatListAdpater.notifyDataSetChanged();
            mTalkListView.scrollToPosition(mChatListAdpater.getItemCount() - 1);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(ChatRoomActivity.this, "데이터를 불러올수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    private String mChatRoomID;
    private ValueEventListener mChatItemListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ChatItemData data = dataSnapshot.getValue(ChatItemData.class);
            if (data == null) {
                return;
            }
            getSupportActionBar().setTitle(data.title);
            mChatRoomID = data.refChatRoom;
            if(data.noticeCount != 0){
                data.noticeCount = 0l;
                FirebaseDatabase.getInstance().getReference(refChatItem).setValue(data);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(ChatRoomActivity.this, "데이터를 불러올수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_room_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sheet_list:{
                Intent intent = new Intent(this, SheetList.class);
                intent.putExtra(SheetList.P_REF_SHEET_LIST,refChatRoom+"/sheets");
                intent.putExtra(SheetList.P_MSG_SENDER, new MessageSender(mChatRoomID,refChatRoom,mAttender));
                startActivity(intent);
            }break;
            case R.id.change_title:{
                new TextDialog(this, "새로운 방 이름 입력", new TextDialog.OnConfirm() {
                    @Override
                    public void onConfirm(final String text) {
                        FirebaseDatabase.getInstance().getReference(refChatItem).runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                ChatItemData o = mutableData.getValue(ChatItemData.class);
                                o.title = text;
                                mutableData.setValue(o);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
                    }
                }).show();
            }break;
            case R.id.out_room:{
                new AlertDialog.Builder(this)
                        .setTitle("방 나가기")
                        .setMessage("정말로 방을 나가시겠습니까?")
                        .setPositiveButton("나가기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String nick = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                new MessageSender(mChatRoomID, refChatRoom, mAttender)
                                        .SendMessage(nick + "님이 방을 나가셨습니다.");
                                finish();
                                FirebaseDatabase.getInstance().getReference(refChatItem).removeValue();
                                FirebaseDatabase.getInstance().getReference(refChatRoom+"/attender/" + uid).removeValue();
                                FirebaseDatabase.getInstance().getReference(refChatRoom).runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        long count = mutableData.child("attender").getChildrenCount();
                                        if(count == 0){
                                            mutableData.setValue(null);
                                        }
                                        return Transaction.success(mutableData);
                                    }
                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setCancelable(true)
                        .create()
                        .show();
            }break;
        }
        return super.onOptionsItemSelected(item);
    }
}
