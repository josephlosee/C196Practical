package edu.jlosee.c196practical;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static edu.jlosee.c196practical.ViewCourseActivity.NOTE_ID;

public class AssessmentActivity extends AppCompatActivity {

    public static final String ASSESSMENT_ID = "assessmentID";
    private long assessmentID = -1; //flag value
    EditText etDueDate, etTitle;
    RadioButton objectiveRadio;
    RadioButton performanceRadio;
    ListView noteList;
    private Switch alertToggle;
    private WakefulReceiver alertReceiver = new WakefulReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get all the references to the widgets
        etTitle = (EditText)findViewById(R.id.etAssessmentTitle);
        etDueDate = (EditText) findViewById(R.id.assessmentDueDate);
        noteList = (ListView) findViewById(R.id.assessmentNotes);
        objectiveRadio = (RadioButton)findViewById(R.id.objButton);
        alertToggle = (Switch)findViewById(R.id.assessmentAlertSwitch);

        //TODO: get information from the db to set items.

        //Fill all the information
        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            assessmentID = extras.getLong(AssessmentActivity.ASSESSMENT_ID);
            if(assessmentID!=-1){
                String idQuery = "where _id = ?";
                String[] idArg = {String.valueOf(assessmentID)};

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
        boolean alarmState = this.alertToggle.isChecked();
        ContentValues content = new ContentValues();
        content.put(DBOpenHelper.ASSESSMENT_DUE_DATE, etDueDate.getText().toString());
        content.put(DBOpenHelper.ASSESSMENT_IS_OBJECTIVE, objectiveRadio.isChecked());
        content.put(DBOpenHelper.END_ALARM, alarmState);
        if (assessmentID == -1){
            //if we don't have an assessment id, must be a new assessment
            Uri insertUri = MainActivity.dbProvider.insert(DBProvider.ASSESSMENT_URI, content);
            assessmentID = Long.parseLong(insertUri.getLastPathSegment());
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
                        "Reminder: You have an assessment today for course ",
                        AssessmentActivity.class,
                        (int)assessmentID);
            } catch (ParseException e) {
                //TODO: Make an alert telling the user to select a valid date
                e.printStackTrace();
            }

        }else{
            //Doubt this cast will ever be a problem
            alertReceiver.cancelAlarm(this, (int)assessmentID);
        }

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
                //Todo: flag for assessment notes
                startActivity(selectedItemIntent);
            }
        });
    }

}//END OF CLASS

/*
        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        // Creates an Intent for the Activity
        Intent notifyIntent =
                new Intent(this, AssessmentActivity.class);
        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        //PendingIntent.get
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // Puts the PendingIntent into the notification builder
        builder.setContentIntent(notifyPendingIntent);
        // Notifications are issued by sending them to the
        // NotificationManager system service.
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds an anonymous Notification object from the builder, and
        // passes it to the NotificationManager
        int id = 66525;
        mNotificationManager.notify(id, builder.build());*/
