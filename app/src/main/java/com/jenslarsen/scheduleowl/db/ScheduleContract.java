package com.jenslarsen.scheduleowl.db;

import android.provider.BaseColumns;

public final class ScheduleContract {

    private ScheduleContract() {
    }

    public static final class AssessmentEntry implements BaseColumns {
        public static final String TABLE_NAME = "assessments";
        public static final String _ID = BaseColumns._ID;
        public static final String TITLE = "title";
        public static final String DUE_DATE = "dueDate";
        public static final String REMINDER = "reminder";
        public static final String COURSEID = "courseId";
    }

    public static final class CourseEntry implements BaseColumns {
        public static final String TABLE_NAME = "courses";
        public static final String _ID = BaseColumns._ID;
        public static final String TITLE = "title";
        public static final String START_DATE = "startDate";
        public static final String START_REMINDER = "startReminder";
        public static final String END_DATE = "endDate";
        public static final String END_REMINDER = "endReminder";
        public static final String STATUS = "status";
        public static final String NOTES = "notes";
        public static final String MENTORID = "mentorId";
        public static final String TERMID = "termId";
        public static final int IN_PROGRESS = 0;
        public static final int COMPLETED = 1;
        public static final int DROPPED = 2;
        public static final int PLANNED = 3;
    }

    public static final class MentorEntry implements BaseColumns {
        public static final String TABLE_NAME = "mentors";
        public static final String _ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";
    }

    public static final class TermEntry implements BaseColumns {
        public static final String TABLE_NAME = "terms";
        public static final String _ID = BaseColumns._ID;
        public static final String TITLE = "title";
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
    }
}
