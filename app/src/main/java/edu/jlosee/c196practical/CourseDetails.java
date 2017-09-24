package edu.jlosee.c196practical;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CourseDetails extends AppCompatActivity {
    public static final String IS_ASSESSMENT = "isAssessment";
    public static final String COURSE_ID = "courseID";
    private long courseID = -1;
    public static final String NOTE_ID = "noteID";

    private EditText etCode;
    private EditText etTitle;
    private EditText etDesc;
    private EditText etStartDate, etEndDate;
    private ListView mentorList;
    private EditText etStatus;
    private Spinner courseStatus;

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

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.course_status, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        courseStatus.setAdapter(adapter);
        //courseStatus.setAdapter();

        if (bundle!=null){
            courseID = bundle.getLong(CourseDetails.COURSE_ID);

            //String[] columns = {DBOpenHelper.TABLE_ID, DBOpenHelper.TITLE};
            String selection = DBOpenHelper.TABLE_ID+"=?";
            String[] selectionArgs = {String.valueOf(courseID)};

            Cursor courseInfo = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, selection, selectionArgs, null);

            if (courseInfo.moveToFirst()){
                String courseCode = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_CODE));
                String courseTitle = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.TITLE));
                String courseDescription = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_DESCRIPTION));
                String endDate = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.END_DATE));
                String startDate = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.START_DATE));
                int iStatus = courseInfo.getInt(courseInfo.getColumnIndex(DBOpenHelper.COURSE_STATUS));

                //Set all the parts
                etStartDate.setText(startDate);
                etEndDate.setText(endDate);
                etCode.setText(courseCode);
                etTitle.setText(courseTitle);
                etDesc.setText(courseDescription);
                courseStatus.setSelection(iStatus);

            }

            String[]joinArgs = {String.valueOf(courseID)};
            Cursor mentorsCursor = MainActivity.dbProvider.rawQuery(DBOpenHelper.MENTOR_JOIN_QUERY, joinArgs);
//            Cursor mentorsCursor = MainActivity.dbProvider.rawQuery("Select * from Mentor where _idCourse=?;", joinArgs);

            mentorList = (ListView)findViewById(R.id.mentorList);
            CursorAdapter mentorAdapter = new CursorAdapterMentor(this, mentorsCursor);

            mentorList.setAdapter(mentorAdapter);
        }

        toolbar.inflateMenu(R.menu.delete_only);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Trying this to stop the keyboard from showing
        if (this.getCurrentFocus()!=null){
            this.getCurrentFocus().clearFocus();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_editsavecancel, menu);
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
                saveCourse();
                this.finish();
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
                Snackbar.make(getCurrentFocus(), "OK selected", Snackbar.LENGTH_LONG).show();
                //ret=true;
                deleteCourse();
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
     * Deletes the current course
     */
    public void deleteCourse(){
        //Delete the assessment from the table
        String delete = DBOpenHelper.TABLE_ID+"=?";
        String[] vals = {String.valueOf(this.courseID)};
        MainActivity.dbProvider.delete(DBProvider.COURSE_URI, delete, vals);
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
        startActivity(addMentorIntent);
    }

    /**
     * Starts the Mentor List activity to remove mentors from this course
     */
    public void removeMentorClicked(View view) {
        Intent addMentorIntent = new Intent(this, MentorList.class);
        addMentorIntent.putExtra(COURSE_ID, this.courseID);
        addMentorIntent.putExtra(MentorList.FLAG_REMOVE, true);
        startActivity(addMentorIntent);
    }

    /**
     * Opens the list of assessments attached to this
     * @param view
     */
    public void courseAssessmentsClicked(View view) {
        Intent assessmentIntent = new Intent(this, NoteListActivity.class);
        assessmentIntent.putExtra(COURSE_ID, this.courseID);
        assessmentIntent.putExtra(IS_ASSESSMENT, true);
        startActivity(assessmentIntent);
    }

    /**
     * Opens the list of notes attached to this course
     * @param view
     */
    public void courseNotesClicked(View view) {
        Intent noteIntent = new Intent(this, NoteListActivity.class);
        noteIntent.putExtra(CourseDetails.COURSE_ID, this.courseID);
        startActivity(noteIntent);
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
                        etStartDate.setText(""+year+"-"+month+"-"+day);
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
                        etEndDate.setText(""+year+"-"+month+"-"+day);
                    }}, endCal.get(Calendar.YEAR),
                endCal.get(Calendar.MONTH),
                endCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveCourse(){
        ContentValues courseInfo = new ContentValues();
        courseInfo.put(DBOpenHelper.TITLE, etTitle.getText().toString());
        courseInfo.put(DBOpenHelper.COURSE_CODE, etCode.getText().toString());
        courseInfo.put(DBOpenHelper.START_DATE, etStartDate.getText().toString());
        courseInfo.put(DBOpenHelper.END_DATE, etEndDate.getText().toString());
        courseInfo.put(DBOpenHelper.COURSE_STATUS, courseStatus.getSelectedItemPosition());

        if (courseID==-1){
            Uri insertUri = MainActivity.dbProvider.insert(DBProvider.COURSE_URI, courseInfo);
            if (insertUri!=null){
                this.courseID=Long.parseLong(insertUri.getLastPathSegment());
            }
        }else{
            String where = DBOpenHelper.TABLE_ID+"=?";
            String[] args = {String.valueOf(courseID)};
            MainActivity.dbProvider.update(DBProvider.COURSE_URI, courseInfo, where, args);
        }
    }
}//End of Class
