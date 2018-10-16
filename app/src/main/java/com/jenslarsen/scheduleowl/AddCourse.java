package com.jenslarsen.scheduleowl;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.Datasource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddCourse extends AppCompatActivity {

    AssessmentChooserAdapter adapter;

    public EditText editTextStartDate;
    public EditText editTextEndDate;
    public EditText editTextTitle;

    public Calendar calendar;
    DatePickerDialog.OnDateSetListener startDatePicker;
    DatePickerDialog.OnDateSetListener endDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);

        // set up array adapter
        final ListView listView = findViewById(R.id.listViewAssessments);

        adapter = new AssessmentChooserAdapter(this, Datasource.assessments);
        listView.setAdapter(adapter);

        calendar = Calendar.getInstance();

        // set up start date picker
        editTextStartDate = findViewById(R.id.editTextStartDate);
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
                new DatePickerDialog(AddCourse.this, startDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        // set up end date picker
        editTextEndDate = findViewById(R.id.editTextEndDate);
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
                new DatePickerDialog(AddCourse.this, endDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }

    private void updateStartDate() {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        editTextStartDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateEndDate() {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        editTextEndDate.setText(sdf.format(calendar.getTime()));
    }

    public void buttonSaveClicked(View view) {
        Intent intent = new Intent();

        String courseTitle = editTextTitle.getText().toString();
        String startDate = editTextStartDate.getText().toString();
        String endDate = editTextEndDate.getText().toString();
        if (courseTitle.isEmpty()) {
            Toast.makeText(this,
                    "No title entered! Unable to add new course", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
        } else {
            intent.putExtra("courseTitle", courseTitle);
            intent.putExtra("startDate", startDate);
            intent.putExtra("endDate", endDate);
            intent.putExtra("selectedCourses", adapter.getSelectedAssessments());
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }

    public void buttonShareNotesClicked(View view) {
        EditText editTextNotes = findViewById(R.id.editTextNotes);
        String notes = editTextNotes.getText().toString();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, editTextTitle.getText().toString() + ": "
                + notes);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, editTextTitle.getText().toString()));
    }
}
