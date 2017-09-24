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

public class TermListActivity extends AppCompatActivity{
    private CursorAdapter cursorAdapter;
    private ListView termListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
/*
        cursorAdapter = new CursorAdapterTerm(this, null);
        termListView = (ListView)findViewById(R.id.lvTermsSummary);
        termListView.setAdapter(cursorAdapter);

        termListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(TermListActivity.this, TermDetailsActivity.class);

                Snackbar.make(view, ""+id, Snackbar.LENGTH_LONG).show();
                //TESTING long l as IDd
                intent.putExtra(MainActivity.TERM_ID, id);
                startActivity(intent);
            }
        });*/
        setTermListView();
    }

    /*    NOTE: This doesn't appear to work without a content resolver, which requires 4.4+
    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        Uri term = DB
        return new CursorLoader(getApplicationContext(), DBProvider.TERM_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        cursorAdapter.swapCursor(null);

    }*/

    public void setTermListView(){
        termListView = (ListView)findViewById(R.id.lvTermsSummary);

        Cursor cursor = MainActivity.dbProvider.query(DBProvider.TERM_URI, DBOpenHelper.ALL_TERM_COLS, null, null, null);

        //Unused, using a custom cursor adapter now:
        //String[] from = {DBOpenHelper.TITLE};//, DBOpenHelper.START_DATE, DBOpenHelper.END_DATE};
        //int[] to = {android.R.id.text1};//, android.R.id.text1, android.R.id.text1};
        //CursorAdapter cursAdaptor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        this.cursorAdapter = new CursorAdapterTerm(this, cursor);

        termListView.setAdapter(cursorAdapter);

        termListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(TermListActivity.this, TermDetails.class);

                Snackbar.make(view, ""+id, Snackbar.LENGTH_LONG).show();

                intent.putExtra(MainActivity.TERM_ID, id);
                //intent.putExtra(BOOL_ISCOURSENOTE, true);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        setTermListView();
    }
} //End of Class
