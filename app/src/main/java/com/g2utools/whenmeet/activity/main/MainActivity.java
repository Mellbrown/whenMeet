package com.g2utools.whenmeet.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.g2utools.whenmeet.service.ReceiveNotification;
import com.g2utools.whenmeet.activity.account.LoginActivity;
import com.g2utools.whenmeet.fragment.ChatListFragment;
import com.g2utools.whenmeet.adapter.FragmentPagerAdapater;
import com.g2utools.whenmeet.fragment.PeopleListFragment;
import com.g2utools.whenmeet.R;
import com.g2utools.whenmeet.fragment.SettingFragment;
import com.g2utools.whenmeet.util.ActivityStacker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/*
* 메인 화면
* 친구 목록, 대화목록, 설정 목록
* 로그인 체크 한다.
* 프레그 먼트 사용으로 바꾼다
* */
public class MainActivity extends AppCompatActivity {
    private static final int REQ_LOGIN = 3;

    private BottomNavigationView mNavigation; // 하단 네비게이션 바
    private ViewPager mPager; //프레그먼크 페이저
    private FragmentStatePagerAdapter mPagerAdapter; // 프레그먼트 어뎁터
    private List<Fragment> fragments = new ArrayList<>(); //프레그 먼트들

    //[Start Navigate 선택]
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_people:
                    mPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_chat:
                    mPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_setting:
                    mPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };
    //[End Navigate 선택]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mNavigation= findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mPager = findViewById(R.id.pager);
        //프레그 먼트 추가
        fragments.add(new PeopleListFragment());
        fragments.add(new ChatListFragment());
        fragments.add(new SettingFragment());
        //프레그먼트 페이지 적용
        mPagerAdapter = new FragmentPagerAdapater(getSupportFragmentManager(),fragments);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(0);
        getSupportActionBar().setTitle("친구");
/*        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        getSupportActionBar().setTitle("친구");
                        mNavigation.setSelectedItemId(R.id.navigation_people);
                        break;
                    case 1:
                        getSupportActionBar().setTitle("대화");
                        mNavigation.setSelectedItemId(R.id.navigation_people);
                        break;
                    case 2:
                        getSupportActionBar().setTitle("설정");
                        mNavigation.setSelectedItemId(R.id.navigation_setting);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    Intent intent = new Intent(MainActivity.this, ReceiveNotification.class);
                    intent.putExtra(ReceiveNotification.P_REF_CHAT_ITEMS, "private/"+uid+"/chat");
                    startService(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, ReceiveNotification.class);
                    stopService(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent,REQ_LOGIN);
            ActivityStacker.push("main",this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQ_LOGIN:{
                ActivityStacker.pop("main",this);
            }break;
            default:
                Log.d("onActivityResult", "sjfieosefjiejfo");
                mPagerAdapter.getItem(mPager.getCurrentItem()).onActivityResult(requestCode,resultCode,data);
                break;
        }
    }
}
