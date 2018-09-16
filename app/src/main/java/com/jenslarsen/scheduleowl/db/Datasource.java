package com.jenslarsen.scheduleowl.db;

import com.jenslarsen.scheduleowl.model.Course;
import com.jenslarsen.scheduleowl.model.Term;
import com.jenslarsen.scheduleowl.model.Mentor;
import com.jenslarsen.scheduleowl.model.Assessment;


import java.util.ArrayList;

public class Datasource {

    // Dummy list of terms to get layouts working
    public static ArrayList<Term> terms = new ArrayList<>();
    public static ArrayList<Mentor> mentors = new ArrayList<>();
    public static ArrayList<Course> courses = new ArrayList<>();
    public static ArrayList<Assessment> assessments = new ArrayList<>();
}
