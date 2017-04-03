package com.thinc_easy.schoolmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Created by M on 17.06.2015.
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationService.class));

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */
            String[][] myArray = toArray(context, "Notifications.txt");
            int Rows = nmbrRowsCols(context, "Notifications.txt")[0];
            int Cols = 12;

            for (int i = 0; i < Rows; i++){
                //MakeNotification(context, myArray[i][1], myArray[i][2], myArray[i][4], myArray[i][6], myArray[i][7], myArray[i][8], myArray[i][9], myArray[i][10], myArray[i][11]);
            }
        }

    }
    public void MakeNotification (Context context, String day, String periodF, String periodT, String sName, String sAbbrev, String room, String teacherName, String color, String textcolor){
        Calendar calNow = Calendar.getInstance();
        int nowYear = calNow.get(Calendar.YEAR);
        int nowHour = calNow.get(Calendar.HOUR_OF_DAY);
        int nowMinute = calNow.get(Calendar.MINUTE);
        int nowDayOfWeek = calNow.get(Calendar.DAY_OF_WEEK);
        int nowDayOfYear = calNow.get(Calendar.DAY_OF_YEAR);
        int nowTimeMinutes = nowHour * 60 + nowMinute;

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);

        String p1start = (shared.getString("pref_key_period1_start", "07:55"));
        String p1end = (shared.getString("pref_key_period1_end", "08:40"));
        String p2start = (shared.getString("pref_key_period2_start", "8:40"));
        String p2end = (shared.getString("pref_key_period2_end", "09:25"));
        String p3start = (shared.getString("pref_key_period3_start", "09:45"));
        String p3end = (shared.getString("pref_key_period3_end", "10:30"));
        String p4start = (shared.getString("pref_key_period4_start", "10:35"));
        String p4end = (shared.getString("pref_key_period4_end", "11:20"));
        String p5start = (shared.getString("pref_key_period5_start", "11:40"));
        String p5end = (shared.getString("pref_key_period5_end", "12:25"));
        String p6start = (shared.getString("pref_key_period6_start", "12:25"));
        String p6end = (shared.getString("pref_key_period6_end", "13:10"));
        String p7start = (shared.getString("pref_key_period7_start", "13:45"));
        String p7end = (shared.getString("pref_key_period7_end", "14:30"));
        String p8start = (shared.getString("pref_key_period8_start", "14:30"));
        String p8end = (shared.getString("pref_key_period8_end", "15:15"));
        String p9start = (shared.getString("pref_key_period9_start", "15:15"));
        String p9end = (shared.getString("pref_key_period9_end", "16:00"));
        String p10start = (shared.getString("pref_key_period10_start", "16:00"));
        String p10end = (shared.getString("pref_key_period10_end", "16:45"));
        String p11start = (shared.getString("pref_key_period11_start", "16:45"));
        String p11end = (shared.getString("pref_key_period11_end", "17:30"));
        String p12start = (shared.getString("pref_key_period12_start", "17:30"));
        String p12end = (shared.getString("pref_key_period12_end", "18:15"));

        String[] timesStart = {p1start, p2start, p3start, p4start, p5start, p6start, p7start, p8start, p9start, p10start, p11start, p12start};
        String[] timesEnd = {p1end, p2end, p3end, p4end, p5end, p6end, p7end, p8end, p9end, p10end, p11end, p12end};
        int[] periodNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        int[] dayNumbers = {2, 3, 4, 5, 6, 7, 1};
        String[] mDayNames = context.getResources().getStringArray(R.array.DayNames);
        String[] mDayIDs = context.getResources().getStringArray(R.array.DayIDs);

        // Check if it is a real entry or just a "-"
        if (!day.equals("-")){
            // Put every period into the array
            int from = Integer.parseInt(periodF) - 1;
            int to = Integer.parseInt(periodT) - 1;
            for (int i77 = 0; i77 < periodNumbers.length; i77++){
                if (Integer.parseInt(periodF) == periodNumbers[i77]) from = i77;
            }
            for (int i88 = 0; i88 < periodNumbers.length; i88++){
                if (Integer.parseInt(periodT) == periodNumbers[i88]) to = i88;
            }

            String[][] myArray = toArray(context, "Notifications.txt");
            int Rows = nmbrRowsCols(context, "Notifications.txt")[0];
            int Cols = 12;

            File file = new File(context.getExternalFilesDir(null), "Notifications.txt");

            int dayNumber = -1;
            for (int i = 0; i < mDayIDs.length; i++) {
                if (mDayIDs[i].equals(day)) dayNumber = i;
            }
            if(dayNumber >= 0) {
                int NotifIDint = (dayNumber * 12) + from;
                String NotifID = String.valueOf((dayNumber * 12) + from);

                String[] timeStartSplit = timesStart[from].split(":");
                int hS = Integer.valueOf(timeStartSplit[0]);
                int mS = Integer.valueOf(timeStartSplit[1]);
                int timeSminutes = hS * 60 + mS;

                String[] timeEndSplit = timesEnd[to].split(":");
                int hE = Integer.valueOf(timeEndSplit[0]);
                int mE = Integer.valueOf(timeEndSplit[1]);
                int timeEminutes = hE * 60 + mE;

                int dayDifference = 0;
                for (int i2 = 0; i2 < dayNumbers.length; i2++) {
                    if (nowDayOfWeek == dayNumbers[i2]){
                        //dayDifference = i2 - dayNumber;
                        int nowDayNumber = i2;
                        dayDifference = dayNumber - nowDayNumber;
                        if (dayDifference < 0){
                            dayDifference = dayDifference + 7;
                        }
                        if (dayDifference == 0){
                            if (timeSminutes < nowTimeMinutes){
                                dayDifference = dayDifference + 7;
                            }
                        }
                    }
                }
                int dayOfYear = nowDayOfYear + dayDifference;

                Calendar calStart = Calendar.getInstance();
                calStart.set(Calendar.YEAR, nowYear);
                calStart.set(Calendar.DAY_OF_YEAR, dayOfYear);
                calStart.set(Calendar.HOUR_OF_DAY, hS);
                calStart.set(Calendar.MINUTE, mS);
                calStart.set(Calendar.SECOND, 0);

                Calendar calEnd = Calendar.getInstance();
                calEnd.set(Calendar.YEAR, nowYear);
                calEnd.set(Calendar.DAY_OF_YEAR, dayOfYear);
                calEnd.set(Calendar.HOUR_OF_DAY, hE);
                calEnd.set(Calendar.MINUTE, mE);
                calEnd.set(Calendar.SECOND, 0);

                long id = (long) NotifIDint;
                new ReminderManager(context).setLessonReminder1(id, calStart, calEnd, sName, sAbbrev, room, teacherName, color, textcolor);


                boolean existsField;
                existsField = false;

                if (file.length() > 0) {
                    for (int s = 0; s < Rows; s++) {
                        if (myArray[s][0].equals(NotifID)) {
                            existsField = true;
                            myArray[s][1] = day;
                            myArray[s][2] = periodF;
                            myArray[s][3] = String.valueOf(timeSminutes);
                            myArray[s][4] = periodT;
                            myArray[s][5] = String.valueOf(timeEminutes);
                            myArray[s][6] = sName;
                            myArray[s][7] = sAbbrev;
                            myArray[s][8] = room;
                            myArray[s][9] = teacherName;
                            myArray[s][10] = color;
                            myArray[s][11] = textcolor;
                        }
                    }
                }
                if (existsField == false) {
                    String[][] myArray2 = new String[Rows + 1][Cols];
                    for (int r = 0; r < Rows; r++) {
                        for (int c = 0; c < Cols; c++) {
                            myArray2[r][c] = myArray[r][c];
                        }
                    }
                    myArray2[Rows][0] = NotifID;
                    myArray2[Rows][1] = day;
                    myArray2[Rows][2] = periodF;
                    myArray2[Rows][3] = String.valueOf(timeSminutes);
                    myArray2[Rows][4] = periodT;
                    myArray2[Rows][5] = String.valueOf(timeEminutes);
                    myArray2[Rows][6] = sName;
                    myArray2[Rows][7] = sAbbrev;
                    myArray2[Rows][8] = room;
                    myArray2[Rows][9] = teacherName;
                    myArray2[Rows][10] = color;
                    myArray2[Rows][11] = textcolor;
                    myArray = myArray2;
                    Rows++;
                }
                try {
                    BufferedWriter buf = new BufferedWriter(new FileWriter(file));
                    for (int t = 0; t < Rows; t++) {
                        if (myArray[t][0].equals(null) == false && myArray[t][1].equals(null) == false && myArray[t][2].equals(null) == false && myArray[t][3].equals(null) == false) {
                            buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4] + "," + myArray[t][5] + "," + myArray[t][6] + "," + myArray[t][7] + "," + myArray[t][8]);
                            buf.newLine();
                        } else {
                            Toast.makeText(context.getApplicationContext(), "DON'T write notification:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                        }
                    }
                    buf.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context.getApplicationContext(), "Could not set notification. Day name not readable.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String[][] toArray(Context context, String filename){

        int Rows = nmbrRowsCols(context, filename)[0];
        int Cols = nmbrRowsCols(context, filename)[1];
        String InputLine = "";
        Scanner scanIn = null;

        // Read existing file
        String[][] myArray = new String[Rows][Cols];
        int Rowc = 0;
        scanIn = null;
        InputLine = "";


        File file = new File(context.getExternalFilesDir(null), filename);
        try{
            scanIn = new Scanner(new BufferedReader(new FileReader(file)));

            while (scanIn.hasNextLine()){
                InputLine = scanIn.nextLine();
                String[] InArray = InputLine.split(",");
                for (int x = 0; x < InArray.length; x++){
                    myArray[Rowc][x] = String.valueOf(InArray[x]);
                }
                Rowc++;
            }
            scanIn.close();
        }catch (Exception e){
            System.out.println(e);
        }

        return myArray;
    }

    public int[] nmbrRowsCols(Context context, String filename){
        String InputLine = "";
        Scanner scanIn = null;

        // Get number of rows and columns
        int Rows = 0;
        int Cols = 0;

        File file = new File(context.getExternalFilesDir(null), filename);
        try{
            scanIn = new Scanner(new BufferedReader(new FileReader(file)));
            while (scanIn.hasNextLine()){
                InputLine = scanIn.nextLine();
                String[] InArray = InputLine.split(",");
                Rows++;
                Cols = InArray.length;
            }
            scanIn.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        int[] RowsCols = new int[2];
        RowsCols[0] = Rows;
        RowsCols[1] = Cols;
        return RowsCols;
    }
}
