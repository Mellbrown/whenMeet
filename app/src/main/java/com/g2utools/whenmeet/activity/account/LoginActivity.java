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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQ_SIGNUP = 1;

    private EditText mEmail;
    private EditText mPassword;
    private EditText mRePassword;
    private Button mSignIn;
    private Button mForget;
    private Button mSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mSignIn = findViewById(R.id.signin);
        mSignup = findViewById(R.id.signup);
        mForget = findViewById(R.id.forget);

        mSignIn.setOnClickListener(this);
        mSignup.setOnClickListener(this);
        mForget.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signin:{
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("로그인 중...");
                progressDialog.show();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            Toast.makeText(LoginActivity.this, displayName + "님 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "로그인 실패하였습니다.\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }break;
            case R.id.signup:{
                ActivityStacker.push("login",this);
                Intent intent = new Intent(this,SignUpActivity.class);
                startActivityForResult(intent,REQ_SIGNUP);
            }break;
            case R.id.forget:{
                Intent intent = new Intent(this,ForgetPasswordActivity.class);
                startActivity(intent);
            }break;
        }
    }

    @Override
    public void onBackPressed() {
        ActivityStacker.kill("main");
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQ_SIGNUP:{
                ActivityStacker.pop("login",this);
            }break;
        }
    }
}
