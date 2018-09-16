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

public class fragmentCourses extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_courses, container, false);
        Button buttonCourses = rootView.findViewById(R.id.buttonCourses);
        buttonCourses.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "fragmentCourses Button", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
