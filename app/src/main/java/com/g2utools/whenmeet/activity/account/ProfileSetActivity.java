package com.g2utools.whenmeet.activity.account;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.g2utools.whenmeet.Manifest;
import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.dialog.ProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileSetActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQ_CAMERA = 189;
    private static final int REQ_TAKE_PHOTO = 498;
    private static final int REQ_TAKE_ALBUM = 914;
    private static final int REQ_IMAGE_CROP = 991;


    private RoundedImageView mThumnail;
    private FloatingActionButton mProfile;
    private ProgressBar mUpload;

    private EditText mNickname;
    private Button mStart;

    private Uri imageURI, resultUIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_set);

        mThumnail = findViewById(R.id.thumnail);
        mProfile = findViewById(R.id.profile);
        mUpload = findViewById(R.id.upload);
        mNickname = findViewById(R.id.nickname);
        mStart = findViewById(R.id.start);

        //todo: 썸네일 삽입 코드
        String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(displayName != null) mNickname.setText(displayName);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage.getInstance().getReference("profiles/"+uid +".jpg");
        Glide.with(this).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReference("profiles/"+uid +".jpg")).into(mThumnail);

        mStart.setOnClickListener(this);
        mProfile.setOnClickListener(this);
        checkPermission();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start:{
                UserProfileChangeRequest build = new UserProfileChangeRequest.Builder()
                        .setDisplayName(mNickname.getText().toString())
                        .build();
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("프로필 업데이트 중...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                FirebaseAuth.getInstance().getCurrentUser().updateProfile(build).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileSetActivity.this, "적용됨", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(ProfileSetActivity.this, "오류남ㅇㅇ\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference refPublicUser = FirebaseDatabase.getInstance().getReference("public").child(uid);
                refPublicUser.child("nickname").setValue(mNickname.getText().toString());

            }break;
            case R.id.profile:{
                new AlertDialog.Builder(this)
                        .setTitle("사진 가져오기")
                        .setMessage("사진을 어디서 가져올까요?")
                        .setCancelable(true)
                        .setPositiveButton("갤러리", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getAlbum();
                            }
                        })
                        .setNeutralButton("사진 찍기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                captureCamera();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // 이미지 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures","uploads");
        //해당 디렉토리가 없으면 생성
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }
        //파일 생성
        imageFile = new File(storageDir,imageFileName);
        //mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void captureCamera(){
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)){
            //외장 메모리가 사용 가능시(사용가능)
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager()) != null){
                File photoFile = null;
                try { photoFile = createImageFile(); } catch (IOException e) { e.printStackTrace(); }
                //이미지 파일 만들기 성공했다면
                if(photoFile != null){
                    //다른 앱에 파일 공유하기 위한 프로바이더 생성
                    imageURI = FileProvider.getUriForFile(this,getPackageName(),photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                    startActivityForResult(intent, REQ_TAKE_PHOTO);
                }
            }
            else {
                Toast.makeText(this,"저장 공간에 접근이 불가능합니다.",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,REQ_TAKE_ALBUM);
    }

    private void cropImage(){
        try {
            resultUIR = Uri.fromFile(createImageFile());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(imageURI,"image/*");
        intent.putExtra("outputX",200);
        intent.putExtra("outputY",200);
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("scale",true);
        intent.putExtra("output",resultUIR);
        startActivityForResult(intent,REQ_IMAGE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQ_TAKE_PHOTO:{
                if(resultCode == RESULT_OK){
                    cropImage();
                }
            }break;
            case REQ_TAKE_ALBUM:{
                if(resultCode == RESULT_OK){
                    if(data.getData() != null){
                        imageURI = data.getData();
                        cropImage();
                    }
                }
            }break;
            case REQ_IMAGE_CROP:{
                if(resultCode == RESULT_OK){
                    //mThumnail.setImageURI(resultUIR);
                    mUpload.setVisibility(View.VISIBLE);
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseStorage.getInstance().getReference("profiles/"+uid +".jpg").putFile(resultUIR).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            mUpload.setVisibility(View.INVISIBLE);
                            if(!task.isSuccessful()){
                                String message = task.getException().getMessage();
                                Toast.makeText(ProfileSetActivity.this, "이미지 업로드 실패하였습니다.\n" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }break;
        }
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, PERMISSION_REQ_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQ_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] < 0) {
                        Toast.makeText(ProfileSetActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }
}
