package com.jenslarsen.scheduleowl;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.jenslarsen.scheduleowl.db.Datasource;
import com.jenslarsen.scheduleowl.model.Mentor;
import com.jenslarsen.scheduleowl.model.Term;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        // add some dummy data to the terms
        for( int index = 1; index < 5; index++) {
            Term tempTerm = new Term();
            tempTerm.setTitle("Term " + index);
            Datasource.terms.add(tempTerm);
        }

        // add some dummy data to the mentors
        for( int index = 1; index < 5; index++) {
            Mentor tempMentor = new Mentor();
            tempMentor.setName("Mentor " + index);
            Datasource.mentors.add(tempMentor);
        }
    }
}
