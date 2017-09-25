package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MentorDetails extends AppCompatActivity {

    public static final String MENTOR_ID = "mentorID";
    EditText etMentorName, etMentorPhone, etMentorEmail;
    private long mentorID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Mentor Details");

        etMentorName = (EditText)findViewById(R.id.etName);
        etMentorPhone = (EditText)findViewById(R.id.etPhone);
        etMentorEmail = (EditText)findViewById(R.id.etEmail);

        Bundle extras = getIntent().getExtras();

        //If extras was passed:
        if (extras!= null){
            mentorID = extras.getLong(MENTOR_ID);
            String where = DBOpenHelper.TABLE_ID+"=?";
            String[] whereArgs = {String.valueOf(mentorID)};
            Cursor mentorCursor = MainActivity.dbProvider.query(DBProvider.MENTOR_URI, null, where, whereArgs, null);

            if (mentorCursor!=null && mentorCursor.moveToFirst()){
                etMentorName.setText(mentorCursor.getString(mentorCursor.getColumnIndex(DBOpenHelper.MENTOR_NAME)));
                etMentorPhone.setText(mentorCursor.getString(mentorCursor.getColumnIndex(DBOpenHelper.PHONE)));
                etMentorEmail.setText(mentorCursor.getString(mentorCursor.getColumnIndex(DBOpenHelper.EMAIL)));
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        saveMentor();
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
                alertConfirmation();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMentor() {
        String mentorName = etMentorName.getText().toString();
        String mentorPhone = etMentorPhone.getText().toString();
        String mentorEmail = etMentorEmail.getText().toString();

        ContentValues mentorVals = new ContentValues();
        mentorVals.put(DBOpenHelper.MENTOR_NAME, mentorName);
        mentorVals.put(DBOpenHelper.EMAIL, mentorEmail);
        mentorVals.put(DBOpenHelper.PHONE, mentorPhone);

        if (mentorID==-1){
            MainActivity.dbProvider.insert(DBProvider.MENTOR_URI, mentorVals);
        }
        else{
            String where = DBOpenHelper.TABLE_ID+"=?";
            String[] whereArgs = {String.valueOf(mentorID)};
            MainActivity.dbProvider.update(DBProvider.MENTOR_URI, mentorVals, where, whereArgs);
        }

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
                deleteMentor();
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

    public void deleteMentor(){
        String delete = DBOpenHelper.TABLE_ID+"=?";
        String[] vals = {String.valueOf(this.mentorID)};
        MainActivity.dbProvider.delete(DBProvider.MENTOR_URI, delete, vals);

        this.finish();
    }
}//END OF CLASS
