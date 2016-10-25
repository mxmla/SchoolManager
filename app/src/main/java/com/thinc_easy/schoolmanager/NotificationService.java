package com.thinc_easy.schoolmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by M on 23.02.2016.
 */
public class NotificationService extends Service {
    public String[] thisArray = {"false", "false", "false", "false", "false", "false", "false", "false", "false"};
    public int nowRow = 0;

    //@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        System.out.println("NotificationService onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        System.out.println("NotificationService onStartCommand()");
        /*try{
            mTimer.cancel();
            timerTask.cancel();

        } catch (Exception e){
            e.printStackTrace();
        }*/

        int time = timeTillNextMillis();
        System.out.println("NotificationService time: "+time);
        //Toast.makeText(getApplicationContext(), String.valueOf(time), Toast.LENGTH_SHORT).show();
        if (time >= 0) {
            Timer timer = new Timer();
            TimerTask timerTask = new MyTimerTask();
            timer.schedule(timerTask, time);
            System.out.println("NotificationService start timer");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private Timer mTimer;

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Log.e("Log", "Running");
            System.out.println("NotificationService timerTask");
            try{
                this.cancel();

                /*int time = timeTillNextMillis();
                if (time >= 0) {
                    mTimer.schedule(timerTask, time);
                }*/
            } catch (Exception e){
                e.printStackTrace();
            }

            notifyNow();
            getApplicationContext().startService(new Intent(getApplicationContext(), NotificationService.class));
        }
    };

    public void onDestroy(){
        /*try{
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e){
            e.printStackTrace();
        }*/
        Intent intent = new Intent("com.thinc_easy.schoolmanager.MainActivity");
        //intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }

    public void notifyNow(){
        System.out.println("notifyNow");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("RSSPullService");

        Intent myIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, myIntent, PendingIntent.FLAG_IMMUTABLE);
        Context context = getApplicationContext();


        if (thisArray.length >= 6) {
            final String timeInMil = thisArray[0];
            final String lessonID = thisArray[1];
            final String textName = thisArray[2].replace("[none]", "").replace("[null]", "").replace("[comma]", ",");
            final String textTime = thisArray[3].replace("[none]", "").replace("[null]", "").replace("[comma]", ",");
            final String textPlace = thisArray[4].replace("[none]", "").replace("[null]", "").replace("[comma]", ",");
            final String textTeacher = thisArray[5].replace("[none]", "").replace("[null]", "").replace("[comma]", ",");

            String titleLine = textName;

            String subjectLine = "";
            if (!textTime.equals("") && !textTime.equals("[none]")) subjectLine =
                    (subjectLine.length()>0) ? subjectLine + " | " + textTime : textTime;
            if (!textPlace.equals("") && !textPlace.equals("[none]"))
                subjectLine = (subjectLine.length()>0) ? subjectLine + " | " + textPlace : textPlace;
            if (!textTeacher.equals("") && !textTeacher.equals("[none]"))
                subjectLine = (subjectLine.length()>0) ? subjectLine + " | " + textTeacher : textTeacher;

            Bitmap largeIcon = DataStorageHandler.drawableToBitmap(getResources().getDrawable(R.drawable.ic_launcher));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                Notification.Builder builder;

                builder = new Notification.Builder(context)
                        .setContentTitle(titleLine)
                        .setContentText(subjectLine)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setLargeIcon(largeIcon)
                        .setSmallIcon(R.drawable.ic_launcher);

                Notification notification = builder.build();

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(nowRow, notification);
            }
        }
    }

    private int timeTillNextMillis(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String ttFolder = prefs.getString(getApplicationContext().getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        final String notifications_filepath = ttFolder + "/" + getApplicationContext().getResources().getString(R.string.file_name_lesson_notifications);

        final String[][] myArray = DataStorageHandler.toArray(getApplicationContext(), notifications_filepath);
        final int[] rc = DataStorageHandler.nmbrRowsCols(getApplicationContext(), notifications_filepath);
        final int Rows = rc[0];
        final int Cols = rc[1];
        System.out.println("timeTillNextMillis filepath: "+notifications_filepath);
        System.out.println("timeTillNextMillis rows: "+Rows);

        if (Rows > 0) {
            int now = nowMillis(ttFolder);
            int difference = -1;
            int thisRow = -1;
            int firstNextCycleRow = -1;
            int minTimeYet = -1;
            int minTimeYetNextCycle = -1;

            //Try to find the time difference between now and the next Notification
            boolean found = false;
            for (int r = 0; r < Rows; r++) {
                if (!myArray[r][0].equals("false") && DataStorageHandler.isStringNumeric(myArray[r][1])
                        && myArray[r].length >= 12) {

                    int tMillis = Integer.parseInt(myArray[r][1]) * 60 * 1000;
                    System.out.println("timeTillNextMillis tMillis: "+now+", "+tMillis);

                    if (tMillis > now && (tMillis < minTimeYet || minTimeYet < 0)) {
                        difference = tMillis - now;
                        minTimeYet = difference;
                        thisRow = r;
                        found = true;

                    }

                    if (tMillis < minTimeYetNextCycle || minTimeYetNextCycle < 0) {
                        minTimeYetNextCycle = tMillis - now;
                        firstNextCycleRow = r;
                    }
                }
            }

            if (thisRow < 0){
                thisRow = firstNextCycleRow;
                minTimeYet = minTimeYetNextCycle;
                difference = minTimeYetNextCycle;
            }

            if (thisRow >= 0 && difference >= 0){
                nowRow = thisRow;

                final String timeInMil = String.valueOf(difference);
                final String lessonID = myArray[thisRow][2];
                final String textName = myArray[thisRow][8];
                final String textTime = myArray[thisRow][9];
                final String textPlace = myArray[thisRow][10];
                final String textTeacher = myArray[thisRow][11];

                thisArray = new String[] {timeInMil, lessonID, textName, textTime, textPlace, textTeacher};
                return difference;

            } else {
                return -1;
            }

        } else {
            return -1;
        }
    }

    private int nowMillis(String ttFolder){
        Calendar now = Calendar.getInstance();

        int ab = DataStorageHandler.getCurrentAB(getApplicationContext(), ttFolder, now);

        final int[] dayIDs = new int[] {2, 3, 4, 5, 6, 7, 1};
        int weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int dayweek = 0;
        for (int d = 0; d < dayIDs.length; d++){
            if (weekday == dayIDs[d]) dayweek = d;
        }

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millisecond = now.get(Calendar.MILLISECOND);

        System.out.println("Weekday: "+weekday+", dayweek: "+dayweek);

        int nowInMillis = ab*7*24*60*60*1000 + dayweek*24*60*60*1000 + hour*60*60*1000 + minute*60*1000 + second * 1000 + millisecond;
        return  nowInMillis;
    }

}
