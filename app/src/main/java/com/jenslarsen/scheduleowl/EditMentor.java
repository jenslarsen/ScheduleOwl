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
import com.jenslarsen.scheduleowl.model.Mentor;

public class EditMentor extends AppCompatActivity {

    private int selectedPosition;
    Intent intent = new Intent();
    private static Mentor currentMentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);

        Bundle bundle = getIntent().getExtras();
        selectedPosition = bundle.getInt("selectedPosition");
        currentMentor = ScheduleProvider.mentors.get(selectedPosition);
        EditText editTextName = findViewById(R.id.editTextName);
        editTextName.setText(currentMentor.getName());
    }

    public void buttonSaveClicked(View view) {
        Intent intent = new Intent();

        EditText editTextName = findViewById(R.id.editTextName);
        String mentorName = editTextName.getText().toString();
        if (mentorName.isEmpty()) {
            Toast.makeText(this, "No name entered! Unable to update mentor", Toast.LENGTH_SHORT).show();
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
                .setTitle("Delete Mentor")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeMentor();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void removeMentor() {
        intent.putExtra("deleteMentor", true);
        setResult(RESULT_OK, intent);
        finish();
    }
}