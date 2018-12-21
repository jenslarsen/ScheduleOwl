package com.jenslarsen.scheduleowl;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.jenslarsen.scheduleowl.db.ScheduleContract.CourseEntry;

/**
 * {@link CourseSelectorForMentorCursorAdapter} is an adapter for a list or grid view that uses a {@link Cursor}
 * of course data as its data source. This adapter knows how to create list items for each row of
 * course data in the {@link Cursor}.
 */

public class CourseSelectorForMentorCursorAdapter extends CursorAdapter {

    private int currentMentorId;

    public CourseSelectorForMentorCursorAdapter(Context context, Cursor cursor, int mentorId) {
        super(context, cursor, 0);
        currentMentorId = mentorId;
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

        int titleColumn = cursor.getColumnIndex(CourseEntry.TITLE);
        String title = cursor.getString(titleColumn);
        textViewTitle.setText(title);

        int mentorIdIndex = cursor.getColumnIndex(CourseEntry.MENTORID);
        int mentorId = cursor.getInt(mentorIdIndex);

        if (mentorId == currentMentorId) {
            checkBox.setChecked(true);
        }

        int courseIdIndex = cursor.getColumnIndex(CourseEntry._ID);
        int courseId = cursor.getInt(courseIdIndex);
        textViewId.setText(Integer.toString(courseId));
    }
}