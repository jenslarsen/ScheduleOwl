package com.jenslarsen.scheduleowl;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleContract;

public class AddAssessment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);
    }

    public void buttonSaveClicked(View view) {
        ContentValues values = new ContentValues();

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        EditText editTextDueDate = findViewById(R.id.editTextDueDate);

        String assessmentName = editTextTitle.getText().toString();
        String assessmentPhone = editTextDueDate.getText().toString();

        if (assessmentName.isEmpty()) {
            Toast.makeText(this, "Missing information! Unable to add new assessment", Toast.LENGTH_SHORT).show();
        } else {
            values.put(ScheduleContract.AssessmentEntry.TITLE, assessmentName);
            values.put(ScheduleContract.AssessmentEntry.DUE_DATE, assessmentPhone);

            Uri newUri = getContentResolver().insert(ScheduleContract.AssessmentEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Insert Assessment Failed!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }
}
