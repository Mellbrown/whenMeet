package com.g2utools.whenmeet.activity.account;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.dialog.ProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmail;
    private Button mSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mEmail = findViewById(R.id.email);
        mSend = findViewById(R.id.send);

        mSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send:{
                String email = mEmail.getText().toString();
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("이메일 발송중");
                progressDialog.setCancelable(false);
                progressDialog.show();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetPasswordActivity.this, "메일이 발송 되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(ForgetPasswordActivity.this, "메일을 발송할수 없습니다.\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }break;
        }
    }
}
