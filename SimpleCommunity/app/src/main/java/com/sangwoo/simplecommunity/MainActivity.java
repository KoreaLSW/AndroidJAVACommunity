package com.sangwoo.simplecommunity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.sangwoo.simplecommunity.Adapter.MyPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity  {

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = MainActivity.this;

        Intent intent = getIntent();
        String userID = intent.getExtras().getString("userID");

        TextView UserID_TextView = findViewById(R.id.userID);
        UserID_TextView.setText(userID + "님 반갑습니다. 오늘도 즐거운 하루 되세요.");

        Animation UserID_TextView_Anim = AnimationUtils.loadAnimation(this, R.anim.textview_anim);
        UserID_TextView.startAnimation(UserID_TextView_Anim);

        ViewPager viewPager = findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
    }




}
