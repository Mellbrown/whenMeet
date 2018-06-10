package com.g2utools.whenmeet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.g2utools.whenmeet.R;
import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mlyg2 on 2018-03-07.
 */

public class DatePickerDialog extends Dialog{

    private DatePicker datePicker;
    private Button confirm;

    public DatePickerDialog(@NonNull Context context, final OnConfirm onConfirm) {
        super(context);
        setContentView(R.layout.dialog_date_picker);
        datePicker = findViewById(R.id.datePicker);
        confirm = findViewById(R.id.confirm);

        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        datePicker.init(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),null);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                onConfirm.onConfirm(cal.getTime());
                dismiss();
            }
        });
    }

    public interface OnConfirm{
        void onConfirm(Date date);
    }
}
