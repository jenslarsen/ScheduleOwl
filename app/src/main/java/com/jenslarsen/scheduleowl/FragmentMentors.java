package com.jenslarsen.scheduleowl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.db.ScheduleProvider;
import com.jenslarsen.scheduleowl.model.Mentor;

public class FragmentMentors extends Fragment {

    private static final int ADD_MENTOR = 1;
    private static final int EDIT_MENTOR = 2;
    private int selectedPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_mentors, container, false);

        ListView listView = rootView.findViewById(R.id.listViewMentors);

        ArrayAdapter<Mentor> adapter = new ArrayAdapter<>(getContext(), R.layout.listitem_tab,
                R.id.textViewListItem, ScheduleProvider.mentors);
        listView.setAdapter(adapter);

        Button buttonAddMentor = rootView.findViewById(R.id.buttonAddMentor);
        buttonAddMentor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddMentorClicked();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                mentorItemClicked(selectedPosition);
            }
        });

        return rootView;
    }

    public void buttonAddMentorClicked() {
        Intent intent = new Intent(getActivity(), AddMentor.class);
        startActivityForResult(intent, ADD_MENTOR);
    }

    public void mentorItemClicked(int selectedPosition) {
        Intent intent = new Intent(getActivity(), EditMentor.class);
        intent.putExtra("selectedPosition", selectedPosition);
        startActivityForResult(intent, EDIT_MENTOR);
    }
}
