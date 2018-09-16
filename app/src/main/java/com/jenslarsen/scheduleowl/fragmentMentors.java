package com.jenslarsen.scheduleowl;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.db.Datasource;
import com.jenslarsen.scheduleowl.model.Mentor;

public class fragmentMentors extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_mentors, container, false);

        ListView listView = rootView.findViewById(R.id.listViewMentors);

        ArrayAdapter<Mentor> adapter = new ArrayAdapter<>(getContext(), R.layout.listitem_tab,
                R.id.textViewListItem, Datasource.mentors);
        listView.setAdapter(adapter);

        return rootView;
    }
}
