package com.jenslarsen.scheduleowl;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.jenslarsen.scheduleowl.db.ScheduleContract.MentorEntry;

/**
 * {@link MentorSelectorCursorAdapter} is an adapter for a list or grid view that uses a {@link Cursor}
 * of mentor data as its data source. This adapter knows how to create list items for each row of
 * mentor data in the {@link Cursor}.
 */

public class MentorSelectorCursorAdapter extends CursorAdapter {

    private int currentCourseId;

    public MentorSelectorCursorAdapter(Context context, Cursor cursor, int courseId) {
        super(context, cursor, 0);
        currentCourseId = courseId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem_chooser, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewTitle = view.findViewById(R.id.textViewChooser);
        CheckBox checkBox = view.findViewById(R.id.checkBoxChooser);
        TextView textViewId = view.findViewById(R.id.textViewId);

        int titleColumn = cursor.getColumnIndex(MentorEntry.NAME);
        String title = cursor.getString(titleColumn);
        textViewTitle.setText(title);

        // TODO: Make load the correct mentors

//        int courseIdIndex = cursor.getColumnIndex(MentorEntry.COURSEID);
//        int courseId = cursor.getInt(courseIdIndex);
//
//        if (courseId == currentCourseId) {
//            checkBox.setChecked(true);
//        }

        int mentorIdIndex = cursor.getColumnIndex(MentorEntry._ID);
        int mentorId = cursor.getInt(mentorIdIndex);
        textViewId.setText(Integer.toString(mentorId));
    }
}