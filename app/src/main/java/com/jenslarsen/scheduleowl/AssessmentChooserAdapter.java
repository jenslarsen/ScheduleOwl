package com.jenslarsen.scheduleowl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jenslarsen.scheduleowl.model.Assessment;

import java.util.ArrayList;
import java.util.List;

public class AssessmentChooserAdapter extends ArrayAdapter<Assessment> {

    private Context context;
    private List<Assessment> assessments;

    private ArrayList<Assessment> selectedAssessments = new ArrayList<>();

    public AssessmentChooserAdapter(Context context, List<Assessment> assessments) {
        super(context, 0, assessments);
        this.context = context;
        this.assessments = assessments;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(context)
                    .inflate(R.layout.listitem_chooser, parent, false);
        }

        final Assessment currentAssessment = assessments.get(position);

        TextView assessmentTitle = listItem.findViewById(R.id.textViewChooser);
        assessmentTitle.setText(currentAssessment.getTitle());

        final CheckBox checkBox = listItem.findViewById(R.id.checkBoxChooser);
        checkBox.setChecked(false);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedAssessments.add(currentAssessment);
                } else {
                    selectedAssessments.remove(currentAssessment);
                }
            }
        });

        assessmentTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
            }
        });

        return listItem;
    }

    public ArrayList<Assessment> getSelectedAssessments() {
        return selectedAssessments;
    }
}
