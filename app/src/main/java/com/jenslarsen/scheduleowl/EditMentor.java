package com.jenslarsen.scheduleowl;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleContract.MentorEntry;

/**
 * Controls the Add/Edit term activity
 */
public class EditMentor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentMentorUri;
    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextEmail;

    public EditMentor() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);

        Intent intent = getIntent();
        currentMentorUri = intent.getData();

        Button deleteButton = findViewById(R.id.buttonDelete);
        TextView textViewAddMentor = findViewById(R.id.textViewAddMentor);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);

        if (currentMentorUri == null) {
            // No Uri so we must be adding a pet
            textViewAddMentor.setText(getString(R.string.add_new_mentor));
            deleteButton.setVisibility(View.GONE);
        } else {
            textViewAddMentor.setText(getString(R.string.edit_mentor));
            int EDIT_MENTOR = 1000;
            getSupportLoaderManager().initLoader(EDIT_MENTOR, null, this);
        }
    }

    public void buttonSaveClicked(View view) {

        // get input from fields
        String title = editTextName.getText().toString().trim();
        String start = editTextPhone.getText().toString().trim();
        String end = editTextEmail.getText().toString().trim();

        if (currentMentorUri == null
                && TextUtils.isEmpty(title)
                && TextUtils.isEmpty(start)
                && TextUtils.isEmpty(end)) {
            // nothing entered, nothing to do
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MentorEntry.NAME, title);
        values.put(MentorEntry.PHONE, start);
        values.put(MentorEntry.EMAIL, end);

        // if this is a new mentor the Uri will be null
        if (currentMentorUri == null) {
            Uri newUri = getContentResolver().insert(MentorEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // existing mentor
            int rowsChanged = getContentResolver().update(currentMentorUri, values, null, null);

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

    private void deleteMentor() {

        // only delete if this is an existing mentor
        if (currentMentorUri != null) {
            int numMentorsRemoved = getContentResolver().delete(currentMentorUri, null, null);

            if (numMentorsRemoved == 0) {
                Toast.makeText(this, getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_successful), Toast.LENGTH_SHORT).show();
            }

        }

        finish();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[]{
                MentorEntry._ID,
                MentorEntry.NAME,
                MentorEntry.PHONE,
                MentorEntry.EMAIL
        };

        return new CursorLoader(
                this,
                MentorEntry.CONTENT_URI,
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
            int titleIndex = cursor.getColumnIndex(MentorEntry.NAME);
            int startIndex = cursor.getColumnIndex(MentorEntry.PHONE);
            int endIndex = cursor.getColumnIndex(MentorEntry.EMAIL);

            String title = cursor.getString(titleIndex);
            String start = cursor.getString(startIndex);
            String end = cursor.getString(endIndex);

            editTextName.setText(title);
            editTextPhone.setText(start);
            editTextEmail.setText(end);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        editTextName.setText("");
        editTextPhone.setText("");
        editTextEmail.setText("");
    }
}
