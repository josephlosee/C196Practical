package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;

import static edu.jlosee.c196practical.ViewCourseActivity.NOTE_ID;

public class AssessmentActivity extends AppCompatActivity {

    public static final String ASSESSMENT_ID = "assessmentID";
    private long assessmentID = -1; //flag value
    EditText etDueDate;
    RadioButton objectiveRadio;
    RadioButton performanceRadio;
    ListView noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etDueDate = (EditText) findViewById(R.id.assessmentDueDate);
        noteList = (ListView) findViewById(R.id.assessmentNotes);
        objectiveRadio = (RadioButton)findViewById(R.id.objButton);

        //TODO: get information from the db to set items.

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            assessmentID = extras.getLong(AssessmentActivity.ASSESSMENT_ID);
            setNoteList();
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Make this add a note, code is in note list activity class
                Intent assessmentNoteIntent = new Intent(AssessmentActivity.this, NoteDetails.class);
                assessmentNoteIntent.putExtra(ASSESSMENT_ID, assessmentID);
                startActivity(assessmentNoteIntent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        saveAssessment();
        this.finish();
    }

    //Save the assessment information and finish
    public void saveAssessment(){
        ContentValues content = new ContentValues();
        content.put(DBOpenHelper.ASSESSMENT_DUE_DATE, etDueDate.getText().toString());
        content.put(DBOpenHelper.ASSESSMENT_IS_OBJECTIVE, objectiveRadio.isChecked());
        if (assessmentID == -1){
            //if we don't have an assessment id, must be a new assessment
            MainActivity.dbProvider.insert(DBProvider.ASSESSMENT_URI, content);
        }
        else{
            String updateWhere = DBOpenHelper.TABLE_ID+"=?";
            String[] updateArgs = {String.valueOf(assessmentID)};
            MainActivity.dbProvider.update(DBProvider.ASSESSMENT_URI, content, updateWhere, updateArgs);
        }
    }

    public void assessmentAlertToggled(View v){
        //TODO: Set Alert
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_only, menu);
        return true;
    }

    public void alertConfirmation(){
        //boolean ret = false;
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Delete this item? This action cannot be undone.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(getCurrentFocus(), "OK selected", Snackbar.LENGTH_LONG).show();
                //ret=true;
                deleteAssessment();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(getCurrentFocus(), "Cancel selected", Snackbar.LENGTH_LONG).show();
            }
        });

        alertDialog.show();
    }

    public void deleteAssessment(){
        String delete = DBOpenHelper.TABLE_ID+"=?";
        String[] vals = {String.valueOf(this.assessmentID)};
        MainActivity.dbProvider.delete(DBProvider.ASSESSMENT_URI, delete, vals);

        this.finish();
    }

    //Populates the list of notes
    private void setNoteList(){
        String where = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_ASSESSMENT+"=?";
        String[] whereArgs = {String.valueOf(assessmentID)};
        Cursor notes = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);

        String[] from = {DBOpenHelper.TITLE};// + " " + DBOpenHelper.COURSE_CODE};
        int[] to = {android.R.id.text1};
        CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, notes, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        noteList.setAdapter(cursAdaptor);

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent selectedItemIntent = new Intent(AssessmentActivity.this, NoteDetails.class);
                selectedItemIntent.putExtra(NOTE_ID, id);
                selectedItemIntent.putExtra(AssessmentActivity.ASSESSMENT_ID, assessmentID);
                //Todo: flag for assessment notes
                startActivity(selectedItemIntent);
            }
        });
    }
}
