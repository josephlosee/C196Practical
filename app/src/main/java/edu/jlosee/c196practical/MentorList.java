package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    private boolean removeFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_mentors);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mentorListView = (ListView)findViewById(R.id.mentorListView);

        Bundle courseBundle = getIntent().getExtras();

        if (courseBundle!=null){
            courseId = courseBundle.getLong(CourseDetails.COURSE_ID);
            String[] selectionArgs = {String.valueOf(courseId)};
            removeFlag = courseBundle.getBoolean(FLAG_REMOVE);
            //Setup the correct query
            if (removeFlag) {
                mentorForCourse = MainActivity.dbProvider.rawQuery(DBOpenHelper.MENTOR_JOIN_QUERY, selectionArgs);
            }else{
                mentorForCourse = MainActivity.dbProvider.rawQuery(DBOpenHelper.MENTORS_NOT_IN_COURSE_QUERY, selectionArgs);
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
            this.viewMentorListSetup();
        }

        //The floating action button will always add a new mentor
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mentorDetails = new Intent(MentorList.this, MentorDetails.class);
                startActivity(mentorDetails);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshCursor();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshCursor(){
        mentorForCourse.requery();
        this.adapter.changeCursor(mentorForCourse);
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
        mentorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR+"=?";
                String[] whereArgs = {String.valueOf(id)};

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
    private void viewMentorListSetup(){
        //If a courseID was not passed (-1 flag value)
        Cursor mentorCursor = MainActivity.dbProvider.query(DBProvider.MENTOR_URI, null, null, null, "ORDER BY "+DBOpenHelper.MENTOR_NAME);
        CursorAdapter adapter = new CursorAdapterMentor(this, mentorCursor);
        mentorListView.setAdapter(adapter);
        mentorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent mentorDetails = new Intent(MentorList.this, MentorDetails.class);
                mentorDetails.putExtra(MentorDetails.MENTOR_ID, id);
                startActivity(mentorDetails);
            }
        });
    }


}//END OF CLASS
