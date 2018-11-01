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

public class AddTerm extends AppCompatActivity {

    CourseChooserAdapter adapter;

    private EditText editTextStartDate;
    private EditText editTextEndDate;

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener startDatePicker;
    private DatePickerDialog.OnDateSetListener endDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        // set up array adapter
        final ListView listView = findViewById(R.id.listViewCourses);

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
                new DatePickerDialog(AddTerm.this, startDatePicker,
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
                new DatePickerDialog(AddTerm.this, endDatePicker,
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
        ContentValues values = new ContentValues();

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        String termTitle = editTextTitle.getText().toString();
        String startDate = editTextStartDate.getText().toString();
        String endDate = editTextEndDate.getText().toString();
        if (termTitle.isEmpty()) {
            Toast.makeText(this, "No title entered! Unable to add new term", Toast.LENGTH_SHORT).show();
        } else {
            values.put(ScheduleContract.TermEntry.TITLE, termTitle);
            values.put(ScheduleContract.TermEntry.START_DATE, startDate);
            values.put(ScheduleContract.TermEntry.END_DATE, endDate);

            Uri newUri = getContentResolver().insert(ScheduleContract.TermEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Insert Term Failed!", Toast.LENGTH_SHORT).show();
            }

            ScheduleProvider.updateTermsList();
            FragmentTerms.adapter.notifyDataSetChanged();

            // TODO: Add code to update courses with the correct termId
        }
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }
}
