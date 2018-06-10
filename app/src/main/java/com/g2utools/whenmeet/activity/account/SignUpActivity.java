package com.g2utools.whenmeet.activity.account;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.dialog.ProgressDialog;
import com.g2utools.whenmeet.util.ActivityStacker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmail;
    private EditText mPassword;
    private EditText mRepassword;
    private Button mCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRepassword = findViewById(R.id.repassword);
        mCreate = findViewById(R.id.create);
        mCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.create:{
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String repassword = mRepassword.getText().toString();
                if(!password.equals(repassword)){
                    Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("계정 생성중");
                progressDialog.setCancelable(false);
                progressDialog.show();
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "메일 인증 메일이 발송 되었습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(SignUpActivity.this, "메일 인증 메일이 발송 되지 못하였습니다.\n" + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            DatabaseReference refPublicUser = FirebaseDatabase.getInstance().getReference("public").child(uid);
                            refPublicUser.child("email").setValue(email);
                            refPublicUser.child("uid").setValue(uid);
                            Toast.makeText(SignUpActivity.this, "계정이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this,ProfileSetActivity.class);
                            startActivity(intent);
                            ActivityStacker.relase("main");
                            ActivityStacker.kill("login");
                            finish();
                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(SignUpActivity.this, "계정 생성 실패하였습니다.\n" + message, Toast.LENGTH_SHORT).show();
                        }
                    };
                });
            }break;
        }
    }


}
