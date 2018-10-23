package com.jenslarsen.scheduleowl.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.jenslarsen.scheduleowl.model.Assessment;
import com.jenslarsen.scheduleowl.model.Course;
import com.jenslarsen.scheduleowl.model.Mentor;
import com.jenslarsen.scheduleowl.model.Term;

import java.util.ArrayList;

/**
 * {@link ContentProvider} for the ScheduleOwl app.
 */
public class ScheduleProvider extends ContentProvider {
    public static ArrayList<Term> terms = new ArrayList<>();
    public static ArrayList<Mentor> mentors = new ArrayList<>();
    public static ArrayList<Course> courses = new ArrayList<>();
    public static ArrayList<Assessment> assessments = new ArrayList<>();

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
        return null;
    }

    /**
     * Insert new data into the provider with the give ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
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
}
