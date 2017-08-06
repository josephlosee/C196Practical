package edu.jlosee.c196practical;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ViewTermActivity extends AppCompatActivity {

    ListView courseList;
    public static final String COURSE_ID = "courseID";
    private int termID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_term);

        setCourseListView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Term");
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

    private void setCourseListView(){
        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            int termID = extras.getInt(MainActivity.TERM_ID);

            String[] columns = {DBOpenHelper.TABLE_ID, DBOpenHelper.TITLE};
            String selection = DBOpenHelper.TABLE_ID + DBOpenHelper.TABLE_TERM + "=?";
            String[] selectionArgs = {String.valueOf(termID)};

            final Cursor termCourses = MainActivity.dbProvider.query(DBProvider.COURSE_URI, columns, selection, selectionArgs, null);

            courseList = (ListView) findViewById(R.id.termViewOfcourses);

            String[] from = {DBOpenHelper.TITLE};// + " " + DBOpenHelper.COURSE_CODE};
            int[] to = {android.R.id.text1};
            CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, termCourses, from, to, 0);

            courseList.setAdapter(cursAdaptor);

            courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent(ViewTermActivity.this, ViewCourseActivity.class);
                    int selectedCourse = -1;
                    if (termCourses.moveToPosition(position)) {
                        selectedCourse = termCourses.getInt(termCourses.getColumnIndex("_id"));
                        Snackbar.make(view, "" + selectedCourse, Snackbar.LENGTH_LONG).show();
                    }
                    intent.putExtra(COURSE_ID, selectedCourse);
                    startActivity(intent);
                }
            });
        }
    }



}
