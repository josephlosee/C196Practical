package edu.jlosee.c196practical;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joe on 9/12/2017.
 * Significantly modified from https://stackoverflow.com/questions/36902667/how-to-schedule-notification-in-android
 */

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then posts the notification.
 */
public class WakefulReceiver extends WakefulBroadcastReceiver {
    // provides access to the system alarm services.
    private AlarmManager mAlarmManager;

    private String notificationMessage = "DEBUG: This message was not assigned.";
    private Class<?> activityClass = MainActivity.class;
    private Calendar targetAlarmTime = Calendar.getInstance();

    //When the time is reached, this is called.
    public void onReceive(Context context, Intent intent) {
        Bundle notificationExtras = intent.getExtras();

        String noteMsg = this.notificationMessage;
        int notificationID = 65535;
        //See if the intent extras are retained.
        if (notificationExtras!=null){

            noteMsg = notificationExtras.getString("notificationMessage");
            notificationID=notificationExtras.getInt("alertID");
            Log.d("WakefulReceiver", "onReceive arg Intent notification message: " +noteMsg);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentText(noteMsg);

        // Creates an Intent for the Activity
        Intent notifyIntent = new Intent(context, activityClass);
        // Sets the Activity to start in a new, empty task

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        //PendingIntent.get
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // Puts the PendingIntent into the notification builder
        builder.setContentIntent(notifyPendingIntent);
        // Notifications are issued by sending them to the
        // NotificationManager system service.
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds an anonymous Notification object from the builder, and
        // passes it to the NotificationManager
        mNotificationManager.notify(notificationID, builder.build());

        //Indicate that this has been completed.
        WakefulReceiver.completeWakefulIntent(intent);
    }

    /**
     *
     * @param context - context
     * @param targetAlarmTime - Calendar time that the alarm will be set for
     * @param notificationMessage - Message to place in the notification when it fires.
     * @param activityClass - Related class
     * @param id - id of the object that the alert is set for
     */
    public void setAlarm(Context context, Calendar targetAlarmTime, String notificationMessage, Class<?> activityClass, int id) {
        if (notificationMessage!= null){
            this.notificationMessage=notificationMessage;
        }
        if (activityClass!=null){
            this.activityClass=activityClass;
        }

        if (targetAlarmTime==null) {
            Log.d("WakefulReceiver", "null Calendar passed to setAlarm");
        }else{
            Log.d("WakefulReceiver", "setAlarm set to "+targetAlarmTime.get(Calendar.YEAR)+"-"
                    +(targetAlarmTime.get(Calendar.MONTH)+1)+"-"+
                    targetAlarmTime.get(Calendar.DAY_OF_MONTH));
        }

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakefulReceiver.class);
        intent.putExtra("notificationMessage", this.notificationMessage);
        intent.putExtra("alertID", id);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, 0);

        if (targetAlarmTime != null) {
            //DEBUG: REMOVE THIS:
            //targetAlarmTime.setTimeInMillis(System.currentTimeMillis()+2000);

            //DO NOT REMOVE THIS:
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, targetAlarmTime.getTimeInMillis(), alarmIntent);
        }
        MainActivity.sdfNoTime.setCalendar(targetAlarmTime);

        // Enable {@code BootReceiver} to automatically restart when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        //receiver.
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Cancels the next alarm from running. Removes any intents set by this
     * WakefulBroadcastReceiver.
     *
     * @param context the context of the app's Activity
     */
    public void cancelAlarm(Context context, int id) {
        Log.d("WakefulAlarmReceiver", "{cancelAlarm}");

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakefulReceiver.class);

        //Cancels the alarm only if its found.
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE);
        if (alarmIntent!=null){
            mAlarmManager.cancel(alarmIntent);
        }
        // Disable BootReceiver so that it doesn't automatically restart when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
