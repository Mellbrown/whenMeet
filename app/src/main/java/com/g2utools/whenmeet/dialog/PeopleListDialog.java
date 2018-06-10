package com.g2utools.whenmeet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.model.PeopleItemData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

/**
 * Created by mlyg2 on 2018-03-13.
 */

public class PeopleListDialog extends Dialog {
    private TextView mTitle;
    private LinearLayout mContent;
    public PeopleListDialog(@NonNull final Context context, String title , final Set<String> uids) {
        super(context);
        setContentView(R.layout.dialog_people_list);

        mTitle = findViewById(R.id.title);
        mContent = findViewById(R.id.content);

        mTitle.setText(title);
        FirebaseDatabase.getInstance().getReference("public").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(String uid : uids){
                    PeopleItemData data = dataSnapshot.child(uid).getValue(PeopleItemData.class);
                    TextView item = new TextView(context);
                    item.setText(data.nickname);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    item.setLayoutParams(layoutParams);
                    mContent.addView(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setCancelable(true);
        show();
    }
}
