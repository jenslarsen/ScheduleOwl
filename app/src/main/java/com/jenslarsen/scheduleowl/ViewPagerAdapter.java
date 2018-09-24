package com.jenslarsen.scheduleowl;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] childFragments;
    private String[] tabTitles = new String[] { "Terms", "Courses", "Mentors", "Assessments"};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);

        childFragments = new Fragment[]{
                new FragmentTerms(),
                new FragmentCourses(),
                new FragmentMentors(),
                new FragmentAssessments()
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

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
