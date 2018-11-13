package com.jenslarsen.scheduleowl;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.db.ScheduleContract.TermEntry;
import com.jenslarsen.scheduleowl.db.ScheduleDbHelper;

public class FragmentTerms extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int ADD_TERM = 1;
    private final static int EDIT_TERM = 2;

    private int selectedPosition;
    private ScheduleDbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        TermCursorAdapter adapter;
        Cursor cursor;

        View rootView = inflater.inflate(R.layout.fragment_terms, container, false);

        ListView listView = rootView.findViewById(R.id.listViewTerms);

        dbHelper = new ScheduleDbHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String[] projection = {TermEntry._ID, TermEntry.TITLE, TermEntry.START_DATE, TermEntry.END_DATE};
        cursor = database.query(
                TermEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        adapter = new TermCursorAdapter(getContext(), cursor);
        View emptyView = rootView.findViewById(R.id.emptyTermItem);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                termItemClicked(selectedPosition);
            }
        });

        Button buttonAddTerm = rootView.findViewById(R.id.buttonAddTerm);
        buttonAddTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddTermClicked();
            }
        });
        return rootView;
    }

    public void buttonAddTermClicked() {
        Intent intent = new Intent(getActivity(), AddTerm.class);
        startActivityForResult(intent, ADD_TERM);
    }

    public void termItemClicked(int selectedPosition) {
        Intent intent = new Intent(getActivity(), EditTerm.class);
        intent.putExtra("selectedPosition", selectedPosition);
        startActivityForResult(intent, EDIT_TERM);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }
}


