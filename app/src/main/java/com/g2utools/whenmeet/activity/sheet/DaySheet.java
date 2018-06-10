package com.g2utools.whenmeet.activity.sheet;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.dialog.PeopleListDialog;
import com.g2utools.whenmeet.model.SheetData;
import com.g2utools.whenmeet.model.SheetDataControler;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

public class DaySheet extends AppCompatActivity {

    public static final String P_SHEET_DATA = "sheet_data";
    public static final String P_REF_MY_Sheet = "my sheet";
    private String refMySheet;
    private SheetData sheetData;
    private SheetDataControler sheetDataControler;

    private TextView txtDate;
    private FloatingActionButton mOverlay;
    private LinearLayout linearLayout;
    private ArrayList<TimeOfDayViewHolder> timeOfDayViewHolders = new ArrayList<>();

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_sheet);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        sheetData = ((SheetData) getIntent().getSerializableExtra(P_SHEET_DATA));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(sheetData.date);
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        txtDate = findViewById(R.id.date);
        txtDate.setText(year + "년 " + month + "월 " + dayOfMonth + "일");
        getSupportActionBar().setTitle(sheetData.title);

        linearLayout = findViewById(R.id.linearLayout);
        for(int i = 0; 24 > i; i ++){
             TimeOfDayViewHolder timeOfDayViewHolder = new TimeOfDayViewHolder(linearLayout);
             timeOfDayViewHolder.setTime(i,0);
             timeOfDayViewHolders.add(timeOfDayViewHolder);
            timeOfDayViewHolder = new TimeOfDayViewHolder(linearLayout);
            timeOfDayViewHolder.setTime(i,30);
            timeOfDayViewHolders.add(timeOfDayViewHolder);

        }

        sheetDataControler = new SheetDataControler(sheetData.refData, new SheetDataControler.OnChangeDataSetLisnter() {
             @Override
             public void onChangDataSetLisnter() {
                 for(int i = 0; 48 > i; i++){
                     TimeOfDayViewHolder timeOfDayViewHolder = timeOfDayViewHolders.get(i);
                     String strTime = timeOfDayViewHolder.getTime();
                     timeOfDayViewHolder.setCount(sheetDataControler.getCount(strTime));
                     timeOfDayViewHolder.setHightlight(sheetDataControler.isUID(strTime,uid));
                 }
             }
        });

        Toast.makeText(this, "안되는 시간을 터치", Toast.LENGTH_LONG).show();
    }

    public class TimeOfDayViewHolder{
        View itemView;
        TextView time;
        TextView count;
        public TimeOfDayViewHolder(ViewGroup root){
            LayoutInflater inflater = LayoutInflater.from(root.getContext());
            itemView = inflater.inflate(R.layout.part_day, null);
            time = itemView.findViewById(R.id.time);
            count = itemView.findViewById(R.id.count);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);
            root.addView(itemView);

            //아이템 클릭했을 때
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String d = time.getText().toString();
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
                    String d = time.getText().toString();
                    new PeopleListDialog(DaySheet.this,d,sheetDataControler.getUIDs(d));
                    return true;
                }
            });
        }

        public void setTime(int hour, int min){
            time.setText((hour<10?"0"+hour:hour) + " : " + (min<10?"0"+min:min));
        }
        public String getTime(){ return time.getText().toString();}
        public void setHightlight(boolean b){
            if(b) count.setTextColor(Color.CYAN);
            else count.setTextColor(Color.LTGRAY);
        }
        public void setCount(Integer val){
            count.setText(val != null ? val.toString() : "");
            if(val == null || val == 0){
                count.setBackgroundResource(android.R.color.white);
            } else if(val == 1){
                count.setBackgroundResource(android.R.color.holo_green_light);
            } else if(val == 2){
                count.setBackgroundResource(android.R.color.holo_orange_light);
            } else if(val == 3){
                count.setBackgroundResource(android.R.color.holo_orange_dark);
            } else {
                count.setBackgroundResource(android.R.color.holo_red_light);
            }
        }
        public String getCount(){ return count.getText().toString(); }
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
