package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CourseList extends AppCompatActivity {

    public static final String FLAG_REMOVE_COURSE = "isCourseRemove";
    private long termID = -1;
    private ListView courseList;
    private boolean remove;
    Cursor courseCursor;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Course List");

        courseList=(ListView)findViewById(R.id.courseList);
        extras = getIntent().getExtras();

        setCourseListView();

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent courseIntent = new Intent(CourseList.this, CourseDetails.class);
                startActivityForResult(courseIntent, 12345);
            }
        });

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setCourseListView(){
        if (extras!=null){
            termID = extras.getLong(MainActivity.TERM_ID);

            remove = extras.getBoolean(FLAG_REMOVE_COURSE);

            String where;
            String [] whereArgs = {String.valueOf(termID)};

            if (remove){
                where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_TERM+"=?";
            }else{
                where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_TERM+"!=?";
                courseCursor = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, where, whereArgs, null);

            }
            courseCursor = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, where, whereArgs, null);
        }else{
            courseCursor = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, null, null, null);
            //Assume we're looking for all courses and set the list view to show all courses.
        }

        String[] from = {DBOpenHelper.TITLE};// + " " + DBOpenHelper.COURSE_CODE};
        int[] to = {android.R.id.text1};
        CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, courseCursor, from, to, 0);

        courseList.setAdapter(cursAdaptor);

        if (termID==-1) {
            courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent courseIntent = new Intent(CourseList.this, CourseDetails.class);
                    courseIntent.putExtra(CourseDetails.COURSE_ID, id);
                    startActivityForResult(courseIntent, 12345);
                }
            });
        }else if(remove) {
            courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    ContentValues vals = new ContentValues();
                    vals.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_TERM, "null");
                    String []whereArgs = {String.valueOf(id)};
                    MainActivity.dbProvider.update(DBProvider.COURSE_URI, vals, "_id=?", whereArgs);
                    Snackbar.make(getCurrentFocus(), "Removed course from term.", Snackbar.LENGTH_SHORT).show();
                    CourseList.this.refreshCursor();
                }
            });
            //set the course termID to null
        }else{
            courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    ContentValues vals = new ContentValues();
                    vals.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_TERM, termID);
                    String []whereArgs = {String.valueOf(id)};
                    MainActivity.dbProvider.update(DBProvider.COURSE_URI, vals, "_id=?", whereArgs);
                    Snackbar.make(getCurrentFocus(), "Added course to term.", Snackbar.LENGTH_SHORT).show();
                    CourseList.this.refreshCursor();
                }
            });
        }
    }

    private void refreshCursor(){
        boolean requery = courseCursor.requery();
        String[] from = {DBOpenHelper.TITLE};// + " " + DBOpenHelper.COURSE_CODE};
        int[] to = {android.R.id.text1};
        CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, courseCursor, from, to, 0);

        courseList.setAdapter(cursAdaptor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setCourseListView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        setCourseListView();
        super.onActivityReenter(resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}//End of class
