package com.jenslarsen.scheduleowl;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.db.Datasource;
import com.jenslarsen.scheduleowl.model.Course;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTerm extends AppCompatActivity {

    public Calendar calendar;

    public EditText editTextStartDate;
    DatePickerDialog.OnDateSetListener startDatePicker;
    public EditText editTextEndDate;
    DatePickerDialog.OnDateSetListener endDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        final ListView listView = findViewById(R.id.listViewCourses);

        ArrayAdapter<Course> adapter = new ArrayAdapter<>(this, R.layout.listitem_chooser,
                R.id.textViewChooser, Datasource.courses);
        listView.setAdapter(adapter);

        calendar = Calendar.getInstance();

        editTextStartDate = findViewById(R.id.editTextStartDate);
        startDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                updateStartDate();
            }
        };

        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddTerm.this, startDatePicker, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        editTextEndDate = findViewById(R.id.editTextEndDate);
        endDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                updateEndDate();
            }
        };

        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddTerm.this, endDatePicker, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        editTextEndDate = findViewById(R.id.editTextEndDate);
        endDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                updateEndDate();
            }
        };

        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddTerm.this, endDatePicker, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        String termTitle = editTextTitle.getText().toString();
        if (termTitle.isEmpty()) {
            setResult(RESULT_CANCELED);
        } else {
            intent.putExtra("termTitle", termTitle);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }
}
