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
import android.widget.Button;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.db.ScheduleContract;
import com.jenslarsen.scheduleowl.db.ScheduleContract.MentorEntry;

/**
 * Loads the list of mentors, an onClickListener and sets up a FAB action to add a new mentor.
 */
public class FragmentMentors extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int MENTOR_LOADER = 3000;
    private MentorCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_mentors, container, false);

        ListView listView = rootView.findViewById(R.id.listViewMentors);

        adapter = new MentorCursorAdapter(getContext(), null);
        listView.setAdapter(adapter);

        View emptyView = rootView.findViewById(R.id.emptyMentorView);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditMentor.class);
                Uri currentMentorUri = ContentUris.withAppendedId(ScheduleContract.MentorEntry.CONTENT_URI, id);
                intent.setData(currentMentorUri);
                startActivity(intent);
            }
        });

        FloatingActionButton buttonAddMentor = rootView.findViewById(R.id.buttonAddMentor);
        buttonAddMentor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddMentorClicked();
            }
        });

        getLoaderManager().initLoader(MENTOR_LOADER, null, this);

        return rootView;
    }

    public void buttonAddMentorClicked() {
        Intent intent = new Intent(getActivity(), EditMentor.class);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[]{
                MentorEntry._ID,
                MentorEntry.NAME,
                MentorEntry.EMAIL};

        return new CursorLoader(
                getContext(),
                MentorEntry.CONTENT_URI,
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


