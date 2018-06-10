package com.g2utools.whenmeet.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.model.ChatItemData;
import com.g2utools.whenmeet.model.TalkItemData;
import com.g2utools.whenmeet.util.NullCheck;
import com.g2utools.whenmeet.util.NullDateCheck;
import com.g2utools.whenmeet.viewHolder.MyTalkViewHolder;
import com.g2utools.whenmeet.viewHolder.PeopleTalkViewHolder;
import com.g2utools.whenmeet.viewHolder.TalkViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mlyg2 on 2018-02-12.
 */

public class ChatListAdapter extends RecyclerView.Adapter<TalkViewHolder>{

    private static final int VIEWTYPE_MY = 0;
    private static final int VIEWTYPE_PEOPLE = 1;

    @NonNull
    public ArrayList<TalkItemData> dataList = new ArrayList<>();
    public String uid;

    public ChatListAdapter(@NonNull String uid){
        this.uid = uid;
    }

    @Override
    public TalkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEWTYPE_MY:{
                return new MyTalkViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_my_talk, parent, false));
            }
            case VIEWTYPE_PEOPLE:
            default: {
                return new PeopleTalkViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_people_talk, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(TalkViewHolder holder, int position) {
        TalkItemData data = dataList.get(position);
        /*이전 데이터 가져오기 코드 없으면 null*/
        TalkItemData preData;
        try { preData = dataList.get(position - 1);}
        catch (IndexOutOfBoundsException e) { preData = null; }
        /*다음 데이터 가져오기 코드 없으면 null*/
        TalkItemData nextData;
        try { nextData = dataList.get(position + 1);}
        catch (IndexOutOfBoundsException e) { nextData = null; }
        /*뷰타입에 따른 코드 분기*/
        if(holder instanceof MyTalkViewHolder){
            /*내가 쓴 톡이면*/
            MyTalkViewHolder myTalkViewHolder = (MyTalkViewHolder) holder;
            myTalkViewHolder.dataBind(data);
            /*시간 중복 생략 코드*/
            if(NullDateCheck.equalDATE(data.getDate(),preData != null ? preData.getDate() : null)){ //일단 날짜는 같아서
                boolean b = //날짜 시간 같고, 다음 사용자와 같다면 생략
                        NullDateCheck.equalTime(data.getDate(),preData != null ? preData.getDate() : null);
                //이상하게 삼항 연산자가 안먹혀?
                if(nextData == null) b = b && false; else b = b && NullCheck.equal(nextData.uid,data.uid);
                if(b){
                    myTalkViewHolder.date.setVisibility(View.GONE);
                } else { //날짜는데 시간일 다르거나 사용자가 달라 시간표시
                    myTalkViewHolder.date.setVisibility(View.VISIBLE);
                    myTalkViewHolder.date.setText(data.getStringDate("a hh:mm"));
                }
            } else { // 애시당초 날짜부터 달라서 다표시
                myTalkViewHolder.date.setVisibility(View.VISIBLE);
                myTalkViewHolder.date.setText(data.getStringDate("yy/MM/dd a hh:mm"));
            }
        }else{
            /*남이 쓴 톡이면*/
            PeopleTalkViewHolder peopleTalkViewHolder = (PeopleTalkViewHolder) holder;
            peopleTalkViewHolder.dataBind(data);
            /*프로필 중복 생략 코드*/
            if(preData != null && preData.uid.equals(data.uid)){//동일 사용자
                peopleTalkViewHolder.thumbnail.setVisibility(View.INVISIBLE);
                peopleTalkViewHolder.nickname.setVisibility(View.GONE);
            }else{//다른 사용자
                peopleTalkViewHolder.thumbnail.setVisibility(View.VISIBLE);
                peopleTalkViewHolder.nickname.setVisibility(View.VISIBLE);
            }
            /*시간 중복 생략 코드*/
            if(NullDateCheck.equalDATE(data.getDate(),preData != null ? preData.getDate() : null)){ //일단 날짜는 같아서
                boolean b = //날짜 시간 같고, 다음 사용자와 같다면 생략
                        NullDateCheck.equalTime(data.getDate(),preData != null ? preData.getDate() : null);
                //이상하게 삼항 연산자가 안먹혀?
                if(nextData == null) b = b && false; else b = b && NullCheck.equal(nextData.uid,data.uid);
                if(b){
                    peopleTalkViewHolder.date.setVisibility(View.GONE);
                } else { //날짜는데 시간일 다르거나 사용자가 달라 시간표시
                    peopleTalkViewHolder.date.setVisibility(View.VISIBLE);
                    peopleTalkViewHolder.date.setText(data.getStringDate("a hh:mm"));
                }
            } else { // 애시당초 날짜부터 달라서 다표시
                peopleTalkViewHolder.date.setVisibility(View.VISIBLE);
                peopleTalkViewHolder.date.setText(data.getStringDate("yy/MM/dd a hh:mm"));
            }
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).uid.equals(uid)) {
            return VIEWTYPE_MY;
        } else {
            return VIEWTYPE_PEOPLE;
        }
    }
}
