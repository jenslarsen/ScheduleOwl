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
import android.widget.TextView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleContract.AssessmentEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditAssessment extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    CourseChooserAdapter adapter;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener dueDatePicker;
    private Uri currentAssessmentUri;
    private EditText editTextTitle;
    private EditText editTextDueDate;

    private String dateFormat = "yyyy-MM-dd";

    private SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    public EditAssessment() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO: set alerts for goal dates, that will trigger when the application is not running

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);

        Intent intent = getIntent();
        currentAssessmentUri = intent.getData();

        Button deleteButton = findViewById(R.id.buttonDelete);
        TextView textViewEditAssessment = findViewById(R.id.textViewEditAssessment);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDueDate = findViewById(R.id.editTextDueDate);

        if (currentAssessmentUri == null) {
            // No Uri so we must be adding an assessment
            textViewEditAssessment.setText(getString(R.string.add_new_assessment));
            deleteButton.setVisibility(View.GONE);
        } else {
            textViewEditAssessment.setText(getString(R.string.edit_assessment));
            int EDIT_ASSESSMENT = 1000;
            getSupportLoaderManager().initLoader(EDIT_ASSESSMENT, null, this);
        }

        calendar = Calendar.getInstance();

        // set up due date picker
        // TODO: Fix date picker so it shows the date from the current assessment
        dueDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDueDate();
            }
        };

        editTextDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditAssessment.this, dueDatePicker,
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
        String due = editTextDueDate.getText().toString().trim();

        if (currentAssessmentUri == null
                && TextUtils.isEmpty(title)
                && TextUtils.isEmpty(due)) {
            // nothing entered, nothing to do
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AssessmentEntry.TITLE, title);
        values.put(AssessmentEntry.DUE_DATE, due);

        // if this is a new assessment the Uri will be null
        if (currentAssessmentUri == null) {
            Uri newUri = getContentResolver().insert(AssessmentEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // existing assessment
            int rowsChanged = getContentResolver().update(currentAssessmentUri, values, null, null);

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
                .setTitle("Delete Assessment")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAssessment();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteAssessment() {

        // only delete if this is an existing assessment
        if (currentAssessmentUri != null) {
            int numAssessmentsRemoved = getContentResolver().delete(currentAssessmentUri, null, null);

            if (numAssessmentsRemoved == 0) {
                Toast.makeText(this, getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_successful), Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void updateDueDate() {
        editTextDueDate.setText(sdf.format(calendar.getTime()));
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[]{
                AssessmentEntry._ID,
                AssessmentEntry.TITLE,
                AssessmentEntry.DUE_DATE
        };

        return new CursorLoader(
                this,
                AssessmentEntry.CONTENT_URI,
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
            int titleIndex = cursor.getColumnIndex(AssessmentEntry.TITLE);
            int dueIndex = cursor.getColumnIndex(AssessmentEntry.DUE_DATE);

            String title = cursor.getString(titleIndex);
            String due = cursor.getString(dueIndex);

            editTextTitle.setText(title);
            editTextDueDate.setText(due);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        editTextTitle.setText("");
        editTextDueDate.setText("");
    }
}
