package com.g2utools.whenmeet.activity.sheet;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.adapter.BaseListAdapter;
import com.g2utools.whenmeet.dialog.DatePickerDialog;
import com.g2utools.whenmeet.dialog.MothPickerDialog;
import com.g2utools.whenmeet.dialog.NewSheetDialog;
import com.g2utools.whenmeet.model.SheetData;
import com.g2utools.whenmeet.util.MessageSender;
import com.g2utools.whenmeet.viewHolder.SimpleTitleViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class SheetList extends AppCompatActivity {
    public static final String P_REF_SHEET_LIST = "sheet list";
    public static final String P_MSG_SENDER = "message sender";
    public static final String P_REF_MY_Sheet = "my sheet";

    private RecyclerView mSheetListView;
    private LinearLayoutManager mSheetListLayoutMgr;
    private BaseListAdapter<SheetData,SimpleTitleViewHolder> mSheetListAdpater;

    private String refSheetList;
    private MessageSender mMsgSender;
    private String refMySheet;

    private FloatingActionButton mAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_list);
        //파라메터 설정
        refSheetList = getIntent().getStringExtra(P_REF_SHEET_LIST);
        mMsgSender = ((MessageSender) getIntent().getSerializableExtra(P_MSG_SENDER));
        refMySheet = getIntent().getStringExtra(P_REF_MY_Sheet);
        //UI 연결
        mAdd = findViewById(R.id.add);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //새로운 스케쥴 생성
                new NewSheetDialog(SheetList.this, new NewSheetDialog.OnCreateListener() {
                    @Override
                    public void onCreate(final String title, String type) {
                        final SheetData data = new SheetData();
                        data.timestamp = new Date().getTime();
                        data.title = title;
                        data.type = type;
                        data.refData = refSheetList + "/" + FirebaseDatabase.getInstance().getReference(refSheetList).push().getKey();
                        if(type.equals(SheetData.TYPE_DAY))
                            new DatePickerDialog(SheetList.this, new DatePickerDialog.OnConfirm() {
                                @Override
                                public void onConfirm(Date date) {
                                    data.date = date.getTime();
                                    FirebaseDatabase.getInstance().getReference(data.refData).setValue(data);
                                    if(mMsgSender != null){
                                        String nick = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                        mMsgSender.SendMessage(nick+"님이 새로운 스케쥴 '" +  title + "'을 생성하였습니다.");
                                    }
                                }
                            }).show();
                        else if (type.equals(SheetData.TYPE_MONTH))
                            new MothPickerDialog(SheetList.this, new MothPickerDialog.OnConfrim() {
                                @Override
                                public void onConfrim(Date date) {
                                    data.date = date.getTime();
                                    FirebaseDatabase.getInstance().getReference(data.refData).setValue(data);
                                    if(mMsgSender != null){
                                        String nick = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                        mMsgSender.SendMessage(nick+"님이 새로운 스케쥴 '" +  title + "'을 생성하였습니다.");
                                    }
                                }
                            }).show();
                    }
                }).show();
            }
        });
        //리스트 뷰 설정
        mSheetListView = findViewById(R.id.recycler);
        mSheetListLayoutMgr = new LinearLayoutManager(this);
        mSheetListAdpater = new BaseListAdapter<SheetData,SimpleTitleViewHolder>(R.layout.recycler_simple_title,SimpleTitleViewHolder.class) {
            @Override
            public void dataConvertViewHolder(SimpleTitleViewHolder holder, SheetData data) {
                //데이터 바인딩
                String strTitle = "";
                if (data.type.equals(SheetData.TYPE_MONTH)) {
                    strTitle = "[월간] " + data.title + " - " + new SimpleDateFormat("YY/MM").format(new Date(data.date)) ;
                } else if(data.type.equals(SheetData.TYPE_DAY)){
                    strTitle = "[일간] " + data.title + " - "  + new SimpleDateFormat("YY/MM/dd").format(new Date(data.date)) ;
                }
                holder.title.setText(strTitle);
            }

            @Override
            public void onCreateAfterViewHolder(final SimpleTitleViewHolder holder) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    //클릭했을 때 반응
                    int pos = holder.getLayoutPosition();
                    SheetData data = mSheetListAdpater.dataList.get(pos);
                    Intent intent = new Intent();
                    if(data.type.equals(SheetData.TYPE_MONTH)){
                        intent = new Intent(SheetList.this, MonthSheet.class);
                        intent.putExtra(MonthSheet.P_SHEET_DATA,data);
                        intent.putExtra(MonthSheet.P_REF_MY_Sheet, refMySheet);
                    } else if(data.type.equals(SheetData.TYPE_DAY)){
                        intent = new Intent(SheetList.this, DaySheet.class);
                        intent.putExtra(DaySheet.P_SHEET_DATA,data);
                        intent.putExtra(DaySheet.P_REF_MY_Sheet, refMySheet);
                    }
                    startActivity(intent);
                    }
                });
                super.onCreateAfterViewHolder(holder);
            }
        };
        mSheetListView.setLayoutManager(mSheetListLayoutMgr);
        mSheetListView.setAdapter(mSheetListAdpater);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference(refSheetList).addValueEventListener(mSheetListListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference(refSheetList).removeEventListener(mSheetListListener);
    }

    private ValueEventListener mSheetListListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mSheetListAdpater.dataList.clear();

            for(DataSnapshot o : dataSnapshot.getChildren() ){
                SheetData data = o.getValue(SheetData.class);
                mSheetListAdpater.dataList.add(data);
            }
            Collections.sort(mSheetListAdpater.dataList);
            Collections.reverse(mSheetListAdpater.dataList);

            mSheetListAdpater.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
