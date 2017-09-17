package edu.jlosee.c196practical;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Joe on 9/12/2017.
 */

public class BootReceiver extends BroadcastReceiver {

    private String assessmentAlarmString = "Assessment reminder for: ";
    private String termAlarmString = "Term End reminder for: ";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            //context = MainActivity.getApplicationContect();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent renewIntent = new Intent(context, WakefulReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            String hasAlarm = "where hasAlarm=? and alarmTime>?";
            String[] alarmStatus = {"true", String.valueOf(System.currentTimeMillis())};
            Cursor assessmentAlarms = MainActivity.dbProvider.query(DBProvider.ASSESSMENT_URI, null, hasAlarm, alarmStatus, null);
            Cursor termAlarms = MainActivity.dbProvider.query(DBProvider.TERM_URI, null, hasAlarm, alarmStatus, null);

            if (assessmentAlarms!=null && assessmentAlarms.moveToFirst()){
                while(assessmentAlarms.moveToNext()){
                    WakefulReceiver alarmReceiver = new WakefulReceiver();
                    //This won't work
                    String alarmTime = assessmentAlarms.getString(assessmentAlarms.getColumnIndex(DBOpenHelper.END_DATE));

                    //TODO:
                    String alarmMessage = "test message";
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
                    //TODO: update

                }
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            //// TODO: use calendar.add(Calendar.SECOND,MINUTE,HOUR, int);

            //This is a debug
            calendar.add(Calendar.SECOND, 10);

            //ALWAYS recompute the calendar after using add, set, roll
            Date date = calendar.getTime();
            //TODO:

            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);
        }

        //TODO: parse through all alarms that have been set and set new alarms
    }
}
