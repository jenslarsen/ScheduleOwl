package com.jenslarsen.scheduleowl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jenslarsen.scheduleowl.db.Datasource;
import com.jenslarsen.scheduleowl.model.Term;

public class EditTerm extends AppCompatActivity {

    private int selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);

        Bundle bundle = getIntent().getExtras();
        selectedPosition = bundle.getInt("selectedPosition");
        Term currentTerm = Datasource.terms.get(selectedPosition);
        EditText editTextTitle = findViewById(R.id.editTextTitle);
        editTextTitle.setText(currentTerm.getTitle());
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
}
