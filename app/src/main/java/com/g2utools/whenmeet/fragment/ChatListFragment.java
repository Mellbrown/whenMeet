package com.g2utools.whenmeet.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.activity.main.ChatRoomActivity;
import com.g2utools.whenmeet.activity.main.SelectPeopleActivity;
import com.g2utools.whenmeet.adapter.BaseListAdapter;
import com.g2utools.whenmeet.dialog.ProgressDialog;
import com.g2utools.whenmeet.model.ChatItemData;
import com.g2utools.whenmeet.model.PeopleItemData;
import com.g2utools.whenmeet.model.TalkItemData;
import com.g2utools.whenmeet.util.EventChain;
import com.g2utools.whenmeet.viewHolder.ChatViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;


public class ChatListFragment extends Fragment implements ValueEventListener {
    private static final int REQ_INVITE_FRIEND = 100;

    private FloatingActionButton mAddChatButton;
    private RecyclerView mChatListView;

    private LinearLayoutManager mRecLayoutMgr;
    private BaseListAdapter<ChatItemData,ChatViewHolder> mChatListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_chat_list, container, false);

        //추가 버튼 리스너 연결
        mAddChatButton = layoutView.findViewById(R.id.add_chat);
        mAddChatButton.setOnClickListener(onClickAddChatListener); //아래에서 멤버로 만들어 가져왔음
        //리스트뷰 설정
        mChatListView = layoutView.findViewById(R.id.recycler);
        mRecLayoutMgr = new LinearLayoutManager(getActivity());
        mChatListAdapter = new BaseListAdapter<ChatItemData, ChatViewHolder>(R.layout.recycler_chat, ChatViewHolder.class) {
            @Override
            public void onCreateAfterViewHolder(final ChatViewHolder holder) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 아이템 클릭
                        int pos = holder.getAdapterPosition();
                        ChatItemData data = mChatListAdapter.dataList.get(pos);
                        String refID = data.refChatRoom;
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                        intent.putExtra(ChatRoomActivity.P_REF_CHAT_ITEM,"private/"+uid+"/chat/"+refID);
                        intent.putExtra(ChatRoomActivity.P_REF_CHAT_ROOM,"chatroom/"+refID);
                        getActivity().startActivity(intent);
                    }
                });
            }

            @Override
            public void dataConvertViewHolder(ChatViewHolder holder, ChatItemData data) {
                holder.dataBind(data);
            }
        };
        mChatListView.setLayoutManager(mRecLayoutMgr);
        mChatListView.setAdapter(mChatListAdapter);

        return layoutView;
    }

    private View.OnClickListener onClickAddChatListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //친구 찾기 액티비티 실행
            Intent intent = new Intent(getActivity(), SelectPeopleActivity.class);
            intent.putExtra(SelectPeopleActivity.P_TITLE,"친구 초대");
            //내 친구 목록 넘기기
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            intent.putExtra(SelectPeopleActivity.P_REF_ORIGIN,"private/" + uid + "/people");
            intent.putExtra(SelectPeopleActivity.P_HIDDEN_MODE,false);
            getActivity().startActivityForResult(intent,REQ_INVITE_FRIEND);
        }
    };

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        Log.d("onActivityResult","requestCode:" + requestCode + ", resultCode:" + resultCode);
        switch (requestCode){
            case REQ_INVITE_FRIEND:{
                switch (resultCode){
                    case SelectPeopleActivity.RES_SUCCESS:{
                        final Set<PeopleItemData> resultData = (Set<PeopleItemData>) data.getSerializableExtra(SelectPeopleActivity.R_SELECTED);
                        final ArrayList<PeopleItemData> peopleItemData = new ArrayList<>(resultData);
                        if(peopleItemData.size() < 1){
                            return;
                        }
                        //사전 데이터 준비
                        String message = FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "님이 ";
                        final Long time = new Date().getTime();
                        for(PeopleItemData p : peopleItemData){
                            message += p.nickname + "님 ";
                        }message += "을 초대하였습니다.";
                        final EventChain eventChain = new EventChain();
                        eventChain.ready("invite");
                        eventChain.ready("message");
                        eventChain.ready("notice");

                        //대화상대 초대
                        final DatabaseReference chatroom = FirebaseDatabase.getInstance().getReference("chatroom").push();
                        chatroom.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                for(PeopleItemData p : peopleItemData){
                                    mutableData.child("attender/" + p.uid).setValue(p);
                                }
                                PeopleItemData me = new PeopleItemData();
                                me.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                me.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                me.nickname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                mutableData.child("attender/" + me.uid).setValue(me);
                                return Transaction.success(mutableData);
                            }
                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                eventChain.complete("invite");
                            }
                        });
                        //메시지 푸시
                        TalkItemData talkItemData = new TalkItemData();
                        talkItemData.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        talkItemData.nickname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        talkItemData.talk = message;
                        talkItemData.timestmap = time;
                        chatroom.child("talkes").push().setValue(talkItemData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                eventChain.complete("message");
                            }
                        });
                        //상대 알림 푸시
                        final String finalMessage = message;
                        FirebaseDatabase.getInstance().getReference("private").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                //개인 톡 아이템
                                ChatItemData chatItemData = new ChatItemData();
                                if(peopleItemData.size() == 1)
                                    chatItemData.title = peopleItemData.get(0).nickname + "님과 함께";
                                else
                                    chatItemData.title = peopleItemData.get(0).nickname + "님과 그 외 " + peopleItemData.size() + "명과 함께";
                                chatItemData.chatting = finalMessage;
                                chatItemData.noticeCount = 1l;
                                chatItemData.memberCount = peopleItemData.size() + 1L;
                                chatItemData.timestamp = time;
                                chatItemData.refChatRoom = chatroom.getKey();
                                for(PeopleItemData p : peopleItemData){
                                    mutableData.child(p.uid).child("chat").child(chatItemData.refChatRoom).setValue(chatItemData);
                                }
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                mutableData.child(uid).child("chat").child(chatItemData.refChatRoom).setValue(chatItemData);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                eventChain.complete("notice");
                            }
                        });
                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        eventChain.andthen(new EventChain.CallBack() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Intent intent = new Intent(getActivity(),ChatRoomActivity.class);
                                String refChatItem = "private/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/chat/" + chatroom.getKey();
                                intent.putExtra(ChatRoomActivity.P_REF_CHAT_ITEM, refChatItem);
                                intent.putExtra(ChatRoomActivity.P_REF_CHAT_ROOM, "chatroom/"+chatroom.getKey());
                                getActivity().startActivity(intent);
                            }
                        },"invite","message","notice");
                    }break;
                    case SelectPeopleActivity.RES_NOTING:{

                    }break;
                }
            }break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            mChatListAdapter.dataList.clear();
            mChatListAdapter.notifyDataSetChanged();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase
                .getInstance()
                .getReference("private")
                .child(uid)
                .child("chat")
                .addValueEventListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            mChatListAdapter.dataList.clear();
            mChatListAdapter.notifyDataSetChanged();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase
                .getInstance()
                .getReference("private")
                .child(uid)
                .child("chat")
                .removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        mChatListAdapter.dataList.clear();
        for(DataSnapshot o : dataSnapshot.getChildren()){
            ChatItemData chatItemData = o.getValue(ChatItemData.class);
            mChatListAdapter.dataList.add(chatItemData);
        }
        Collections.sort(mChatListAdapter.dataList);
        mChatListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String message = databaseError.getMessage();
        Toast.makeText(getActivity(), "데이터를 불러올수 없습니다.\n" + message , Toast.LENGTH_SHORT).show();
    }
}
