package edu.jlosee.c196practical;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import android.net.Uri;
import android.widget.SimpleCursorAdapter;


public class DeleteItemsActivity extends AppCompatActivity {

    public static final String TABLE_TO_DELETE_FROM = "table";
    public static final String QUERY_ID = "queryID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_items);

        ListView deleteItems = (ListView)findViewById(R.id.lvDeleteItems);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            Uri table = (Uri)extras.get(TABLE_TO_DELETE_FROM);
            int queryID = extras.getInt(QUERY_ID);
            String[] args = {String.valueOf(queryID)};
            final Cursor cursor = MainActivity.dbProvider.query(table, null, DBOpenHelper.TABLE_ID+ " =?", args, null);

            String[] from = {DBOpenHelper.TITLE};// + " " + DBOpenHelper.COURSE_CODE};
            int[] to = {android.R.id.text1};
            CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);

            deleteItems.setAdapter(cursAdaptor);

            deleteItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    //Intent intent = new Intent(TermDetailsActivity.this, ViewCourseActivity.class);
                    int selectedItem = -1;
                    if (cursor.moveToPosition(position)) {
                        selectedItem = cursor.getInt(cursor.getColumnIndex("_id"));
                        Snackbar.make(view, "You want to delete item with ID#: " + selectedItem, Snackbar.LENGTH_LONG).show();

                        //TODO: Got the selected id, prompt the user to delete

                        //TODO:  do a quick check on other tables, if its a term, check for assigned courses then stop and display an error
                        //
                    }
                }
            });

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
