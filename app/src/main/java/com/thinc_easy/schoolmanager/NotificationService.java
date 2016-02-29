package com.thinc_easy.schoolmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by M on 23.02.2016.
 */
public class NotificationService extends Service {
    public String[] thisArray = {"false", "false", "false", "false", "false", "false", "false", "false", "false"};

    //@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        try{
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e){
            e.printStackTrace();
        }
        mTimer =new Timer();
        mTimer.schedule(timerTask, timeTillNextMillis());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        try{

        } catch (Exception e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Timer mTimer;

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e("Log", "Running");
            notifyNow();
            mTimer.schedule(timerTask, timeTillNextMillis());
        }
    };

    public void onDestroy(){
        try{
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent("com.thinc_easy.schoolmanager");
        //intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }

    public void notifyNow(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("RSSPullService");

        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, myIntent, PendingIntent.FLAG_ONE_SHOT);
        Context context = getApplicationContext();

        String titleLine = "";
        if (thisArray[2].equals("")){
            titleLine = thisArray[1];
        } else {
            titleLine = thisArray[1]+" ("+thisArray[2]+")";
        }

        String subjectLine = "";
        if (!thisArray[4].equals("")) subjectLine = thisArray[4] + " | ";
        if (!thisArray[5].equals("")) subjectLine = subjectLine + thisArray[5];
        if (!thisArray[6].equals("")) subjectLine = subjectLine + " ("+thisArray[6]+") ";
        if (!thisArray[5].equals("")) subjectLine = subjectLine + " | ";
        subjectLine = subjectLine + thisArray[7]+" - "+thisArray[8];


        Notification.Builder builder;

        builder = new Notification.Builder(context)
                .setContentTitle(titleLine)
                .setContentText(subjectLine)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Notification notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }
    }

    private int timeTillNextMillis(){
        String[][] myArray = DataStorageHandler.toArray(getApplicationContext(), "ActiveTtNotifications.txt");
        int Rows = DataStorageHandler.nmbrRowsCols(getApplicationContext(), "ActiveTtNotifications.txt")[0];
        int Cols = DataStorageHandler.nmbrRowsCols(getApplicationContext(), "ActiveTtNotifications.txt")[1];

        if (Rows > 0) {
            int now = nowMillis();
            int difference = -1;
            int thisRow = -1;

            //Try to find the time difference between now and the next Notification
            boolean found = false;
            for (int r = 0; r < Rows; r++) {
                if (!found) {
                    int tMillis = Integer.parseInt(myArray[r][0]) * 60 * 1000;
                    if (tMillis > now) {
                        difference = tMillis - now;
                        thisRow = r;
                        found = true;
                    }
                }
            }

            if (!found){
                //get first one in array as this is the first one in the next week
                int tMillis = Integer.parseInt(myArray[0][0]) * 60 * 1000;
                difference = tMillis - now;
                thisRow = 0;
                found = true;
            }

            if (thisRow >= 0){
                for (int c = 0; c < Cols; c++){
                    thisArray[c] = myArray[thisRow][c];
                }
            }

            return difference;
        } else {
            return -1;
        }
    }

    private int nowMillis(){
        Calendar now = Calendar.getInstance();

        int weekday = now.get(Calendar.DAY_OF_WEEK);
        int dayweek = 0;
        if (weekday == Calendar.MONDAY) {
            dayweek = 0;
        } else if (weekday == Calendar.TUESDAY) {
            dayweek = 1;
        } else if (weekday == Calendar.WEDNESDAY) {
            dayweek = 2;
        } else if (weekday == Calendar.THURSDAY) {
            dayweek = 3;
        } else if (weekday == Calendar.FRIDAY) {
            dayweek = 4;
        } else if (weekday == Calendar.SATURDAY) {
            dayweek = 5;
        } else if (weekday == Calendar.SUNDAY) {
            dayweek = 6;
        }

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millisecond = now.get(Calendar.MILLISECOND);

        int nowInMillis = dayweek*24*60*60*1000 + hour*60*60*1000 + minute*60*1000 + second * 1000 + millisecond;
        return  nowInMillis;
    }

}
