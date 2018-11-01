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
import com.jenslarsen.scheduleowl.model.Term;

public class FragmentTerms extends Fragment {

    public final static int ADD_TERM = 1;
    public final static int EDIT_TERM = 2;

    public static ArrayAdapter<Term> adapter;
    private int selectedPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_terms, container, false);

        final ListView listView = rootView.findViewById(R.id.listViewTerms);

        adapter = new ArrayAdapter<>(getContext(), R.layout.listitem_tab,
                R.id.textViewListItem, ScheduleProvider.terms);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                termItemClicked(selectedPosition);
            }
        });

        Button buttonAddTerm = rootView.findViewById(R.id.buttonAddTerm);
        buttonAddTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAddTermClicked();
            }
        });

        return rootView;
    }

    public void buttonAddTermClicked() {
        Intent intent = new Intent(getActivity(), AddTerm.class);
        startActivityForResult(intent, ADD_TERM);
    }

    public void termItemClicked(int selectedPosition) {
        Intent intent = new Intent(getActivity(), EditTerm.class);
        intent.putExtra("selectedPosition", selectedPosition);
        startActivityForResult(intent, EDIT_TERM);
    }
}


