package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Term> terms = new ArrayList<>();
    public final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TITLE, "Term 2");
        simpleDateFormat.setCalendar(Calendar.getInstance());
        String startDate = simpleDateFormat.format(Calendar.getInstance().getTime());
        //simpleDateFormat.
        //Calendar.getInstance().to
        Log.d("MainActivity", "Created start date: "+ startDate);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 6);
        String endDate = simpleDateFormat.format(cal.getTime());
        Log.d("MainActivity", "Created end date: "+ endDate);
        values.put(DBOpenHelper.START_DATE, startDate);
        values.put(DBOpenHelper.END_DATE, endDate);
        Uri termUri = getContentResolver().insert(DBProvider.COURSE_URI, values);
        Log.d("MainActivity", "Inserted term " + termUri.getLastPathSegment());*/

        ListView termListView = (ListView)findViewById(R.id.termList);

        Cursor cursor = getContentResolver().query(DBProvider.TERM_URI, DBOpenHelper.ALL_TERM_COLS, null, null, null, null);
        String[] from = {DBOpenHelper.TITLE};
        int[] to = {android.R.id.text1};
        CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        termListView.setAdapter(cursAdaptor);
        //termListView.ad


        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final int REQ_IMG_CAP =1;
        if (takePictureIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePictureIntent, REQ_IMG_CAP);
        }*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//TODO: Make this a Add Term Button? Share?
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
