package com.jenslarsen.scheduleowl;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.jenslarsen.scheduleowl.db.ScheduleContract.TermEntry;

/**
 * {@link TermCursorAdapter} is an adapter for a list or grid view that uses a {@link Cursor}
 * of term data as its data source. This adapter knows how to create list items for each row of
 * term data in the {@link Cursor}.
 */
public class TermCursorAdapter extends CursorAdapter {
    public TermCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /*flags*/);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem_tab, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewTitle = view.findViewById(R.id.textViewListItem);
        TextView textViewStartDate = view.findViewById(R.id.textViewSubItem);

        int titleColumn = cursor.getColumnIndex(TermEntry.TITLE);
        int startDateColumn = cursor.getColumnIndex(TermEntry.START_DATE);

        String title = cursor.getString(titleColumn);
        String startDate = cursor.getString(startDateColumn);
        textViewTitle.setText(title);
        textViewStartDate.setText(startDate);
    }
}