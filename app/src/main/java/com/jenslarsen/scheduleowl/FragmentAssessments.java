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

import com.jenslarsen.scheduleowl.db.ScheduleContract;
import com.jenslarsen.scheduleowl.db.ScheduleContract.AssessmentEntry;

/**
 * Loads the list of assessments, an onClickListener and sets up a FAB action to add a new assessment.
 */
public class FragmentAssessments extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ASSESSMENT_LOADER = 4000;

    private AssessmentCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_assessments, container, false);

        ListView listView = rootView.findViewById(R.id.listViewAssessments);

        adapter = new AssessmentCursorAdapter(getContext(), null);
        listView.setAdapter(adapter);

        View emptyView = rootView.findViewById(R.id.emptyAssessmentView);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditAssessment.class);
                Uri currentTermUri = ContentUris.withAppendedId(ScheduleContract.AssessmentEntry.CONTENT_URI, id);
                intent.setData(currentTermUri);
                startActivity(intent);
            }
        });

        FloatingActionButton buttonAddAssessment = rootView.findViewById(R.id.buttonAddAssessment);
        buttonAddAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddAssessmentClicked();
            }
        });

        getLoaderManager().initLoader(ASSESSMENT_LOADER, null, this);

        return rootView;
    }

    public void buttonAddAssessmentClicked() {
        Intent intent = new Intent(getActivity(), EditAssessment.class);
        startActivity(intent);
        startActivityForResult(intent, ASSESSMENT_LOADER);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[]{
                AssessmentEntry._ID,
                AssessmentEntry.TITLE,
                AssessmentEntry.DUE_DATE};

        return new CursorLoader(
                getContext(),
                AssessmentEntry.CONTENT_URI,
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


