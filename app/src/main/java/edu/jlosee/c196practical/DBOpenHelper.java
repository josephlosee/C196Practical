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

    //public constant for a join:
    public static final String MENTOR_JOIN_QUERY = "Select * from Mentor join CourseMentors on Mentor._id = CourseMentors._idMentor where CourseMentors._idCourse = ?";

    //Constants for identifying table and columns
    public static final String TABLE_NOTES = "CourseNotes";
    public static final String TABLE_COURSE = "Course";
    public static final String TABLE_ASSESSMENT = "Assessment";
    public static final String TABLE_NOTE_IMAGE = "Course_Image";
    public static final String TABLE_TERM = "Term";
    public static final String TABLE_MENTOR = "Mentor";
    public static final String TABLE_COURSE_MENTORS = "CourseMentors";

    //Note Specific Strings
    public static final String NOTE_TEXT = "noteText";
    public static final String NOTE_CREATED = "noteCreated";
    public static final String NOTE_IMAGE_URI = "noteImageURI";
    //Generic Strings for construction of columnNames
    public static final String TABLE_ID = "_id";
    public static final String TITLE = "title";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";


    //Course Specific Strings
    public static final String COURSE_CODE = "courseCode";
    public static final String COURSE_STATUS = "courseStatus";
    public static final String COURSE_DESCRIPTION = "courseDescription";
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
                    COURSE_DESCRIPTION + " TEXT, " +
                    MENTOR + " INTEGER, " +
                    COURSE_CODE + " TEXT, " +
                    START_DATE + " TEXT, "+
                    END_DATE + " TEXT, "+
                    COURSE_STATUS + " TEXT, " +
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
                    "FOREIGN KEY (" + TABLE_ID+TABLE_ASSESSMENT + ") REFERENCES " +TABLE_ASSESSMENT + " ("+TABLE_ID+ "), " +
                    "FOREIGN KEY (" + TABLE_ID+TABLE_COURSE + ") REFERENCES " +TABLE_COURSE + " ("+TABLE_ID+ "))";

    //Note Complete
    private static final String NOTES_IMAGES_CREATE =
            "CREATE TABLE " + TABLE_NOTE_IMAGE + " (" +
                    TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_IMAGE_URI+ " TEXT, " +
                    TABLE_ID+TABLE_NOTES + " INTEGER, " +
                    "FOREIGN KEY (" + TABLE_ID+TABLE_NOTES + ") REFERENCES " +TABLE_NOTES + " ("+TABLE_ID+ "))";

    //SQL to create notes table
    private static final String ASSESSMENT_CREATE =
            "CREATE TABLE " + TABLE_ASSESSMENT + " (" +
                    TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_DUE_DATE + " TEXT, "+
                    ASSESSMENT_IS_OBJECTIVE + " BOOLEAN, "+
                    ASSESSMENT_TARGET_SCORE + " INTEGER," +
                    ASSESSMENT_EARNED_SCORE + " INTEGER," +
                    TABLE_ID+TABLE_COURSE + " INTEGER, " +
                    "FOREIGN KEY (" + TABLE_ID+TABLE_COURSE + ") REFERENCES " +TABLE_COURSE + " ("+TABLE_ID+ "))";

    public static final String MENTOR_NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";

    private static final String MENTOR_CREATE = "CREATE TABLE " + TABLE_MENTOR + " (" +
            TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MENTOR_NAME + " TEXT, " + EMAIL + " TEXT, " + PHONE + " TEXT)";


    private static final String COURSE_MENTORS_CREATE = "CREATE TABLE " + TABLE_COURSE_MENTORS + " ("+
            TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TABLE_ID+TABLE_COURSE + " INTEGER, " +
            TABLE_ID+TABLE_MENTOR + " INTEGER, " +
            "FOREIGN KEY (" + TABLE_ID+TABLE_COURSE + ") REFERENCES " + TABLE_COURSE + " (" + TABLE_ID+ "), " +
            "FOREIGN KEY (" + TABLE_ID+TABLE_COURSE + ") REFERENCES " + TABLE_MENTOR + " (" + TABLE_ID+ "))";
    public static final String[] ALL_TERM_COLS =
            {TABLE_ID, TITLE, START_DATE, END_DATE};


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TERM_CREATE);
        sqLiteDatabase.execSQL(COURSE_CREATE);
        sqLiteDatabase.execSQL(ASSESSMENT_CREATE);
        sqLiteDatabase.execSQL(NOTES_CREATE);
        sqLiteDatabase.execSQL(NOTES_IMAGES_CREATE);
        sqLiteDatabase.execSQL(MENTOR_CREATE);
        sqLiteDatabase.execSQL(COURSE_MENTORS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MENTOR + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE_IMAGE + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_MENTORS + ";");
        this.onCreate(sqLiteDatabase);
    }
}//END OF CLASS
