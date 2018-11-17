package com.jenslarsen.scheduleowl;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.db.ScheduleContract.CourseEntry;

public class FragmentCourses extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int ADD_COURSE = 1;
    private final static int EDIT_COURSE = 2;
    public static final int COURSE_LOADER = 1000;

    private int selectedPosition;
    private CourseCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_courses, container, false);

        ListView listView = rootView.findViewById(R.id.listViewCourses);

        adapter = new CourseCursorAdapter(getContext(), null);
        listView.setAdapter(adapter);

        View emptyView = rootView.findViewById(R.id.emptyCourseView);
        listView.setEmptyView(emptyView);

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

        getLoaderManager().initLoader(COURSE_LOADER, null, this);

        return rootView;
    }

    public void buttonAddCourseClicked() {
        Intent intent = new Intent(getActivity(), AddCourse.class);
        startActivityForResult(intent, ADD_COURSE);
    }

    public void courseItemClicked(int selectedPosition) {
        Intent intent = new Intent(getActivity(), EditCourse.class);
        intent.putExtra("selectedPosition", selectedPosition);
        startActivityForResult(intent, EDIT_COURSE);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[]{
                CourseEntry._ID,
                CourseEntry.TITLE,
                CourseEntry.START_DATE};

        return new CursorLoader(
                getContext(),
                CourseEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}


