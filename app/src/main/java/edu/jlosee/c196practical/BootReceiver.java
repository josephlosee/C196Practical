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
    private Cursor assessmentAlarms;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //context = MainActivity.getApplicationContect();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent renewIntent = new Intent(context, WakefulReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            String hasAlarm = DBOpenHelper.END_ALARM+"=?;// and alarmTime>?";
            String[] alarmStatus = {String.valueOf(1)};//1==true

            assessmentAlarms = MainActivity.dbProvider.query(DBProvider.ASSESSMENT_URI, null, hasAlarm, alarmStatus, null);
            createAssessmentAlarms();

            //Slight change to the query and value
            hasAlarm = DBOpenHelper.END_ALARM+"=? or "+DBOpenHelper.START_ALARM+"=?";// and alarmTime>?";
            alarmStatus = new String[]{String.valueOf(1), String.valueOf(1)};//1==true

            Cursor termAlarms = MainActivity.dbProvider.query(DBProvider.TERM_URI, null, hasAlarm, alarmStatus, null);
            createStartAndEndAlarms(termAlarms, TermDetails.class, DBOpenHelper.TABLE_TERM,
                    TermDetails.TERM_START_PREFIX, TermDetails.TERM_END_PREFIX);

            Cursor courseAlarms = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, hasAlarm, alarmStatus, null);
            createStartAndEndAlarms(courseAlarms, CourseDetails.class, DBOpenHelper.TABLE_COURSE,
                    CourseDetails.COURSE_START_PREFIX, CourseDetails.COURSE_END_PREFIX);
        }
    }//end of onReceive

    /**
     * Builds the alarmMessage with the course title
     * @return
     */
    private String alarmAssessmentMessage(long courseID, boolean isObjective){
        String courseCode = "Missing Course Code";
        String courseTitle = "Missing Course Title";
        String idQuery = "_id = ?";

        String[] courseIDArg = {String.valueOf(courseID)};

        //Get the course info we need
        Cursor courseInfo = MainActivity.dbProvider.query(DBProvider.COURSE_URI, null, idQuery, courseIDArg, null);
        if (courseInfo!=null && courseInfo.moveToFirst()){
            courseTitle = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.COURSE_CODE));
            courseCode = courseInfo.getString(courseInfo.getColumnIndex(DBOpenHelper.TITLE));
        }

        StringBuilder alarmMessage = new StringBuilder("Reminder: ");
        alarmMessage.append(courseCode);
        if (isObjective){
            alarmMessage.append("objective assessment today!");

        }else{
            alarmMessage.append("performance assessment today!");
        }

        alarmMessage.append(" ");
        alarmMessage.append(courseTitle);

        return alarmMessage.toString();
    }

    private void createAssessmentAlarms(){
        if (assessmentAlarms!=null && assessmentAlarms.moveToFirst()){
            do{
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

                alarmReceiver.setAlarm(context, time, alarmMessage, AssessmentActivity.class,
                        (int)assessmentID+AssessmentActivity.ASSESSMENT_END_PREFIX);
            }while(assessmentAlarms.moveToNext());
        }
    }

    /**
     *
     * @param cursorWithTwoAlarms - Cursor with start and end alarms
     * @param reminderType
     */
    private void createStartAndEndAlarms(Cursor cursorWithTwoAlarms, Class<?> targetClass,
                                         String reminderType, int startIDPrefix, int endIDPrefix){
        if (cursorWithTwoAlarms!=null && cursorWithTwoAlarms.moveToFirst()){
            do{
                boolean hasStart = cursorWithTwoAlarms.getInt(cursorWithTwoAlarms.getColumnIndex(DBOpenHelper.START_ALARM))==1;
                boolean hasEnd = cursorWithTwoAlarms.getInt(cursorWithTwoAlarms.getColumnIndex(DBOpenHelper.END_ALARM))==1;
                String title = cursorWithTwoAlarms.getString(cursorWithTwoAlarms.getColumnIndex(DBOpenHelper.TITLE));
                long id = cursorWithTwoAlarms.getLong(cursorWithTwoAlarms.getColumnIndex(DBOpenHelper.TABLE_ID));

                if(hasEnd) {
                    WakefulReceiver alarmReceiver = new WakefulReceiver();
                    //Get the relevant information
                    String alarmTime = cursorWithTwoAlarms.getString(cursorWithTwoAlarms.getColumnIndex(DBOpenHelper.END_DATE));
                    //long courseID = termAlarms.getLong(termAlarms.getColumnIndex(DBOpenHelper.TABLE_ID+DBOpenHelper.TABLE_COURSE));


                    String alarmMessage = "Reminder: Your "+reminderType+" " + title + " is ending today! ";
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

                    alarmReceiver.setAlarm(context, time, alarmMessage, targetClass, ((int) id+endIDPrefix));
                }
                if (hasStart){

                    WakefulReceiver alarmReceiver = new WakefulReceiver();
                    //Get the relevant information
                    String alarmTime = cursorWithTwoAlarms.getString(cursorWithTwoAlarms.getColumnIndex(DBOpenHelper.START_DATE));

                    String alarmMessage = "Reminder: Your "+reminderType+" " + title + " is ending today! ";
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

                    alarmReceiver.setAlarm(context, time, alarmMessage, targetClass, ((int) id+startIDPrefix));
                }
            }while(cursorWithTwoAlarms.moveToNext());
        }
    }

}//END OF CLASS
