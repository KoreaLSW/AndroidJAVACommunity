package com.sangwoo.simplecommunity.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sangwoo.simplecommunity.Fragment.CommunityFragment;
import com.sangwoo.simplecommunity.Fragment.GameFragment;
import com.sangwoo.simplecommunity.Fragment.PlusOneFragment;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mData;

    public MyPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);

        mData = new ArrayList<>();
        mData.add(new CommunityFragment());
        mData.add(new GameFragment());
        mData.add(new PlusOneFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size()-1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0) {
            return "모두의 게시판.";
        }else if(position == 1){
            return "심심풀이";
        }else {
            return position + "번째";
        }
    }
}















