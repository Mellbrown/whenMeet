package com.g2utools.whenmeet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.model.SheetData;

import org.w3c.dom.Text;

/**
 * Created by mlyg2 on 2018-03-06.
 */

public class NewSheetDialog extends Dialog{
    private EditText mTitle;
    private RadioGroup mType;
    private Button mCreate;

    public NewSheetDialog(@NonNull final Context context, final OnCreateListener onCreate ){
        super(context);
        setContentView(R.layout.dialog_new_sheet);

        mTitle = findViewById(R.id.title);
        mType = findViewById(R.id.type);
        mCreate = findViewById(R.id.create);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitle.getText().toString();
                if(title.equals("")){
                    return;
                }
                switch (mType.getCheckedRadioButtonId()){
                    case R.id.month: onCreate.onCreate(title, SheetData.TYPE_MONTH); break;
                    case R.id.day: onCreate.onCreate(title, SheetData.TYPE_DAY); break;
                }
                dismiss();
            }
        });
    }

    public interface OnCreateListener{
        void onCreate(String title, String type);
    }
}
