package com.jenslarsen.scheduleowl;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] childFragments;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        
        childFragments = new Fragment[] {
                new Terms(),
                new Courses(),
                new Mentors(),
                new Assessments()
        };
    }

    @Override
    public Fragment getItem(int i) {
        return childFragments[i];
    }

    @Override
    public int getCount() {
        return childFragments.length;
    }
}
