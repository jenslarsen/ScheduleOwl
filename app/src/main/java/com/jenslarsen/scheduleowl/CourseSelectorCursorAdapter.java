package com.jenslarsen.scheduleowl;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.jenslarsen.scheduleowl.db.ScheduleContract.CourseEntry;

/**
 * {@link CourseSelectorCursorAdapter} is an adapter for a list or grid view that uses a {@link Cursor}
 * of course data as its data source. This adapter knows how to create list items for each row of
 * course data in the {@link Cursor}.
 */
public class CourseSelectorCursorAdapter extends CursorAdapter {
    public CourseSelectorCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem_chooser, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewTitle = view.findViewById(R.id.textViewChooser);

        int titleColumn = cursor.getColumnIndex(CourseEntry.TITLE);

        String title = cursor.getString(titleColumn);
        textViewTitle.setText(title);
    }
}