package com.jenslarsen.scheduleowl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jenslarsen.scheduleowl.db.Datasource;
import com.jenslarsen.scheduleowl.model.Term;

public class AddTerm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);
    }

    public void buttonSaveClicked(View view) {
        EditText editTextTitle = findViewById(R.id.editTextTitle);

        String termTitle = editTextTitle.getText().toString();

        Term newTerm = new Term();
        newTerm.setTitle(termTitle);

        Datasource.terms.add(newTerm);
        finish();
    }

    public void buttonCancelClicked(View view) {
        finish();
    }
}
