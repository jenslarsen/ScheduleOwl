package com.jenslarsen.scheduleowl.model;

import java.util.Date;
import java.util.List;

public class Term {
    private String title;
    private Date startDate;
    private List<Course> courses;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public boolean removeCourse(Course course) {
        if(courses.contains(course)) {
            courses.remove(course);
            return true;
        }
        return false;
    }
}