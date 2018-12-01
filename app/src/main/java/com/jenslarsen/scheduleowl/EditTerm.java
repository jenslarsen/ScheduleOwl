package com.jenslarsen.scheduleowl;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleContract;
import com.jenslarsen.scheduleowl.db.ScheduleContract.TermEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditTerm extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    CourseCursorAdapter adapter;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener startDatePicker;
    private DatePickerDialog.OnDateSetListener endDatePicker;
    private Uri currentTermUri;
    private EditText editTextTitle;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private ListView listViewCourses;

    private String dateFormat = "yyyy-MM-dd";

    private int TERM_LOADER = 1000;
    private int COURSE_LOADER = 2000;

    private SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    public EditTerm() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);

        Intent intent = getIntent();
        currentTermUri = intent.getData();

        getSupportLoaderManager().initLoader(TERM_LOADER, null, this);
        getSupportLoaderManager().initLoader(COURSE_LOADER, null, this);

        Button deleteButton = findViewById(R.id.buttonDelete);
        TextView textViewAddTerm = findViewById(R.id.textViewAddTerm);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        listViewCourses = findViewById(R.id.listViewCourses);

        if (currentTermUri == null) {
            // No Uri so we must be adding a pet
            textViewAddTerm.setText(getString(R.string.add_new_term));
            deleteButton.setVisibility(View.GONE);
        } else {
            textViewAddTerm.setText(getString(R.string.edit_term));
        }

        calendar = Calendar.getInstance();

        // set up start date picker
        // TODO: Fix date picker so it shows the date from the current term
        startDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartDate();
            }
        };

        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditTerm.this, startDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        // set up end date picker
        // TODO: Fix date picker so it shows the date from the current term
        endDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndDate();
            }
        };

        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditTerm.this, endDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }

    public void buttonSaveClicked(View view) {

        // get input from fields
        String title = editTextTitle.getText().toString().trim();
        String start = editTextStartDate.getText().toString().trim();
        String end = editTextEndDate.getText().toString().trim();

        if (currentTermUri == null
                && TextUtils.isEmpty(title)
                && TextUtils.isEmpty(start)
                && TextUtils.isEmpty(end)) {
            // nothing entered, nothing to do
            return;
        }

        ContentValues values = new ContentValues();
        values.put(TermEntry.TITLE, title);
        values.put(TermEntry.START_DATE, start);
        values.put(TermEntry.END_DATE, end);

        // if this is a new term the Uri will be null
        if (currentTermUri == null) {
            Uri newUri = getContentResolver().insert(TermEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // existing term
            int rowsChanged = getContentResolver().update(currentTermUri, values, null, null);

            if (rowsChanged == 0) {
                Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    public void buttonCancelClicked(View view) {
        finish();
    }

    public void buttonDeleteClicked(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Term")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTerm();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteTerm() {

        // only delete if this is an existing term
        if (currentTermUri != null) {
            int numTermsRemoved = getContentResolver().delete(currentTermUri, null, null);

            if (numTermsRemoved == 0) {
                Toast.makeText(this, getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_successful), Toast.LENGTH_SHORT).show();
            }

        }

        finish();
    }

    private void updateStartDate() {
        editTextStartDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateEndDate() {
        editTextEndDate.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {

        if (id == TERM_LOADER) {
            String[] projection = new String[]{
                    TermEntry._ID,
                    TermEntry.TITLE,
                    TermEntry.START_DATE,
                    TermEntry.END_DATE
            };

            return new CursorLoader(
                    this,
                    TermEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        } else if (id == COURSE_LOADER) {
            String[] projection = new String[]{
                    ScheduleContract.CourseEntry._ID,
                    ScheduleContract.CourseEntry.TITLE,
                    ScheduleContract.CourseEntry.START_DATE,
                    ScheduleContract.CourseEntry.END_DATE,
            };

            // only return courses not already associated with a term
            String selection = ScheduleContract.CourseEntry.TERMID + " IS NULL";

            return new CursorLoader(this,
                    ScheduleContract.CourseEntry.CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);
        }
        Log.e("EditTerm", "Invalid ID: " + id + " in onCreateLoader()!");
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {

        int id = loader.getId();
        Cursor cursor = (Cursor) data;

        // if the cursor is empty, nothing to do
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (id == TERM_LOADER) {

            if (cursor.moveToFirst()) {
                int titleIndex = cursor.getColumnIndex(TermEntry.TITLE);
                int startIndex = cursor.getColumnIndex(TermEntry.START_DATE);
                int endIndex = cursor.getColumnIndex(TermEntry.END_DATE);

                String title = cursor.getString(titleIndex);
                String start = cursor.getString(startIndex);
                String end = cursor.getString(endIndex);

                editTextTitle.setText(title);
                editTextStartDate.setText(start);
                editTextEndDate.setText(end);
            }
        } else if (id == COURSE_LOADER) {
            CourseSelectorCursorAdapter courseAdapter = new CourseSelectorCursorAdapter(this, cursor);
            listViewCourses.setAdapter(courseAdapter);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        int id = loader.getId();

        if (id == TERM_LOADER) {
            editTextTitle.setText("");
            editTextStartDate.setText("");
            editTextEndDate.setText("");
        } else if (id == COURSE_LOADER) {
        }
    }
}
