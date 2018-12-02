package com.jenslarsen.scheduleowl.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.jenslarsen.scheduleowl.db.ScheduleContract.AssessmentEntry;
import com.jenslarsen.scheduleowl.db.ScheduleContract.CourseEntry;
import com.jenslarsen.scheduleowl.db.ScheduleContract.MentorEntry;
import com.jenslarsen.scheduleowl.db.ScheduleContract.TermEntry;

public class ScheduleDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "scheduleowl.db";

    private static final String SQL_CREATE_MENTOR_TABLE
            = "CREATE TABLE IF NOT EXISTS " + MentorEntry.TABLE_NAME + "("
            + MentorEntry._ID + " INTEGER PRIMARY KEY, "
            + MentorEntry.NAME + " TEXT NOT NULL, "
            + MentorEntry.PHONE + " TEXT, "
            + MentorEntry.EMAIL + " TEXT"
            + ");";

    private static final String SQL_CREATE_TERM_TABLE
            = "CREATE TABLE IF NOT EXISTS " + TermEntry.TABLE_NAME + "("
            + TermEntry._ID + " INTEGER PRIMARY KEY, "
            + TermEntry.TITLE + " TEXT NOT NULL, "
            + TermEntry.START_DATE + " TEXT, "
            + TermEntry.END_DATE + " TEXT "
            + ");";

    private static final String SQL_CREATE_COURSE_TABLE
            = "CREATE TABLE IF NOT EXISTS " + CourseEntry.TABLE_NAME + "("
            + CourseEntry._ID + " INTEGER PRIMARY KEY, "
            + CourseEntry.TITLE + " TEXT NOT NULL, "
            + CourseEntry.START_DATE + " TEXT, "
            + CourseEntry.START_REMINDER + " INTEGER, "
            + CourseEntry.END_DATE + " TEXT, "
            + CourseEntry.END_REMINDER + " INTEGER, "
            + CourseEntry.STATUS + " INTEGER, "
            + CourseEntry.NOTES + " TEXT, "
            + CourseEntry.MENTORID + " INTEGER, "
            + CourseEntry.TERMID + " INTEGER, "
            + "FOREIGN KEY(" + CourseEntry.MENTORID + ") " +
            "   REFERENCES " + MentorEntry.TABLE_NAME + "(" + MentorEntry._ID + "),"
            + "FOREIGN KEY(" + CourseEntry.TERMID + ") " +
            "   REFERENCES " + TermEntry.TABLE_NAME + "(" + TermEntry._ID + ")"
            + ");";

    private static final String SQL_CREATE_ASSESSMENT_TABLE
            = "CREATE TABLE IF NOT EXISTS " + AssessmentEntry.TABLE_NAME + "("
            + AssessmentEntry._ID + " INTEGER PRIMARY KEY, "
            + AssessmentEntry.TITLE + " TEXT NOT NULL, "
            + AssessmentEntry.DUE_DATE + " TEXT, "
            + AssessmentEntry.REMINDER + " INTEGER, "
            + AssessmentEntry.COURSEID + " INTEGER, "
            + "FOREIGN KEY(" + AssessmentEntry.COURSEID + ") " +
            "   REFERENCES " + CourseEntry.TABLE_NAME + "(" + CourseEntry._ID + ")"
            + ");";

    private static final String SQL_DELETE_ASSESSMENT_TABLE = "DROP TABLE ASSESSMENT";
    private static final String SQL_DELETE_COURSE_TABLE = "DROP TABLE COURSE;";
    private static final String SQL_DELETE_TERM_TABLE = "DROP TABLE TERM;";
    private static final String SQL_DELETE_MENTOR_TABLE = "DROP TABLE MENTOR;";

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MENTOR_TABLE);
        db.execSQL(SQL_CREATE_TERM_TABLE);
        db.execSQL(SQL_CREATE_COURSE_TABLE);
        db.execSQL(SQL_CREATE_ASSESSMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ASSESSMENT_TABLE);
        db.execSQL(SQL_DELETE_COURSE_TABLE);
        db.execSQL(SQL_DELETE_TERM_TABLE);
        db.execSQL(SQL_DELETE_MENTOR_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static int getIdFromUri(Uri uri) {
        String path = uri.getPath();
        return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
    }
}