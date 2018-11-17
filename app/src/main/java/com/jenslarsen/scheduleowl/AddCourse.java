package com.jenslarsen.scheduleowl;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleContract;
import com.jenslarsen.scheduleowl.db.ScheduleProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddCourse extends AppCompatActivity {

    CourseChooserAdapter adapter;

    private EditText editTextStartDate;
    private EditText editTextEndDate;

    private String dateFormat = "yyyy-MM-dd";

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener startDatePicker;
    private DatePickerDialog.OnDateSetListener endDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        // set up array adapter
        final ListView listView = findViewById(R.id.listViewAssessments);

        adapter = new CourseChooserAdapter(this, ScheduleProvider.courses);
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
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        editTextStartDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateEndDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        editTextEndDate.setText(sdf.format(calendar.getTime()));
    }

    public void buttonSaveClicked(View view) {
        ContentValues values = new ContentValues();

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        String courseTitle = editTextTitle.getText().toString();
        String startDate = editTextStartDate.getText().toString();
        String endDate = editTextEndDate.getText().toString();
        if (courseTitle.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Missing information! Unable to add new course", Toast.LENGTH_SHORT).show();
        } else {
            values.put(ScheduleContract.CourseEntry.TITLE, courseTitle);
            values.put(ScheduleContract.CourseEntry.START_DATE, startDate);
            values.put(ScheduleContract.CourseEntry.END_DATE, endDate);

            Uri newUri = getContentResolver().insert(ScheduleContract.CourseEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Insert Course Failed!", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Add code to update courses with the correct courseId
        }
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }
}
