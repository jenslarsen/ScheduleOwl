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

import com.jenslarsen.scheduleowl.db.Datasource;
import com.jenslarsen.scheduleowl.model.Term;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditTerm extends AppCompatActivity {

    private int selectedPosition;
    Intent intent = new Intent();
    CourseChooserAdapter adapter;
    private Calendar calendar;
    private TextView editTextStartDate;
    private DatePickerDialog.OnDateSetListener startDatePicker;
    private TextView editTextEndDate;
    private DatePickerDialog.OnDateSetListener endDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);

        Bundle bundle = getIntent().getExtras();
        selectedPosition = bundle.getInt("selectedPosition");
        Term currentTerm = Datasource.terms.get(selectedPosition);
        EditText editTextTitle = findViewById(R.id.editTextTitle);
        editTextTitle.setText(currentTerm.getTitle());

        // set up array adapter
        final ListView listView = findViewById(R.id.listViewCourses);

        adapter = new CourseChooserAdapter(this, Datasource.courses);
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
                new DatePickerDialog(EditTerm.this, startDatePicker,
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
                new DatePickerDialog(EditTerm.this, endDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }

    public void buttonSaveClicked(View view) {
        EditText editTextTitle = findViewById(R.id.editTextTitle);

        String termTitle = editTextTitle.getText().toString();

        Term newTerm = new Term();
        newTerm.setTitle(termTitle);

        Datasource.terms.set(selectedPosition, newTerm);
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }

    public void buttonDeleteClicked(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Term")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeTerm();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void removeTerm() {
        intent.putExtra("deleteTerm", true);
        setResult(RESULT_OK, intent);
        finish();
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
}
