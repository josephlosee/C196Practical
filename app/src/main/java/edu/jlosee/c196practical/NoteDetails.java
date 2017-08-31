package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoteDetails extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 8888;

    EditText etTitle;

    private long noteID = -1; //Flag value
    ArrayList<ImageView> listOfNoteImages;
    private EditText etContent;
    private long parentID;
    private GridView imageGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageGrid = (GridView) findViewById(R.id.noteImages);

        Bundle noteExtras = getIntent().getExtras();


        if (noteExtras != null){
            parentID = noteExtras.getLong(TermDetailsActivity.COURSE_ID);
            noteID = noteExtras.getLong(ViewCourseActivity.NOTE_ID);

            etTitle = (EditText)findViewById(R.id.etNoteTitle);
            etContent = (EditText) findViewById(R.id.etNoteContent);

            String where = DBOpenHelper.TABLE_ID+"=?";
            String[] whereArgs = {String.valueOf(noteID)};

            //TODO: Add handling for if course note or assessment note:
            boolean isCourse = noteExtras.getBoolean("isCourseNote");
            if (isCourse){
                //Cursor note = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);
            }
            else{
                Cursor note = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);
            }
            Cursor note = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);

            if(note.moveToFirst()) {
                etTitle.setText(note.getString(note.getColumnIndex(DBOpenHelper.TITLE)));
                etContent.setText(note.getString(note.getColumnIndex(DBOpenHelper.NOTE_TEXT)));
            }

            //This code should get a cursor of all the images associated with this note,
            //Set it to an image adapter,
            //Then set it the gridview and display all the related images.
            //KEY WORD: SHOULD. Todo: test this.
            String imgWhere = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE+"=?";
            Cursor imagesCursor = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, imgWhere, whereArgs, null);
            ImageAdapter noteImagesAdapter = new ImageAdapter(this);
            noteImagesAdapter.setCursor(imagesCursor);
            imageGrid.setAdapter(noteImagesAdapter);
        }//END OF SETUP IF A courseID or assessmentID was passed

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //Camera button in lower right to take a picture of notes or whatever
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    File imageFile = null;
                    try{
                        imageFile = createImageFile();
                    }catch(IOException e){
                        Log.d("NoteDetails","Exception when attempting to create picture file");
                    }

                    if (imageFile!=null){
                        Uri photoURI = FileProvider.getUriForFile(NoteDetails.this, "edu.jlosee.fileprovider", imageFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }


                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //ImageView mImageView = (ImageView) findViewById(R.id.imageView);
        ImageView imageView = (ImageView)findViewById(R.id.imageView2);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //TODO: This causes a crash because data is null?
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            GridView imageGrid = (GridView)findViewById(R.id.noteImages);

            //TODO: Does this work?
            //Uri photoUri = (Uri) extras.get(MediaStore.EXTRA_OUTPUT);

            //Send the image to the gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

            //Save the information to the database:
            ContentValues noteImageValues = new ContentValues();
            noteImageValues.put(DBOpenHelper.NOTE_IMAGE_URI, contentUri.toString());
            MainActivity.dbProvider.insert(DBProvider.NOTE_IMAGE_URI, noteImageValues);

            //And we're done handling the image... I think //TODO: are we done handling the image?
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("NoteDetails", "Image created at pate: " + mCurrentPhotoPath);
        //TODO:
        return image;
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
        noteInfo.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE, parentID);

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
