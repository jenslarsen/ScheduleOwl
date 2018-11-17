package com.jenslarsen.scheduleowl;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleProvider;
import com.jenslarsen.scheduleowl.model.Course;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditCourse extends AppCompatActivity {

    private int selectedPosition;
    Intent intent = new Intent();
    CourseChooserAdapter adapter;
    private Calendar calendar;
    private TextView editTextStartDate;
    private DatePickerDialog.OnDateSetListener startDatePicker;
    private TextView editTextEndDate;
    private DatePickerDialog.OnDateSetListener endDatePicker;

    private static Course currentCourse;

    private String dateFormat = "yyyy-MM-dd";

    private SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        Bundle bundle = getIntent().getExtras();
        selectedPosition = bundle.getInt("selectedPosition");
        currentCourse = ScheduleProvider.courses.get(selectedPosition);
        EditText editTextTitle = findViewById(R.id.editTextTitle);
        editTextTitle.setText(currentCourse.getTitle());

        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextStartDate = findViewById(R.id.editTextStartDate);

        Date startDate = currentCourse.getStartDate();
        if (startDate != null) {
            editTextStartDate.setText(sdf.format(startDate));
        }

        Date endDate = currentCourse.getEndDate();
        if (endDate != null) {
            editTextEndDate.setText(sdf.format(endDate));
        }

        // set up array adapter
        ListView listView = findViewById(R.id.listViewCourses);
        adapter = new CourseChooserAdapter(this, ScheduleProvider.courses);
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
        Intent intent = new Intent();

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        String courseTitle = editTextTitle.getText().toString();
        if (courseTitle.isEmpty()) {
            Toast.makeText(this, "No title entered! Unable to update course", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
        } else {
            intent.putExtra("selectedCourses", adapter.getSelectedCourses());
            setResult(RESULT_OK, intent);
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
                        removeCourse();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void removeCourse() {
        intent.putExtra("deleteCourse", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateStartDate() {
        editTextStartDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateEndDate() {
        editTextEndDate.setText(sdf.format(calendar.getTime()));
    }
}
