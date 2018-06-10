package com.g2utools.whenmeet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.activity.main.SelectPeopleActivity;
import com.g2utools.whenmeet.adapter.BaseListAdapter;
import com.g2utools.whenmeet.dialog.ProfileDialog;
import com.g2utools.whenmeet.model.PeopleItemData;
import com.g2utools.whenmeet.viewHolder.PeopleViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
* 친구 목록이 담겨 있는 리사이클러 뷰가 있다.
* + 버튼으로 친구 추가 액티비티를 실행 시킨다.
* 검색기능은 넣지 않는다.
* 삭제는 팝업창에서 할까? ㅇㅇ 그러자
* 정렬은 닥치고 닉넴순
* */
public class PeopleListFragment extends Fragment implements ValueEventListener {
    private static final int REQ_ADD_FRIEND = 1004;

    private FloatingActionButton mAddPersonButton;
    private RecyclerView mPeopleListView;

    private LinearLayoutManager mRecLayoutMgr;
    private BaseListAdapter<PeopleItemData,PeopleViewHolder> mPeopleListAdpater;

    private RoundedImageView mThumnalie;
    private TextView mNickname;
    private TextView mEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_people_list, container, false);
        //추가 버튼 리스너 연결
        mAddPersonButton = layoutView.findViewById(R.id.add_person);
        mAddPersonButton.setOnClickListener(onClickAddPersonListener);//아래에서 멤버로 만들어 가져왔음
        //프로파일 연결
        mThumnalie = layoutView.findViewById(R.id.thumnail);
        mNickname = layoutView.findViewById(R.id.nickname);
        mEmail = layoutView.findViewById(R.id.email);
        //리스트뷰 설정
        mPeopleListView = layoutView.findViewById(R.id.recycler);
        mRecLayoutMgr = new LinearLayoutManager(getActivity());
        mPeopleListAdpater = new BaseListAdapter<PeopleItemData, PeopleViewHolder>(R.layout.recycler_people, PeopleViewHolder.class) {
            @Override
            public void onCreateAfterViewHolder(final PeopleViewHolder holder) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //아이템 클릭시 프로필 보여주기
                        PeopleItemData data = mPeopleListAdpater.dataList.get(holder.getLayoutPosition());
                        ProfileDialog profileDialog = new ProfileDialog(getActivity(),data);
                        profileDialog.show();
                    }
                });
            }

            @Override
            public void dataConvertViewHolder(PeopleViewHolder holder, PeopleItemData data) {
                holder.dataBind(data);
            }
        };
        mPeopleListView.setLayoutManager(mRecLayoutMgr);
        mPeopleListView.setAdapter(mPeopleListAdpater);

        return layoutView;
    }

    private View.OnClickListener onClickAddPersonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //친구 찾기 액티비티 실행
            Intent intent = new Intent(getActivity(), SelectPeopleActivity.class);
            intent.putExtra(SelectPeopleActivity.P_TITLE,"친구 추가");
            HashSet<PeopleItemData> data = new HashSet<>();
            //공공 프로필 내용
            intent.putExtra(SelectPeopleActivity.P_REF_ORIGIN,"public");
            getActivity().startActivityForResult(intent,REQ_ADD_FRIEND);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult","requestCode:" + requestCode + ", resultCode:" + resultCode);
        switch (requestCode){
            case REQ_ADD_FRIEND:{
                switch (resultCode){
                    case SelectPeopleActivity.RES_SUCCESS:{
                        Set<PeopleItemData> resultData = (Set<PeopleItemData>) data.getSerializableExtra(SelectPeopleActivity.R_SELECTED);
                        Log.d("REQ_ADD_FRIEND", "is sucess count :" + resultData.size());
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("private/" + uid + "/people");
                        PeopleItemData me = new PeopleItemData();
                        me.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        me.nickname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        me.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        for(PeopleItemData o : resultData){
                            ref.child(o.uid).setValue(o);
                            FirebaseDatabase.getInstance().getReference("private/"+o.uid+"/people/"+uid).setValue(me);
                        }
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
            mPeopleListAdpater.dataList.clear();
            mPeopleListAdpater.notifyDataSetChanged();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mNickname.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        mEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReference("profiles/"+uid +".jpg")).into(mThumnalie);
        FirebaseDatabase
                .getInstance()
                .getReference("private")
                .child(uid)
                .child("people")
                .addValueEventListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase
                .getInstance()
                .getReference("private")
                .child(uid)
                .child("people")
                .removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        mPeopleListAdpater.dataList.clear();
        for(DataSnapshot o : dataSnapshot.getChildren()){
            PeopleItemData peopleItemData = o.getValue(PeopleItemData.class);
            mPeopleListAdpater.dataList.add(peopleItemData);
        }
        Collections.sort(mPeopleListAdpater.dataList);
        mPeopleListAdpater.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String message = databaseError.getMessage();
        Toast.makeText(getActivity(), "데이터를 불러올수 없습니다.\n" + message , Toast.LENGTH_SHORT).show();
    }
}
