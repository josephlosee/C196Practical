package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class NoteDetails extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 8888;

    EditText etTitle;

    private long noteID = -1; //Flag value
    ArrayList<ImageView> listOfNoteImages;
    private EditText etContent;
    private long courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle noteExtras = getIntent().getExtras();
        if (noteExtras != null){
            courseID = noteExtras.getLong(TermDetailsActivity.COURSE_ID);
            noteID = noteExtras.getLong(ViewCourseActivity.NOTE_ID);

            etTitle = (EditText)findViewById(R.id.etNoteTitle);
            etContent = (EditText) findViewById(R.id.etNoteContent);

            String where = DBOpenHelper.TABLE_ID+"=?";
            String[] whereArgs = {String.valueOf(noteID)};

            Cursor note = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);

            if(note.moveToFirst()) {
                etTitle.setText(note.getString(note.getColumnIndex(DBOpenHelper.TITLE)));
                etContent.setText(note.getString(note.getColumnIndex(DBOpenHelper.NOTE_TEXT)));
            }

            //TODO: get note images
            String imgWhere = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_NOTES+"=?";
            //TODO: IMPLEMENT to get URIs:
            // Cursor images = MainActivity.dbProvider.query(DBProvider.NOTE_IMAGE_URI, null, imgWhere, whereArgs, null);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //ImageView mImageView = (ImageView) findViewById(R.id.imageView);
        ImageView imageView = (ImageView)findViewById(R.id.imageView2);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            GridView imageGrid = (GridView)findViewById(R.id.noteImages);
            //imageGrid.

            //TODO: Save the image tot he external public gallery
            //Then save the URI to the notes img table
            //Todo: change notes img table to take a string as the uri instead of a blob to satisfy requirement
            //TODO: write image adapter. Need an arraylist for the imageviews, can pass it in in the onCreate method
        }

    }

    @Override
    public void onBackPressed() {
        //TODO: Save information
        ContentValues noteValues = new ContentValues();
        saveNote();
        finish();
        //MainActivity.dbProvider.insert(DBProvider.NOTES_URI, noteValues);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO: Add handling for home
        switch (item.getItemId()){
            case android.R.id.home:
                saveNote();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveNote(){
        //TODO: check for note id being present already then choose between save and insert
        ContentValues noteInfo = new ContentValues();
        ;
        //noteInfo.put();


        noteInfo.put(DBOpenHelper.TITLE, etTitle.getText().toString());
        noteInfo.put(DBOpenHelper.NOTE_TEXT, etContent.getText().toString());
        noteInfo.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE, courseID);

        if (noteID==-1){

            MainActivity.dbProvider.insert(DBProvider.NOTES_URI, noteInfo);
            //TODO: save the image information
            //TODO: Change Note List to update
            //Use arraylist of URIs to update the
            //ImageView test = new ImageView();
            //test.setImageBitmap();
            //Bitmap test = new Bitmap();
            //test./

        }else{
            String where = DBOpenHelper.TABLE_ID+"=?";
            String[] whereArgs = {String.valueOf(noteID)};
            MainActivity.dbProvider.update(DBProvider.NOTES_URI, noteInfo, where, whereArgs);
        }
    }

    //TODO: Handle Delete button


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_only, menu);
        return true;
    }
}//END OF CLASS
