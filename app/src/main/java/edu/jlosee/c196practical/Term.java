package edu.jlosee.c196practical;

import android.content.ContentValues;
import android.database.Cursor;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joe on 7/2/2017.
 */

public class Term {
    private ArrayList<Course> courses;
    private String title;

    public Term(){
        courses = new ArrayList<>();
    }

    public Term(String title, Calendar startDate, Calendar endDate){
        this.title=title;
        this.startDate=startDate;
        this.endDate=endDate;

        //TODO: insert into term table and get ID back
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TITLE, title);
        values.put(DBOpenHelper.START_DATE, startDate.toString());
        values.put(DBOpenHelper.END_DATE, endDate.toString());
    }


    public Term(Cursor termCursor){
        //TODO:
        this.title = termCursor.getString(termCursor.getColumnIndex(DBOpenHelper.TITLE));
        Calendar cal = Calendar.getInstance();
        //TODO: THIS MAY NOT WORK. TEST IT
        SimpleDateFormat sdf = new SimpleDateFormat();

        try {
            Date d = sdf.parse(termCursor.getString(termCursor.getColumnIndex(DBOpenHelper.END_DATE)));
            endDate = sdf.getCalendar();
            d= sdf.parse(termCursor.getString(termCursor.getColumnIndex(DBOpenHelper.START_DATE)));
            this.startDate = sdf.getCalendar();


        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.id = termCursor.getInt(termCursor.getColumnIndex(DBOpenHelper.TABLE_ID));

    }
    //TODO: Handle SQLite data updates as well

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Calendar startDate, endDate;
    private int id;

    public void setTitle(String newTitle){
        this.title = newTitle;
    }

    public String getTitle(){
        return this.title;
    }
}
