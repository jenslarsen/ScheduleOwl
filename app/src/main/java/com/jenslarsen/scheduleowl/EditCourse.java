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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleContract.CourseEntry;
import com.jenslarsen.scheduleowl.db.ScheduleProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditCourse extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    AssessmentChooserAdapter adapter;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener startDatePicker;
    private DatePickerDialog.OnDateSetListener endDatePicker;
    private Uri currentCourseUri;
    private EditText editTextTitle;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private EditText editTextNotes;

    private String dateFormat = "yyyy-MM-dd";

    private SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    public EditCourse() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        Intent intent = getIntent();
        currentCourseUri = intent.getData();

        Button deleteButton = findViewById(R.id.buttonDelete);
        TextView textViewAddCourse = findViewById(R.id.textViewAddCourse);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextNotes = findViewById(R.id.editTextNotes);

        if (currentCourseUri == null) {
            // No Uri so we must be adding a pet
            textViewAddCourse.setText(getString(R.string.add_new_course));
            deleteButton.setVisibility(View.GONE);
        } else {
            textViewAddCourse.setText(getString(R.string.edit_course));
            int EDIT_COURSE = 1000;
            getSupportLoaderManager().initLoader(EDIT_COURSE, null, this);
        }

        // set up array adapter
        ListView listView = findViewById(R.id.listViewAssessments);
        adapter = new AssessmentChooserAdapter(this, ScheduleProvider.assessments);
        listView.setAdapter(adapter);

        calendar = Calendar.getInstance();

        // set up start date picker
        // TODO: Fix date picker so it shows the date from the current course
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
                new DatePickerDialog(EditCourse.this, startDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        // set up end date picker
        // TODO: Fix date picker so it shows the date from the current course
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
                new DatePickerDialog(EditCourse.this, endDatePicker,
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

        if (currentCourseUri == null
                && TextUtils.isEmpty(title)
                && TextUtils.isEmpty(start)
                && TextUtils.isEmpty(end)) {
            // nothing entered, nothing to do
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CourseEntry.TITLE, title);
        values.put(CourseEntry.START_DATE, start);
        values.put(CourseEntry.END_DATE, end);

        // if this is a new course the Uri will be null
        if (currentCourseUri == null) {
            Uri newUri = getContentResolver().insert(CourseEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // existing course
            int rowsChanged = getContentResolver().update(currentCourseUri, values, null, null);

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
                .setTitle("Delete Course")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCourse();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteCourse() {

        // only delete if this is an existing course
        if (currentCourseUri != null) {
            int numCoursesRemoved = getContentResolver().delete(currentCourseUri, null, null);

            if (numCoursesRemoved == 0) {
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[]{
                CourseEntry._ID,
                CourseEntry.TITLE,
                CourseEntry.START_DATE,
                CourseEntry.END_DATE
        };

        return new CursorLoader(
                this,
                CourseEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        // if the cursor is empty, nothing to do
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndex(CourseEntry.TITLE);
            int startIndex = cursor.getColumnIndex(CourseEntry.START_DATE);
            int endIndex = cursor.getColumnIndex(CourseEntry.END_DATE);

            String title = cursor.getString(titleIndex);
            String start = cursor.getString(startIndex);
            String end = cursor.getString(endIndex);

            editTextTitle.setText(title);
            editTextStartDate.setText(start);
            editTextEndDate.setText(end);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        editTextTitle.setText("");
        editTextStartDate.setText("");
        editTextEndDate.setText("");
    }

    public void buttonShareNotesClicked(View view) {
        Intent notesIntent = new Intent();
        notesIntent.setAction(Intent.ACTION_SEND);
        notesIntent.putExtra(Intent.EXTRA_TEXT,
                editTextTitle.getText().toString().trim() + ": "
                        + editTextNotes.getText().toString().trim());
        notesIntent.setType("text/plain");
        startActivity(Intent.createChooser(notesIntent, getResources().getText(R.string.send_to)));

    }
}
