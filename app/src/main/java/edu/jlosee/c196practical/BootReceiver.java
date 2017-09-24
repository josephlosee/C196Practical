package edu.jlosee.c196practical;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Joe on 9/12/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //context = MainActivity.getApplicationContect();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent renewIntent = new Intent(context, WakefulReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            String hasAlarm = "where hasAlarm=?";// and alarmTime>?";
            String[] alarmStatus = {String.valueOf(1)};//1==true

            Cursor assessmentAlarms = MainActivity.dbProvider.query(DBProvider.ASSESSMENT_URI, null, hasAlarm, alarmStatus, null);
            Cursor termAlarms = MainActivity.dbProvider.query(DBProvider.TERM_URI, null, hasAlarm, alarmStatus, null);

            if (assessmentAlarms!=null && assessmentAlarms.moveToFirst()){
                while(assessmentAlarms.moveToNext()){
                    WakefulReceiver alarmReceiver = new WakefulReceiver();

                    //Get the relevant information
                    String alarmTime = assessmentAlarms.getString(assessmentAlarms.getColumnIndex(DBOpenHelper.END_DATE));
                    long courseID = assessmentAlarms.getLong(assessmentAlarms.getColumnIndex(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE));
                    boolean objective = assessmentAlarms.getInt(assessmentAlarms.getColumnIndex(DBOpenHelper.ASSESSMENT_IS_OBJECTIVE))==1;

                    String alarmMessage = alarmAssessmentMessage(courseID, objective);
                    Calendar time = Calendar.getInstance();
                    Locale current = context.getResources().getConfiguration().locale;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", current);
                    try {
                        sdf.parse(alarmTime);
                        time = sdf.getCalendar();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //time.setTimeInMillis(alarmTime);
                    long assessmentID = assessmentAlarms.getLong(assessmentAlarms.getColumnIndex(DBOpenHelper.TABLE_ID));

                    alarmReceiver.setAlarm(context, time, alarmMessage, AssessmentActivity.class, (int)assessmentID);
                }
            }

            if (termAlarms!=null && termAlarms.moveToFirst()){
                while(termAlarms.moveToNext()){
                    WakefulReceiver alarmReceiver = new WakefulReceiver();

                    //Get the relevant information
                    String alarmTime = termAlarms.getString(termAlarms.getColumnIndex(DBOpenHelper.END_DATE));
                    //long courseID = termAlarms.getLong(termAlarms.getColumnIndex(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE));
                    boolean objective = termAlarms.getInt(termAlarms.getColumnIndex(DBOpenHelper.ASSESSMENT_IS_OBJECTIVE))==1;
                    String termTitle = termAlarms.getString(termAlarms.getColumnIndex(DBOpenHelper.TITLE));
                    String alarmMessage = "Reminder: Your term "+termTitle+ " is ending today! ";
                    Calendar time = Calendar.getInstance();
                    Locale current = context.getResources().getConfiguration().locale;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", current);
                    try {
                        sdf.parse(alarmTime);
                        time = sdf.getCalendar();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //time.setTimeInMillis(alarmTime);
                    long termID = termAlarms.getLong(termAlarms.getColumnIndex(DBOpenHelper.TABLE_ID));

                    alarmReceiver.setAlarm(context, time, alarmMessage, TermDetails.class, (int)termID);
                }
            }
        }
    }//end of onReceive

    /**
     * Builds the alarmMessage with the course title
     * @return
     */
    private String alarmAssessmentMessage(long courseID, boolean isObjective){
        String courseCode = "Missing Course Code";
        String courseTitle = "Missing Course Title";
        String idQuery = "where _id = ?";

        String[] courseIDArg = {String.valueOf(courseID)};

        //Get the course info we need
        Cursor courseInfo = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, idQuery, courseIDArg, null);
        if (courseInfo!=null && courseInfo.moveToFirst()){
            courseTitle = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_CODE));
            courseCode = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.TITLE));
        }

        StringBuilder alarmMessage = new StringBuilder("Reminder: You have an ");
        if (isObjective){
            alarmMessage.append("objective assessment today for course ");

        }else{
            alarmMessage.append("performance assessment today for course ");
        }
        alarmMessage.append(courseCode);
        alarmMessage.append(" ");
        alarmMessage.append(courseTitle);

        return alarmMessage.toString();
    }

}//END OF CLASS
