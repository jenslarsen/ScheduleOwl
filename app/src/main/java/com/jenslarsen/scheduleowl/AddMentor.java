package com.jenslarsen.scheduleowl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddMentor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mentor);
    }

    public void buttonSaveClicked(View view) {
        Intent intent = new Intent();

        EditText editTextName = findViewById(R.id.editTextName);
        String mentorName = editTextName.getText().toString();
        if (mentorName.isEmpty()) {
            Toast.makeText(this, "No name entered! Unable to add new mentor",
                    Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
        } else {
            intent.putExtra("mentorName", mentorName);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }
}
