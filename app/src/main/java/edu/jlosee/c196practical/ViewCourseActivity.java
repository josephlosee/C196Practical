package edu.jlosee.c196practical;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ViewCourseActivity extends AppCompatActivity {
    public static final String IS_ASSESSMENT = "isAssessment";
    private long courseID = -1;
    public static final String NOTE_ID = "noteID";

    private EditText etCode;
    private EditText etTitle;
    private EditText etDesc;

    private ListView mentorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle bundle = getIntent().getExtras();

        if (bundle!=null){
            courseID = bundle.getLong(TermDetailsActivity.COURSE_ID);

            String[] columns = {DBOpenHelper.TABLE_ID, DBOpenHelper.TITLE};
            String selection = DBOpenHelper.TABLE_ID+"=?";
            String[] selectionArgs = {String.valueOf(courseID)};

            Cursor courseInfo = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, selection, selectionArgs, null);

            if (courseInfo.moveToFirst()){
                String courseCode = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_CODE));
                String courseTitle = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.TITLE));
                String courseDescription = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_DESCRIPTION));

                etCode = (EditText)findViewById(R.id.etCourseCode);

                etTitle = (EditText) findViewById(R.id.etCourseTitle);

                etDesc = (EditText)findViewById(R.id.etDescription);

                etCode.setText(courseCode);
                //etCode.setEnabled(false);
                etTitle.setText(courseTitle);
                //etTitle.setEnabled(false);
                etDesc.setText(courseDescription);
                //etDesc.setEnabled(false);
            }

            String[]joinArgs = {String.valueOf(courseID)};
            //Cursor mentorsCursor = MainActivity.dbProvider.rawQuery(DBOpenHelper.MENTOR_JOIN_QUERY, joinArgs);
            Cursor mentorsCursor = MainActivity.dbProvider.rawQuery("Select * from Mentor;", null);

            mentorList = (ListView)findViewById(R.id.mentorList);
            CursorAdapter mentorAdapter = new MentorCursorAdapter(this, mentorsCursor);

            mentorList.setAdapter(mentorAdapter);
        }

        toolbar.setTitle("Course");
        toolbar.inflateMenu(R.menu.menu_editsavecancel);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_editsavecancel, menu);
        return true;
    }


    public void addMentorClicked(View view) {
        //TODO: display the mentors list, put an extra with add/delete/view flag
    }

    public void removeMentorClicked(View view) {
        //TODO: display the mentors list associated with this course, put an extra with add/delete/view flag
    }

    public void courseAssessmentsClicked(View view) {
        //TODO: diplays the assessment list activity, right now this
        Intent assessmentIntent = new Intent(this, NoteListActivity.class);
        assessmentIntent.putExtra(TermDetailsActivity.COURSE_ID, this.courseID);
        assessmentIntent.putExtra(IS_ASSESSMENT, true);
        startActivity(assessmentIntent);
    }

    public void courseNotesClicked(View view) {
        Intent noteIntent = new Intent(this, NoteListActivity.class);
        noteIntent.putExtra(TermDetailsActivity.COURSE_ID, this.courseID);
        startActivity(noteIntent);
    }
}//End of Class
