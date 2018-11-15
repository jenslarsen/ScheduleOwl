package com.jenslarsen.scheduleowl;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleContract;

public class AddMentor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mentor);
    }

    public void buttonSaveClicked(View view) {
        ContentValues values = new ContentValues();

        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextPhone = findViewById(R.id.editTextPhone);
        EditText editTextEmail = findViewById(R.id.editTextEmail);

        String mentorName = editTextName.getText().toString();
        String mentorPhone = editTextPhone.getText().toString();
        String mentorEmail = editTextEmail.getText().toString();

        if (mentorName.isEmpty()) {
            Toast.makeText(this, "Missing information! Unable to add new mentor", Toast.LENGTH_SHORT).show();
        } else {
            values.put(ScheduleContract.MentorEntry.NAME, mentorName);
            values.put(ScheduleContract.MentorEntry.PHONE, mentorPhone);
            values.put(ScheduleContract.MentorEntry.EMAIL, mentorEmail);

            Uri newUri = getContentResolver().insert(ScheduleContract.MentorEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Insert Mentor Failed!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }
}
