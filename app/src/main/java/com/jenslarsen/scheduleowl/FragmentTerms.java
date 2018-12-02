package com.jenslarsen.scheduleowl;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.db.ScheduleContract.TermEntry;

/**
 * Loads the list of terms, an onClickListener and sets up a FAB action to add a new term.
 */
public class FragmentTerms extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TERM_LOADER = 1000;

    private TermCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_terms, container, false);

        ListView listView = rootView.findViewById(R.id.listViewTerms);

        adapter = new TermCursorAdapter(getContext(), null);
        listView.setAdapter(adapter);

        View emptyView = rootView.findViewById(R.id.emptyTermView);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditTerm.class);
                Uri currentTermUri = ContentUris.withAppendedId(TermEntry.CONTENT_URI, id);
                intent.setData(currentTermUri);
                startActivity(intent);
            }
        });

        FloatingActionButton buttonAddTerm = rootView.findViewById(R.id.buttonAddTerm);
        buttonAddTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddTermClicked();
            }
        });

        getLoaderManager().initLoader(TERM_LOADER, null, this);

        return rootView;
    }

    public void buttonAddTermClicked() {
        Intent intent = new Intent(getActivity(), EditTerm.class);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[]{
                TermEntry._ID,
                TermEntry.TITLE,
                TermEntry.START_DATE};

        return new CursorLoader(
                getContext(),
                TermEntry.CONTENT_URI,
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


