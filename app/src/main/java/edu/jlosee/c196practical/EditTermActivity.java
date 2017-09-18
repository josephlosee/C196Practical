package edu.jlosee.c196practical;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditTermActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_edit_term);

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

        //Start Date Dialog Picker setup
        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
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
                // TODO Auto-generated method stub
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndLabel();
            }
        };

        termStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EditTermActivity.this,
                        startDate, startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH),
                        startCalendar.get(Calendar.YEAR)).show();
            }
        });

        termEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EditTermActivity.this,
                        endDate, endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH),
                        endCalendar.get(Calendar.YEAR)).show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                EditText termTitle = (EditText) findViewById(R.id.termTitle);
                EditText termStart = (EditText) findViewById(R.id.termStart);
                EditText termEnd = (EditText) findViewById(R.id.termEnd);

                String title = termTitle.getText().toString();
                String start = termStart.getText().toString();
                String end = termEnd.toString();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        (getCurrentFocus()!=null)?getCurrentFocus().clearFocus():null;
    }

    protected void save(){

        EditText termTitle = (EditText) findViewById(R.id.termTitle);
        EditText termStart = (EditText) findViewById(R.id.termStart);
        EditText termEnd = (EditText) findViewById(R.id.termEnd);

        String title = termTitle.getText().toString();
        String start = termStart.getText().toString();
        String end = termEnd.getText().toString();

        Snackbar.make(this.getCurrentFocus(), "Title: "+title+ " start: " + start + " end: " +end, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        boolean startAlarm = this.startAlarmSwitch.isChecked();
        boolean endAlarm = this.endAlarmSwitch.isChecked();

        ContentValues termInfo = new ContentValues();
        termInfo.put(DBOpenHelper.TITLE, title);
        termInfo.put(DBOpenHelper.START_DATE, start);
        termInfo.put(DBOpenHelper.END_DATE, end);
        termInfo.put(DBOpenHelper.START_ALARM, startAlarm);
        termInfo.put(DBOpenHelper.END_ALARM, endAlarm);

        if (termID==-1){
            //If the term is a new one (-1 flag value), insert it
            Log.d("EditTerm", "Adding a new term...");
            MainActivity.dbProvider.insert(DBProvider.TERM_URI, termInfo);
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
        if (alert.isChecked()){
            Calendar alarmCal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sdf.parse(termEnd.getText().toString());
                alarmCal = sdf.getCalendar();
                startReceiver.setAlarm(this,
                        alarmCal,
                        "Reminder: Your term "+termTitle+ " is starting today! ",
                        EditTermActivity.class,
                        (int)termID);
            } catch (ParseException e) {
                Snackbar.make(this.getCurrentFocus(), "Enter a valid start date with format YYYY-MM-DD.", Snackbar.LENGTH_LONG)
                        .show();

                e.printStackTrace();
            }

        }else{
            //Doubt this cast will ever be a problem
            startReceiver.cancelAlarm(this, (int)termID);
        }
    }

    public void endAlarmToggled(View view) {
        Switch alert = (Switch)view;
        save();
        Log.d("EditTerm", "Toggled switch to "+alert.isChecked());

        //Set or cancel the alarm depending on if the toggle is now checked.
        if (alert.isChecked()){
            Calendar alarmCal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sdf.parse(termEnd.getText().toString());
                alarmCal = sdf.getCalendar();
                endReceiver.setAlarm(this,
                        alarmCal,
                        "Reminder: Your term "+termTitle+ " is ending today! ",
                        EditTermActivity.class,
                        (int)termID);
            } catch (ParseException e) {
                Snackbar.make(this.getCurrentFocus(), "Enter a valid end date with format YYYY-MM-DD.", Snackbar.LENGTH_LONG)
                        .show();

                e.printStackTrace();
            }

        }else{
            //Doubt this cast will ever be a problem
            endReceiver.cancelAlarm(this, (int)termID);
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
                Intent courseIntent = new Intent(EditTermActivity.this, ViewCourseActivity.class);
                courseIntent.putExtra(ViewCourseActivity.COURSE_ID, id);
                startActivity(courseIntent);
            }
        });
    }

    private void refreshCourseList(){
        courseCursor.requery();
        String[] from = {DBOpenHelper.TITLE};
        int[] to = {android.R.id.text1};
        CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, courseCursor, from, to, 0);

        courseList.setAdapter(cursAdaptor);
    }

    public void removeCourseClicked(View view) {
        Intent removeCourseIntent = new Intent(this, CourseList.class);
        removeCourseIntent.putExtra(MainActivity.TERM_ID, termID);
        removeCourseIntent.putExtra(CourseList.FLAG_REMOVE_COURSE, true);
        startActivity(removeCourseIntent);
    }

    public void addCourseClicked(View view) {
        Intent addCourseIntent = new Intent(this, CourseList.class);
        addCourseIntent.putExtra(MainActivity.TERM_ID, termID);
        addCourseIntent.putExtra(CourseList.FLAG_REMOVE_COURSE, false);
        startActivity(addCourseIntent);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        refreshCourseList();
    }
}//end of class
