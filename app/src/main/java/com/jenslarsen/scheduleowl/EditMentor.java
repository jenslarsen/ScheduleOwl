package com.jenslarsen.scheduleowl;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleContract.CourseEntry;
import com.jenslarsen.scheduleowl.db.ScheduleContract.MentorEntry;
import com.jenslarsen.scheduleowl.db.ScheduleDbHelper;
import com.jenslarsen.scheduleowl.db.ScheduleProvider;

/**
 * Controls the Add/Edit mentor activity
 */
public class EditMentor extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    CourseCursorAdapter adapter;
    private Uri currentMentorUri = null;
    private int currentMentorId = -1;
    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private ListView listViewCourses;

    private int MENTOR_LOADER = 3000;
    private int COURSE_LOADER = 2000;

    public EditMentor() {
    }

    /**
     * Sets up the Add/Edit Mentor activity. Uses currentMentorId from the intent to determine if this
     * is a new or existing mentor.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);

        Intent intent = getIntent();
        currentMentorUri = intent.getData();
        currentMentorId = ScheduleDbHelper.getIdFromUri(currentMentorUri);

        getSupportLoaderManager().initLoader(MENTOR_LOADER, null, this);
        getSupportLoaderManager().initLoader(COURSE_LOADER, null, this);

        Button deleteButton = findViewById(R.id.buttonDelete);
        TextView textViewAddMentor = findViewById(R.id.textViewAddMentor);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        listViewCourses = findViewById(R.id.listViewCourses);

        if (currentMentorUri == null) {
            // No Uri so we must be adding a mentor
            textViewAddMentor.setText(getString(R.string.add_new_mentor));
            deleteButton.setVisibility(View.GONE);
        } else {
            textViewAddMentor.setText(getString(R.string.edit_mentor));
            currentMentorId = ScheduleDbHelper.getIdFromUri(currentMentorUri);
        }
    }

    /**
     * Saves entered mentor data into the database. Also updates selected courses with the associated
     * mentorId
     *
     * @param view
     */
    public void buttonSaveClicked(View view) {
        // get input from fields
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (currentMentorUri == null
                && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(phone)
                && TextUtils.isEmpty(email)) {
            // nothing entered, nothing to do
            Toast.makeText(this, "Unable to save. Nothing entered!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MentorEntry.NAME, name);
        values.put(MentorEntry.PHONE, phone);
        values.put(MentorEntry.EMAIL, email);

        // if this is a new mentor the Uri will be null
        if (currentMentorUri == null) {
            Uri newUri = getContentResolver().insert(MentorEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                // loop through the courses and update the mentorId if necessary
                currentMentorId = ScheduleDbHelper.getIdFromUri(newUri);
                CheckBox checkBox;
                TextView textViewId;

                for (int index = 0; index < listViewCourses.getCount(); index++) {
                    checkBox = listViewCourses.getChildAt(index).findViewById(R.id.checkBoxChooser);
                    textViewId = listViewCourses.getChildAt(index).findViewById(R.id.textViewId);
                    if (checkBox.isChecked()) {
                        String courseId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(CourseEntry.CONTENT_URI, courseId);
                        ContentValues addMentorId = new ContentValues();
                        addMentorId.put(CourseEntry.MENTORID, currentMentorId);
                        int courseRowsUpdated = getContentResolver()
                                .update(uri, addMentorId, null, null);
                        if (courseRowsUpdated < 1) {
                            Toast.makeText(this, "Error updating course " + courseId + " with mentorId!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String courseId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(CourseEntry.CONTENT_URI, courseId);
                        ContentValues addMentorId = new ContentValues();
                        addMentorId.put(CourseEntry.MENTORID, 0);
                        int courseRowsUpdated = getContentResolver()
                                .update(uri, addMentorId, null, null);
                        if (courseRowsUpdated < 1) {
                            Toast.makeText(this, "Error updating course " + courseId + " with mentorId!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // existing mentor
            int rowsChanged = getContentResolver().update(currentMentorUri, values, null, null);

            if (rowsChanged == 0) {
                Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            } else {
                // loop through the courses and update the mentorId if necessary
                CheckBox checkBox;
                TextView textViewId;

                for (int index = 0; index < listViewCourses.getCount(); index++) {
                    checkBox = listViewCourses.getChildAt(index).findViewById(R.id.checkBoxChooser);
                    textViewId = listViewCourses.getChildAt(index).findViewById(R.id.textViewId);
                    if (checkBox.isChecked()) {
                        String courseId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(CourseEntry.CONTENT_URI, courseId);
                        ContentValues addMentorId = new ContentValues();
                        addMentorId.put(CourseEntry.MENTORID, currentMentorId);
                        int courseRowsUpdated = getContentResolver()
                                .update(uri, addMentorId, null, null);
                        if (courseRowsUpdated < 1) {
                            Toast.makeText(this, "Error updating course " + courseId + " with mentorId!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String courseId = textViewId.getText().toString();
                        Uri uri = Uri.withAppendedPath(CourseEntry.CONTENT_URI, courseId);
                        ContentValues addMentorId = new ContentValues();
                        addMentorId.put(CourseEntry.MENTORID, 0);
                        int courseRowsUpdated = getContentResolver()
                                .update(uri, addMentorId, null, null);
                        if (courseRowsUpdated < 1) {
                            Toast.makeText(this, "Error updating course " + courseId + " with mentorId!", Toast.LENGTH_SHORT).show();
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
     * Prompt the user if they really want to delete this mentor, then calls deleteMentor if they do.
     *
     * @param view
     */
    public void buttonDeleteClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Mentor")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMentor();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    /**
     * Removes a mentor from the database and sends a toast to give the status
     */
    private void deleteMentor() {

        // only delete if this is an existing mentor
        if (currentMentorUri != null) {
            // check if there are any courses associated with this course
            SQLiteDatabase db = ScheduleProvider.dbHelper.getReadableDatabase();

            String[] projection = new String[]{
                    CourseEntry._ID,
                    CourseEntry.TITLE,
                    CourseEntry.MENTORID
            };

            String selection = CourseEntry.MENTORID + "=?";
            String[] selectionArgs = {Integer.toString(currentMentorId)};

            Cursor cursor = db.query(CourseEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, null);

            if (cursor.moveToFirst()) {
                Toast.makeText(this, "Unable to delete Mentor. There are still courses associated with it.", Toast.LENGTH_SHORT).show();
            } else {
                int numMentorsRemoved = getContentResolver().delete(currentMentorUri, null, null);

                if (numMentorsRemoved == 0) {
                    Toast.makeText(this, getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.delete_successful), Toast.LENGTH_SHORT).show();
                }
            }
        }
        finish();
    }

    /**
     * Load data. Uses id to determine if this is mentor or course data, and uses the currentMentorId to
     * determine if this a new or existing mentor.
     *
     * @param id
     * @param bundle
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        Uri uri;
        if (id == MENTOR_LOADER) {
            String[] projection = new String[]{
                    MentorEntry._ID,
                    MentorEntry.NAME,
                    MentorEntry.PHONE,
                    MentorEntry.EMAIL
            };

            if (currentMentorUri == null) {
                uri = MentorEntry.CONTENT_URI;
            } else {
                uri = currentMentorUri;
                currentMentorId = ScheduleDbHelper.getIdFromUri(uri);
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
                    CourseEntry.MENTORID
            };

            if (currentMentorUri == null) { // only return courses not associated
                String selection = CourseEntry.MENTORID + " IS NULL or " + CourseEntry.MENTORID + " = 0";
                return new CursorLoader(this,
                        CourseEntry.CONTENT_URI,
                        projection,
                        selection,
                        null,
                        null);
            } else {  // get mentors that are associated with the current mentor and unassociated mentors
                String selection = CourseEntry.MENTORID + " IS NULL or " + CourseEntry.MENTORID + " = 0  or "
                        + CourseEntry.MENTORID + "=?";
                String[] selectionArgs = {String.valueOf(currentMentorId)};

                return new CursorLoader(this,
                        CourseEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
            }
        }
        Log.e("EditMentor", "Invalid ID: " + id + " in onCreateLoader()!");
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

        if (id == MENTOR_LOADER) {
            if (currentMentorUri == null) {
                // new mentor - don't load any fields
                return;
            }

            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(MentorEntry.NAME);
                int phoneIndex = cursor.getColumnIndex(MentorEntry.PHONE);
                int emailIndex = cursor.getColumnIndex(MentorEntry.EMAIL);

                String title = cursor.getString(nameIndex);
                String start = cursor.getString(phoneIndex);
                String end = cursor.getString(emailIndex);

                editTextName.setText(title);
                editTextPhone.setText(start);
                editTextEmail.setText(end);
            }
        } else if (id == COURSE_LOADER) {
            // get a list of courses associated with the current mentor
            CourseSelectorForMentorCursorAdapter courseAdapter =
                    new CourseSelectorForMentorCursorAdapter(this, cursor, currentMentorId);
            listViewCourses.setAdapter(courseAdapter);
        }
    }

    /**
     * Clear the editTexts if the loader is reset
     *
     * @param loader used to get the id to determine if this is a mentor or course loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        int id = loader.getId();

        if (id == MENTOR_LOADER) {
            editTextName.setText("");
            editTextPhone.setText("");
            editTextEmail.setText("");
        } else if (id == COURSE_LOADER) {
            // don't do anything I guess? Not sure if I need to do something here yet.
        }
    }
}
