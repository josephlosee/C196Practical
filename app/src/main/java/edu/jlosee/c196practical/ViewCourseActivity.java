package edu.jlosee.c196practical;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ViewCourseActivity extends AppCompatActivity {

    public static final String NOTE_ID = "noteID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle bundle = getIntent().getExtras();
        int courseID = -1;
        if (bundle!=null){
            courseID = bundle.getInt(ViewTermActivity.COURSE_ID);

            String[] columns = {DBOpenHelper.TABLE_ID, DBOpenHelper.TITLE};
            String selection = DBOpenHelper.TABLE_ID+"=?";
            String[] selectionArgs = {String.valueOf(courseID)};

            Cursor courseInfo = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, selection, selectionArgs, null);

            if (courseInfo.moveToFirst()){
                String courseCode = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_CODE));
                String courseTitle = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.TITLE));
                String courseDescription = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_DESCRIPTION));

                EditText etCode = (EditText)findViewById(R.id.etCourseCode);
                EditText etTitle = (EditText) findViewById(R.id.etCourseTitle);
                EditText etDesc = (EditText)findViewById(R.id.etDescription);

                etCode.setText(courseCode);
                etCode.setEnabled(false);
                etTitle.setText(courseTitle);
                etTitle.setEnabled(false);
                etDesc.setText(courseDescription);
                etDesc.setEnabled(false);
            }
            String[]joinArgs = {String.valueOf(courseID)};
            //Cursor mentorsCursor = MainActivity.dbProvider.rawQuery(DBOpenHelper.MENTOR_JOIN_QUERY, joinArgs);
            Cursor mentorsCursor = MainActivity.dbProvider.rawQuery("Select * from Mentor;", null);
            ListView mentorlist = (ListView)findViewById(R.id.mentorList);
            CursorAdapter mentorAdapter = new MentorCursorAdapter(this, mentorsCursor);

            mentorlist.setAdapter(mentorAdapter);
        }

        toolbar.setTitle("Course");
        toolbar.inflateMenu(R.menu.menu_editsavecancel);
        setSupportActionBar(toolbar);
        //Button editButton = new Button(this);
        //;;editButton.setText("Edit");
        //editButton.setAlpha((float) .5);
        //toolbar.addView(editButton);



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

}
