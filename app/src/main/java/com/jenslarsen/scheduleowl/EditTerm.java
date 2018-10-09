package com.jenslarsen.scheduleowl;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.Datasource;
import com.jenslarsen.scheduleowl.model.Term;

public class EditTerm extends AppCompatActivity {

    private int selectedPosition;
    Intent intent = new Intent();

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
}
