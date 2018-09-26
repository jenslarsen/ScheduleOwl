package com.jenslarsen.scheduleowl;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jenslarsen.scheduleowl.db.Datasource;
import com.jenslarsen.scheduleowl.model.Term;

public class FragmentTerms extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_terms, container, false);

        final ListView listView = rootView.findViewById(R.id.listViewTerms);

        ArrayAdapter<Term> adapter = new ArrayAdapter<>(getContext(), R.layout.listitem_tab,
                R.id.textViewListItem, Datasource.terms);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Term", "Item " + position);
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

    public void buttonAddTermClicked(){
        Toast.makeText(getContext(), "Add Term here", Toast.LENGTH_SHORT).show();
    }
}
