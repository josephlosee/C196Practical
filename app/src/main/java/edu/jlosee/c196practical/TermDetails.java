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
import android.widget.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TermDetails extends AppCompatActivity {

    public static final int TERM_START_PREFIX = 400000;
    public static final int TERM_END_PREFIX = 500000;
    //private Term newOrEditedTerm;
    private ListView courseList;

    private long termID =-1;

    private EditText termTitle;
    private EditText termStart;
    private EditText termEnd;

    private Switch startAlarmSwitch, endAlarmSwitch;

    private WakefulReceiver startReceiver = new WakefulReceiver();
    private WakefulReceiver endReceiver = new WakefulReceiver();

    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();

    private Cursor courseCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);

        //Get all the references to the views
        termTitle = (EditText) findViewById(R.id.termTitle);
        termStart = (EditText) findViewById(R.id.termStart);
        termEnd = (EditText) findViewById(R.id.termEnd);
        courseList = (ListView)findViewById(R.id.courseList);
        startAlarmSwitch = (Switch)findViewById(R.id.startAlarmSwitch);
        endAlarmSwitch = (Switch)findViewById(R.id.endAlarmSwitch);

        Bundle extras = getIntent().getExtras();

        //Get all the relevant information
        if (extras != null){
            termID = extras.getLong(MainActivity.TERM_ID);
            //int selectedTerm = getIntent().getExtras().getInt(MainActivity.TERM_ID);
            String[] termArgs = {String.valueOf(termID)};
            Cursor termInfo = MainActivity.dbProvider.query(DBProvider.TERM_URI, null, "_id=?", termArgs, null);

            if (termInfo!=null && termInfo.moveToFirst()){
                //Text info.
                termTitle.setText(termInfo.getString(termInfo.getColumnIndex(DBOpenHelper.TITLE)));
                termStart.setText(termInfo.getString(termInfo.getColumnIndex(DBOpenHelper.START_DATE)));
                termEnd.setText(termInfo.getString(termInfo.getColumnIndex(DBOpenHelper.END_DATE)));
                //Alert info for switches
                boolean startAlert = termInfo.getInt(termInfo.getColumnIndex(DBOpenHelper.START_ALARM))==1;
                boolean endAlert = termInfo.getInt(termInfo.getColumnIndex(DBOpenHelper.END_ALARM))==1;
                startAlarmSwitch.setChecked(startAlert);
                endAlarmSwitch.setChecked(endAlert);

                setCourseListView();
            }

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Term Details");

        //Start Date Dialog Picker setup
        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartLabel();
            }
        };

        //End Date Dialog Picker setup
        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndLabel();
            }
        };

        termStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TermDetails.this,
                        startDate, startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH),
                        startCalendar.get(Calendar.YEAR)).show();
            }
        });

        termEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TermDetails.this,
                        endDate, endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH),
                        endCalendar.get(Calendar.YEAR)).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getCurrentFocus()!=null){
            getCurrentFocus().clearFocus();
        }
    }
    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                this.onBackPressed();
                break;
            case R.id.action_delete:
                if (courseCursor!=null) {
                    //nullity check because if the user is quick this will crash the program.
                    if (courseCursor.getCount() > 0) {
                        Snackbar.make(getWindow().getDecorView(),
                                "You cannot delete a term that has courses assigned.",
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        alertDeleteConfirmation();
                    }
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
                //Snackbar.make(getWindow().getDecorView(), "OK selected", Snackbar.LENGTH_LONG).show();
                deleteTerm();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Snackbar.make(getWindow().getDecorView(), "Cancel selected", Snackbar.LENGTH_LONG).show();
            }
        });

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.delete_only, menu);
        return true;
    }
    /**
     * Deletes the current term from the database
     */
    private void deleteTerm(){
        //Delete the assessment from the table
        String delete = DBOpenHelper.TABLE_ID+"=?";
        String[] vals = {String.valueOf(this.termID)};
        MainActivity.dbProvider.delete(DBProvider.TERM_URI, delete, vals);
        //Delete the alert if its been created.
        startReceiver.cancelAlarm(this, (int)termID);
        endReceiver.cancelAlarm(this, (int)termID);

        this.finish();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        setCourseListView();
        super.onActivityReenter(resultCode, data);
    }

    /**
     * Saves all relevant term information to the SQLite DB
     */
    protected void save(){
        EditText termTitle = (EditText) findViewById(R.id.termTitle);
        EditText termStart = (EditText) findViewById(R.id.termStart);
        EditText termEnd = (EditText) findViewById(R.id.termEnd);

        String title = termTitle.getText().toString();
        String start = termStart.getText().toString();
        String end = termEnd.getText().toString();

        /*Snackbar.make(this.getCurrentFocus(), "Title: "+title+ " start: " + start + " end: " +end, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/

        boolean startAlarm = this.startAlarmSwitch.isChecked();
        boolean endAlarm = this.endAlarmSwitch.isChecked();

        //Save all the current values
        ContentValues termInfo = new ContentValues();
        termInfo.put(DBOpenHelper.TITLE, title);
        termInfo.put(DBOpenHelper.START_DATE, start);
        termInfo.put(DBOpenHelper.END_DATE, end);
        termInfo.put(DBOpenHelper.START_ALARM, startAlarm);
        termInfo.put(DBOpenHelper.END_ALARM, endAlarm);

        if (termID==-1){
            //If the term is a new one (-1 flag value), insert it
            Log.d("EditTerm", "Adding a new term...");
            //Sanity check to avoid empty terms being added
            if (!(title.isEmpty()&start.isEmpty()&end.isEmpty())) {
                Uri insertUri = MainActivity.dbProvider.insert(DBProvider.TERM_URI, termInfo);
                if (insertUri != null) {
                    this.termID = Long.parseLong(insertUri.getLastPathSegment());
                }
            }else{
                Snackbar.make(getWindow().getDecorView(), "Enter term information.", Snackbar.LENGTH_LONG).show();
            }
        }else{
            Log.d("EditTerm", "Updating term: "+termID);
            String where = DBOpenHelper.TABLE_ID+"=?";
            String[] termArg = {String.valueOf(this.termID)};
            MainActivity.dbProvider.update(DBProvider.TERM_URI, termInfo, where, termArg);
        }
        finishActivity(RESULT_OK);
    }

    void updateStartLabel(){
        this.termStart.setText(""+startCalendar.get(Calendar.YEAR)+"-"+(startCalendar.get(Calendar.MONTH)+1)+"-"+startCalendar.get(Calendar.DAY_OF_MONTH));
    }

    void updateEndLabel(){
        //Month+1 because Calendar is bad and indexes with 0=January
        this.termEnd.setText(""+endCalendar.get(Calendar.YEAR)+"-"+(endCalendar.get(Calendar.MONTH)+1)+"-"+endCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void startAlarmToggled(View view) {
        Switch alert = (Switch)view;
        save();
        Log.d("EditTerm", "Toggled switch to "+alert.isChecked());

        //Set or cancel the alarm depending on if the toggle is now checked.
        if (alert.isChecked()&termID!=-1){
            Calendar alarmCal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sdf.parse(termEnd.getText().toString());
                alarmCal = sdf.getCalendar();
                startReceiver.setAlarm(this,
                        alarmCal,
                        "Reminder: Your term "+termTitle.getText().toString()+ " is starting today! ",
                        TermDetails.class,
                        TERM_START_PREFIX+(int)termID);
            } catch (ParseException e) {
                Snackbar.make(getWindow().getDecorView(), "Enter a valid start date with format YYYY-MM-DD.", Snackbar.LENGTH_LONG)
                        .show();

                e.printStackTrace();
            }

        }else{
            //Doubt this cast will ever be a problem
            startReceiver.cancelAlarm(this, TERM_START_PREFIX+(int)termID);
        }
    }

    public void endAlarmToggled(View view) {
        Switch alert = (Switch)view;
        save();
        Log.d("EditTerm", "Toggled switch to "+alert.isChecked());

        //Set or cancel the alarm depending on if the toggle is now checked.
        if (alert.isChecked()&termID!=-1){
            Calendar alarmCal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sdf.parse(termEnd.getText().toString());
                alarmCal = sdf.getCalendar();
                endReceiver.setAlarm(this,
                        alarmCal,
                        "Reminder: Your term "+termTitle.getText().toString()+ " is ending today! ",
                        TermDetails.class,
                        TERM_END_PREFIX+(int)termID);
            } catch (ParseException e) {
                Snackbar.make(getWindow().getDecorView(), "Enter a valid end date with format YYYY-MM-DD.", Snackbar.LENGTH_LONG)
                        .show();

                e.printStackTrace();
            }

        }else{
            //Doubt this cast will ever be a problem
            endReceiver.cancelAlarm(this, TERM_END_PREFIX+(int)termID);
        }
    }

    /**
     * Sets the course list and behavior. It'll be refreshed in onActivityResult
     */
    private void setCourseListView(){
        String[] columns = {DBOpenHelper.TABLE_ID, DBOpenHelper.TITLE};
        String selection = DBOpenHelper.TABLE_ID + DBOpenHelper.TABLE_TERM + "=?";
        String[] selectionArgs = {String.valueOf(termID)};

        courseCursor= MainActivity.dbProvider.query(DBProvider.COURSE_URI, columns, selection, selectionArgs, null);

        String[] from = {DBOpenHelper.TITLE};
        int[] to = {android.R.id.text1};
        CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, courseCursor, from, to, 0);

        courseList.setAdapter(cursAdaptor);

        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent courseIntent = new Intent(TermDetails.this, CourseDetails.class);
                courseIntent.putExtra(CourseDetails.COURSE_ID, id);
                startActivityForResult(courseIntent, 8888);
            }
        });
    }

    /**
     * Calls the course list of courses currently assigned to the term
     * @param view
     */
    public void removeCourseClicked(View view) {
        save();
        Intent removeCourseIntent = new Intent(this, CourseList.class);
        removeCourseIntent.putExtra(MainActivity.TERM_ID, termID);
        removeCourseIntent.putExtra(CourseList.FLAG_REMOVE_COURSE, true);
        startActivityForResult(removeCourseIntent, 8888);
    }

    /**
     * Calls the course list of courses not currently assigned to the term
     * @param view
     */
    public void addCourseClicked(View view) {
        save();
        Intent addCourseIntent = new Intent(this, CourseList.class);
        addCourseIntent.putExtra(MainActivity.TERM_ID, termID);
        addCourseIntent.putExtra(CourseList.FLAG_REMOVE_COURSE, false);
        startActivityForResult(addCourseIntent, 8888);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setCourseListView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startDateCalendar(View view) {
        SimpleDateFormat sdfNoTime = new SimpleDateFormat("yyyy-MM-dd");

        try {
            sdfNoTime.parse(this.termStart.getText().toString());
        } catch (ParseException e) {
            sdfNoTime.setCalendar(Calendar.getInstance());
            e.printStackTrace();
        }
        Calendar startCal = sdfNoTime.getCalendar();

        new DatePickerDialog(TermDetails.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        termStart.setText(""+year+"-"+(month+1)+"-"+day);
                    }}, startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH),
                startCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void endDateCalendar(View view) {
        SimpleDateFormat sdfNoTime = new SimpleDateFormat("yyyy-MM-dd");

        try {
            sdfNoTime.parse(this.termEnd.getText().toString());
        } catch (ParseException e) {
            sdfNoTime.setCalendar(Calendar.getInstance());
            e.printStackTrace();
        }
        Calendar endCal = sdfNoTime.getCalendar();

        new DatePickerDialog(TermDetails.this,
                new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                termEnd.setText(""+year+"-"+(month+1)+"-"+day);
                }}, endCal.get(Calendar.YEAR),
                endCal.get(Calendar.MONTH),
                endCal.get(Calendar.DAY_OF_MONTH)).show();
    }
}//end of class
