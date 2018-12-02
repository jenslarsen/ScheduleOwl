package com.jenslarsen.scheduleowl;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.jenslarsen.scheduleowl.db.ScheduleContract;

import static com.jenslarsen.scheduleowl.db.ScheduleContract.CourseEntry;

/**
 * {@link CourseSelectorCursorAdapter} is an adapter for a list or grid view that uses a {@link Cursor}
 * of course data as its data source. This adapter knows how to create list items for each row of
 * course data in the {@link Cursor}.
 */

public class CourseSelectorCursorAdapter extends CursorAdapter {

    private int currentTermId;

    public CourseSelectorCursorAdapter(Context context, Cursor cursor, int termId) {
        super(context, cursor, 0);
        currentTermId = termId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem_chooser, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewTitle = view.findViewById(R.id.textViewChooser);
        CheckBox checkBox = view.findViewById(R.id.checkBoxChooser);

        int titleColumn = cursor.getColumnIndex(CourseEntry.TITLE);
        String title = cursor.getString(titleColumn);
        textViewTitle.setText(title);

        int termIdIndex = cursor.getColumnIndex(CourseEntry.TERMID);
        int termId = cursor.getInt(termIdIndex);

        if (termId == currentTermId) {
            checkBox.setChecked(true);
        }
    }
}