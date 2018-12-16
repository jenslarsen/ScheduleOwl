package com.jenslarsen.scheduleowl.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class to store all the constants used to create and manage the database
 */
public final class ScheduleContract {

    private ScheduleContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.jenslarsen.scheduleowl";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ASSESSMENT = "assessment";
    public static final String PATH_COURSE = "course";
    public static final String PATH_TERM = "term";
    public static final String PATH_MENTOR = "mentor";

    public static final class AssessmentEntry implements BaseColumns {
        public static final String TABLE_NAME = "assessment";
        public static final String _ID = BaseColumns._ID;
        public static final String TITLE = "title";
        public static final String DUE_DATE = "dueDate";
        public static final String REMINDER = "reminder";
        public static final String COURSEID = "courseId";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ASSESSMENT);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of assessments.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSESSMENT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single assessment.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSESSMENT;
    }

    public static final class CourseEntry implements BaseColumns {
        public static final String TABLE_NAME = "course";
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
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COURSE);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of courses.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single course.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;
    }

    public static final class MentorEntry implements BaseColumns {
        public static final String TABLE_NAME = "mentor";
        public static final String _ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MENTOR);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of mentors.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MENTOR;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single mentor.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MENTOR;
    }

    public static final class TermEntry implements BaseColumns {
        public static final String TABLE_NAME = "term";
        public static final String _ID = BaseColumns._ID;
        public static final String TITLE = "title";
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TERM);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of terms.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TERM;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single term.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TERM;
    }
}
