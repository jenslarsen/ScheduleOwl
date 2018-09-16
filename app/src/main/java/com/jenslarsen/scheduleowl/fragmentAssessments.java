package com.jenslarsen.scheduleowl;

import android.support.v4.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class fragmentAssessments extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_assessments, container, false);
        Button buttonAssessments = rootView.findViewById(R.id.buttonAssessments);
        buttonAssessments.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "fragmentAssessments Button", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
