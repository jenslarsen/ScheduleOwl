package com.jenslarsen.scheduleowl;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleContract;
import com.jenslarsen.scheduleowl.db.ScheduleContract.CourseEntry;
import com.jenslarsen.scheduleowl.db.ScheduleContract.MentorEntry;
import com.jenslarsen.scheduleowl.db.ScheduleDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditCourse extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    AssessmentChooserAdapter adapter;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener startDatePicker;
    private DatePickerDialog.OnDateSetListener endDatePicker;
    private Uri currentCourseUri = null;
    private int currentCourseId = -1;
    private EditText editTextTitle;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private EditText editTextNotes;
    private TextView textViewMentorName;
    private TextView textViewMentorPhone;
    private TextView textViewMentorEmail;
    private ListView listViewAssessments;
    private Spinner spinnerCourseStatus;
    private boolean startBoxChecked;
    private boolean endBoxChecked;
    private int courseStatus;
    private int currentMentorId;


    private String dateFormat = "yyyy-MM-dd";

    private int COURSE_LOADER = 2000;
    private int MENTOR_LOADER = 3000;
    private int ASSESSMENT_LOADER = 4000;

    private SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    public EditCourse() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        Intent intent = getIntent();
        currentCourseUri = intent.getData();

        startBoxChecked = false;
        endBoxChecked = false;

        getSupportLoaderManager().initLoader(COURSE_LOADER, null, this);
        getSupportLoaderManager().initLoader(ASSESSMENT_LOADER, null, this);
        getSupportLoaderManager().initLoader(MENTOR_LOADER, null, this);

        Button deleteButton = findViewById(R.id.buttonDelete);
        TextView textViewAddCourse = findViewById(R.id.textViewAddCourse);
        spinnerCourseStatus = findViewById(R.id.spinnerCourseStatus);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextNotes = findViewById(R.id.editTextNotes);
        textViewMentorName = findViewById(R.id.textViewMentorName);
        textViewMentorPhone = findViewById(R.id.textViewMentorPhone);
        textViewMentorEmail = findViewById(R.id.textViewMentorEmail);
        listViewAssessments = findViewById(R.id.listViewAssessments);
        CheckBox checkBoxStart = findViewById(R.id.checkBoxStart);
        CheckBox checkBoxEnd = findViewById(R.id.checkBoxEnd);


        // set up spinner
        spinnerCourseStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseStatus = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        if (currentCourseUri == null) {
            // No Uri so we must be adding a course
            textViewAddCourse.setText(getString(R.string.add_new_course));
            deleteButton.setVisibility(View.GONE);
        } else {
            textViewAddCourse.setText(getString(R.string.edit_course));
            currentCourseId = ScheduleDbHelper.getIdFromUri(currentCourseUri);
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
                new DatePickerDialog(EditCourse.this, startDatePicker,
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
                new DatePickerDialog(EditCourse.this, endDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        // set up checkbox onClickListeners
        checkBoxStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startBoxChecked = checkBoxStart.isChecked();
                startBoxChecked = !startBoxChecked;
            }
        });

        checkBoxEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // endBoxChecked = checkBoxEnd.isChecked();
                endBoxChecked = !endBoxChecked;
            }
        });
    }

    public void buttonSaveClicked(View view) {

        // get input from fields
        String title = editTextTitle.getText().toString().trim();
        String start = editTextStartDate.getText().toString().trim();
        String end = editTextEndDate.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();
        courseStatus = spinnerCourseStatus.getSelectedItemPosition();

        if (currentCourseUri == null
                && TextUtils.isEmpty(title)
                && TextUtils.isEmpty(start)
                && TextUtils.isEmpty(end)) {
            Toast.makeText(this, "Unable to save. Nothing entered!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CourseEntry.TITLE, title);
        values.put(CourseEntry.START_DATE, start);
        values.put(CourseEntry.END_DATE, end);
        values.put(CourseEntry.STATUS, courseStatus);
        values.put(CourseEntry.NOTES, notes);

        // set up alerts
        if (startBoxChecked) {
            try {
                Date date = sdf.parse(editTextStartDate.getText().toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                createAlert(cal, editTextTitle.getText().toString(), "Starting today");
            } catch (ParseException e) {
                Toast.makeText(this, "Unable to parse start date!", Toast.LENGTH_SHORT).show();
            }
        }

        if (endBoxChecked) {
            try {
                Date date = sdf.parse(editTextEndDate.getText().toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                createAlert(cal, editTextTitle.getText().toString(), "Ending today");
            } catch (ParseException e) {
                Toast.makeText(this, "Unable to parse end date!", Toast.LENGTH_SHORT).show();
            }
        }

        // if this is a new course the Uri will be null
        if (currentCourseUri == null) {
            Uri newUri = getContentResolver().insert(CourseEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                // loop through the assessments and update the courseId if necessary
                currentCourseId = ScheduleDbHelper.getIdFromUri(newUri);
                CheckBox checkBox;
                TextView textViewId;

                for (int index = 0; index < listViewAssessments.getCount(); index++) {
                    checkBox = listViewAssessments.getChildAt(index).findViewById(R.id.checkBoxChooser);
                    textViewId = listViewAssessments.getChildAt(index).findViewById(R.id.textViewId);
                    if (checkBox.isChecked()) {
                        // update the assessment with the currentCourseId
                        String assessmentId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(ScheduleContract.AssessmentEntry.CONTENT_URI, assessmentId);
                        ContentValues addCourseId = new ContentValues();
                        addCourseId.put(ScheduleContract.AssessmentEntry.COURSEID, currentCourseId);
                        int assessmentRowsUpdated = getContentResolver()
                                .update(uri, addCourseId, null, null);
                        if (assessmentRowsUpdated < 1) {
                            Toast.makeText(this, "Error updating assessment " + assessmentId + " with courseId!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // remove the courseId from the assessment
                        String assessmentId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(ScheduleContract.AssessmentEntry.CONTENT_URI, assessmentId);
                        ContentValues addCourseId = new ContentValues();
                        addCourseId.put(ScheduleContract.AssessmentEntry.COURSEID, 0);
                        int assessmentRowsUpdated = getContentResolver()
                                .update(uri, addCourseId, null, null);
                        if (assessmentRowsUpdated < 1) {
                            Toast.makeText(this, "Error removing courseId from " + assessmentId, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // existing course
            int rowsChanged = getContentResolver().update(currentCourseUri, values, null, null);

            if (rowsChanged == 0) {
                Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            } else {
                CheckBox checkBox;
                TextView textViewId;

                for (int index = 0; index < listViewAssessments.getCount(); index++) {
                    checkBox = listViewAssessments.getChildAt(index).findViewById(R.id.checkBoxChooser);
                    textViewId = listViewAssessments.getChildAt(index).findViewById(R.id.textViewId);
                    if (checkBox.isChecked()) {
                        String assessmentId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(ScheduleContract.AssessmentEntry.CONTENT_URI, assessmentId);
                        ContentValues addCourseId = new ContentValues();
                        addCourseId.put(ScheduleContract.AssessmentEntry.COURSEID, currentCourseId);
                        int assessmentRowsUpdated = getContentResolver()
                                .update(uri, addCourseId, null, null);
                        if (assessmentRowsUpdated < 1) {
                            Toast.makeText(this, "Error updating assessment " + assessmentId + " with courseId!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // remove the courseId from the assessment
                        String assessmentId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(ScheduleContract.AssessmentEntry.CONTENT_URI, assessmentId);
                        ContentValues addCourseId = new ContentValues();
                        addCourseId.put(ScheduleContract.AssessmentEntry.COURSEID, 0);
                        int assessmentRowsUpdated = getContentResolver()
                                .update(uri, addCourseId, null, null);
                        if (assessmentRowsUpdated < 1) {
                            Toast.makeText(this, "Error removing courseId from " + assessmentId, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        Uri uri;
        if (id == COURSE_LOADER) {
            String[] projection = new String[]{
                    CourseEntry._ID,
                    CourseEntry.TITLE,
                    CourseEntry.START_DATE,
                    CourseEntry.END_DATE,
                    CourseEntry.STATUS,
                    CourseEntry.NOTES,
                    CourseEntry.MENTORID
            };

            if (currentCourseUri == null) {
                uri = CourseEntry.CONTENT_URI;
            } else {
                uri = currentCourseUri;
                currentCourseId = ScheduleDbHelper.getIdFromUri(uri);
            }

            return new CursorLoader(
                    this,
                    uri,
                    projection,
                    null,
                    null,
                    null);
        } else if (id == ASSESSMENT_LOADER) {
            String[] projection = new String[]{
                    ScheduleContract.AssessmentEntry._ID,
                    ScheduleContract.AssessmentEntry.TITLE,
                    ScheduleContract.AssessmentEntry.DUE_DATE,
                    ScheduleContract.AssessmentEntry.COURSEID
            };

            if (currentCourseUri == null) { // only return assessments not associated
                String selection = ScheduleContract.AssessmentEntry.COURSEID + " IS NULL";

                return new CursorLoader(this,
                        ScheduleContract.AssessmentEntry.CONTENT_URI,
                        projection,
                        selection,
                        null,
                        null);
            } else {  // get assessments that are associated with the current course and unassociated courses
                String selection = ScheduleContract.AssessmentEntry.COURSEID + " IS NULL or "
                        + ScheduleContract.AssessmentEntry.COURSEID + "=?";

                String[] selectionArgs = {Integer.toString(currentCourseId)};

                return new CursorLoader(this,
                        ScheduleContract.AssessmentEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
            }
        } else if (id == MENTOR_LOADER) {
            String selection = MentorEntry._ID + " = ?";

            String[] selectionArgs = {Integer.toString(currentMentorId)};

            String[] projection = new String[]{
                    ScheduleContract.MentorEntry._ID,
                    ScheduleContract.MentorEntry.NAME,
                    ScheduleContract.MentorEntry.EMAIL,
                    ScheduleContract.MentorEntry.PHONE
            };

            return new CursorLoader(this,
                    ScheduleContract.MentorEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
        }
        Log.e("EditCourse", "Invalid ID: " + id + " in onCreateLoader()!");
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

        if (id == COURSE_LOADER) {
            if (currentCourseUri == null) {
                // new course - don't load any fields
                return;
            }

            if (cursor.moveToFirst()) {
                int titleIndex = cursor.getColumnIndex(CourseEntry.TITLE);
                int startIndex = cursor.getColumnIndex(CourseEntry.START_DATE);
                int endIndex = cursor.getColumnIndex(CourseEntry.END_DATE);
                int statusIndex = cursor.getColumnIndex(CourseEntry.STATUS);
                int notesIndex = cursor.getColumnIndex(CourseEntry.NOTES);
                int mentorIndex = cursor.getColumnIndex(CourseEntry.MENTORID);

                String title = cursor.getString(titleIndex);
                String start = cursor.getString(startIndex);
                String end = cursor.getString(endIndex);
                courseStatus = cursor.getInt(statusIndex);
                String notes = cursor.getString(notesIndex);
                currentMentorId = cursor.getInt(mentorIndex);

                editTextTitle.setText(title);
                editTextStartDate.setText(start);
                editTextEndDate.setText(end);
                spinnerCourseStatus.setSelection(courseStatus);
                editTextNotes.setText(notes);
            }
        } else if (id == ASSESSMENT_LOADER) {
            // get a list of assessments associated with the current course
            AssessmentSelectorCursorAdapter assessmentAdapter =
                    new AssessmentSelectorCursorAdapter(this, cursor, currentCourseId);
            listViewAssessments.setAdapter(assessmentAdapter);
        } else if (id == MENTOR_LOADER) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(MentorEntry.NAME);
                int phoneIndex = cursor.getColumnIndex(MentorEntry.PHONE);
                int emailIndex = cursor.getColumnIndex(MentorEntry.EMAIL);

                String name = cursor.getString(nameIndex);
                String phone = cursor.getString(phoneIndex);
                String email = cursor.getString(emailIndex);

                textViewMentorName.setText(name);
                textViewMentorPhone.setText(phone);
                textViewMentorEmail.setText(email);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        int id = loader.getId();
        if (id == COURSE_LOADER) {
            editTextTitle.setText("");
            editTextStartDate.setText("");
            editTextEndDate.setText("");
            editTextNotes.setText("");
        } else if (id == ASSESSMENT_LOADER) {
            // don't do anything I guess? Not sure if I need to do something here yet.
        } else if (id == MENTOR_LOADER) {
            textViewMentorName.setText("");
            textViewMentorPhone.setText("");
            textViewMentorEmail.setText("");

        }
    }

    public void buttonShareNotesClicked(View view) {

        if (editTextNotes.getText().toString().trim().isEmpty()) {
            // nothing entered - nothing to share
            Toast.makeText(this, "Please enter notes before sharing", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent notesIntent = new Intent();
        notesIntent.setAction(Intent.ACTION_SEND);
        notesIntent.putExtra(Intent.EXTRA_TEXT,
                editTextTitle.getText().toString().trim() + ": "
                        + editTextNotes.getText().toString().trim());
        notesIntent.setType("text/plain");
        startActivity(Intent.createChooser(notesIntent, getResources().getText(R.string.send_to)));
    }

    public void createAlert(Calendar alertDate, String alertTitle, String alertSubText) {
        long alert = alertDate.getTimeInMillis();
        Intent intent = new Intent(EditCourse.this, ScheduleReceiver.class);
        intent.putExtra("title", alertTitle);
        intent.putExtra("subText", alertSubText);
        PendingIntent sender = PendingIntent.getBroadcast(EditCourse.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alert, sender);
    }
}