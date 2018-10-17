package com.jenslarsen.scheduleowl;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditAssessment extends AppCompatActivity {

    CourseChooserAdapter adapter;

    public EditText editTextDueDate;

    public Calendar calendar;
    DatePickerDialog.OnDateSetListener dueDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);

        calendar = Calendar.getInstance();

        // set up due date picker
        editTextDueDate = findViewById(R.id.editTextDueDate);
        dueDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDueDate();
            }
        };

        editTextDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditAssessment.this, dueDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

    }

    private void updateDueDate() {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        editTextDueDate.setText(sdf.format(calendar.getTime()));
    }

    public void buttonSaveClicked(View view) {
        Intent intent = new Intent();

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        editTextDueDate = findViewById(R.id.editTextDueDate);
        String assessmentTitle = editTextTitle.getText().toString();
        String dueDate = editTextDueDate.getText().toString();
        if (assessmentTitle.isEmpty()) {
            Toast.makeText(this, "No title entered! Unable to edit new assessment", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
        } else {
            intent.putExtra("assessmentTitle", assessmentTitle);
            intent.putExtra("dueDate", dueDate);
            intent.putExtra("selectedCourses", adapter.getSelectedCourses());
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }
}