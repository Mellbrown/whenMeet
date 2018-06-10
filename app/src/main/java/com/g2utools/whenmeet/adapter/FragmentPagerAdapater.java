package com.g2utools.whenmeet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by mlyg2 on 2018-02-10.
 */

public class FragmentPagerAdapater extends FragmentStatePagerAdapter {

    List<Fragment> fragmentList;
    public FragmentPagerAdapater(FragmentManager fm , List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
