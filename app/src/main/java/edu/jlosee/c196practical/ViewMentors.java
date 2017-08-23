package edu.jlosee.c196practical;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;

public class ViewMentors extends AppCompatActivity {
    private int courseId = -1;
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

            String[] selectionArgs = {String.valueOf(courseBundle.getInt(TermDetailsActivity.COURSE_ID))};
            Cursor mentorForCourse = MainActivity.dbProvider.rawQuery(DBOpenHelper.MENTOR_JOIN_QUERY, selectionArgs);
            CursorAdapter adapter = new MentorCursorAdapter(this, mentorForCourse);
            mentorListView.setAdapter(adapter);

        }else{
            //If a courseID was not passed (-1 flag value)
            Cursor mentorCursor = MainActivity.dbProvider.query(DBProvider.MENTOR_URI, null, null, null, "ORDER BY "+DBOpenHelper.MENTOR_NAME);
            CursorAdapter adapter = new MentorCursorAdapter(this, mentorCursor);
            mentorListView.setAdapter(adapter);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
