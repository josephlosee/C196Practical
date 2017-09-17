package edu.jlosee.c196practical;

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
import android.widget.SimpleCursorAdapter;

import static edu.jlosee.c196practical.ViewCourseActivity.NOTE_ID;

/**
 * NoteList activity
 * Used for displaying both the List of Assessments and List of Notes, called by ViewCourseActivity
 * Tapping the list items displays the details of those items.
 */

public class NoteListActivity extends AppCompatActivity {

    private long courseID = -1;
    private boolean isAssessmentList = false;

    private Cursor notes;
    private CursorAdapter cursorAdapter;
    private ListView noteList;

    //TODO: add assessment foreign key to the Note table
    //TODO: will checking for a nonexistent key crash the program?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Bundle noteExtras = getIntent().getExtras();

        noteList = (ListView)findViewById(R.id.noteList);

        if (noteExtras!= null){ //quick null check
            //Fill in all the fun stuff
            courseID = noteExtras.getLong(TermDetailsActivity.COURSE_ID);
            isAssessmentList = noteExtras.getBoolean(ViewCourseActivity.IS_ASSESSMENT);
            //assessmentID = noteExtras.getLong(AssessmentActivity.ASSESSMENT_ID);
            if (isAssessmentList){
                setAssessmentList();
            }else{
                setNoteList();
            }

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNoteFab);
        if (!isAssessmentList) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Create the note intent and pass in relevant information needed.
                    Intent newNoteIntent = new Intent(NoteListActivity.this, NoteDetails.class);
                    newNoteIntent.putExtra(ViewCourseActivity.NOTE_ID, (long) -1); //flag value
                    newNoteIntent.putExtra(TermDetailsActivity.COURSE_ID, courseID);

                    //Flag for assessment notes
                    newNoteIntent.putExtra(NoteDetails.BOOL_ISCOURSENOTE, true);

                    startActivity(newNoteIntent);
                }
            });
        }else{
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Create the assessment intent and pass in relevant information needed.
                    Intent assessmentIntent = new Intent(NoteListActivity.this, AssessmentActivity.class);
                    assessmentIntent.putExtra(AssessmentActivity.ASSESSMENT_ID, (long) -1); //flag value
                    assessmentIntent.putExtra(TermDetailsActivity.COURSE_ID, courseID);

                    startActivity(assessmentIntent);
                }
            });
        }
    }

    private void setNoteList(){
        String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE+"=?";
        String[] whereArgs = {String.valueOf(courseID)};

        notes = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);

        String[] from = {DBOpenHelper.TITLE};// + " " + DBOpenHelper.COURSE_CODE};
        int[] to = {android.R.id.text1};

        cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, notes, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        noteList.setAdapter(cursorAdapter);

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent selectedItemIntent = new Intent(NoteListActivity.this, NoteDetails.class);
                selectedItemIntent.putExtra(NOTE_ID, id);
                selectedItemIntent.putExtra(TermDetailsActivity.COURSE_ID, courseID);
                selectedItemIntent.putExtra(NoteDetails.BOOL_ISCOURSENOTE, true);
                startActivity(selectedItemIntent);
            }
        });
    }

    private void setAssessmentList(){
        String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE+"=?";
        String[] whereArgs = {String.valueOf(courseID)};

        notes = MainActivity.dbProvider.query(DBProvider.ASSESSMENT_URI, null, where, whereArgs, null);

        String[] from = {DBOpenHelper.TITLE};// + " " + DBOpenHelper.COURSE_CODE};
        int[] to = {android.R.id.text1};

        cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                notes, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        noteList.setAdapter(cursorAdapter);

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent selectedItemIntent = new Intent(NoteListActivity.this, AssessmentActivity.class);
                selectedItemIntent.putExtra(AssessmentActivity.ASSESSMENT_ID, id);
                selectedItemIntent.putExtra(TermDetailsActivity.COURSE_ID, courseID);
                //selectedItemIntent.putExtra(NoteDetails.BOOL_ISCOURSENOTE, true);
                startActivity(selectedItemIntent);
            }
        });
    }


    private void refreshCursorAdapter(){
        String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE+"=?";
        String[] whereArgs = {String.valueOf(courseID)};
        if (!isAssessmentList){
            notes = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);
        }else{
            notes = MainActivity.dbProvider.query(DBProvider.ASSESSMENT_URI, null, where, whereArgs, null);
        }

        cursorAdapter.changeCursor(notes);
    }

    @Override
    public void onResume(){
        super.onResume();

        refreshCursorAdapter();
    }
}//end of class
