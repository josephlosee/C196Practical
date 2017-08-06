package edu.jlosee.c196practical;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ViewCourseActivity extends AppCompatActivity {

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
        }

        toolbar.inflateMenu(R.menu.menu_editsavecancel);
        toolbar.setTitle("Course");
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

}
