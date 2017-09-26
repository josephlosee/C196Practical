package edu.jlosee.c196practical;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CourseDetails extends AppCompatActivity {
    public static final String IS_ASSESSMENT = "isAssessment";
    public static final String COURSE_ID = "courseID";
    private long courseID = -1;
    public static final String NOTE_ID = "noteID";
    public static final int COURSE_START_PREFIX = 700000;
    public static final int COURSE_END_PREFIX = 800000;

    //WIDGETS
    private EditText etCode;
    private EditText etTitle;
    private EditText etDesc;
    private EditText etStartDate, etEndDate;
    private ListView mentorList;
    private EditText etStatus;
    private Spinner courseStatus;
    private Switch startAlarmSwitch, endAlarmSwitch;

    //WakefulReceivers for handling alert notifications
    private WakefulReceiver startReceiver=new WakefulReceiver();
    private WakefulReceiver endReceiver=new WakefulReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Course Details");

        Bundle bundle = getIntent().getExtras();

        etCode = (EditText)findViewById(R.id.etCourseCode);
        etTitle = (EditText) findViewById(R.id.etCourseTitle);
        etDesc = (EditText)findViewById(R.id.etDescription);
        etStartDate = (EditText)findViewById(R.id.termStart);
        etEndDate = (EditText)findViewById(R.id.termEnd);
        courseStatus = (Spinner)findViewById(R.id.spinner);
        startAlarmSwitch = (Switch)findViewById(R.id.startAlarmSwitch);
        endAlarmSwitch = (Switch)findViewById(R.id.endAlarmSwitch);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.course_status, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        courseStatus.setAdapter(adapter);

        if (bundle!=null){
            courseID = bundle.getLong(CourseDetails.COURSE_ID);

            String selection = DBOpenHelper.TABLE_ID+"=?";
            String[] selectionArgs = {String.valueOf(courseID)};

            Cursor courseInfo = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, selection, selectionArgs, null);

            if (courseInfo!=null && courseInfo.moveToFirst()){
                String courseCode = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_CODE));
                String courseTitle = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.TITLE));
                String courseDescription = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_DESCRIPTION));
                String endDate = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.END_DATE));
                String startDate = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.START_DATE));
                int iStatus = courseInfo.getInt(courseInfo.getColumnIndex(DBOpenHelper.COURSE_STATUS));
                boolean startAlarm = courseInfo.getInt(courseInfo.getColumnIndex(DBOpenHelper.START_ALARM))==1;
                boolean endAlarm = courseInfo.getInt(courseInfo.getColumnIndex(DBOpenHelper.END_ALARM))==1;

                //Set all the parts
                etStartDate.setText(startDate);
                etEndDate.setText(endDate);
                etCode.setText(courseCode);
                etTitle.setText(courseTitle);
                etDesc.setText(courseDescription);
                courseStatus.setSelection(iStatus);
                startAlarmSwitch.setChecked(startAlarm);
                endAlarmSwitch.setChecked(endAlarm);

            }

            setMentorCourseList();
        }

        toolbar.inflateMenu(R.menu.delete_only);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Trying this to stop the keyboard from showing
        if (this.getCurrentFocus()!=null){
            this.getCurrentFocus().clearFocus();
        }

    }

    private void setMentorCourseList() {

        String[]joinArgs = {String.valueOf(courseID)};
        Cursor mentorsCursor = MainActivity.dbProvider.rawQuery(DBOpenHelper.MENTOR_JOIN_QUERY, joinArgs);
//            Cursor mentorsCursor = MainActivity.dbProvider.rawQuery("Select * from Mentor where _idCourse=?;", joinArgs);

        mentorList = (ListView)findViewById(R.id.mentorList);
        CursorAdapter mentorAdapter = new CursorAdapterMentor(this, mentorsCursor);

        mentorList.setAdapter(mentorAdapter);
        mentorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mentorIntent = new Intent(CourseDetails.this, MentorDetails.class);
                String where = DBOpenHelper.TABLE_ID+"=?";
                String[] whereArgs = {String.valueOf(l)};
                Cursor mentorInfo = MainActivity.dbProvider.query(DBProvider.COURSE_MENTORS_URI, null, where, whereArgs, null);
                if (mentorInfo!=null && mentorInfo.moveToFirst()){
                    long mentorID = mentorInfo.getLong(mentorInfo.getColumnIndex(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR));
                    mentorIntent.putExtra(MentorDetails.MENTOR_ID, mentorID);
                    startActivityForResult(mentorIntent, 12345);
                }

            }
        });
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        setMentorCourseList();
        super.onActivityReenter(resultCode, data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setMentorCourseList();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.delete_only, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        saveCourse();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean ret = false;
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                this.onBackPressed();
                break;
            case R.id.action_delete:
                alertDeleteConfirmation();
                break;
            default:break;

        }
        return ret;
    }

    public void alertDeleteConfirmation(){
        //boolean ret = false;
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Delete this item? This action cannot be undone.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(getWindow().getDecorView(), "OK selected", Snackbar.LENGTH_LONG).show();
                //ret=true;
                deleteCourse();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(getWindow().getDecorView(), "Cancel selected", Snackbar.LENGTH_LONG).show();
            }
        });

        alertDialog.show();
    }

    /**
     * Deletes the current course
     */
    public void deleteCourse(){
        //Delete the assessment from the table
        String delete = DBOpenHelper.TABLE_ID+"=?";
        String[] vals = {String.valueOf(this.courseID)};
        MainActivity.dbProvider.delete(DBProvider.COURSE_URI, delete, vals);
        startReceiver.cancelAlarm(this, ((int)courseID+COURSE_START_PREFIX));
        endReceiver.cancelAlarm(this, ((int)courseID+COURSE_END_PREFIX));
        this.finish();
    }

    /**
     * Starts the Mentor List Activity to add mentors to this course that are not currently attached to it.
     * @param view
     */
    public void addMentorClicked(View view) {
        Intent addMentorIntent = new Intent(this, MentorList.class);
        addMentorIntent.putExtra(COURSE_ID, this.courseID);
        addMentorIntent.putExtra(MentorList.FLAG_REMOVE, false);
        startActivityForResult(addMentorIntent, 12345);
    }

    /**
     * Starts the Mentor List activity to remove mentors from this course
     */
    public void removeMentorClicked(View view) {
        Intent removeMentorIntent = new Intent(this, MentorList.class);
        removeMentorIntent.putExtra(COURSE_ID, this.courseID);
        removeMentorIntent.putExtra(MentorList.FLAG_REMOVE, true);
        startActivityForResult(removeMentorIntent, 12345);
    }

    /**
     * Opens the list of assessments attached to this
     * @param view
     */
    public void courseAssessmentsClicked(View view) {
        String title = etTitle.getText().toString();
        String code =  etCode.getText().toString();
        String description = etDesc.getText().toString();
        if (courseID==-1 && (title.isEmpty() & code.isEmpty() & description.isEmpty())) {
            Snackbar.make(getWindow().getDecorView(),
                    "Enter a course title, code, or description before entering notes or assessments.",
                    Snackbar.LENGTH_LONG);
        }else {
            Intent assessmentIntent = new Intent(this, NoteListActivity.class);
            assessmentIntent.putExtra(COURSE_ID, this.courseID);
            assessmentIntent.putExtra(IS_ASSESSMENT, true);
            startActivity(assessmentIntent);
        }
    }

    /**
     * Opens the list of notes attached to this course
     * @param view
     */
    public void courseNotesClicked(View view) {
        String title = etTitle.getText().toString();
        String code =  etCode.getText().toString();
        String description = etDesc.getText().toString();
        if (courseID==-1 && (title.isEmpty() & code.isEmpty() & description.isEmpty())){
            Snackbar.make(getWindow().getDecorView(),
                    "Enter a course title, code, or description before entering notes or assessments.",
                    Snackbar.LENGTH_LONG);
        }else{
            saveCourse();
            Intent noteIntent = new Intent(this, NoteListActivity.class);
            noteIntent.putExtra(CourseDetails.COURSE_ID, this.courseID);
            startActivity(noteIntent);
        }

    }

    /**
     * Handles the button click for setting the course start date
     * @param view
     */
    public void startDateCalendar(View view) {
        SimpleDateFormat sdfNoTime = new SimpleDateFormat("yyyy-MM-dd");
        String date = this.etStartDate.getText().toString();
        try {
            if (date.length()<9){
                sdfNoTime.setCalendar(Calendar.getInstance());
            }else{
                sdfNoTime.parse(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar startCal = sdfNoTime.getCalendar();

        new DatePickerDialog(CourseDetails.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        etStartDate.setText(""+year+"-"+(month+1)+"-"+day);
                    }}, startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH),
                startCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Handles the button click for setting the course end date
     * @param view
     */
    public void endDateCalendar(View view) {
        SimpleDateFormat sdfNoTime = new SimpleDateFormat("yyyy-MM-dd");
        String date = this.etEndDate.getText().toString();

        try {
            if (date.length()<9){
                sdfNoTime.setCalendar(Calendar.getInstance());
            }else{
                sdfNoTime.parse(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar endCal = sdfNoTime.getCalendar();

        new DatePickerDialog(CourseDetails.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        etEndDate.setText(""+year+"-"+(month+1)+"-"+day);
                    }}, endCal.get(Calendar.YEAR),
                endCal.get(Calendar.MONTH),
                endCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveCourse(){
        boolean startAlarm = this.startAlarmSwitch.isChecked();
        boolean endAlarm = this.endAlarmSwitch.isChecked();

        String title = etTitle.getText().toString();
        String code =  etCode.getText().toString();
        String description = etDesc.getText().toString();

        ContentValues courseInfo = new ContentValues();
        courseInfo.put(DBOpenHelper.TITLE, title);
        courseInfo.put(DBOpenHelper.COURSE_CODE, code);
        courseInfo.put(DBOpenHelper.COURSE_DESCRIPTION, description);
        courseInfo.put(DBOpenHelper.START_DATE, etStartDate.getText().toString());
        courseInfo.put(DBOpenHelper.END_DATE, etEndDate.getText().toString());
        courseInfo.put(DBOpenHelper.COURSE_STATUS, courseStatus.getSelectedItemPosition());
        courseInfo.put(DBOpenHelper.START_ALARM, startAlarm);
        courseInfo.put(DBOpenHelper.END_ALARM, endAlarm);

        if (courseID==-1){
            //Sanity check to avoid a bunch of blank courses
            if (!(title.isEmpty() & code.isEmpty() & description.isEmpty())){
                Uri insertUri = MainActivity.dbProvider.insert(DBProvider.COURSE_URI, courseInfo);
                if (insertUri!=null){
                    this.courseID=Long.parseLong(insertUri.getLastPathSegment());
                }
            }else{
                Snackbar.make(getWindow().getDecorView(), "Course title, code, and description are blank. Discarding new course.",
                        Snackbar.LENGTH_LONG);
            }
        }else{
            String where = DBOpenHelper.TABLE_ID+"=?";
            String[] args = {String.valueOf(courseID)};
            MainActivity.dbProvider.update(DBProvider.COURSE_URI, courseInfo, where, args);
        }
    }

    public void endAlarmToggled(View view) {
        Switch alert = (Switch)view;
        saveCourse();
        Log.d("CourseDetails", "Toggled switch to "+alert.isChecked());

        //Set or cancel the alarm depending on if the toggle is now checked.
        if (alert.isChecked()){
            Calendar alarmCal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sdf.parse(etEndDate.getText().toString());
                alarmCal = sdf.getCalendar();
                endReceiver.setAlarm(this,
                        alarmCal,
                        "Reminder: Your course "+etTitle.getText().toString()+ " is ending today! ",
                        CourseDetails.class,
                        ((int)courseID+COURSE_END_PREFIX));
            } catch (ParseException e) {
                Snackbar.make(getWindow().getDecorView(), "Enter a valid end date with format YYYY-MM-DD.", Snackbar.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
        }else{
            //Doubt this cast will ever be a problem
            endReceiver.cancelAlarm(this,  ((int)courseID+COURSE_END_PREFIX));
        }
    }

    public void startAlarmToggled(View view){
        Switch alert = (Switch)view;
        saveCourse();
        Log.d("CourseDetail", "Toggled switch to "+alert.isChecked());

        //Set or cancel the alarm depending on if the toggle is now checked.
        if (alert.isChecked()){
            Calendar alarmCal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sdf.parse(etStartDate.getText().toString());
                alarmCal = sdf.getCalendar();
                startReceiver.setAlarm(this,
                        alarmCal,
                        "Reminder: Your course "+etTitle.getText().toString()+ " is starting today! ",
                        CourseDetails.class,
                        (int)courseID);
            } catch (ParseException e) {
                Snackbar.make(getWindow().getDecorView(), "Enter a valid end date with format YYYY-MM-DD.", Snackbar.LENGTH_LONG)
                        .show();

                e.printStackTrace();
            }

        }else{
            //Doubt this cast will ever be a problem
            startReceiver.cancelAlarm(this, ((int)courseID+COURSE_START_PREFIX));
        }
    }
}//End of Class
