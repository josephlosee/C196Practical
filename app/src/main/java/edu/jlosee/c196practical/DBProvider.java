package edu.jlosee.c196practical;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Joe on 7/16/2017.
 */

public class DBProvider{//} extends ContentProvider {
    private static final String AUTHORITY = "edu.jlosee.c196practical.dbprovider";
    private static final String BASE_PATH = "notes";
    public static final Uri TERM_URI = Uri.parse("content://"+AUTHORITY+"/"+DBOpenHelper.TABLE_TERM);
    public static final Uri NOTES_URI = Uri.parse("content://"+AUTHORITY+"/"+DBOpenHelper.TABLE_NOTES);
    public static final Uri COURSE_URI = Uri.parse("content://"+AUTHORITY+"/"+DBOpenHelper.TABLE_COURSE);
    public static final Uri ASSESSMENT_URI = Uri.parse("content://"+AUTHORITY+"/"+DBOpenHelper.TABLE_ASSESSMENT);
    public static final Uri NOTE_IMAGE_URI = Uri.parse("content://"+AUTHORITY+"/"+DBOpenHelper.TABLE_NOTE_IMAGE);
    public static final Uri MENTOR_URI = Uri.parse("content://"+AUTHORITY+"/"+DBOpenHelper.TABLE_MENTOR);
    public static final Uri COURSE_MENTORS_URI = Uri.parse("content://"+AUTHORITY+"/"+DBOpenHelper.TABLE_COURSE_MENTORS);

    private static final int TERM = 1;
    private static final int TERM_ID = 1;
    private static final int COURSE = 2;
    private static final int COURSE_ID = 2;
    private static final int NOTES = 3;
    private static final int NOTES_ID =3;
    private static final int ASSESSMENT = 4;
    private static final int ASSESSMENT_ID = 4;
    private static final int NOTES_IMG = 5;
    private static final int NOTES_IMG_ID = 5;
    private static final int MENTOR = 6;
    private static final int MENTOR_ID = 6;
    private static final int COURSE_MENTORS = 7;
    private static final int COURSE_MENTORS_ID = 7;
    //private static final int ASSESSMENT_NOTES = 8; //unused, made as a separate foreign key for the column of notes
    //private static final int ASSESSMENT_NOTES_ID = 8;

    private static final UriMatcher uriMatcher = new UriMatcher((UriMatcher.NO_MATCH));


    static {
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_NOTES, NOTES);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_TERM, TERM);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_COURSE, COURSE);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_ASSESSMENT, ASSESSMENT);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_NOTE_IMAGE, NOTES_IMG);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_MENTOR, MENTOR);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_COURSE_MENTORS, COURSE_MENTORS);

        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_NOTES+"/#",NOTES_ID);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_TERM+"/#",TERM_ID);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_COURSE+"/#",COURSE_ID);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_ASSESSMENT+"/#",ASSESSMENT_ID);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_NOTE_IMAGE+"/#",NOTES_IMG_ID);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_MENTOR+"/#",MENTOR_ID);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_COURSE_MENTORS+"/#", COURSE_MENTORS_ID);
    }

    private SQLiteDatabase database;
    private DBOpenHelper helper;

    //@Override
    public DBProvider(Context context) {
        helper = new DBOpenHelper(context);
        database = helper.getWritableDatabase();
    }

    @Nullable
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor ret = null;
        switch(uriMatcher.match(uri)){
            case TERM:
                ret = database.query(DBOpenHelper.TABLE_TERM, columns, selection, selectionArgs, null, null, DBOpenHelper.START_DATE+" ASC");
                break;
            case COURSE:
                ret = database.query(DBOpenHelper.TABLE_COURSE, columns, selection, selectionArgs, null, null, DBOpenHelper.START_DATE+" ASC");
                //ret = database.query(DBOpenHelper.TABLE_COURSE, null, null, null, null, null, DBOpenHelper.START_DATE+" ASC");
                break;
            case ASSESSMENT:
                ret = database.query(DBOpenHelper.TABLE_ASSESSMENT, columns, selection, selectionArgs, null, null, DBOpenHelper.ASSESSMENT_DUE_DATE+" DESC");
                break;
            case NOTES:
                ret = database.query(DBOpenHelper.TABLE_NOTES, columns, selection, selectionArgs, null, null, DBOpenHelper.TITLE+" ASC");
                break;
            case NOTES_IMG:
                ret = database.query(DBOpenHelper.TABLE_NOTE_IMAGE, columns, selection, selectionArgs, null, null, null);
                break;
            case MENTOR:
                ret = database.query(DBOpenHelper.TABLE_MENTOR, columns, selection, selectionArgs, null, null, DBOpenHelper.MENTOR_NAME+" ASC");
                break;
            case COURSE_MENTORS:
                ret = database.query(DBOpenHelper.TABLE_COURSE_MENTORS, columns, selection, selectionArgs, null, null, null);
            default:
                Log.d("DBProvider", "No match found for URI: "+uri.toString());
                break;
        }

        return ret;
    }

    @Nullable
    //@Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    //@Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long id = -1;
        switch(uriMatcher.match(uri)){
            case TERM:
                id = database.insert(DBOpenHelper.TABLE_TERM, null, contentValues);
                break;
            case COURSE:
                id = database.insert(DBOpenHelper.TABLE_COURSE, null, contentValues);
                break;
            case ASSESSMENT:
                id = database.insert(DBOpenHelper.TABLE_ASSESSMENT, null, contentValues);
                break;
            case NOTES:
                id = database.insert(DBOpenHelper.TABLE_NOTES, null, contentValues);
                break;
            case NOTES_IMG:
                id = database.insert(DBOpenHelper.TABLE_NOTE_IMAGE, null, contentValues);
                break;
            case MENTOR:
                id = database.insert(DBOpenHelper.TABLE_MENTOR, null, contentValues);
                break;
            case COURSE_MENTORS:
                id = database.insert(DBOpenHelper.TABLE_COURSE_MENTORS, null, contentValues);
                break;
            default:
                Log.d("DBProvider", "No match found for URI: "+uri.toString());
                break;
        }

        return Uri.parse(BASE_PATH+"/"+id);
    }

    //@Override
    public int delete(@NonNull Uri uri, @Nullable String where, @Nullable String[] whereArgs) {
        int numDeleted = -1;
        switch(uriMatcher.match(uri)){
            case TERM:
                numDeleted = database.delete(DBOpenHelper.TABLE_TERM, where, whereArgs);
                break;
            case COURSE:
                numDeleted = database.delete(DBOpenHelper.TABLE_COURSE, where, whereArgs);
                break;
            case ASSESSMENT:
                numDeleted = database.delete(DBOpenHelper.TABLE_ASSESSMENT, where, whereArgs);
                break;
            case NOTES:
                numDeleted = database.delete(DBOpenHelper.TABLE_NOTES, where, whereArgs);
                break;
            case NOTES_IMG:
                numDeleted = database.delete(DBOpenHelper.TABLE_NOTE_IMAGE, where, whereArgs);
                break;
            case MENTOR:
                numDeleted = database.delete(DBOpenHelper.TABLE_MENTOR, where, whereArgs);
                break;
            case COURSE_MENTORS:
                numDeleted = database.delete(DBOpenHelper.TABLE_COURSE_MENTORS, where, whereArgs);
                break;
            default:
                Log.d("DBProvider", "No match found for URI: "+uri.toString());
                break;
        }

        return numDeleted;
    }

    //@Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String where, @Nullable String[] whereArgs) {
        int ret = -1;
        switch(uriMatcher.match(uri)){
            case TERM:
                ret = database.update(DBOpenHelper.TABLE_TERM, contentValues, where, whereArgs);
                break;
            case COURSE:
                ret = database.update(DBOpenHelper.TABLE_COURSE, contentValues,where, whereArgs);
                break;
            case ASSESSMENT:
                ret = database.update(DBOpenHelper.TABLE_ASSESSMENT, contentValues,where, whereArgs);
                break;
            case NOTES:
                ret = database.update(DBOpenHelper.TABLE_NOTES,contentValues, where, whereArgs);
                break;
            case NOTES_IMG:
                ret = database.update(DBOpenHelper.TABLE_NOTE_IMAGE, contentValues,where, whereArgs);
                break;
            case MENTOR:
                ret = database.update(DBOpenHelper.TABLE_MENTOR, contentValues,where, whereArgs);
                break;
            case COURSE_MENTORS:
                ret = database.update(DBOpenHelper.TABLE_COURSE_MENTORS, contentValues,where, whereArgs);
                break;
            default:
                Log.d("DBProvider", "Update Query: No match found for URI: "+uri.toString());
                break;
        }

        return ret;
    }

    public Cursor rawQuery(String query, String[]selectionArgs){
        return database.rawQuery(query, selectionArgs);
    }

    public void wipeDatabase(){
        this.helper.onUpgrade(database, 1, 1);
    }
}
