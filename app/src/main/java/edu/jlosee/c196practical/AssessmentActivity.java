package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class AssessmentActivity extends AppCompatActivity {

    private long assessmentID = -1; //flag value
    EditText etDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etDueDate = (EditText) findViewById(R.id.assessmentDueDate);

        //TODO: set the notes list, this code is in the note list activity class
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Make this add a note, code is in note list activity class
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //TODO: Add


    @Override
    public void onBackPressed() {


    }

    public void saveAssessment(){
        if (assessmentID == -1){
            //
            ContentValues content = new ContentValues();
            content.put(DBOpenHelper.ASSESSMENT_DUE_DATE, etDueDate.getText().toString());
            MainActivity.dbProvider.insert(DBProvider.ASSESSMENT_URI, content);
        }
    }
}
