package com.jenslarsen.scheduleowl;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleContract.CourseEntry;
import com.jenslarsen.scheduleowl.db.ScheduleContract.TermEntry;
import com.jenslarsen.scheduleowl.db.ScheduleDbHelper;
import com.jenslarsen.scheduleowl.db.ScheduleProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Controls the Add/Edit term activity
 */
public class EditTerm extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    CourseCursorAdapter adapter;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener startDatePicker;
    private DatePickerDialog.OnDateSetListener endDatePicker;
    private Uri currentTermUri = null;
    private int currentTermId = -1;
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

    /**
     * Sets up the Add/Edit Term activity. Uses currentTermId from the intent to determine if this
     * is a new or existing term.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);

        Intent intent = getIntent();
        currentTermUri = intent.getData();
        currentTermId = ScheduleDbHelper.getIdFromUri(currentTermUri);

        getSupportLoaderManager().initLoader(TERM_LOADER, null, this);
        getSupportLoaderManager().initLoader(COURSE_LOADER, null, this);

        Button deleteButton = findViewById(R.id.buttonDelete);
        TextView textViewAddTerm = findViewById(R.id.textViewAddTerm);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        listViewCourses = findViewById(R.id.listViewCourses);

        if (currentTermUri == null) {
            // No Uri so we must be adding a term
            textViewAddTerm.setText(getString(R.string.add_new_term));
            deleteButton.setVisibility(View.GONE);
        } else {
            textViewAddTerm.setText(getString(R.string.edit_term));
            currentTermId = ScheduleDbHelper.getIdFromUri(currentTermUri);
        }

        calendar = Calendar.getInstance();

        // set up start date picker
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

    /**
     * Saves entered term data into the database. Also updates selected courses with the associated
     * termId
     *
     * @param view
     */
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
            Toast.makeText(this, "Unable to save. Nothing entered!", Toast.LENGTH_SHORT).show();
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
                // loop through the courses and update the termId if necessary
                currentTermId = ScheduleDbHelper.getIdFromUri(newUri);
                CheckBox checkBox;
                TextView textViewId;

                for (int index = 0; index < listViewCourses.getCount(); index++) {
                    checkBox = listViewCourses.getChildAt(index).findViewById(R.id.checkBoxChooser);
                    textViewId = listViewCourses.getChildAt(index).findViewById(R.id.textViewId);
                    if (checkBox.isChecked()) {
                        String courseId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(CourseEntry.CONTENT_URI, courseId);
                        ContentValues addTermId = new ContentValues();
                        addTermId.put(CourseEntry.TERMID, currentTermId);
                        int courseRowsUpdated = getContentResolver()
                                .update(uri, addTermId, null, null);
                        if (courseRowsUpdated < 1) {
                            Toast.makeText(this, "Error updating course " + courseId + " with termId!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String courseId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(CourseEntry.CONTENT_URI, courseId);
                        ContentValues addTermId = new ContentValues();
                        addTermId.put(CourseEntry.TERMID, 0);
                        int courseRowsUpdated = getContentResolver()
                                .update(uri, addTermId, null, null);
                        if (courseRowsUpdated < 1) {
                            Toast.makeText(this, "Error updating course " + courseId + " with termId!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // existing term
            int rowsChanged = getContentResolver().update(currentTermUri, values, null, null);

            if (rowsChanged == 0) {
                Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            } else {
                // loop through the courses and update the termId if necessary
                CheckBox checkBox;
                TextView textViewId;

                for (int index = 0; index < listViewCourses.getCount(); index++) {
                    checkBox = listViewCourses.getChildAt(index).findViewById(R.id.checkBoxChooser);
                    textViewId = listViewCourses.getChildAt(index).findViewById(R.id.textViewId);
                    if (checkBox.isChecked()) {
                        String courseId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(CourseEntry.CONTENT_URI, courseId);
                        ContentValues addTermId = new ContentValues();
                        addTermId.put(CourseEntry.TERMID, currentTermId);
                        int courseRowsUpdated = getContentResolver()
                                .update(uri, addTermId, null, null);
                        if (courseRowsUpdated < 1) {
                            Toast.makeText(this, "Error updating course " + courseId + " with termId!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String courseId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(CourseEntry.CONTENT_URI, courseId);
                        ContentValues addTermId = new ContentValues();
                        addTermId.put(CourseEntry.TERMID, 0);
                        int courseRowsUpdated = getContentResolver()
                                .update(uri, addTermId, null, null);
                        if (courseRowsUpdated < 1) {
                            Toast.makeText(this, "Error updating course " + courseId + " with termId!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                Toast.makeText(this, getString(R.string.update_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    /**
     * User cancelled. Nothing to do so finish()
     *
     * @param view
     */
    public void buttonCancelClicked(View view) {
        finish();
    }

    /**
     * Prompt the user if they really want to delete this term, then calls deleteTerm if they do.
     *
     * @param view
     */
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

    /**
     * Removes a term from the database and sends a toast to give the status
     */
    private void deleteTerm() {

        // only delete if this is an existing term
        if (currentTermUri != null) {
            // check if there are any courses associated with this course
            SQLiteDatabase db = ScheduleProvider.dbHelper.getReadableDatabase();

            String[] projection = new String[]{
                    CourseEntry._ID,
                    CourseEntry.TITLE,
                    CourseEntry.TERMID
            };

            String selection = CourseEntry.TERMID + "=?";
            String[] selectionArgs = {Integer.toString(currentTermId)};

            Cursor cursor = db.query(CourseEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, null);

            if (cursor.moveToFirst()) {
                Toast.makeText(this, "Unable to delete Term. There are still courses associated with it.", Toast.LENGTH_SHORT).show();
            } else {
                int numTermsRemoved = getContentResolver().delete(currentTermUri, null, null);

                if (numTermsRemoved == 0) {
                    Toast.makeText(this, getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.delete_successful), Toast.LENGTH_SHORT).show();
                }
            }
        }
        finish();
    }

    /**
     * Popluate start date field
     */
    private void updateStartDate() {
        editTextStartDate.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Populate end date field
     */
    private void updateEndDate() {
        editTextEndDate.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Load data. Uses id to determine if this is term or course data, and uses the currentTermId to
     * determine if this a new or existing term.
     *
     * @param id
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        Uri uri;
        if (id == TERM_LOADER) {
            String[] projection = new String[]{
                    TermEntry._ID,
                    TermEntry.TITLE,
                    TermEntry.START_DATE,
                    TermEntry.END_DATE
            };

            if (currentTermUri == null) {
                uri = TermEntry.CONTENT_URI;
            } else {
                uri = currentTermUri;
                currentTermId = ScheduleDbHelper.getIdFromUri(uri);
            }

            return new CursorLoader(
                    this,
                    uri,
                    projection,
                    null,
                    null,
                    null);
        } else if (id == COURSE_LOADER) {
            String[] projection = new String[]{
                    CourseEntry._ID,
                    CourseEntry.TITLE,
                    CourseEntry.START_DATE,
                    CourseEntry.END_DATE,
                    CourseEntry.TERMID
            };

            if (currentTermUri == null) { // only return courses not associated
                String selection = CourseEntry.TERMID + " IS NULL or " + CourseEntry.TERMID + " = 0";
                return new CursorLoader(this,
                        CourseEntry.CONTENT_URI,
                        projection,
                        selection,
                        null,
                        null);
            } else {  // get terms that are associated with the current term and unassociated terms
                String selection = CourseEntry.TERMID + " IS NULL or " + CourseEntry.TERMID + " = 0  or "
                        + CourseEntry.TERMID + "=?";
                String[] selectionArgs = {String.valueOf(currentTermId)};

                return new CursorLoader(this,
                        CourseEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
            }
        }
        Log.e("EditTerm", "Invalid ID: " + id + " in onCreateLoader()!");
        return null;
    }

    /**
     * Populate the editTexts and course listview once data has been retrieved
     *
     * @param loader
     * @param data   returned data. Cast back to a cursor.
     */
    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {

        int id = loader.getId();
        Cursor cursor = (Cursor) data;

        // if the cursor is empty, nothing to do
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (id == TERM_LOADER) {
            if (currentTermUri == null) {
                // new term - don't load any fields
                return;
            }

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
            // get a list of courses associated with the current term
            CourseSelectorCursorAdapter courseAdapter =
                    new CourseSelectorCursorAdapter(this, cursor, currentTermId);
            listViewCourses.setAdapter(courseAdapter);
        }
    }

    /**
     * Clear the editTexts if the loader is reset
     *
     * @param loader used to get the id to determine if this is a term or course loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        int id = loader.getId();

        if (id == TERM_LOADER) {
            editTextTitle.setText("");
            editTextStartDate.setText("");
            editTextEndDate.setText("");
        } else if (id == COURSE_LOADER) {
            // don't do anything I guess? Not sure if I need to do something here yet.
        }
    }
}
