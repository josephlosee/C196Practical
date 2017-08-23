package edu.jlosee.c196practical;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import static edu.jlosee.c196practical.ViewCourseActivity.NOTE_ID;

public class NoteListActivity extends AppCompatActivity {

    private long courseID = -1;
    private long assessmentID = -1;

    ListView noteList;

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

        if (noteExtras!= null){

            //TODO: Should add URI and Foreign Key identifier to the extras use the
            // TODO:foreign key and the related table to get note information
            //TODO: set the list of items with note title
            //TODO: set the list onClick to open the note
            final long courseID = noteExtras.getLong(TermDetailsActivity.COURSE_ID);
            String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE+"=?";
            String[] whereArgs = {String.valueOf(courseID)};
            Cursor notes = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);

            String[] from = {DBOpenHelper.TITLE};// + " " + DBOpenHelper.COURSE_CODE};
            int[] to = {android.R.id.text1};
            CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, notes, from, to, 0);

            noteList.setAdapter(cursAdaptor);

            noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                    Intent selectedItemIntent = new Intent(NoteListActivity.this, NoteDetails.class);
                    selectedItemIntent.putExtra(NOTE_ID, id);
                    selectedItemIntent.putExtra(TermDetailsActivity.COURSE_ID, courseID);
                    //Todo: flag for assessment notes
                    startActivity(selectedItemIntent);
                }
            });
        }

        //TODO: Add floating acction button to add a note


    }
}//end of class
