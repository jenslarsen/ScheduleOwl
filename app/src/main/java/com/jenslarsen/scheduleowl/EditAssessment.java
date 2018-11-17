package com.jenslarsen.scheduleowl;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.ScheduleProvider;
import com.jenslarsen.scheduleowl.model.Assessment;

public class EditAssessment extends AppCompatActivity {

    private int selectedPosition;
    Intent intent = new Intent();
    private static Assessment currentAssessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment);

        Bundle bundle = getIntent().getExtras();
        selectedPosition = bundle.getInt("selectedPosition");
        currentAssessment = ScheduleProvider.assessments.get(selectedPosition);
        EditText editTextTitle = findViewById(R.id.editTextTitle);
        editTextTitle.setText(currentAssessment.getTitle());
    }

    public void buttonSaveClicked(View view) {
        Intent intent = new Intent();

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        String assessmentTitle = editTextTitle.getText().toString();
        if (assessmentTitle.isEmpty()) {
            Toast.makeText(this, "No title entered! Unable to update assessment", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
        } else {
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }

    public void buttonDeleteClicked(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Assessment")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAssessment();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void removeAssessment() {
        intent.putExtra("deleteAssessment", true);
        setResult(RESULT_OK, intent);
        finish();
    }
}