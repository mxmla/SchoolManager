package com.thinc_easy.schoolmanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M on 30.01.2016.
 */
public class HomeworkPagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList= new ArrayList<Fragment>();

    public HomeworkPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList.add(new HomeworkFragment());
        fragmentList.add(new HomeworkDoneFragment());
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
