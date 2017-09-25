package edu.jlosee.c196practical;

//THIS CLASS IS DONE -9/17 JLosee

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    public static final String BOOL_ISCOURSENOTE = "isCourseNote";
    public static final String IMAGE_URI_STRING = "imageURI";
    public static final String IMAGE_ID = "imageID";
    public static final String PARENT_ID = "parentID";
    private final int SHARE_ID = 544123;
    private boolean isCourseNote;

    EditText etTitle;
    private long noteID = -1; //Flag value
    ArrayList<ImageView> listOfNoteImages;
    ImageAdapter noteImagesAdapter;
    private EditText etContent;
    private long parentID;
    private GridView imageGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageGrid = (GridView) findViewById(R.id.noteImages);

        Bundle noteExtras = getIntent().getExtras();

        if (noteExtras != null){

            noteID = noteExtras.getLong(CourseDetails.NOTE_ID);

            etTitle = (EditText)findViewById(R.id.etNoteTitle);
            etContent = (EditText) findViewById(R.id.etNoteContent);

            String where = DBOpenHelper.TABLE_ID+"=?";
            String[] whereArgs = {String.valueOf(noteID)};

            isCourseNote = noteExtras.getBoolean(NoteDetails.BOOL_ISCOURSENOTE);
            if (isCourseNote){
                //Cursor note = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);
                Cursor note = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);
                parentID = noteExtras.getLong(PARENT_ID);
            }
            else{//assumed: is assessmentNote
                parentID = noteExtras.getLong(PARENT_ID);
            }
            Cursor note = MainActivity.dbProvider.query(DBProvider.NOTES_URI, null, where, whereArgs, null);

            if (note!=null) {
                if (note.moveToFirst()) {
                    etTitle.setText(note.getString(note.getColumnIndex(DBOpenHelper.TITLE)));
                    etContent.setText(note.getString(note.getColumnIndex(DBOpenHelper.NOTE_TEXT)));
                }
            }

            //Get a cursor to the associated images
            String imgWhere = DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_NOTES+"=?";
            final Cursor imagesCursor = MainActivity.dbProvider.query(DBProvider.NOTE_IMAGE_URI, null, imgWhere, whereArgs, null);
            noteImagesAdapter = new ImageAdapter(this);
            noteImagesAdapter.setCursor(imagesCursor);
            //Logging yay
            Log.d("NoteDetails", "ImageAdapter created for "+ DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_NOTES + " length: "+ noteImagesAdapter.getCount());

            imageGrid.setAdapter(noteImagesAdapter);
            imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long itemID) {
                        Log.d("NoteDetails", "onItemClick for imageGrid item "+itemID);
                        ImageAdapter.NoteImage image = (ImageAdapter.NoteImage)adapterView.getAdapter().getItem(i);
                        ImageView imageView = (ImageView)view;
                        String imageURI = image.imgUri.getPath();
                        Intent imageExpand = new Intent(NoteDetails.this, NoteImageView.class);
                        imageExpand.putExtra(IMAGE_URI_STRING, imageURI);
                        imageExpand.putExtra(IMAGE_ID, itemID);
                        startActivity(imageExpand);
                    }
            });
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
        //ImageView imageView = (ImageView)findViewById(R.id.imageView2);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //Send the image to the gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            //File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.parse(this.mCurrentPhotoPath);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

            //Save the information to the database:
            ContentValues noteImageValues = new ContentValues();
            noteImageValues.put(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_NOTES, noteID);
            noteImageValues.put(DBOpenHelper.NOTE_IMAGE_URI, contentUri.toString());
            Uri imageUri = MainActivity.dbProvider.insert(DBProvider.NOTE_IMAGE_URI, noteImageValues);
            if (imageUri != null) {
                long id = Long.parseLong(imageUri.getLastPathSegment());
                noteImagesAdapter.add(contentUri, id);
            }


            noteImagesAdapter.notifyDataSetChanged();
        }
    }

    String mCurrentPhotoPath;

    /**
     * Creates an image file for the camera to write to
     * @return
     * @throws IOException
     */
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
        Log.d("NoteDetails", "Image created at path: " + mCurrentPhotoPath);

        return image;
    }

    /**
     * Saves the note and closes the activity
     */
    @Override
    public void onBackPressed() {
        saveNote();
        //noteImagesAdapter.imageList.clear(); //Do i need this?
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.onBackPressed();
                break;
            case R.id.action_delete:
                alertConfirmation();
                break;
            case SHARE_ID:
                shareNote();
                break;
            default:
                String strNoCodeForMenuItem = "The selected menu item does not have code.";
                Snackbar.make(getCurrentFocus(), strNoCodeForMenuItem, Snackbar.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareNote() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, etTitle.getText()+"\n"+etContent.getText());
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    //Save the note to the database
    public void saveNote(){

        String title = etTitle.getText().toString();
        String body = etContent.getText().toString();

        ContentValues noteInfo = new ContentValues();

        noteInfo.put(DBOpenHelper.TITLE, title);
        noteInfo.put(DBOpenHelper.NOTE_TEXT, body);

            //If the coursenote bool is true, then set the foreign key for the course
            //otherwise, assume the note is for an assessment.
            if (isCourseNote) {
                noteInfo.put(DBOpenHelper.TABLE_ID + DBOpenHelper.TABLE_COURSE, parentID);
            } else {
                noteInfo.put(DBOpenHelper.TABLE_ID + DBOpenHelper.TABLE_ASSESSMENT, parentID);
            }


            if (noteID == -1) {
                Uri insert = MainActivity.dbProvider.insert(DBProvider.NOTES_URI, noteInfo);

                if (insert!=null) {
                    String where = DBOpenHelper.TABLE_ID + DBOpenHelper.TABLE_NOTES + "=?";
                    String[] whereArgs = {String.valueOf(-1)};
                    ContentValues update = new ContentValues();
                    update.put(DBOpenHelper.TABLE_ID + DBOpenHelper.TABLE_NOTES, insert.getLastPathSegment());
                    MainActivity.dbProvider.update(DBProvider.NOTE_IMAGE_URI, update, where, whereArgs);
                }

            } else {
                noteInfo.put(DBOpenHelper.TABLE_ID, noteID);
                String where = DBOpenHelper.TABLE_ID + "=?";
                String[] whereArgs = {String.valueOf(noteID)};
                MainActivity.dbProvider.update(DBProvider.NOTES_URI, noteInfo, where, whereArgs);
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
                deleteNote();
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

    public void deleteNote(){
        String delete = DBOpenHelper.TABLE_ID+"=?";
        String[] vals = {String.valueOf(this.noteID)};
        MainActivity.dbProvider.delete(DBProvider.NOTES_URI, delete, vals);

        this.finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        noteImagesAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem shareItem = menu.add(Menu.NONE, SHARE_ID, Menu.NONE, "Share");
        //this.SHARE_ID = shareItem.getItemId();
        shareItem.setIcon(R.drawable.ic_share);
        shareItem.setVisible(true);
        shareItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onPrepareOptionsMenu(menu);
    }
}//END OF CLASS
