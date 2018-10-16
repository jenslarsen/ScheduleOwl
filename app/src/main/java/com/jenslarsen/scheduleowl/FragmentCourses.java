package com.jenslarsen.scheduleowl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.db.Datasource;
import com.jenslarsen.scheduleowl.model.Course;

public class FragmentCourses extends Fragment {

    private final int ADD_COURSE = 1;
    private final int EDIT_COURSE = 2;
    private int selectedPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_courses, container, false);

        ListView listView = rootView.findViewById(R.id.listViewCourses);

        ArrayAdapter<Course> adapter = new ArrayAdapter<>(getContext(), R.layout.listitem_tab,
                R.id.textViewListItem, Datasource.courses);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                courseItemClicked(selectedPosition);
            }
        });

        Button buttonAddCourse = rootView.findViewById(R.id.buttonAddCourse);
        buttonAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddCourseClicked();
            }
        });

        return rootView;
    }

    private void courseItemClicked(int selectedPosition) {
        Intent intent = new Intent(getActivity(), EditCourse.class);
        intent.putExtra("selectedPosition", selectedPosition);
        startActivityForResult(intent, EDIT_COURSE);
    }

    public void buttonAddCourseClicked() {
        Intent intent = new Intent(getActivity(), AddCourse.class);
        startActivityForResult(intent, ADD_COURSE);
    }
}
