package com.g2utools.whenmeet.activity.main;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.adapter.BaseListAdapter;
import com.g2utools.whenmeet.model.PeopleItemData;
import com.g2utools.whenmeet.viewHolder.PeopleSelectableViewHolder;
import com.g2utools.whenmeet.viewHolder.PeopleSelectedViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SelectPeopleActivity extends AppCompatActivity implements ValueEventListener {
    public static final String P_TITLE = "title";
    public static final String P_HIDDEN_MODE = "hidden mode";
    public static final String P_REF_ORIGIN = "ref";
    public static final String R_SELECTED = "selected";
    public static final int RES_SUCCESS = 3;
    public static final int RES_NOTING = 2;

    private String mActionBarTitle;
    private boolean mHiddenMode;
    private ActionBar mActionBar;

    private SearchView mSearchView;
    private String mRefOrigin;
    private Set<PeopleItemData> originData = new HashSet<>();

    private RecyclerView mSelectableListView;
    private LinearLayoutManager mSelectableLayoutMgr;
    private BaseListAdapter<PeopleItemData,PeopleSelectableViewHolder> mSelectableListAdapter;

    private RecyclerView mSelectedListView;
    private LinearLayoutManager mSelectedLayoutMgr;
    private BaseListAdapter<PeopleItemData,PeopleSelectedViewHolder> mSelectedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_people);

        mActionBar = getSupportActionBar();
        //파라메터 받아오기
        Intent intent = getIntent();
        mActionBarTitle = intent.getStringExtra(P_TITLE);
        mRefOrigin = intent.getStringExtra(P_REF_ORIGIN);
        mHiddenMode = intent.getBooleanExtra(P_HIDDEN_MODE,true);
        //리스트뷰 설정
        readySelectedListView(); // SelectableListView 초기화때문에 앞에 와야됨
        readySelectableListView();
        //검색 설정
        mSearchView = findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSelectableListAdapter.dataList.clear();
                for (PeopleItemData data: originData){
                    if (data.email.indexOf(query) != -1 || data.nickname.indexOf(query) != -1){
                        mSelectableListAdapter.dataList.add(data);
                    }
                }
                if(!mHiddenMode && mSelectableListAdapter.dataList.size() == 0)
                    mSelectableListAdapter.dataList.addAll(originData);
                Collections.sort(mSelectableListAdapter.dataList);
                mSelectableListAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSelectableListAdapter.dataList.clear();
                for (PeopleItemData data: originData){
                    if (data.email.indexOf(newText) != -1 || data.nickname.indexOf(newText) != -1){
                        mSelectableListAdapter.dataList.add(data);
                    }
                }
                if(!mHiddenMode && mSelectableListAdapter.dataList.size() == 0)
                    mSelectableListAdapter.dataList.addAll(originData);
                Collections.sort(mSelectableListAdapter.dataList);
                mSelectableListAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }
    // 선택된 사람 변경
    private void onChangeSelectedCount(){
        int size = mSelectedListAdapter.dataList.size();
        mActionBar.setTitle(mActionBarTitle + " " + size);
        if(size == 0){
            mSelectedListView.setVisibility(View.GONE);
        }else{
            mSelectedListView.setVisibility(View.VISIBLE);
        }
    }

    //START [Selected 리스트뷰 설정}
    private void readySelectedListView(){
        mSelectedListView = findViewById(R.id.selectedes);
        mSelectedLayoutMgr = new LinearLayoutManager(this);
        mSelectedLayoutMgr.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSelectedListAdapter = new BaseListAdapter<PeopleItemData, PeopleSelectedViewHolder>(R.layout.recycler_people_selected_people, PeopleSelectedViewHolder.class) {
            @Override
            public void onCreateAfterViewHolder(final PeopleSelectedViewHolder holder) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PeopleItemData data = mSelectedListAdapter.dataList.get(holder.getAdapterPosition());
                        //아이템 스스로 제거
                        mSelectedListAdapter.dataList.remove(holder.getAdapterPosition());
                        mSelectedListAdapter.notifyItemRemoved(holder.getAdapterPosition());
                        //체크할 리스트 뷰 해당 아이템 변경 통보
                        if(mSelectableListAdapter.dataList.contains(data)){
                            int uncheckedPos = mSelectableListAdapter.dataList.indexOf(data);
                            mSelectableListAdapter.notifyItemChanged(uncheckedPos);
                        }
                        onChangeSelectedCount();
                    }
                });
            }

            @Override
            public void dataConvertViewHolder(PeopleSelectedViewHolder holder, PeopleItemData data) {
                holder.dataBind(data);
            }
        };
        mSelectedListView.setLayoutManager(mSelectedLayoutMgr);
        mSelectedListView.setAdapter(mSelectedListAdapter);
        onChangeSelectedCount();
    }
    //END [Selected 리스트뷰 설정}

    //START [Selectable 리스트뷰 설정}
    private void readySelectableListView(){
        mSelectableListView = findViewById(R.id.selectables);
        mSelectableLayoutMgr = new LinearLayoutManager(this);
        mSelectableListAdapter = new BaseListAdapter<PeopleItemData, PeopleSelectableViewHolder>(R.layout.recycler_people_selectable_people,PeopleSelectableViewHolder.class) {
            @Override
            public void onCreateAfterViewHolder(final PeopleSelectableViewHolder holder) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //프로필 보기?
                    }
                });
                holder.selected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PeopleItemData data = mSelectableListAdapter.dataList.get(holder.getAdapterPosition());
                        if(holder.selected.isChecked()){ // 체크 되었을 때 -> 체크된 리스트 뷰에 추가
                            int insertedPos = mSelectedListAdapter.dataList.size();
                            mSelectedListAdapter.dataList.add(data);
                            mSelectedListAdapter.notifyItemInserted(insertedPos);
                        }else { // 체크 해제 되었을때 -> 체크된 리스트 뷰에 제거
                            int removedPos = mSelectedListAdapter.dataList.indexOf(data);
                            mSelectedListAdapter.dataList.remove(removedPos);
                            mSelectedListAdapter.notifyItemRemoved(removedPos);
                        }
                        onChangeSelectedCount();
                    }
                });
            }

            @Override
            public void dataConvertViewHolder(PeopleSelectableViewHolder holder, PeopleItemData data) {
                //선택된 리스트 뷰안에 데이터가 담겨 있으면 체크 상태(true)
                holder.dataBind(data,mSelectedListAdapter.dataList.contains(data));
            }
        };
        mSelectableListView.setLayoutManager(mSelectableLayoutMgr);
        mSelectableListView.setAdapter(mSelectableListAdapter);
    }
    //END [Selectable 리스트뷰 설정}

    //START [액션 confirm 버튼 동작]
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_people, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.confirm:{
                Intent intent = new Intent();
                HashSet<PeopleItemData> selected = new HashSet<>();
                selected.addAll(mSelectedListAdapter.dataList);
                intent.putExtra(R_SELECTED,selected);
                setResult(RES_SUCCESS,intent);
                finish();
            }break;
        }
        return super.onOptionsItemSelected(item);
    }
    //END [액션 confirm 버튼 동작]

    @Override
    public void onBackPressed() {
        setResult(RES_NOTING);
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference(mRefOrigin).addValueEventListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference(mRefOrigin).removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        originData.clear();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for(DataSnapshot o : dataSnapshot.getChildren()){
            PeopleItemData peopleItemData = o.getValue(PeopleItemData.class);
            if(peopleItemData.uid.equals(uid)) continue;
            originData.add(peopleItemData);
        }
        if(!mHiddenMode && mSearchView.getQuery().toString().equals("")){
            mSelectableListAdapter.dataList.clear();
            mSelectableListAdapter.dataList.addAll(originData);
            mSelectableListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String message = databaseError.getMessage();
        Toast.makeText(this, "데이터를 불러올수 없습니다.\n" + message , Toast.LENGTH_SHORT).show();
    }
}
