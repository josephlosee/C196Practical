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
import android.widget.ListView;

public class MentorList extends AppCompatActivity {
    public static final String FLAG_REMOVE = "removeFlag";
    private long courseId = -1;

    private boolean removeFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_mentors);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView mentorListView = (ListView)findViewById(R.id.mentorListView);

        Bundle courseBundle = getIntent().getExtras();
        if (courseBundle!=null){
            //TODO: QUERY THE COURSE MENTOR TABLE for Mentor IDs
            //TODO: Then Query MENTOR TABLE for mentor info, then populate the ListView

            String[] selectionArgs = {String.valueOf(courseBundle.getInt(ViewCourseActivity.COURSE_ID))};
            Cursor mentorForCourse = MainActivity.dbProvider.rawQuery(DBOpenHelper.MENTOR_JOIN_QUERY, selectionArgs);
            CursorAdapter adapter = new MentorCursorAdapter(this, mentorForCourse);
            mentorListView.setAdapter(adapter);

            removeFlag = courseBundle.getBoolean(FLAG_REMOVE);
            courseId = courseBundle.getLong(ViewCourseActivity.COURSE_ID);
            if (removeFlag){
                //TODO: Set the cursor adapter appropriately
                //If we're removing mentors from the course, set the onItemClickListener
                //to delete the selected mentor fropkm the course mentors bridge table
                mentorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                        String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR+"=?";
                        String[] whereArgs = {String.valueOf(id)};
                        MainActivity.dbProvider.delete(DBProvider.COURSE_MENTORS_URI, where, whereArgs);
                    }
                });

            }else{
                //If we're adding mentors to the course, set the onItemClickListener
                //to add the selected mentor fropkm the course mentors bridge table
                final long TEMP_COURSE_ID = this.courseId;
                //TODO: Set the cursor adapter appropriately
                mentorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                        String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR+"=?";
                        String[] whereArgs = {String.valueOf(id)};
                        ContentValues courseMentorAddition = new ContentValues();
                        adapterView.getAdapter().getItem(id);
                        courseMentorAddition.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR, id);
                        MainActivity.dbProvider.insert(DBProvider.COURSE_MENTORS_URI, courseMentorAddition);
                    }
                });
            }

        }else{
            //If a courseID was not passed (-1 flag value)
            Cursor mentorCursor = MainActivity.dbProvider.query(DBProvider.MENTOR_URI, null, null, null, "ORDER BY "+DBOpenHelper.MENTOR_NAME);
            CursorAdapter adapter = new MentorCursorAdapter(this, mentorCursor);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mentorDetails = new Intent(MentorList.this, MentorDetails.class);
                startActivity(mentorDetails);
            }
        });
    }


}
