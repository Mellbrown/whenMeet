package com.g2utools.whenmeet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.g2utools.whenmeet.R;

import org.w3c.dom.Text;

/**
 * Created by mlyg2 on 2018-03-09.
 */

public class TextDialog extends Dialog {

    private TextView mTitle;
    private EditText mText;
    private Button mConfrim;

    public TextDialog(@NonNull Context context, String title, final OnConfirm onConfirm) {
        super(context);
        setContentView(R.layout.dialog_text);

        mTitle = findViewById(R.id.title);
        mText = findViewById(R.id.text);
        mConfrim = findViewById(R.id.confirm);

        mTitle.setText(title);
        mConfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirm.onConfirm(mText.getText().toString());
                dismiss();
            }
        });
    }

    public interface OnConfirm{
        void onConfirm(String text);
    }
}
