package edu.jlosee.c196practical;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static edu.jlosee.c196practical.CourseDetails.NOTE_ID;

public class AssessmentActivity extends AppCompatActivity {

    public static final String ASSESSMENT_ID = "assessmentID";
    private long assessmentID = -1; //flag value
    EditText etDueDate, etTitle;
    RadioButton objectiveRadio;
    RadioButton performanceRadio;
    ListView noteList;
    private Switch alertToggle;
    private WakefulReceiver alertReceiver = new WakefulReceiver();
    private long courseID;
    private String courseTitle, courseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Assessment Details");

        //Get all the references to the widgets
        etTitle = (EditText)findViewById(R.id.etAssessmentTitle);
        etDueDate = (EditText) findViewById(R.id.assessmentDueDate);
        noteList = (ListView) findViewById(R.id.assessmentNotes);
        objectiveRadio = (RadioButton)findViewById(R.id.objButton);
        alertToggle = (Switch)findViewById(R.id.assessmentAlertSwitch);

        //Fill all the information
        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            assessmentID = extras.getLong(AssessmentActivity.ASSESSMENT_ID);
            courseID = extras.getLong(CourseDetails.COURSE_ID);
            if(assessmentID!=-1){
                String idQuery = "where _id = ?";
                String[] idArg = {String.valueOf(assessmentID)};
                String[] courseIDArg = {String.valueOf(courseID)};
                Cursor assessmentInfo = MainActivity.dbProvider.query(DBProvider.ASSESSMENT_URI, null, idQuery, idArg, null);

                if (assessmentInfo != null && assessmentInfo.moveToFirst()) {

                    etDueDate.setText(assessmentInfo.getString(assessmentInfo.getColumnIndex(DBOpenHelper.ASSESSMENT_DUE_DATE)));
                    etTitle.setText(assessmentInfo.getString(assessmentInfo.getColumnIndex(DBOpenHelper.TITLE)));
                    boolean alarm = (assessmentInfo.getInt(assessmentInfo.getColumnIndex(DBOpenHelper.END_ALARM)) == 1);
                    alertToggle.setChecked(alarm);
                    boolean objective = (assessmentInfo.getInt(assessmentInfo.getColumnIndex(DBOpenHelper.ASSESSMENT_IS_OBJECTIVE)) == 1);
                    objectiveRadio.setChecked(objective);
                }

                setNoteList();
            }
         }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lets the user add a note to the assessment.
                Intent assessmentNoteIntent = new Intent(AssessmentActivity.this, NoteDetails.class);
                assessmentNoteIntent.putExtra(NoteDetails.PARENT_ID, assessmentID);
                assessmentNoteIntent.putExtra(NoteDetails.BOOL_ISCOURSENOTE, false);
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
        boolean alarmState = this.alertToggle.isChecked();
        ContentValues content = new ContentValues();
        content.put(DBOpenHelper.ASSESSMENT_DUE_DATE, etDueDate.getText().toString());
        content.put(DBOpenHelper.ASSESSMENT_IS_OBJECTIVE, objectiveRadio.isChecked());
        content.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE, courseID);
        content.put(DBOpenHelper.END_ALARM, alarmState);

        if (assessmentID == -1){
            //if we don't have an assessment id, must be a new assessment
            Uri insertUri = MainActivity.dbProvider.insert(DBProvider.ASSESSMENT_URI, content);
            if (insertUri!=null) {
                assessmentID = Long.parseLong(insertUri.getLastPathSegment());
            }
        }
        else{
            String updateWhere = DBOpenHelper.TABLE_ID+"=?";
            String[] updateArgs = {String.valueOf(assessmentID)};
            MainActivity.dbProvider.update(DBProvider.ASSESSMENT_URI, content, updateWhere, updateArgs);
        }
    }

    /**
     *
     * @param v
     */
    public void assessmentAlertToggled(View v){
        Switch alert = (Switch)v;
        saveAssessment();
        Log.d("AssessmentActivity", "Toggled switch to "+alert.isChecked());

        //Set or cancel the alarm depending on if the toggle is now checked.
        if (alert.isChecked()){
            Calendar alarmCal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {


                sdf.parse(etDueDate.getText().toString());
                alarmCal = sdf.getCalendar();
                alertReceiver.setAlarm(this,
                        alarmCal,
                        this.alarmMessage(),
                        AssessmentActivity.class,
                        (int)assessmentID);
                //TEST SNACKBAR
                Snackbar.make(this.getCurrentFocus(),
                        "Notification alert set for "+ MainActivity.sdfNoTime.toPattern(),
                        Snackbar.LENGTH_SHORT).show();

            } catch (ParseException e) {
                Snackbar.make(this.getCurrentFocus(), "Enter a valid due date with format YYYY-MM-DD.", Snackbar.LENGTH_LONG)
                        .show();

                e.printStackTrace();
            }

        }else{
            //Doubt this cast will ever be a problem
            alertReceiver.cancelAlarm(this, (int)assessmentID);
        }

    }

    /**
     * Builds the alarmMessage with the course title
     * @return
     */
    private String alarmMessage(){
        if (courseCode!=null || courseTitle!=null){
            String idQuery = "where _id = ?";
            //String[] idArg = {String.valueOf(assessmentID)};
            String[] courseIDArg = {String.valueOf(courseID)};

            Cursor courseInfo = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, idQuery, courseIDArg, null);
            if (courseInfo!=null && courseInfo.moveToFirst()){
                courseTitle = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_CODE));
                courseCode = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.TITLE));
            }
        }
        StringBuilder alarmMessage = new StringBuilder("Reminder: You have an ");
        if (objectiveRadio.isChecked()){
            alarmMessage.append("objective assessment today for course ");

        }else{
            alarmMessage.append("performance assessment today for course ");
        }
        alarmMessage.append(this.courseCode);
        alarmMessage.append(" ");
        alarmMessage.append(this.courseTitle);

        return alarmMessage.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        boolean ret = false;

        switch(id){
            case(android.R.id.home):
                saveAssessment();
                this.finish();
                break;
            case(R.id.action_delete):
                alertDeleteConfirmation();
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     * Displays an alert that asks if the user wants to delete the assessment`
     */
    public void alertDeleteConfirmation(){
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

    /**
     * Deletes the current assessment
     */
    public void deleteAssessment(){
        //Delete the assessment from the table
        String delete = DBOpenHelper.TABLE_ID+"=?";
        String[] vals = {String.valueOf(this.assessmentID)};
        MainActivity.dbProvider.delete(DBProvider.ASSESSMENT_URI, delete, vals);
        //Delete the alert if its been created.
        alertReceiver.cancelAlarm(this, (int)assessmentID);

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
                startActivity(selectedItemIntent);
            }
        });
    }

    public void endDateCalendar(View view) {
        SimpleDateFormat sdfNoTime = new SimpleDateFormat("yyyy-MM-dd");

        try {
            sdfNoTime.parse(this.etDueDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar endCal = sdfNoTime.getCalendar();

        new DatePickerDialog(AssessmentActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        etDueDate.setText(""+year+"-"+month+"-"+day);
                    }}, endCal.get(Calendar.YEAR),
                endCal.get(Calendar.MONTH),
                endCal.get(Calendar.DAY_OF_MONTH)).show();
    }
}//END OF CLASS