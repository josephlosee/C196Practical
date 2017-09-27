package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MentorList extends AppCompatActivity {
    public static final String FLAG_REMOVE = "removeFlag";
    private Cursor mentorForCourse;
    private long courseId = -1;
    private ListView mentorListView;
    private CursorAdapter adapter;
    private Bundle courseBundle;
    private boolean removeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mentor_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Mentor List");

        mentorListView = (ListView)findViewById(R.id.mentorListView);

        courseBundle = getIntent().getExtras();
        setMentorListView();

        //The floating action button will always add a new mentor
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mentorDetails = new Intent(MentorList.this, MentorDetails.class);
                startActivityForResult(mentorDetails,8888);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setMentorListView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setMentorListView(){
        if (courseBundle!=null){
            courseId = courseBundle.getLong(CourseDetails.COURSE_ID);
            String[] selectionArgs = {String.valueOf(courseId)};
            removeFlag = courseBundle.getBoolean(FLAG_REMOVE);
            //Setup the correct query
            if (removeFlag) {
                mentorForCourse = MainActivity.dbProvider.rawQuery(DBOpenHelper.MENTOR_JOIN_QUERY, selectionArgs);
            }else{
                //mentorForCourse = MainActivity.dbProvider.rawQuery(DBOpenHelper.MENTORS_NOT_IN_COURSE_QUERY, selectionArgs);
                String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE+"=?";

                Cursor mentorsInCourse = MainActivity.dbProvider.query(DBProvider.COURSE_MENTORS_URI, null, where, selectionArgs, null);

                if (mentorsInCourse!=null && mentorsInCourse.moveToFirst()) {
                    StringBuilder mentorRawQuery = new StringBuilder(DBOpenHelper.MENTORS_NOT_IN_COURSE2);
                    //String[] mentors = new String[mentorsInCourse.getCount()];
                    int numMentorsInCourse = mentorsInCourse.getCount();
                    for(int i=0; i<numMentorsInCourse; i++) {
                        mentorRawQuery.append(mentorsInCourse.getLong(mentorsInCourse.getColumnIndex(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR)));
                        if (mentorsInCourse.moveToNext()){
                            mentorRawQuery.append(",");
                        }else{
                            mentorRawQuery.append(")");
                        }
                    }
                    mentorForCourse = MainActivity.dbProvider.rawQuery(mentorRawQuery.toString(), null);
                }else{
                    mentorForCourse = MainActivity.dbProvider.query(DBProvider.MENTOR_URI, null, null, null, null);
                }

            }
            //setup the adapter and the course view
            adapter = new CursorAdapterMentor(this, mentorForCourse);
            mentorListView.setAdapter(adapter);

            //Once initial setup is done for getting
            if (removeFlag){
                this.setupRemoveMentorFromCourse();
            }else{
                this.setupAddMentorToCourse();
            }

        }else{
            this.setupViewMentors();
        }
    }

    private void refreshCursor(){
        setMentorListView();
        //this.adapter.changeCursor(mentorForCourse);
    }

    /**
     * This is the setup method called when we're starting the activity to add mentors to a course.
     */
    private void setupAddMentorToCourse(){
        //If we're adding mentors to the course, set the onItemClickListener
        //to add the selected mentor fropkm the course mentors bridge table
        final long TEMP_COURSE_ID = this.courseId;

        mentorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                //String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR+"=?";
                //String[] whereArgs = {String.valueOf(id)};

                ContentValues courseMentorAddition = new ContentValues();
                courseMentorAddition.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR, id);
                courseMentorAddition.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE, TEMP_COURSE_ID);
                MainActivity.dbProvider.insert(DBProvider.COURSE_MENTORS_URI, courseMentorAddition);
                refreshCursor();
            }
        });
    }

    /**
     * This is the setup method called when we're starting the activity to remove mentors from a course.
     */
    private void setupRemoveMentorFromCourse(){
        //If we're removing mentors from the course, set the onItemClickListener
        //to delete the selected mentor fropkm the course mentors bridge table
        final long TEMP_COURSE_ID = this.courseId;
        mentorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                //String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR+"=? and "+DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE+"=?";
                String where = DBOpenHelper.TABLE_ID+"=? and "+DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE+"=?";
                String[] whereArgs = {String.valueOf(id), String.valueOf(TEMP_COURSE_ID)};

                //NOTE: probably don't need do do these two things:
                LinearLayout mentorLL = (LinearLayout)view;
                TextView tvMentorID = (TextView) mentorLL.getChildAt(4);

                MainActivity.dbProvider.delete(DBProvider.COURSE_MENTORS_URI, where, whereArgs);
                refreshCursor();
            }
        });
    }

    /**
     * This function is used for when we just want to display all mentors
     */
    private void setupViewMentors(){
        //If a courseID was not passed (-1 flag value)
        Cursor mentorCursor = MainActivity.dbProvider.query(DBProvider.MENTOR_URI, null, null, null, "ORDER BY "+DBOpenHelper.MENTOR_NAME);
        CursorAdapter adapter = new CursorAdapterMentor(this, mentorCursor);
        mentorListView.setAdapter(adapter);
        mentorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent mentorDetails = new Intent(MentorList.this, MentorDetails.class);
                mentorDetails.putExtra(MentorDetails.MENTOR_ID, id);
                startActivityForResult(mentorDetails,8888);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean ret = false;
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                this.finish();
                break;
            default:break;

        }
        return ret;
    }
}//END OF CLASS
