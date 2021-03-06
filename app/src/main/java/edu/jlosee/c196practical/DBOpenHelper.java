package edu.jlosee.c196practical;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Joe on 7/10/2017.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    //Constants for db name and version
    private static final String DATABASE_NAME = "course.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for identifying table and columns
    public static final String TABLE_NOTES = "CourseNotes";
    public static final String TABLE_COURSE = "Course";
    public static final String TABLE_ASSESSMENT = "Assessment";
    public static final String TABLE_NOTE_IMAGE = "Course_Image";
    public static final String TABLE_TERM = "Term";

    //Note Specific Strings
    public static final String NOTE_TEXT = "noteText";
    public static final String NOTE_CREATED = "noteCreated";
    public static final String NOTE_IMAGE = "noteImage";
    //Generic Strings for construction of columnNames
    public static final String TABLE_ID = "_id";
    public static final String TITLE = "title";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";


    //Course Specific Strings
    public static final String COURSE_CODE = "courseCode";
    public static final String COURSE_STATUS = "courseStatus";
    public static final String MENTOR = "mentor";

    //Assessment table specific strings
    public static final String ASSESSMENT_DUE_DATE = "dueDate";
    public static final String ASSESSMENT_TARGET_SCORE = "targetScore";
    public static final String ASSESSMENT_EARNED_SCORE = "earnedScore";
    public static final String ASSESSMENT_IS_OBJECTIVE = "isObjective";

    //SQL to create term table:
    private static final String TERM_CREATE =
            "CREATE TABLE " + TABLE_TERM + " (" +
                    TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE + " TEXT, " +
                    START_DATE + " TEXT,"+
                    END_DATE + " TEXT "+ ")";
    //Term complete

    //SQL to create course table
    private static final String COURSE_CREATE =
            "CREATE TABLE " + TABLE_COURSE + " (" +
                    TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE + " TEXT, " +
                    MENTOR + " TEXT, " +
                    COURSE_CODE + "TEXT, " +
                    START_DATE + " TEXT, "+
                    END_DATE + " TEXT, "+
                    COURSE_STATUS + " TINYINT, " +
                    TABLE_ID+TABLE_TERM + " INTEGER, " +
                    "FOREIGN KEY(" + TABLE_ID+TABLE_TERM + ") REFERENCES " +TABLE_TERM + " ("+TABLE_ID+ "))";
    //Course complete

    //SQL to create notes table
    private static final String NOTES_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE + " TEXT, " +
                    NOTE_TEXT + " TEXT, " +
                    NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP," +
                    TABLE_ID+TABLE_COURSE + " INTEGER, " +
                    "FOREIGN KEY (" + TABLE_ID+TABLE_COURSE + ") REFERENCES " +TABLE_COURSE + " ("+TABLE_ID+ "))";
    //Note Complete
    private static final String NOTES_IMAGES_CREATE =
            "CREATE TABLE " + TABLE_NOTE_IMAGE + " (" +
                    TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_IMAGE+ " BLOB, " +
                    TABLE_ID+TABLE_NOTES + " INTEGER, " +
                    "FOREIGN KEY (" + TABLE_ID+TABLE_NOTES + ") REFERENCES " +TABLE_NOTES + " ("+TABLE_ID+ "))";

    //SQL to create notes table
    private static final String ASSESSMENT_CREATE =
            "CREATE TABLE " + TABLE_ASSESSMENT + " (" +
                    TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_DUE_DATE + "TEXT, "+
                    ASSESSMENT_IS_OBJECTIVE + "BOOLEAN, "+
                    ASSESSMENT_TARGET_SCORE + " INTEGER," +
                    ASSESSMENT_EARNED_SCORE + " INTEGER," +
                    TABLE_ID+TABLE_COURSE + " INTEGER, " +
                    "FOREIGN KEY (" + TABLE_ID+TABLE_COURSE + ") REFERENCES " +TABLE_COURSE + " ("+TABLE_ID+ "))";

    public static final String[] ALL_TERM_COLS =
            {TABLE_ID, TITLE, START_DATE, END_DATE};

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TERM_CREATE);
        sqLiteDatabase.execSQL(COURSE_CREATE);
        sqLiteDatabase.execSQL(NOTES_CREATE);
        sqLiteDatabase.execSQL(NOTES_IMAGES_CREATE);
        sqLiteDatabase.execSQL(ASSESSMENT_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS +" + TABLE_TERM);
        this.onCreate(sqLiteDatabase);
    }
}
