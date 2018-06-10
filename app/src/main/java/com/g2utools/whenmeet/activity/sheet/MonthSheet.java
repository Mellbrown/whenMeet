package com.g2utools.whenmeet.activity.sheet;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.dialog.PeopleListDialog;
import com.g2utools.whenmeet.model.SheetData;
import com.g2utools.whenmeet.model.SheetDataControler;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

public class MonthSheet extends AppCompatActivity {

    public static final String P_SHEET_DATA = "sheet_data";
    public static final String P_REF_MY_Sheet = "my sheet";
    private String refMySheet;
    private SheetData sheetData;
    private SheetDataControler sheetDataControler;

    private TextView txtMonth;
    private FloatingActionButton mOverlay;
    private TableLayout tableLayout;
    private ArrayList<DayOfMonthViewHolder> dayOfMonthViewHolders = new ArrayList<>();

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_sheet);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //기본 세팅
        sheetData = ((SheetData) getIntent().getSerializableExtra(P_SHEET_DATA));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(sheetData.date);
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        txtMonth = findViewById(R.id.month);
        txtMonth.setText(year + "년 " + month + "월");
        getSupportActionBar().setTitle(sheetData.title);

        tableLayout = findViewById(R.id.tableLayout);
        for(int i = 0 ; 6 >i ; i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundResource(android.R.color.darker_gray);
            tableRow.setWeightSum(7);
            for (int j = 0; 7 > j; j++){
                DayOfMonthViewHolder dayOfMonthViewHolder = new DayOfMonthViewHolder(tableRow);
                dayOfMonthViewHolder.setDate(7*i + j);
                dayOfMonthViewHolders.add(dayOfMonthViewHolder);
            }
            tableLayout.addView(tableRow);
        }

        cal.set(year,month-1,1);
        final int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int maximum = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for(int i = 0; 42 > i; i++){
            DayOfMonthViewHolder dayOfMonthViewHolder = dayOfMonthViewHolders.get(i);
            if( day <= i && i < day + maximum){
                dayOfMonthViewHolder.setDate(i - day + 1);
            } else {
                dayOfMonthViewHolder.setDate(null);
            }
            dayOfMonthViewHolder.setCount(null);
        }

        sheetDataControler = new SheetDataControler(sheetData.refData, new SheetDataControler.OnChangeDataSetLisnter() {
            @Override
            public void onChangDataSetLisnter() {
                for(DayOfMonthViewHolder dayOfMonthViewHolder : dayOfMonthViewHolders){
                    if (!dayOfMonthViewHolder.getDate().equals("")) {
                        String strDate = dayOfMonthViewHolder.getDate();
                        dayOfMonthViewHolder.setCount(sheetDataControler.getCount(strDate));
                        dayOfMonthViewHolder.setHighlist(sheetDataControler.isUID(strDate,uid));
                    }
                }
            }
        });

        mOverlay = findViewById(R.id.overlay);
        refMySheet = getIntent().getStringExtra(P_REF_MY_Sheet);
        if(refMySheet == null){
            mOverlay.setVisibility(View.GONE);
        }else{
            mOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        Toast.makeText(this, "안되는 날짜를 터치", Toast.LENGTH_LONG).show();
    }

    public class DayOfMonthViewHolder {
        View itemView;
        TextView date;
        TextView count;
        public DayOfMonthViewHolder(ViewGroup root){
            LayoutInflater inflater = LayoutInflater.from(root.getContext());
            itemView = inflater.inflate(R.layout.part_month_day, null);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
            layoutParams.setMargins(4,4,4,4);
            layoutParams.weight = 1;
            itemView.setLayoutParams(layoutParams);
            root.addView(itemView);
            date = itemView.findViewById(R.id.date);
            count = itemView.findViewById(R.id.count);
            //아이템 클릭했을 때
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String d = date.getText().toString();
                    if(d.equals("")) return;
                    if (sheetDataControler.isUID(d,uid)) {
                        sheetDataControler.popUID(d,uid);
                    }else{
                        sheetDataControler.pushUID(d,uid);
                    }
                }
            });
            //아이템 롱클릭
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String d = date.getText().toString();
                    new PeopleListDialog(MonthSheet.this,d,sheetDataControler.getUIDs(d));
                    return true;
                }
            });
        }

        public void setDate(Integer val){
            date.setText(val != null ? val.toString() : "");
        }
        public String getDate(){ return date.getText().toString(); }
        public void setCount(Integer val){
            count.setText(val != null ? val.toString() : "");
            if(val == null || val == 0){
                itemView.setBackgroundResource(android.R.color.white);
            } else if(val == 1){
                itemView.setBackgroundResource(android.R.color.holo_green_light);
            } else if(val == 2){
                itemView.setBackgroundResource(android.R.color.holo_orange_light);
            } else if(val == 3){
                itemView.setBackgroundResource(android.R.color.holo_orange_dark);
            } else {
                itemView.setBackgroundResource(android.R.color.holo_red_light);
            }
        }
        public String getCount(){ return count.getText().toString(); }
        public void setHighlist(boolean b){
            if(b) count.setTextColor(Color.CYAN);
            else count.setTextColor(Color.LTGRAY);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sheetDataControler.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sheetDataControler.stop();
    }
}
