package com.g2utools.whenmeet.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.activity.account.ProfileSetActivity;
import com.g2utools.whenmeet.adapter.BaseListAdapter;
import com.g2utools.whenmeet.dialog.ProgressDialog;
import com.g2utools.whenmeet.dialog.TextDialog;
import com.g2utools.whenmeet.viewHolder.SimpleTitleViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment {

    private RecyclerView mListView;
    private LinearLayoutManager mLinearLayoutMgr;
    private BaseListAdapter<SettingItem,SimpleTitleViewHolder> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewLayout = inflater.inflate(R.layout.fragment_setting, container, false);
        mListView = viewLayout.findViewById(R.id.listview);
        mAdapter = new BaseListAdapter<SettingItem, SimpleTitleViewHolder>(R.layout.recycler_simple_title,SimpleTitleViewHolder.class) {
            @Override
            public void dataConvertViewHolder(SimpleTitleViewHolder holder, SettingItem data) {
                holder.title.setText(data.content);
                holder.itemView.setOnClickListener(data.onClickListener);
            }
        };

        pushItem("닉네임", "닉넴임 : ", null);
        pushItem("프로필 수정", "프로필 수정", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileSetActivity.class);
                getActivity().startActivity(intent);
            }
        });
//        pushItem("내 시간표", "내 시간표", new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        pushItem("로그아웃", "로그아웃", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("정말로 로그아웃하시겠습니까?")
                        .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                getActivity().recreate();
                            }
                        }).create().show();
            }
        });
        pushItem("이메일", "이메일 : email (인증안됨) - 인증 메세지 보내기", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                    new AlertDialog.Builder(getContext())
                            .setTitle("이메일 인증 메일 발송")
                            .setMessage("이메일 인증 메일을 발송할까요?")
                            .setPositiveButton("발송", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                    Toast.makeText(getContext(), "이메일 인증 메일을 발송하였습니다.", Toast.LENGTH_SHORT).show();
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
            }
        });
        pushItem("이메일 변경", "이메일 변경", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("이메일을 변경")
                        .setMessage("이메일을 변경하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new TextDialog(getContext(), "새로운 이메일 입력", new TextDialog.OnConfirm() {
                                    @Override
                                    public void onConfirm(String text) {
                                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                                        progressDialog.setTitle("이메일 업데이트 중...");
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();
                                        FirebaseAuth.getInstance().getCurrentUser().updateEmail(text).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.dismiss();
                                                if(!task.isSuccessful()){
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(getContext(), "이메일 업데이트를 실패하였습니다. \n" + message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }).show();
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
            }
        });
        pushItem("비밀번호 변경", "비밀번호 변경", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("비밀번호 재메일 발송")
                        .setMessage("비밀번호 재설정 메일을 발송할까요?")
                        .setPositiveButton("발송", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                FirebaseAuth.getInstance().sendPasswordResetEmail(email);
                                Toast.makeText(getContext(), "비밀번호 재설정 메일을 발송 하였습니다.", Toast.LENGTH_SHORT).show();
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
            }
        });
        pushItem("계정 탈퇴", "계정 탈퇴", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mLinearLayoutMgr = new LinearLayoutManager(getContext());
        mListView.setLayoutManager(mLinearLayoutMgr);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return viewLayout;
    }

    private void pushItem(String id, String content, View.OnClickListener onClickListener){
        mAdapter.dataList.add(new SettingItem(id,content,onClickListener));
    }

    private void setContent(String id, String content){
        for(int i = 0; mAdapter.dataList.size() > i; i++){
            SettingItem item = mAdapter.dataList.get(i);
            if(item.id.equals(id)){
                item.content = content;
                mAdapter.notifyItemChanged(i);
                return;
            }
        }
    }

    class SettingItem{
        public String id;
        public String content;
        public View.OnClickListener onClickListener;
        public SettingItem(String id, String content, View.OnClickListener onClickListener){
            this.id = id;
            this.content = content;
            this.onClickListener = onClickListener;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    String nickname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    boolean emailVerified = FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();
                    setContent("닉네임", "닉네임 : " + nickname);
                    setContent("이메일", "이메일 : " + email + (emailVerified?" (인증됨)":" (인증안됨) - 인증메시지 보내기"));
                }
            });
        }
    }
}
