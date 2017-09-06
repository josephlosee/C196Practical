package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class NoteImageView extends AppCompatActivity {

    long imageID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_image_view);

        ImageView image = (ImageView)findViewById(R.id.imageView);
        String imageURI = getIntent().getExtras().getString(NoteDetails.IMAGE_URI_STRING);
        //Uri imgURI = Uri.parse(imageURI);
        image.setImageBitmap(BitmapFactory.decodeFile(imageURI));
        //image.setImageBitmap(BitmapFactory.decodeFile(imageURI));
        imageID = getIntent().getExtras().getLong(NoteDetails.IMAGE_ID);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_delete:
                alertConfirmation();
                break;
            default:
                String test = "The selected menu item does not have code.";
                Snackbar.make(getCurrentFocus(), test, Snackbar.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_only, menu);
        return true;
    }

    /**
     * Displays an alert to ask the user if they want to delete the photo, and if OK is selected, does so and finishes the activity
     */
    public void alertConfirmation(){
        //boolean ret = false;
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Delete this item? This action cannot be undone.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Snackbar.make(getCurrentFocus(), "OK selected", Snackbar.LENGTH_LONG).show();
                //ret=true;
                deleteImage();
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

    //Deletes the iumage displayed
    public void deleteImage(){
        String delete = DBOpenHelper.TABLE_ID+"=?";
        String[] vals = {String.valueOf(this.imageID)};
        MainActivity.dbProvider.delete(DBProvider.NOTE_IMAGE_URI, delete, vals);
        this.finish();
    }
}//End of class