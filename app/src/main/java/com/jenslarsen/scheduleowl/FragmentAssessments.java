package com.jenslarsen.scheduleowl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.db.ScheduleProvider;
import com.jenslarsen.scheduleowl.model.Assessment;

public class FragmentAssessments extends Fragment {

    private static final int ADD_ASSESSMENT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_assessments, container, false);

        ListView listView = rootView.findViewById(R.id.listViewAssessments);

        ArrayAdapter<Assessment> adapter = new ArrayAdapter<>(getContext(), R.layout.listitem_tab,
                R.id.textViewListItem, ScheduleProvider.assessments);
        listView.setAdapter(adapter);

        Button buttonAddAssessment = rootView.findViewById(R.id.buttonAddAssessment);

        buttonAddAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddAssessmentClicked();
            }
        });

        return rootView;
    }

    public void buttonAddAssessmentClicked() {
        Intent intent = new Intent(getActivity(), AddAssessment.class);
        startActivityForResult(intent, ADD_ASSESSMENT);

    }
}
