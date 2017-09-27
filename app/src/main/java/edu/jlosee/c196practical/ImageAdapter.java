package edu.jlosee.c196practical;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Created by Joe on 8/16/2017.
 */

public class ImageAdapter extends BaseAdapter {
    ArrayList<NoteImage> imageList = new ArrayList<>();

    private int imageHeightDP = 48;
    private int imageWidthDP = 48;
    private int dpWidthInPx;
    private int dpHeightInPx;

    Context context;

    public ImageAdapter(Context c) {
        context = c;
        Resources test = c.getResources();
        float scale = 1;
        if (test!=null){

            scale = c.getResources().getDisplayMetrics().density;
        }

        dpWidthInPx  = (int) (imageWidthDP * scale);
        dpHeightInPx = (int) (imageHeightDP * scale);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int i) {
        return imageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return imageList.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ImageView imageView;

        if (view == null) {
            imageView = new ImageView(context);

            imageView.setLayoutParams(new GridView.LayoutParams(dpWidthInPx, dpHeightInPx));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(10, 10, 10, 10);

        } else {
            imageView = (ImageView) view;

        }

        //Get the relevant bitmap

        NoteImage noteImage = imageList.get(i);

        setScaledBitmap(imageView, noteImage.imgUri);

        try {
     //imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            //handle exception
            Log.d("ImageAdapter", "Exception when attempting to load image URI: " + noteImage.imgUri.toString());
        }

        //imageView.setImageBitmap(imageList.get(i).image);

        return imageView;
    }

    /**
     * Populates the arraylist
     *
     * @param imageCursor
     */
    public void setCursor(Cursor imageCursor) {
        if (imageCursor != null) {
            if (imageCursor.moveToFirst()) {
                do {
                    String imgURI = imageCursor.getString(imageCursor.getColumnIndex(DBOpenHelper.NOTE_IMAGE_URI));
                    long imgId = imageCursor.getInt(imageCursor.getColumnIndex(DBOpenHelper.TABLE_ID));
                    NoteImage img = new NoteImage(Uri.parse(imgURI), imgId);
                    imageList.add(img);
                }while (imageCursor.moveToNext());
            }
        }
    }

    /**
     * Directly sets the array list of images
     *
     * @param imgList
     */
    public void setArrayList(ArrayList<NoteImage> imgList) {
        this.imageList = imgList;
    }

    private void setScaledBitmap(ImageView mImageView, Uri imageUri){
        int targetW = this.dpWidthInPx;//mImageView.getWidth();
        int targetH = this.dpHeightInPx;//mImageView.getHeight();

        String imagePath = imageUri.getPath();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds =true;
        BitmapFactory.decodeFile(imagePath,bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds =false;
        bmOptions.inSampleSize =scaleFactor;

        //Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        mImageView.setImageBitmap(bitmap);
        mImageView.setContentDescription(imagePath);
    }

    public void add(Uri contentUri, long id) {
        imageList.add(new NoteImage(contentUri, id));
    }

    public static class NoteImage {
        Uri imgUri;
        long id;
        //Bitmap image;

        public NoteImage(Uri imageUri, long _id) {
            imgUri = imageUri;
            this.id = _id;

        }
    }//end of inner NoteIMage class
}
