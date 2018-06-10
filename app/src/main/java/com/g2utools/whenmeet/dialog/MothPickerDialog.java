package com.g2utools.whenmeet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.g2utools.whenmeet.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mlyg2 on 2018-03-07.
 */

public class MothPickerDialog extends Dialog {

    private Spinner year;
    private Spinner month;
    private Button confirm;

    private ArrayList<String> srcYear = new ArrayList<>();
    private ArrayList<String> srcMonth = new ArrayList<>();

    private Calendar cal = Calendar.getInstance();

    public MothPickerDialog(@NonNull Context context, final OnConfrim onConfrim) {
        super(context);
        setContentView(R.layout.dialog_month_picker);

        year = findViewById(R.id.year);
        month = findViewById(R.id.month);
        confirm = findViewById(R.id.confirm);

        year.setOnItemSelectedListener(onItemSelectedListener);
        month.setOnItemSelectedListener(onItemSelectedListener);

        cal.setTime(new Date());

        for (int i=cal.get(Calendar.YEAR); i > 1997 ; i--) srcYear.add(i+"");
        for (int i=1; 12 >= i ; i++) srcMonth.add(i+"");

        year.setAdapter(new ArrayAdapter(context,android.R.layout.simple_dropdown_item_1line,srcYear));
        month.setAdapter(new ArrayAdapter(context,android.R.layout.simple_dropdown_item_1line,srcMonth));

        year.setSelection(0);
        year.setSelection(srcMonth.indexOf(cal.get(Calendar.MONTH) + ""));

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfrim.onConfrim(cal.getTime());
                dismiss();
            }
        });
    }

    private Spinner.OnItemSelectedListener onItemSelectedListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            switch (adapterView.getId()){
                case R.id.year:{
                    cal.set(Calendar.YEAR, Integer.parseInt(srcYear.get(i)) );
                }break;
                case R.id.month:{
                    cal.set(Calendar.MONTH, Integer.parseInt(srcMonth.get(i)));
                }break;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    public interface OnConfrim{
        void onConfrim(Date date);
    }
}
