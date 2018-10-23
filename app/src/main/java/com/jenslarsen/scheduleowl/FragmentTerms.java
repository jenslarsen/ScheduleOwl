package com.jenslarsen.scheduleowl;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleProvider;
import com.jenslarsen.scheduleowl.db.ScheduleContract.TermEntry;
import com.jenslarsen.scheduleowl.db.ScheduleDbHelper;
import com.jenslarsen.scheduleowl.model.Course;
import com.jenslarsen.scheduleowl.model.Term;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentTerms extends Fragment {

    public final static int ADD_TERM = 1;
    public final static int EDIT_TERM = 2;

    private ArrayAdapter<Term> adapter;
    private int selectedPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_terms, container, false);

        final ListView listView = rootView.findViewById(R.id.listViewTerms);

        adapter = new ArrayAdapter<>(getContext(), R.layout.listitem_tab,
                R.id.textViewListItem, ScheduleProvider.terms);
        listView.setAdapter(adapter);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ScheduleDbHelper dbHelper = new ScheduleDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == ADD_TERM) {
                if (resultCode == RESULT_OK) {
                    String termTitle = data.getStringExtra("termTitle");

                    String stringStartDate = data.getStringExtra("startDate");
                    String stringEndDate = data.getStringExtra("endDate");
                    try {
                        Date startDate = new SimpleDateFormat("DD/mm/yyyy")
                                .parse(stringStartDate);
                    } catch (ParseException e) {
                        Log.e("AddTerm", "Unable to parse start date " + stringStartDate);
                        e.printStackTrace();
                        return;
                    }
                    try {
                        Date endDate = new SimpleDateFormat("DD/mm/yyyy")
                                .parse(stringEndDate);
                    } catch (ParseException e) {
                        Log.e("AddTerm", "Unable to parse end date " + stringStartDate);
                        e.printStackTrace();
                        return;
                    }

                    ArrayList<Course> selectedCourses
                            = (ArrayList<Course>) data.getExtras()
                            .getSerializable("selectedCourses");

                    // insert data into database
                    ContentValues values = new ContentValues();

                    values.put(TermEntry.TITLE, termTitle);
                    values.put(TermEntry.START_DATE, stringStartDate);
                    values.put(TermEntry.END_DATE, stringEndDate);

                    long newRowId = db.insert(TermEntry.TABLE_NAME, null, values);

                    if (newRowId < 0) {
                        Toast.makeText(getContext(), "Unable to update database", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // TODO: Add code to update courses with the correct termId

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No Term added!", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == EDIT_TERM) {
                boolean deleteTerm = data.getExtras().getBoolean("deleteTerm");
                if (deleteTerm) {
                    ScheduleProvider.terms.remove(selectedPosition);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}


