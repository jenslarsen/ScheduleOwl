package com.jenslarsen.scheduleowl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.db.Datasource;
import com.jenslarsen.scheduleowl.model.Course;

public class AddTerm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        final ListView listView = findViewById(R.id.listViewCourses);

        ArrayAdapter<Course> adapter = new ArrayAdapter<>(this, R.layout.listitem_chooser,
                R.id.textViewChooser, Datasource.courses);
        listView.setAdapter(adapter);
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
