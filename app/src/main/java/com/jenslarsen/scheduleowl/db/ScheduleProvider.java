package com.jenslarsen.scheduleowl.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.jenslarsen.scheduleowl.db.ScheduleContract.AssessmentEntry;
import com.jenslarsen.scheduleowl.db.ScheduleContract.CourseEntry;
import com.jenslarsen.scheduleowl.db.ScheduleContract.MentorEntry;
import com.jenslarsen.scheduleowl.db.ScheduleContract.TermEntry;
import com.jenslarsen.scheduleowl.model.Assessment;
import com.jenslarsen.scheduleowl.model.Course;
import com.jenslarsen.scheduleowl.model.Mentor;
import com.jenslarsen.scheduleowl.model.Term;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * {@link ContentProvider} for the ScheduleOwl app.
 */
public class ScheduleProvider extends ContentProvider {
    public static ArrayList<Term> terms = new ArrayList<>();
    public static ArrayList<Mentor> mentors = new ArrayList<>();
    public static ArrayList<Course> courses = new ArrayList<>();
    public static ArrayList<Assessment> assessments = new ArrayList<>();

    /**
     * URI Matcher constants
     */
    public static final int ASSESSMENT = 1000;
    public static final int ASSESSMENT_ID = 1001;
    public static final int COURSE = 2000;
    public static final int COURSE_ID = 2001;
    public static final int MENTOR = 3000;
    public static final int MENTOR_ID = 3001;
    public static final int TERM = 4000;
    public static final int TERM_ID = 4001;

    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static SimpleDateFormat sdf;
    private static String dateFormat = "yyyy-MM-dd";

    static {
        sdf = new SimpleDateFormat(dateFormat);
        uriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_ASSESSMENT, ASSESSMENT);
        uriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_ASSESSMENT + "/#", ASSESSMENT_ID);
        uriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_COURSE, COURSE);
        uriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_COURSE + "/#", COURSE_ID);
        uriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_MENTOR, MENTOR);
        uriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_MENTOR + "/#", MENTOR_ID);
        uriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_TERM, TERM);
        uriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_TERM + "/#", TERM_ID);
    }

    /**
     * Database helper for external use
     */
    public static ScheduleDbHelper dbHelper;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ScheduleProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object
     */
    @Override
    public boolean onCreate() {
        dbHelper = new ScheduleDbHelper(getContext());

        updateTermsList();

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;

        int match = uriMatcher.match(uri);

        switch (match) {
            case ASSESSMENT:
                cursor = db.query(AssessmentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ASSESSMENT_ID:
                selection = ScheduleContract.AssessmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(AssessmentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSE:
                cursor = db.query(CourseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSE_ID:
                selection = ScheduleContract.CourseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(CourseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MENTOR:
                cursor = db.query(MentorEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MENTOR_ID:
                selection = ScheduleContract.MentorEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(MentorEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TERM:
                cursor = db.query(TermEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TERM_ID:
                selection = ScheduleContract.TermEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TermEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the give ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case ASSESSMENT:
                return insertAssessment(uri, contentValues);
            case COURSE:
                return insertCourse(uri, contentValues);
            case MENTOR:
                return insertMentor(uri, contentValues);
            case TERM:
                return insertTerm(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported " + uri);
        }
    }

    private Uri insertTerm(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Date startDate;
        Date endDate;
        if (contentValues.get(TermEntry.TITLE) == null) {
            Log.e(LOG_TAG, "Unable to insert term. Title cannot be null");
            return null;
        } else if (contentValues.get(TermEntry.START_DATE) == null) {
            Log.e(LOG_TAG, "Unable to insert term. Start Date cannot be null");
            return null;
        } else if (contentValues.get(TermEntry.END_DATE) == null) {
            Log.e(LOG_TAG, "Unable to insert term. End Date cannot be null.");
            return null;
        }
        try {
            startDate = sdf.parse((String) contentValues.get(TermEntry.START_DATE));
            endDate = sdf.parse((String) contentValues.get(TermEntry.END_DATE));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Unable to parse dates to insert!");
            return null;
        }

        if (startDate.after(endDate) || endDate.before(startDate)) {
            Log.e(LOG_TAG, "Start date: " + startDate);
            Log.e(LOG_TAG, "End Date: " + endDate);
            Log.e(LOG_TAG, "Unable to insert term. Start and End dates do not make sense");
            return null;
        }

        long id = db.insert(TermEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Insert failed for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertMentor(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(MentorEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Insert failed for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertCourse(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(CourseEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Insert failed for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertAssessment(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(AssessmentEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Insert failed for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MINE type of data for the content URI
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Update the list of terms from the database
     */
    public static void updateTermsList() {
        terms.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor termCursor = db.query(
                TermEntry.TABLE_NAME, null, null, null, null,
                null, null);
        while (termCursor.moveToNext()) {
            Term tempTerm = new Term();
            tempTerm.setTitle(termCursor.getString(1));

            String stringStartDate = termCursor.getString(2);
            String stringEndDate = termCursor.getString(3);
            try {
                Date startDate = sdf.parse(stringStartDate);
                tempTerm.setStartDate(startDate);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Unable to parse start date " + stringStartDate);
                e.printStackTrace();
                return;
            }
            try {
                Date endDate = sdf.parse(stringEndDate);
                tempTerm.setEndDate(endDate);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Unable to parse end date " + stringEndDate);
                e.printStackTrace();
                return;
            }
            terms.add(tempTerm);
        }
    }
}
