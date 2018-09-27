package com.jenslarsen.scheduleowl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditTerm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);

        Bundle bundle = getIntent().getExtras();
        int position = bundle.getInt("position");
    }
}
