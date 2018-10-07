package com.jenslarsen.scheduleowl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jenslarsen.scheduleowl.model.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseChooserAdapter extends ArrayAdapter<Course> {

    private Context context;
    private List<Course> courses = new ArrayList<>();

    public CourseChooserAdapter(Context context, List<Course> courses) {
        super(context, 0, courses);
        this.context = context;
        this.courses = courses;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null) {
            listItem = LayoutInflater.from(context)
                    .inflate(R.layout.listitem_chooser,parent,false);
        }

        Course currentCourse = courses.get(position);

        TextView courseTitle = listItem.findViewById(R.id.textViewChooser);
        courseTitle.setText(currentCourse.getTitle());

        CheckBox checkBox = listItem.findViewById(R.id.checkBoxChooser);
        checkBox.setChecked(false);

        return listItem;
    }
}
