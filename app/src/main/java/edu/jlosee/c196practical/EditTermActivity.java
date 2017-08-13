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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Calendar;

public class EditTermActivity extends AppCompatActivity {

    //private Term newOrEditedTerm;
    private int termID =-1;

    EditText termTitle;
    EditText termStart;
    EditText termEnd;

    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);

        termTitle = (EditText) findViewById(R.id.termTitle);
        termStart = (EditText) findViewById(R.id.termStart);
        termEnd = (EditText) findViewById(R.id.termEnd);

        Intent testIntent = getIntent();
        Bundle extras = testIntent.getExtras();
        //Todo: Fill in information from passed TERM_ID extra

        if (extras != null){
            termID = extras.getInt(MainActivity.TERM_ID);
            //int selectedTerm = getIntent().getExtras().getInt(MainActivity.TERM_ID);
            String[] termArgs = {String.valueOf(termID)};
            Cursor termInfo = MainActivity.dbProvider.query(DBProvider.TERM_URI, null, "where id=? ", termArgs, null);
            termTitle.setText(termInfo.getString(termInfo.getColumnIndex(DBOpenHelper.TITLE)));
            termStart.setText(termInfo.getString(termInfo.getColumnIndex(DBOpenHelper.START_DATE)));
            termEnd.setText(termInfo.getString(termInfo.getColumnIndex(DBOpenHelper.END_DATE)));

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


        ContentValues termInfo = new ContentValues();
        termInfo.put(DBOpenHelper.TITLE, title);
        termInfo.put(DBOpenHelper.START_DATE, start);
        termInfo.put(DBOpenHelper.END_DATE, end);

        if (termID==-1){
            //If the term is a new one (-1 flag value), insert it
            Log.d("EditTerm", "Adding a new term...");
            MainActivity.dbProvider.insert(DBProvider.TERM_URI, termInfo);
        }else{
            Log.d("EditTerm", "Updating term: "+termID);
            String[] termArg = {String.valueOf(this.termID)};
            MainActivity.dbProvider.update(DBProvider.TERM_URI, termInfo, "where _id=? ", termArg);
        }
        finishActivity(RESULT_OK);
/*        if(newOrEditedTerm==null){
           newOrEditedTerm = new Term(termTitle.getText().toString(), startCalendar, endCalendar);
        }

        else{
            newOrEditedTerm.setTitle(termTitle.getText().toString());
            newOrEditedTerm.setStartDate(startCalendar);
            newOrEditedTerm.setEndDate(endCalendar);
            //return the term to the activity
        }*/
    }

    void updateStartLabel(){
        this.termStart.setText(""+startCalendar.get(Calendar.YEAR)+"-"+(startCalendar.get(Calendar.MONTH)+1)+"-"+startCalendar.get(Calendar.DAY_OF_MONTH));
    }

    void updateEndLabel(){
        //Month+1 because Calendar is bad and indexes with 0=January
        this.termEnd.setText(""+endCalendar.get(Calendar.YEAR)+"-"+(endCalendar.get(Calendar.MONTH)+1)+"-"+endCalendar.get(Calendar.DAY_OF_MONTH));
    }

}
