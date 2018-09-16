package com.jenslarsen.scheduleowl;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jenslarsen.scheduleowl.model.Term;

import java.util.ArrayList;

public class fragmentTerms extends Fragment {

    // Dummy list of terms to get layouts working
    ArrayList<Term> terms = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_terms, container, false);

        // populate the temp array of terms
        for (int index = 0; index < 5; index++) {
            Term tempTerm = new Term();
            tempTerm.setTitle("Term " + index);
            terms.add(tempTerm);
        }

        ListView listView = rootView.findViewById(R.id.listViewTerms);

        ArrayAdapter<Term> adapter = new ArrayAdapter<>(getContext(), R.layout.listitem_tab,
                R.id.textViewListItem,terms);
        listView.setAdapter(adapter);

        return rootView;
    }
}
