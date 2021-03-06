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
 * {@link ContentProvider} for the ScheduleOwl app. Abstract away all that ugly SQL stuff.
 */
public class ScheduleProvider extends ContentProvider {
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

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

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
        if (contentValues.getAsString(TermEntry.TITLE) == null) {
            throw new IllegalArgumentException("Unable to insert term. Title cannot be null");
        } else if (contentValues.getAsString(TermEntry.START_DATE) == null) {
            throw new IllegalArgumentException("Unable to insert term. Start Date cannot be null");
        } else if (contentValues.getAsString(TermEntry.END_DATE) == null) {
            throw new IllegalArgumentException("Unable to insert term. End Date cannot be null.");
        }
        try {
            startDate = sdf.parse(contentValues.getAsString(TermEntry.START_DATE));
            endDate = sdf.parse(contentValues.getAsString(TermEntry.END_DATE));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Unable to parse dates while inserting a new Term!");
            e.printStackTrace();
            return null;
        }

        if (startDate.after(endDate) || endDate.before(startDate)) {
            throw new IllegalArgumentException("Unable to insert term. Start and End dates do not make sense");
        }

        long id = db.insert(TermEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Insert failed for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertMentor(Uri uri, ContentValues contentValues) {

        if (contentValues.getAsString(MentorEntry.NAME) == null) {
            throw new IllegalArgumentException("Unable to insert mentor. Name cannot be null");
        } else if (contentValues.getAsString(MentorEntry.PHONE) == null) {
            throw new IllegalArgumentException("Unable to insert mentor. Phone number cannot be null");
        } else if (contentValues.getAsString(MentorEntry.EMAIL) == null) {
            throw new IllegalArgumentException("Unable to insert mentor. Email address cannot be null.");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(MentorEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Insert failed for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertCourse(Uri uri, ContentValues contentValues) {
        if (contentValues.getAsString(CourseEntry.TITLE) == null) {
            throw new IllegalArgumentException("Unable to insert course. Title cannot be null");
        } else if (contentValues.getAsString(CourseEntry.START_DATE) == null) {
            throw new IllegalArgumentException("Unable to insert course. Start Date cannot be null");
        } else if (contentValues.getAsString(CourseEntry.END_DATE) == null) {
            throw new IllegalArgumentException("Unable to insert course. End Date cannot be null.");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(CourseEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Insert failed for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertAssessment(Uri uri, ContentValues contentValues) {
        if (contentValues.getAsString(AssessmentEntry.TITLE) == null) {
            throw new IllegalArgumentException("Unable to insert assessment. Title cannot be null");
        } else if (contentValues.getAsString(AssessmentEntry.DUE_DATE) == null) {
            throw new IllegalArgumentException("Unable to insert assessment. Due Date cannot be null");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(AssessmentEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Insert failed for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case ASSESSMENT:
                return updateAssessment(uri, contentValues, selection, selectionArgs);
            case COURSE:
                return updateCourse(uri, contentValues, selection, selectionArgs);
            case MENTOR:
                return updateMentor(uri, contentValues, selection, selectionArgs);
            case TERM:
                return updateTerm(uri, contentValues, selection, selectionArgs);
            case ASSESSMENT_ID:
                selection = AssessmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateAssessment(uri, contentValues, selection, selectionArgs);
            case COURSE_ID:
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateCourse(uri, contentValues, selection, selectionArgs);
            case MENTOR_ID:
                selection = MentorEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMentor(uri, contentValues, selection, selectionArgs);
            case TERM_ID:
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTerm(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported " + uri);
        }
    }

    private int updateTerm(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        Date startDate = new Date();
        Date endDate = new Date();

        try {
            if (contentValues.containsKey(TermEntry.TITLE)) {
                if (contentValues.getAsString(TermEntry.TITLE) == null) {
                    throw new IllegalArgumentException("Unable to update term. Title cannot be null");
                }
            } else if (contentValues.containsKey(TermEntry.START_DATE)) {
                startDate = sdf.parse(contentValues.getAsString(TermEntry.START_DATE));
                if (contentValues.getAsString(TermEntry.START_DATE) == null) {
                    throw new IllegalArgumentException("Unable to insert term. Start Date cannot be null");
                }
            } else if (contentValues.containsKey(TermEntry.END_DATE)) {
                endDate = sdf.parse(contentValues.getAsString(TermEntry.END_DATE));
                if (contentValues.getAsString(TermEntry.END_DATE) == null) {
                    throw new IllegalArgumentException("Unable to insert term. End Date cannot be null.");
                }
            }
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Unable to parse dates while updating term: " + uri);
            e.printStackTrace();
            return -1;
        }

        if (startDate.after(endDate) || endDate.before(startDate)) {
            throw new IllegalArgumentException("Unable to insert term. Start and End dates do not make sense");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numItemsUpdated = db.update(TermEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (numItemsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numItemsUpdated;
    }

    private int updateMentor(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(MentorEntry.NAME)) {
            if (contentValues.getAsString(MentorEntry.NAME) == null) {
                throw new IllegalArgumentException("Unable to update mentor. Name cannot be null");
            }
        } else if (contentValues.containsKey(MentorEntry.PHONE)) {
            if (contentValues.getAsString(MentorEntry.PHONE) == null) {
                throw new IllegalArgumentException("Unable to update mentor. Phone number cannot be null");
            }
        } else if (contentValues.containsKey(MentorEntry.EMAIL)) {
            if (contentValues.getAsString(MentorEntry.EMAIL) == null) {
                throw new IllegalArgumentException("Unable to update mentor. Email address cannot be null.");
            }
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numItemsUpdated = db.update(MentorEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (numItemsUpdated < 1) {
            Log.e(LOG_TAG, "Failed to update course:" + uri);
            return -1;
        }
        if (numItemsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numItemsUpdated;
    }

    private int updateCourse(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(CourseEntry.TITLE)) {
            if (contentValues.getAsString(CourseEntry.TITLE) == null) {
                throw new IllegalArgumentException("Unable to update course. Title cannot be null");
            }
        } else if (contentValues.containsKey(CourseEntry.START_DATE)) {
            if (contentValues.getAsString(CourseEntry.START_DATE) == null) {
                throw new IllegalArgumentException("Unable to update course. Start Date cannot be null");
            }
        } else if (contentValues.containsKey(CourseEntry.END_DATE)) {
            if (contentValues.getAsString(CourseEntry.END_DATE) == null) {
                throw new IllegalArgumentException("Unable to update course. End Date cannot be null.");
            }
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numItemsUpdated = db.update(CourseEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (numItemsUpdated < 1) {
            Log.e(LOG_TAG, "Failed to update course:" + uri);
            return -1;
        }
        if (numItemsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numItemsUpdated;
    }

    private int updateAssessment(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.containsKey(AssessmentEntry.TITLE)) {
            if (contentValues.getAsString(AssessmentEntry.TITLE) == null) {
                throw new IllegalArgumentException("Unable to update assessment. Title cannot be null");
            }
        } else if (contentValues.containsKey(AssessmentEntry.DUE_DATE)) {
            if (contentValues.getAsString(AssessmentEntry.DUE_DATE) == null) {
                throw new IllegalArgumentException("Unable to update assessment. Due Date cannot be null");
            }
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numItemsUpdated = db.update(AssessmentEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (numItemsUpdated < 1) {
            Log.e(LOG_TAG, "Failed to update assessment:" + uri);
            return -1;
        }
        if (numItemsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numItemsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int numItemsDeleted;

        final int match = uriMatcher.match(uri);

        switch (match) {
            case TERM:
                numItemsDeleted = database.delete(TermEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TERM_ID:
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numItemsDeleted = database.delete(TermEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MENTOR:
                numItemsDeleted = database.delete(MentorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MENTOR_ID:
                selection = MentorEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numItemsDeleted = database.delete(MentorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSE:
                numItemsDeleted = database.delete(CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSE_ID:
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numItemsDeleted = database.delete(CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ASSESSMENT:
                numItemsDeleted = database.delete(AssessmentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ASSESSMENT_ID:
                selection = AssessmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numItemsDeleted = database.delete(AssessmentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (numItemsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numItemsDeleted;
    }

    /**
     * Returns the MINE type of data for the content URI
     */
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case ASSESSMENT:
                return AssessmentEntry.CONTENT_LIST_TYPE;
            case ASSESSMENT_ID:
                return AssessmentEntry.CONTENT_ITEM_TYPE;
            case COURSE:
                return CourseEntry.CONTENT_LIST_TYPE;
            case COURSE_ID:
                return CourseEntry.CONTENT_ITEM_TYPE;
            case MENTOR:
                return MentorEntry.CONTENT_LIST_TYPE;
            case MENTOR_ID:
                return MentorEntry.CONTENT_ITEM_TYPE;
            case TERM:
                return TermEntry.CONTENT_LIST_TYPE;
            case TERM_ID:
                return TermEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
}
