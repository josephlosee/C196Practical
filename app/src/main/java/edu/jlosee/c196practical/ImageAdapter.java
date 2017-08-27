package edu.jlosee.c196practical;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Joe on 8/16/2017.
 */

public class ImageAdapter extends BaseAdapter {
    ArrayList<NoteImage> imageList = new ArrayList<>();
    Context context;
    public ImageAdapter(Context c){
        context=c;
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        /*
                    if (view == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(185, 185));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(imageIDs[position]);
            return imageView;
         */
        //TODO: Make this work:
        ImageView retImageView = new ImageView(context);
        NoteImage image = imageList.get(i);
        // File imageFile = new File(image.imgUri);
        Bitmap bitmap;

        try
        {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver() , Uri.parse(image.imgUri.toString()));

            retImageView.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            //handle exception
            Log.d("ImageAdapter", "Exception when attempting to load image URI: "+image.imgUri.toString());
        }

        return retImageView;
    }

    /**
     * Populates the arraylist
     * @param imageCursor
     */
    public void setCursor(Cursor imageCursor){
        if (imageCursor!=null){
            if (imageCursor.moveToFirst()){
                //TODO: get each image URI and id from the NOTES_IMAGE table
                //create a new NoteImage object
                //Add the new NoteImage to the ArrayList
                //TODO: Is there a faster way of doing this without bouncing between string and URI?
                String imgURI = imageCursor.getString(imageCursor.getColumnIndex(DBOpenHelper.NOTE_IMAGE_URI));
                int imgId = imageCursor.getInt(imageCursor.getColumnIndex(DBOpenHelper.TABLE_ID));
                NoteImage img = new NoteImage(Uri.parse(imgURI), imgId);
                imageList.add(img);
            }
        }
    }

    /**
     * Directly sets the array list of images
     * @param imgList
     */
    public void setArrayList(ArrayList<NoteImage> imgList){
        this.imageList = imgList;
    }

    public class NoteImage{
        Uri imgUri;
        int id;
        public NoteImage(Uri imageUri, int _id){
            imgUri=imageUri;
            this.id=_id;
        }
    }
}