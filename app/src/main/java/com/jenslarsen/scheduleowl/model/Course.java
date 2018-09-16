package com.jenslarsen.scheduleowl.model;

import java.util.Date;
import java.util.List;

public class Course {

    enum Status {
        IN_PROGRESS, COMPLETED, DROPPED, PLANNED;
    }

    private String title;
    private Date startDate;
    private Date endDate;
    Status status;
    Mentor mentor;
    String notes;
    List<Assessment> assessments;

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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    public boolean removeAssessment(Assessment assessment) {
        if(assessments.contains(assessment)){
            assessments.remove(assessment);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }
}