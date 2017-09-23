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
 * Created by Joe on 8/6/2017.
 */

public class MentorCursorAdapter extends CursorAdapter{


    public MentorCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.mentorlinear, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView mentorName = (TextView) view.findViewById(R.id.mentorName);
        TextView mentorPhone = (TextView) view.findViewById(R.id.mentorPhone);
        TextView mentorEmail = (TextView) view.findViewById(R.id.mentorEmail);
        //CheckBox checkBox = (CheckBox)view.findViewById(R.id.mentorAdd);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MENTOR_NAME));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.PHONE));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.EMAIL));

        mentorName.setText(name);
        mentorEmail.setText(email);
        mentorPhone.setText(phone);

        //int priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority"));

        //heckBox.setChecked(true);
        //TODO: add the mentor ID here somehow
        TextView mentorID = (TextView) view.findViewById(R.id.mentorID);
        mentorID.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_MENTOR)));
    }
}
