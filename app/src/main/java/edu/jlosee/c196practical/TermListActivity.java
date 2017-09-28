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
        this.setTitle("Term List");
        toolbar.setTitle("Term List");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termDetails = new Intent(TermListActivity.this, TermDetails.class);
                startActivity(termDetails);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTermListView();
    }

    public void setTermListView(){
        termListView = (ListView)findViewById(R.id.lvTermsSummary);

        Cursor cursor = MainActivity.dbProvider.query(DBProvider.TERM_URI, DBOpenHelper.ALL_TERM_COLS, null, null, null);

        this.cursorAdapter = new CursorAdapterTerm(this, cursor);

        termListView.setAdapter(cursorAdapter);
        termListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(TermListActivity.this, TermDetails.class);

                //Snackbar.make(view, ""+id, Snackbar.LENGTH_LONG).show();

                intent.putExtra(MainActivity.TERM_ID, id);

                startActivityForResult(intent, 8888);
            }
        });
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        setTermListView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onResume();
        setTermListView();
    }
} //End of Class
