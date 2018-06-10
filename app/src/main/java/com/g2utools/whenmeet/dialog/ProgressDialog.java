package com.g2utools.whenmeet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.g2utools.whenmeet.R;

/**
 * Created by mlyg2 on 2018-03-09.
 */

public class ProgressDialog extends Dialog {

    TextView title;
    public ProgressDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_progress);
        title = findViewById(R.id.title);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }
}
