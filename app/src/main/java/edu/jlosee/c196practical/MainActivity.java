package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TERM_ID = "termID";
    private ArrayList<Term> terms = new ArrayList<>();
    public final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
    public DBProvider dbProvider;// = new DBProvider(this.getApplicationContext());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbProvider = new DBProvider(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Cursor test = dbProvider.query(DBProvider.TERM_URI, DBOpenHelper.ALL_TERM_COLS, null, null, null);
        if (test.moveToFirst()){
            do{
                Log.d("MainActivity", "ID: " + test.getString(1));
            }while (test.moveToNext());
        }

        //ContentValues values = new ContentValues();
        //values.put(DBOpenHelper.TITLE, "Term 3");
        simpleDateFormat.setCalendar(Calendar.getInstance());
        String startDate = simpleDateFormat.format(Calendar.getInstance().getTime());
        //simpleDateFormat.
        //Calendar.getInstance().to
        Log.d("MainActivity", "Created start date: "+ startDate);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 6);
        String endDate = simpleDateFormat.format(cal.getTime());
        Log.d("MainActivity", "Created end date: "+ endDate);
        //values.put(DBOpenHelper.START_DATE, startDate);
        //values.put(DBOpenHelper.END_DATE, endDate);
        //Uri termUri = dbProvider.insert(DBProvider.TERM_URI, values);
        //Log.d("MainActivity", "Inserted term " + termUri.getLastPathSegment());

        ListView termListView = (ListView)findViewById(R.id.termList);

        final Cursor cursor = dbProvider.query(DBProvider.TERM_URI, DBOpenHelper.ALL_TERM_COLS, null, null, null);
        String[] from = {DBOpenHelper.TITLE};
        int[] to = {android.R.id.text1};
        CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        termListView.setAdapter(cursAdaptor);

        termListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                    Intent intent = new Intent(MainActivity.this, ViewTermActivity.class);
                                                    int test = -1;
                                                    if (cursor.moveToPosition(position)){
                                                        test = cursor.getInt(cursor.getColumnIndex("_id"));
                                                        Snackbar.make(view, ""+test, Snackbar.LENGTH_LONG).show();
                                                    }
                                                    intent.putExtra(TERM_ID, test);
                                                    //startActivity(intent);

                                                }
                                            });

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
                Intent intent = new Intent(getApplicationContext(), EditTermActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int TESTID = 4;
        menu.add(Menu.NONE, TESTID, Menu.NONE, "test");
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
            Snackbar.make(this.getCurrentFocus(), "Action Settings Selection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (id==4){
            Snackbar.make(this.getCurrentFocus(), "test was selected", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
