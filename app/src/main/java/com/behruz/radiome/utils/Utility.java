package com.behruz.radiome.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;


/**
 * Created by masum on 09/12/2015.
 */
public class Utility {
    //Notification
    // Set up the notification ID
    public static final int NOTIFICATION_ID = 1;
    @SuppressWarnings("unused")
    public static NotificationManager mNotificationManager;

  public static final String NOTIFY_PREVIOUS = "com.tutorialsface.audioplayer.previous";
  public static final String NOTIFY_DELETE = "com.tutorialsface.audioplayer.delete";
  public static final String NOTIFY_PAUSE = "com.tutorialsface.audioplayer.pause";
  public static final String NOTIFY_PLAY = "com.tutorialsface.audioplayer.play";
  public static final String NOTIFY_NEXT = "com.tutorialsface.audioplayer.next";

  public Utility() {

  }
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        //Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Pre appending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


  public  boolean currentVersionSupportBigNotification() {
    int sdkVersion = VERSION.SDK_INT;
    if(sdkVersion >= VERSION_CODES.JELLY_BEAN){
      return true;
    }
    return false;
  }


  private PendingIntent getPendingSelfIntent(Context context, String action) {
    Intent intent = new Intent(context, getClass());
    intent.setAction(action);
    intent.putExtra("model","read");
    return PendingIntent.getBroadcast(context, 0, intent, 0);
  }


}
