package edu.jlosee.c196practical;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Joe on 8/7/2017.
 */

public class TermCursorAdapter extends CursorAdapter {

    public TermCursorAdapter(Context context, Cursor cursor){
        //super(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.termlinear, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView termTitle = (TextView) view.findViewById(R.id.termTitle);
        TextView termStart = (TextView) view.findViewById(R.id.startDate);
        TextView termEnd = (TextView) view.findViewById(R.id.endDate);
        //CheckBox checkBox = (CheckBox)view.findViewById(R.id.mentorAdd);

        // Extract properties from cursor
        String title = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.TITLE));
        String start = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.START_DATE));
        String end = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.END_DATE));

        termTitle.setText(title);
        termStart.setText(start);
        termEnd.setText(end);

        //int priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority"));

        //checkBox.setChecked(true);
    }
}