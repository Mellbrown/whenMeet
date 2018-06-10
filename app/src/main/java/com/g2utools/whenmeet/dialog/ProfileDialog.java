package com.g2utools.whenmeet.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.model.PeopleItemData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by mlyg2 on 2018-02-11.
 */

public class ProfileDialog extends Dialog{

    private RoundedImageView mThumbnail;
    private TextView mNickname;
    private TextView mEmail;
    private FloatingActionButton mAction;

    public ProfileDialog(@NonNull final Context context, final PeopleItemData data) {
        super(context);
        setContentView(R.layout.dialog_profile);

        mThumbnail = findViewById(R.id.thumnail);
        mNickname = findViewById(R.id.nickname);
        mEmail = findViewById(R.id.email);
        mAction = findViewById(R.id.action);

        mNickname.setText(data.nickname);
        mEmail.setText(data.email);
//        if (data.getThumbnail() != null) {
//            mThumbnail.setImageBitmap(data.getThumbnail());
//        }else{
//            mThumbnail.setImageResource(R.drawable.ic_person_black_24dp);
//        }
        data.setThumbnail(context,mThumbnail);

        mAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("친구 삭제")
                        .setMessage("정말로 " + data.nickname + "님을 친구목록에서 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                FirebaseDatabase.getInstance().getReference("private/" + uid + "/people/" + data.uid).removeValue();
                            }
                        })
                        .setNegativeButton("취소", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setCancelable(true)
                        .create()
                        .show();
            }
        });
    }

}
