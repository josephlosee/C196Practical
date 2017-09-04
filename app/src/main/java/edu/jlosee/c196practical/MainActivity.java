package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static final String TERM_ID = "termID";
    //private ArrayList<Term> terms = new ArrayList<>();
    public final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);

    private ListView termListView;

    public final int TESTID = 4;
    public final int FILL_WITH_TEST_DATA_ID = 123;

    public static DBProvider dbProvider;// = new DBProvider(this.getApplicationContext());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbProvider = new DBProvider(this.getApplicationContext());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Resources r = getResources();

        //int marginPX = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
        //float LeftMarginPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
        //float LeftMarginPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
        //float LeftMarginPX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());

        ConstraintLayout maincontent = (ConstraintLayout)findViewById(R.id.mainContent);
        LinearLayout navButtons = (LinearLayout)findViewById(R.id.navButtons);
                //new LinearLayout(this);

        /*LinearLayout.LayoutParams loParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        loParams.setMargins(marginPX, marginPX, marginPX, marginPX);
        loParams.gravity=Gravity.CENTER_VERTICAL;
        navButtons.setOrientation(LinearLayout.VERTICAL);
        navButtons.setLayoutParams(loParams);

        navButtons.setHorizontalGravity(Gravity.CENTER);
        navButtons.setGravity(Gravity.CENTER);

        navButtons.setVerticalGravity(Gravity.CENTER);*/

        //Setup buttons
        Button terms = new Button(this);
        terms.setText("Terms");
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termsIntent = new Intent(MainActivity.this, TermListActivity.class);
                startActivity(termsIntent);
            }
        });

        Button courses = new Button(this);
        courses.setText("Courses");
        courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent courseIntent = new Intent(MainActivity.this, TermDetailsActivity.class);
                startActivity(courseIntent);
            }
        });

        Button mentors = new Button(this);
        mentors.setText("Mentors");
        mentors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mentorIntent = new Intent(MainActivity.this, ViewMentors.class);
                startActivity(mentorIntent);
            }
        });

        //ConstraintLayout maincontent = (ConstraintLayout)findViewById(R.id.mainContent);

        //Add buttons to the linear layout
        navButtons.addView(terms);
        navButtons.addView(courses);
        navButtons.addView(mentors);

        //Add the layout to the constraint layout
        //maincontent.addView(navButtons);

        /* Debug stuff
        simpleDateFormat.setCalendar(Calendar.getInstance());
        String startDate = simpleDateFormat.format(Calendar.getInstance().getTime());
        Log.d("MainActivity", "Created start date: "+ startDate);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 6);
        String endDate = simpleDateFormat.format(cal.getTime());
        Log.d("MainActivity", "Created end date: "+ endDate);
        */

        /* TODO Move this to TermListActivity later
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//TODO: Make this a Add Term Button? Share?
                Intent intent = new Intent(getApplicationContext(), EditTermActivity.class);
                startActivity(intent);

            }
        });*/
    }

    /*
    public void setTermListView(){
        termListView = (ListView)findViewById(R.id.termList);

        Cursor cursor = dbProvider.query(DBProvider.TERM_URI, DBOpenHelper.ALL_TERM_COLS, null, null, null);
        String[] from = {DBOpenHelper.TITLE};//, DBOpenHelper.START_DATE, DBOpenHelper.END_DATE};
        int[] to = {android.R.id.text1};//, android.R.id.text1, android.R.id.text1};
        //CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        CursorAdapter cursAdaptor = new TermCursorAdapter(this, cursor);
        termListView.setAdapter(cursAdaptor);

        termListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TermDetailsActivity.class);
                intent.putExtra(TERM_ID, id);
                startActivity(intent);
            }
        });
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        menu.add(Menu.NONE, TESTID, Menu.NONE, "test");
        menu.add(Menu.NONE, FILL_WITH_TEST_DATA_ID, Menu.NONE, "Test Data");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                //do nothing
                break;
            case FILL_WITH_TEST_DATA_ID:
                this.debugDBInfo(this.dbProvider);
                break;
            case TESTID:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void debugDBInfo(DBProvider provider){
        provider.wipeDatabase();

        ContentValues term = new ContentValues();
        ContentValues course = new ContentValues();
        ContentValues notes = new ContentValues();
        ContentValues assessment = new ContentValues();
        ContentValues courseMentors = new ContentValues();
        String loremIpsem = getResources().getString(R.string.lorem_ipsem);

        addPlaceholderMentors();

        Calendar initial = Calendar.getInstance();
        //Fill in terms
        for (int t = 0; t <=5; t++){
            Calendar start = (Calendar)initial.clone();
            Calendar end = (Calendar)start.clone();
            start.add(Calendar.MONTH, 3*t);
            end.add(Calendar.MONTH, 3*t+1);
            term.put(DBOpenHelper.TITLE, "Term "+(t+1));
            term.put(DBOpenHelper.START_DATE, simpleDateFormat.format(start.getTime()));
            term.put(DBOpenHelper.END_DATE, simpleDateFormat.format(start.getTime()));

            Uri insertedTerm = provider.insert(DBProvider.TERM_URI, term);
            int termID = Integer.parseInt(insertedTerm.getLastPathSegment());

            //Fill in courses
            for (int c = 0; c < 5; c++){
                String courseCode = "T"+t+"C"+c+"0";

                course.put(DBOpenHelper.START_DATE, "TODO");
                course.put(DBOpenHelper.END_DATE, "TODO");
                course.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_TERM, termID);
                course.put(DBOpenHelper.COURSE_CODE, courseCode);
                course.put(DBOpenHelper.TITLE, "Course Title" + courseCode);
                course.put(DBOpenHelper.COURSE_STATUS, "Started");
                course.put(DBOpenHelper.COURSE_DESCRIPTION, loremIpsem);

                int notesMaxRandom = (int)(Math.random()*5+1);

                Uri insertedCourse = provider.insert(DBProvider.COURSE_URI, course);
                int courseID = Integer.parseInt(insertedCourse.getLastPathSegment());

                for (int n = 0; n < notesMaxRandom; n++){
                    //notes.put(DBOpenHelper.START_DATE, "TODO");
                    //notes.put(DBOpenHelper.END_DATE, "TODO");
                    notes.put(DBOpenHelper.TITLE, "Note for Course "+courseID + " Note # "+n);
                    notes.put(DBOpenHelper.NOTE_TEXT, loremIpsem);
                    notes.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE, courseID);

                    provider.insert(DBProvider.NOTES_URI, notes);

                    assessment.put(DBOpenHelper.ASSESSMENT_DUE_DATE, "TODO");
                    assessment.put(DBOpenHelper.ASSESSMENT_IS_OBJECTIVE, false);
                    assessment.put(DBOpenHelper.ASSESSMENT_TARGET_SCORE, 65);
                    assessment.put(DBOpenHelper.ASSESSMENT_EARNED_SCORE, 0);
                    assessment.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE, courseID);

                    provider.insert(DBProvider.ASSESSMENT_URI, assessment);

                    courseMentors.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE, courseID);
                    courseMentors.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR, n);

                    provider.insert(DBProvider.COURSE_MENTORS_URI, courseMentors);

                    /*
                        private static final String ASSESSMENT_CREATE =
            "CREATE TABLE " + TABLE_ASSESSMENT + " (" +
                    TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_DUE_DATE + " TEXT, "+
                    ASSESSMENT_IS_OBJECTIVE + " BOOLEAN, "+
                    ASSESSMENT_TARGET_SCORE + " INTEGER," +
                    ASSESSMENT_EARNED_SCORE + " INTEGER," +
                    TABLE_ID+TABLE_COURSE + " INTEGER, " +
                    "FOREIGN KEY (" + TABLE_ID+TABLE_COURSE + ") REFERENCES " +TABLE_COURSE + " ("+TABLE_ID+ "))";
                     */
                }
            }

            //mentor.clear();

            //

        }

        //setTermListView();
    }

    /**
     * Adds several mentors to the database
     *
     */
    private void addPlaceholderMentors(){
        ContentValues mentor = new ContentValues();

        //mentor.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE, "Jane Doe")
        mentor.put(DBOpenHelper.MENTOR_NAME, "Jane Doe");
        mentor.put(DBOpenHelper.PHONE, "555-555-5555");
        mentor.put(DBOpenHelper.EMAIL, "JaneDoe@wgu.edu");
        dbProvider.insert(DBProvider.MENTOR_URI, mentor);
        mentor.clear();
        mentor.put(DBOpenHelper.MENTOR_NAME, "John Doe");
        mentor.put(DBOpenHelper.PHONE, "555-555-6666");
        mentor.put(DBOpenHelper.EMAIL, "JohnDoe@wgu.edu");
        dbProvider.insert(DBProvider.MENTOR_URI, mentor);
        //mentor.clear();
        mentor.put(DBOpenHelper.MENTOR_NAME, "Alan Smithee");
        mentor.put(DBOpenHelper.PHONE, "555-555-7777");
        mentor.put(DBOpenHelper.EMAIL, "AlanSmithee@wgu.edu");
        dbProvider.insert(DBProvider.MENTOR_URI, mentor);
        //mentor.clear();
        mentor.put(DBOpenHelper.MENTOR_NAME, "Agrajag Blartfast");
        mentor.put(DBOpenHelper.PHONE, "555-555-8888");
        mentor.put(DBOpenHelper.EMAIL, "AgrajagBlartfast@magrathea.com");
        dbProvider.insert(DBProvider.MENTOR_URI, mentor);
        //mentor.clear();
        mentor.put(DBOpenHelper.MENTOR_NAME, "Esme WeatherWax");
        mentor.put(DBOpenHelper.PHONE, "555-555-9999");
        mentor.put(DBOpenHelper.EMAIL, "EsmeWeatherWax@ramtops.edu");
        dbProvider.insert(DBProvider.MENTOR_URI, mentor);
    }
}


