package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.inputmethodservice.Keyboard;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

/**
 * Created by M on 02.02.2016.
 */
public class DataStorageHandler {



    public static void EditSubject(Context context, String sName, String sAbbrev, String tName, String tAbbrev,
                            String day1, String day2, String day3, String day4, String day5,
                            String period1f, String period2f, String period3f, String period4f, String period5f,
                            String period1t, String period2t, String period3t, String period4t, String period5t,
                            String room1, String room2, String room3, String room4, String room5,
                            String color, String textColor,
                                   String timeBeforeMinutes, String neverNotify, String waitForPrevious, String onlyWhenChangingRooms){
        if (isExternalStorageWritable(context)) {

            EditSubjectEntry(context, sName, sAbbrev, tName, tAbbrev,
                    day1, day2, day3, day4, day5,
                    period1f, period2f, period3f, period4f, period5f,
                    period1t, period2t, period3t, period4t, period5t,
                    room1, room2, room3, room4, room5,
                    color, textColor);


            deleteSubjectFromPeriodsTxt(context, sName);
            deleteSubjectFromTtFieldsTxt(context, sName);

            EditPeriodEntry(context, day1, period1f, period1t, sName);
            EditPeriodEntry(context, day2, period2f, period2t, sName);
            EditPeriodEntry(context, day3, period3f, period3t, sName);
            EditPeriodEntry(context, day4, period4f, period4t, sName);
            EditPeriodEntry(context, day5, period5f, period5t, sName);

            EditTimetableEntry(context, day1, period1f, period1t, sName, sAbbrev, color, textColor);
            EditTimetableEntry(context, day2, period2f, period2t, sName, sAbbrev, color, textColor);
            EditTimetableEntry(context, day3, period3f, period3t, sName, sAbbrev, color, textColor);
            EditTimetableEntry(context, day4, period4f, period4t, sName, sAbbrev, color, textColor);
            EditTimetableEntry(context, day5, period5f, period5t, sName, sAbbrev, color, textColor);

            editAllTtNotificationsTxtSubject(context, sName, sAbbrev, tName, tAbbrev,
                    day1, day2, day3, day4, day5,
                    period1f, period2f, period3f, period4f, period5f,
                    period1t, period2t, period3t, period4t, period5t,
                    room1, room2, room3, room4, room5,
                    color, textColor,
                    timeBeforeMinutes, neverNotify, waitForPrevious, onlyWhenChangingRooms);

            /*DeleteWrongNotifications(context);
            MakeNotification(context, day1, period1f, period1t, sName, sAbbrev, room1, tName, color, textColor);
            MakeNotification(context, day2, period2f, period2t, sName, sAbbrev, room2, tName, color, textColor);
            MakeNotification(context, day3, period3f, period3t, sName, sAbbrev, room3, tName, color, textColor);
            MakeNotification(context, day4, period4f, period4t, sName, sAbbrev, room4, tName, color, textColor);
            MakeNotification(context, day5, period5f, period5t, sName, sAbbrev, room5, tName, color, textColor);*/
        }
    }

    public static void deleteSubject(Context context, String subject) {
        deleteSubjectNotifications(context, subject);
        deleteSubjectFromSubjectsTxt(context, subject);
        deleteSubjectFromHomeworkTxt(context, subject);
        deleteSubjectFromNotificationsTxt(context, subject);
        deleteSubjectFromPeriodsTxt(context, subject);
        deleteSubjectFromTtFieldsTxt(context, subject);
        deleteSubjectFromAllTtNotifications(context, subject);
    }





    public static void EditSubjectEntry(Context context, String sName, String sAbbrev, String tName, String tAbbrev,
                                        String day1, String day2, String day3, String day4, String day5,
                                        String period1f, String period2f, String period3f, String period4f, String period5f,
                                        String period1t, String period2t, String period3t, String period4t, String period5t,
                                        String room1, String room2, String room3, String room4, String room5,
                                        String color, String textColor){

        boolean existsSubject = false;

        File file = new File(context.getExternalFilesDir(null), "Subjects.txt");
        String[][] myArray = toArray(context, "Subjects.txt");
        int Rows = nmbrRowsCols(context, "Subjects.txt")[0];
        int Cols = nmbrRowsCols(context, "Subjects.txt")[1];
        if (file.length() > 0){

            for (int r = 0; r < Rows; r++){
                if (myArray[r][0].equals(sName)){
                    existsSubject = true;
                    myArray[r][1] = sAbbrev;
                    myArray[r][2] = tName;
                    myArray[r][3] = tAbbrev;
                    myArray[r][4] = day1;
                    myArray[r][5] = day2;
                    myArray[r][6] = day3;
                    myArray[r][7] = day4;
                    myArray[r][8] = day5;
                    myArray[r][9] = period1f;
                    myArray[r][10] = period2f;
                    myArray[r][11] = period3f;
                    myArray[r][12] = period4f;
                    myArray[r][13] = period5f;
                    myArray[r][14] = period1t;
                    myArray[r][15] = period2t;
                    myArray[r][16] = period3t;
                    myArray[r][17] = period4t;
                    myArray[r][18] = period5t;
                    myArray[r][19] = room1;
                    myArray[r][20] = room2;
                    myArray[r][21] = room3;
                    myArray[r][22] = room4;
                    myArray[r][23] = room5;
                    myArray[r][24] = color;
                    myArray[r][25] = textColor;
                }
            }
        }
        if (existsSubject == true){
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(file));
                for (int s = 0; s < Rows; s++){
                    String string = myArray[s][0] + "," + myArray[s][1] + "," + myArray[s][2] + "," + myArray[s][3] + "," +
                            myArray[s][4] + "," + myArray[s][5] + "," + myArray[s][6] + "," + myArray[s][7] + "," + myArray[s][8] + "," +
                            myArray[s][9] + "," + myArray[s][10] + "," + myArray[s][11] + "," + myArray[s][12] + "," + myArray[s][13] + "," +
                            myArray[s][14] + "," + myArray[s][15] + "," + myArray[s][16] + "," + myArray[s][17] + "," + myArray[s][18] + "," +
                            myArray[s][19] + "," + myArray[s][20] + "," + myArray[s][21] + "," + myArray[s][22] + "," + myArray[s][23] + "," +
                            myArray[s][24] + "," + myArray[s][25];
                    buf.write(string);
                    buf.newLine();
                }
                buf.close();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (existsSubject == false){
            CreateSubjectEntry(context, sName, sAbbrev, tName, tAbbrev,
                    day1, day2, day3, day4, day5,
                    period1f, period2f, period3f, period4f, period5f,
                    period1t, period2t, period3t, period4t, period5t,
                    room1, room2, room3, room4, room5,
                    color, textColor);
        }
    }

    public static void CreateSubjectEntry(Context context, String sName, String sAbbrev, String tName, String tAbbrev,
                                          String day1, String day2, String day3, String day4, String day5,
                                          String period1f, String period2f, String period3f, String period4f, String period5f,
                                          String period1t, String period2t, String period3t, String period4t, String period5t,
                                          String room1, String room2, String room3, String room4, String room5,
                                          String color, String textColor){

        File file = new File(context.getExternalFilesDir(null), "Subjects.txt");
        String[][] myArray = toArray(context, "Subjects.txt");
        int Rows = nmbrRowsCols(context, "Subjects.txt")[0];
        int Cols = 26;

        String[][] myArray2 = new String[Rows + 1][Cols];
        for (int r = 0; r < Rows; r++){
            for (int c = 0; c < Cols; c++){
                myArray2[r][c] = myArray[r][c];
            }
        }
        myArray2[Rows][0] = sName;
        myArray2[Rows][1] = sAbbrev;
        myArray2[Rows][2] = tName;
        myArray2[Rows][3] = tAbbrev;
        myArray2[Rows][4] = day1;
        myArray2[Rows][5] = day2;
        myArray2[Rows][6] = day3;
        myArray2[Rows][7] = day4;
        myArray2[Rows][8] = day5;
        myArray2[Rows][9] = period1f;
        myArray2[Rows][10] = period2f;
        myArray2[Rows][11] = period3f;
        myArray2[Rows][12] = period4f;
        myArray2[Rows][13] = period5f;
        myArray2[Rows][14] = period1t;
        myArray2[Rows][15] = period2t;
        myArray2[Rows][16] = period3t;
        myArray2[Rows][17] = period4t;
        myArray2[Rows][18] = period5t;
        myArray2[Rows][19] = room1;
        myArray2[Rows][20] = room2;
        myArray2[Rows][21] = room3;
        myArray2[Rows][22] = room4;
        myArray2[Rows][23] = room5;
        myArray2[Rows][24] = color;
        myArray2[Rows][25] = textColor;
        myArray = myArray2;
        Rows++;
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            for (int t = 0; t < Rows; t++){
                if (myArray[t][0].equals(null) == false & myArray[t][1].equals(null) == false & myArray[t][2].equals(null) == false){
                    String string = myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," +
                            myArray[t][4] + "," + myArray[t][5] + "," + myArray[t][6] + "," + myArray[t][7] + "," + myArray[t][8] + "," +
                            myArray[t][9] + "," + myArray[t][10] + "," + myArray[t][11] + "," + myArray[t][12] + "," + myArray[t][13] + "," +
                            myArray[t][14] + "," + myArray[t][15] + "," + myArray[t][16] + "," + myArray[t][17] + "," + myArray[t][18] + "," +
                            myArray[t][19] + "," + myArray[t][20] + "," + myArray[t][21] + "," + myArray[t][22] + "," + myArray[t][23] + "," +
                            myArray[t][24] + "," + myArray[t][25];
                    buf.write(string);
                    buf.newLine();
                }else{
                    Toast.makeText(context, "DON'T write:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2], Toast.LENGTH_SHORT).show();
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void EditPeriodEntry(Context context, String day, String periodF, String periodT, String sName){

        // Check if it is a real entry or just a "-"
        if (!day.equals("-")){
            // Put every period into the array
            int from = Integer.parseInt(periodF);
            int to = Integer.parseInt(periodT);
            for (int p = from; p < to + 1; p++){

                String[][] myArray = toArray(context, "Periods.txt");
                int Rows = nmbrRowsCols(context, "Periods.txt")[0];
                int Cols = 3;

                File file = new File(context.getExternalFilesDir(null), "Periods.txt");

                boolean existsp;
                existsp = false;

                if (file.length() > 0){
                    for (int s = 0; s < Rows; s++){
                        if (myArray[s][0].equals(day)){
                            if (myArray[s][1].equals(String.valueOf(p))){
                                existsp = true;
                                myArray[s][2] = sName;
                            }
                        }
                    }
                }
                if (existsp == false){
                    String[][] myArray2 = new String[Rows + 1][Cols];
                    for (int r = 0; r < Rows; r++){
                        myArray2[r][0] = myArray[r][0];
                        myArray2[r][1] = myArray[r][1];
                        myArray2[r][2] = myArray[r][2];
                    }
                    myArray2[Rows][0] = day;
                    myArray2[Rows][1] = String.valueOf(p);
                    myArray2[Rows][2] = sName;
                    myArray = myArray2;
                    Rows++;
                }
                try{
                    BufferedWriter buf = new BufferedWriter(new FileWriter(file));
                    for (int t = 0; t < Rows; t++){
                        if (myArray[t][0].equals(null) == false & myArray[t][1].equals(null) == false & myArray[t][2].equals(null) == false){
                            buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2]);
                            buf.newLine();
                        }else{
                            Toast.makeText(context, "DON'T write period:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2], Toast.LENGTH_SHORT).show();
                        }
                    }
                    buf.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void EditTimetableEntry(Context context, String day, String periodF, String periodT, String sName, String sAbbrev, String color, String textcolor){

        // Check if it is a real entry or just a "-"
        if (!day.equals("-")){
            // Put every period into the array
            int from = Integer.parseInt(periodF);
            int to = Integer.parseInt(periodT);
            String[] mDayNames = context.getResources().getStringArray(R.array.DayIDs);

            String[][] myArray = toArray(context, "TtFields.txt");
            int Rows = nmbrRowsCols(context, "TtFields.txt")[0];
            int Cols = 5;

            File file = new File(context.getExternalFilesDir(null), "TtFields.txt");

            for (int p = from; p < to + 1; p++) {

                // TODO dayNumber = -1 in order to check if day was found?
                int dayNumber = -1;
                for (int i = 0; i < mDayNames.length; i++) {
                    if (mDayNames[i].equals(day)) dayNumber = i;
                }
                if(dayNumber >= 0) {
                    String FieldNumber = String.valueOf((dayNumber * 12) + p);

                    boolean existsField;
                    existsField = false;

                    if (file.length() > 0) {
                        for (int s = 0; s < Rows; s++) {
                            if (myArray[s][0].equals(FieldNumber)) {
                                existsField = true;
                                myArray[s][1] = sName;
                                myArray[s][2] = sAbbrev;
                                myArray[s][3] = color;
                                myArray[s][4] = textcolor;
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
                        myArray2[Rows][0] = FieldNumber;
                        myArray2[Rows][1] = sName;
                        myArray2[Rows][2] = sAbbrev;
                        myArray2[Rows][3] = color;
                        myArray2[Rows][4] = textcolor;
                        myArray = myArray2;
                        Rows++;
                    }
                } else {
                    Toast.makeText(context, "Could not save timetable entry. Day name not readable.", Toast.LENGTH_SHORT).show();
                }
            }
            try{
                BufferedWriter buf = new BufferedWriter(new FileWriter(file));
                for (int t = 0; t < Rows; t++){
                    if (myArray[t][0].equals(null) == false & myArray[t][1].equals(null) == false & myArray[t][2].equals(null) == false & myArray[t][3].equals(null) == false){
                        buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4]);
                        buf.newLine();
                    }else{
                        Toast.makeText(context, "DON'T write period:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                    }
                }
                buf.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void DeleteWrongNotifications(Context context){
        String[][] myArray = toArray(context, "Notifications.txt");
        int Rows = nmbrRowsCols(context, "Notifications.txt")[0];
        int Cols = 12;

        String[][] myArray2 = toArray(context, "Subjects.txt");
        int Rows2 = nmbrRowsCols(context, "Subjects.txt")[0];
        int Cols2 = nmbrRowsCols(context, "Subjects.txt")[1];


        for (int i = 0; i < Rows; i++){
            boolean equivalentInSubjectsTxt = false;

            for (int i2 = 0; i2 < Rows2; i2++){
                if (myArray[i][6].equals(myArray2[i2][0]) && myArray[i][7].equals(myArray2[i2][1]) && myArray[i][9].equals(myArray2[i2][2]) &&
                        myArray[i][10].equals(myArray2[i2][24]) && myArray[i][11].equals(myArray2[i2][25])){

                    for (int i3 = 0; i3 < 5; i3++) {
                        if (myArray[i][1].equals(myArray2[i2][4 + i3]) && myArray[i][2].equals(myArray2[i2][9 + i3]) &&
                                myArray[i][4].equals(myArray2[i2][14 + i3]) && myArray[i][8].equals(myArray2[i2][19 + i3])) {
                            equivalentInSubjectsTxt = true;
                        }
                    }
                }
            }

            if(!equivalentInSubjectsTxt){
                new ReminderManager(context).deleteReminder(Long.parseLong(myArray[i][0]));
            }
        }
    }

    public static void MakeNotification(Context context, String day, String periodF, String periodT, String sName, String sAbbrev, String room, String teacherName, String color, String textcolor){
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
                            buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4] + "," + myArray[t][5] + "," + myArray[t][6] + "," + myArray[t][7] + "," + myArray[t][8] + "," + myArray[t][9] + "," + myArray[t][10] + "," + myArray[t][11]);
                            buf.newLine();
                        } else {
                            Toast.makeText(context, "DON'T write notification:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                        }
                    }
                    buf.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Could not set notification. Day name not readable.", Toast.LENGTH_SHORT).show();
            }
        }
    }




    public static void deleteSubjectFromSubjectsTxt(Context context, String subject){
        String[][] myArray = toArray(context, "Subjects.txt");
        int Rows = nmbrRowsCols(context, "Subjects.txt")[0];
        int Cols = nmbrRowsCols(context, "Subjects.txt")[1];
        File file = new File(context.getExternalFilesDir(null), "Subjects.txt");


        // Save data to myArray
        /*int count = 0;
        if (Rows > 0) {
            for (int is = 0; is < Rows; is++) {
                int rNew = Rows - count;
                if (is < rNew) {
                    if (myArray[is][0].equals(subject)) {
                        String[][] newArraySubjects = new String[rNew - 1][Cols];
                        for (int n = 0; n < is; n++) {
                            for (int c = 0; c < Cols; c++) newArraySubjects[n][c] = myArray[n][c];
                        }
                        for (int n1 = is + 1; n1 < rNew; n1++) {
                            for (int c = 0; c < Cols; c++)
                                newArraySubjects[n1 - 1][c] = myArray[n1][c];
                        }
                        myArray = newArraySubjects;
                        count++;
                    }
                }
            }
        }

        Rows = Rows - count;*/

        int countA = 0;
        String[][] newArray = new String[0][0];
        if (Rows > 0){
            for (int is = 0; is < Rows; is++){
                if (!myArray[is][0].equals(subject)){
                    String[][] transitionArray = new String[countA + 1][Cols];
                    for (int r = 0; r < countA; r++){
                        for (int c = 0; c < Cols; c++){
                            transitionArray[r][c] = newArray[r][c];
                        }
                    }
                    for (int c = 0; c < Cols; c++){
                        transitionArray[countA][c] = myArray[is][c];
                    }
                    newArray = transitionArray;
                    countA++;
                }
            }
        }

        myArray = newArray;
        Rows = countA;

        writeToCSVFile(context, file, myArray, Rows, Cols, "deleteSubjectFromSubjectsTxt");
    }

    public static void deleteSubjectFromHomeworkTxt(Context context, String subject){
        String[][] myArray = toArray(context, "Homework.txt");
        int Rows = nmbrRowsCols(context, "Homework.txt")[0];
        int Cols = nmbrRowsCols(context, "Homework.txt")[1];
        File file = new File(context.getExternalFilesDir(null), "Homework.txt");

        int countA = 0;
        String[][] newArray = new String[0][0];
        if (Rows > 0){
            for (int is = 0; is < Rows; is++){
                if (!myArray[is][1].equals(subject)){
                    String[][] transitionArray = new String[countA + 1][Cols];
                    for (int r = 0; r < countA; r++){
                        for (int c = 0; c < Cols; c++){
                            transitionArray[r][c] = newArray[r][c];
                        }
                    }
                    for (int c = 0; c < Cols; c++){
                        transitionArray[countA][c] = myArray[is][c];
                    }
                    newArray = transitionArray;
                    countA++;
                }
            }
        }

        myArray = newArray;
        Rows = countA;

        writeToCSVFile(context, file, myArray, Rows, Cols, "deleteSubjectFromHomeworkTxt");
    }

    public static void deleteSubjectFromNotificationsTxt(Context context, String subject){
        String[][] myArray = toArray(context, "Notifications.txt");
        int Rows = nmbrRowsCols(context, "Notifications.txt")[0];
        int Cols = nmbrRowsCols(context, "Notifications.txt")[1];
        File file = new File(context.getExternalFilesDir(null), "Notifications.txt");

        int countA = 0;
        String[][] newArray = new String[0][0];
        if (Rows > 0){
            for (int is = 0; is < Rows; is++){
                if (!myArray[is][6].equals(subject)){
                    String[][] transitionArray = new String[countA + 1][Cols];
                    for (int r = 0; r < countA; r++){
                        for (int c = 0; c < Cols; c++){
                            transitionArray[r][c] = newArray[r][c];
                        }
                    }
                    for (int c = 0; c < Cols; c++){
                        transitionArray[countA][c] = myArray[is][c];
                    }
                    newArray = transitionArray;
                    countA++;
                }
            }
        }

        myArray = newArray;
        Rows = countA;

        // write data to file
        /*try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            for (int t = 0; t < Rows; t++) {
                if (myArray[t][0].equals(null) == false && myArray[t][1].equals(null) == false && myArray[t][2].equals(null) == false && myArray[t][3].equals(null) == false) {
                    buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4] + "," + myArray[t][5] + "," + myArray[t][6] + "," + myArray[t][7] + "," + myArray[t][8] + "," + myArray[t][9] + "," + myArray[t][10] + "," + myArray[t][11]);
                    buf.newLine();
                } else {
                    Toast.makeText(context, "DON'T write notification:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        writeToCSVFile(context, file, myArray, Rows, Cols, "deleteSubjectFromNotificationsTxt");
    }

    public static void deleteSubjectFromPeriodsTxt(Context context, String subject){
        String[][] myArray = toArray(context, "Periods.txt");
        int Rows = nmbrRowsCols(context, "Periods.txt")[0];
        int Cols = nmbrRowsCols(context, "Periods.txt")[1];
        File file = new File(context.getExternalFilesDir(null), "Periods.txt");

        int countA = 0;
        String[][] newArray = new String[0][0];
        if (Rows > 0){
            for (int is = 0; is < Rows; is++){
                if (!myArray[is][2].equals(subject)){
                    String[][] transitionArray = new String[countA + 1][Cols];
                    for (int r = 0; r < countA; r++){
                        for (int c = 0; c < Cols; c++){
                            transitionArray[r][c] = newArray[r][c];
                        }
                    }
                    for (int c = 0; c < Cols; c++){
                        transitionArray[countA][c] = myArray[is][c];
                    }
                    newArray = transitionArray;
                    countA++;
                }
            }
        }

        myArray = newArray;
        Rows = countA;

        // write data to file
        /*try{
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            for (int t = 0; t < Rows; t++){
                if (myArray[t][0].equals(null) == false & myArray[t][1].equals(null) == false & myArray[t][2].equals(null) == false){
                    buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2]);
                    buf.newLine();
                }else{
                    Toast.makeText(context, "DON'T write period:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2], Toast.LENGTH_SHORT).show();
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        writeToCSVFile(context, file, myArray, Rows, Cols, "deleteSubjectFromPeriodsTxt");
    }

    public static void deleteSubjectFromTtFieldsTxt(Context context, String subject){
        String[][] myArray = toArray(context, "TtFields.txt");
        int Rows = nmbrRowsCols(context, "TtFields.txt")[0];
        int Cols = nmbrRowsCols(context, "TtFields.txt")[1];
        File file = new File(context.getExternalFilesDir(null), "TtFields.txt");

        int countA = 0;
        String[][] newArray = new String[0][0];
        if (Rows > 0){
            for (int is = 0; is < Rows; is++){
                if (!myArray[is][1].equals(subject)){
                    String[][] transitionArray = new String[countA + 1][Cols];
                    for (int r = 0; r < countA; r++){
                        for (int c = 0; c < Cols; c++){
                            transitionArray[r][c] = newArray[r][c];
                        }
                    }
                    for (int c = 0; c < Cols; c++){
                        transitionArray[countA][c] = myArray[is][c];
                    }
                    newArray = transitionArray;
                    countA++;
                }
            }
        }

        myArray = newArray;
        Rows = countA;


        // write data to file
        /*try{
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            for (int t = 0; t < Rows; t++){
                if (myArray[t][0].equals(null) == false & myArray[t][1].equals(null) == false & myArray[t][2].equals(null) == false & myArray[t][3].equals(null) == false){
                    buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4]);
                    buf.newLine();
                }else{
                    Toast.makeText(context, "DON'T write period:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        writeToCSVFile(context, file, myArray, Rows, Cols, "deleteSubjectFromTtFieldsTxt");
    }

    public static void deleteSubjectNotifications(Context context, String subject){
        String[][] myArray = toArray(context, "Notifications.txt");
        int Rows = nmbrRowsCols(context, "Notifications.txt")[0];
        int Cols = nmbrRowsCols(context, "Notifications.txt")[1];

        if (Rows > 0 && Cols > 6) {
            for (int i = 0; i < Rows; i++) {
                if (myArray[i][6].equals(subject)) {
                    int NotifIDint = Integer.parseInt(myArray[i][0]);
                    long id = (long) NotifIDint;
                    new ReminderManager(context).deleteReminder(id);
                }
            }
        }
    }

    public static void deleteAllSubjectNotifications(Context context){
        String[][] myArray = toArray(context, "Notifications.txt");
        int Rows = nmbrRowsCols(context, "Notifications.txt")[0];
        int Cols = nmbrRowsCols(context, "Notifications.txt")[1];
        File file = new File(context.getExternalFilesDir(null), "Notifications.txt");

        if (Rows > 0 && Cols > 6) {
            for (int i = 0; i < Rows; i++) {
                int NotifIDint = Integer.parseInt(myArray[i][0]);
                long id = (long) NotifIDint;
                new ReminderManager(context).deleteReminder(id);
            }
        }
    }



    public static void updateDayNamesToIDs(Context context){
        String[] dayNames = context.getResources().getStringArray(R.array.DayNames);
        String[] dayIDs = context.getResources().getStringArray(R.array.DayIDs);
        String[] fileNames = {"Subjects.txt", "Homework.txt", "Notifications.txt", "Periods.txt", "TtFields.txt"};
        int[] arrayNames = {R.array.subjects_txt_columns, R.array.homework_txt_columns, R.array.notifications_txt_columns, R.array.periods_txt_columns, R.array.ttFields_txt_columns};

        for (int i = 0; i < fileNames.length; i++){
            String[][] myArray = toArray(context, fileNames[i]);
            int Rows = nmbrRowsCols(context, fileNames[i])[0];
            int Cols = nmbrRowsCols(context, fileNames[i])[1];
            File file = new File(context.getExternalFilesDir(null), fileNames[i]);
            String[] columnNames = context.getResources().getStringArray(arrayNames[i]);
            //Toast.makeText(context, "columnNames " + columnNames[0] + ", " + columnNames[1], Toast.LENGTH_SHORT).show();


            for (int n = 0; n < columnNames.length; n++){
                if (columnNames[n].equals("day") || columnNames[n].equals("day1") || columnNames[n].equals("day2") ||
                        columnNames[n].equals("day3") || columnNames[n].equals("day4") || columnNames[n].equals("day5")){
                    //Toast.makeText(context, "equals " + columnNames[n], Toast.LENGTH_SHORT).show();

                    for (int r = 0; r < Rows; r++){
                        for (int dn = 0; dn < dayNames.length; dn++){
                            if (myArray[r][n].equals(dayNames[dn])){
                                myArray[r][n] = dayIDs[dn];
                                //Toast.makeText(context, "myArray = " + dayIDs[dn], Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
            writeToCSVFile(context, file, myArray, Rows, Cols, "updateDayNamesToIDs");
        }
    }





    public static void editAllTtNotificationsTxtSubject(Context context, String sName, String sAbbrev, String tName, String tAbbrev,
                                                        String day1, String day2, String day3, String day4, String day5,
                                                        String period1f, String period2f, String period3f, String period4f, String period5f,
                                                        String period1t, String period2t, String period3t, String period4t, String period5t,
                                                        String room1, String room2, String room3, String room4, String room5,
                                                        String color, String textColor,
                                                        String timeBeforeMinutes, String neverNotify, String waitForPrevious, String onlyWhenChangingRooms){
        //Toast.makeText(context, "editAllTtNotificationsTxtSubject", Toast.LENGTH_SHORT).show();
        File file = new File(context.getExternalFilesDir(null), "AllTtNotifications.txt");
        String[][] myArray = toArray(context, "AllTtNotifications.txt");
        int Rows = nmbrRowsCols(context, "AllTtNotifications.txt")[0];
        int Cols = 14;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String[] keysPeriodsStart = {"pref_key_period1_start", "pref_key_period2_start", "pref_key_period3_start", "pref_key_period4_start", "pref_key_period5_start",
                "pref_key_period6_start", "pref_key_period7_start", "pref_key_period8_start", "pref_key_period9_start", "pref_key_period10_start", "pref_key_period11_start", "pref_key_period12_start"};
        String[] keysPeriodsEnd = {"pref_key_period1_end", "pref_key_period2_end", "pref_key_period3_end", "pref_key_period4_end", "pref_key_period5_end",
                "pref_key_period6_end", "pref_key_period7_end", "pref_key_period8_end", "pref_key_period9_end", "pref_key_period10_end", "pref_key_period11_end", "pref_key_period12_end"};
        String[] dayIDs = context.getResources().getStringArray(R.array.DayIDs);

        //Delete all entries concerning this subject
        int countDel = 0;
        for (int r1 = 0; r1 < Rows; r1++){
            if(myArray[r1][5].equals(sName)) countDel++;
        }
        if(Rows-countDel <= 0){
            myArray = new String[0][0];
            Rows = 0;
        } else {
            String[][] deleteArray = new String[0][0];
            int countOut = 0;
            int countIn = 0;
            for (int r2 = 0; r2 < Rows; r2++){
                if(!myArray[r2][5].equals(sName)){
                    String[][] interimArray = new String[countIn+1][Cols];
                    for(int r1 = 0; r1 < countIn; r1++) {
                        for (int c1 = 0; c1 < Cols; c1++) {
                            interimArray[r1][c1]=deleteArray[r1][c1];
                        }
                    }
                    for (int n = 0; n < Cols; n++){
                        interimArray[countIn][n]=myArray[r2][n];
                    }
                    deleteArray=interimArray;
                    countIn++;
                } else {
                    countOut++;
                }
            }
            myArray = deleteArray;
            Rows = countIn;
        }



        //add all periods of this subject
        String[] daysArray = {day1, day2, day3, day4, day5};
        String[] periodFArray = {period1f, period2f, period3f, period4f, period5f};
        String[] periodTArray = {period1t, period2t, period3t, period4t, period5t};
        String[] roomsArray = {room1, room2, room3, room4, room5};

        // Do it for all 5 periods
        for (int p1 = 0; p1 < 5; p1++){
            // check if there is data for period
            if(!daysArray[p1].equals("-") && !periodFArray[p1].equals("-") && !periodTArray[p1].equals("-")
                    && !daysArray[p1].equals("") && !periodFArray[p1].equals("") && !periodTArray[p1].equals("")){
                String timePeriodStartMinutes, periods, timeStart, timeEnd, timeEndMinutes;

                // get the day's ID
                int dayInt = -1;
                for (int id = 0; id < dayIDs.length; id++){
                    if (daysArray[p1].equals(dayIDs[id])) dayInt = id;
                }

                // check if day ID was found successfully
                if (dayInt < 0){
                    Log.e("dayID exception", "Could not find Day ID - period won't be notified for. In: DataStorageHandler, editAllTtNotificationsTxtSubject()");
                } else {
                    // find the period's ID
                    int periodInt = Integer.parseInt(periodFArray[p1]) - 1;
                    int periodToInt = Integer.parseInt(periodTArray[p1]) - 1;
                    {
                        Log.e("periodInt exception", "Period could not be parsed to Integer ("+periodFArray[p1]+") - period won't be notified for. In: DataStorageHandler, editAllTtNotificationsTxtSubject()");
                    }

                    // get start and end time for this period from preferences
                    String timeStartUnSplit = prefs.getString(keysPeriodsStart[periodInt], "false");
                    String timeEndUnSplit = prefs.getString(keysPeriodsEnd[periodToInt], "false");
                    // check if times have been fund successfully
                    if (timeStartUnSplit.equals("false") || timeEndUnSplit.equals("false")){
                        Log.e("prefs exception", "period start time preference could not be found - period won't be notified for. In: DataStorageHandler, editAllTtNotificationsTxtSubject()");
                    } else {
                        // calculate times in minutes
                        String[] timeSplit = timeStartUnSplit.split(":");
                        int timePeriodStartMinutesInt = dayInt*24*60 + Integer.parseInt(timeSplit[0])*60 + Integer.parseInt(timeSplit[1]);
                        timePeriodStartMinutes = String.valueOf(timePeriodStartMinutesInt);

                        // set the "periods" text for notification
                        if (periodFArray[p1].equals(periodTArray[p1])) {
                            periods = periodFArray[p1];
                        } else {
                            periods = periodFArray[p1] + "-" + periodTArray[p1];
                        }


                        timeStart = formatTime(timeStartUnSplit);

                        timeEnd = formatTime(timeEndUnSplit);

                        // calculate end time in minutes
                        String[] timeEndSplit = timeEndUnSplit.split(":");
                        int timePeriodEndMinutesInt = dayInt*24*60 + Integer.parseInt(timeEndSplit[0])*60 + Integer.parseInt(timeEndSplit[1]);
                        timeEndMinutes = String.valueOf(timePeriodEndMinutesInt);


                        // check where to put the period's notification (sort by starting time)
                        int newRow = 0;
                        for (int nr = 0; nr < Rows; nr++){
                            if (myArray[nr][0] == null){
                                Log.e("Null problem", "null: "+String.valueOf(nr)+myArray[nr][5]+". In: DataStorageHandler, editAllTtNotificationsSubjectTxt(), 'check where to put the period's notification'");
                            } else if (timePeriodStartMinutesInt > Integer.parseInt(myArray[nr][0])) {
                                newRow++;
                            }
                        }

                        // create newMyArray
                        String[][] newMyArray = new String[Rows+1][Cols];
                        // copy myArray entries to newMyArray until where the new line will be inserted
                        for (int before = 0; before < newRow; before++){
                            for (int c = 0; c < Cols; c++){
                                newMyArray[before][c] = myArray[before][c];
                            }
                        }
                        // insert new line
                        newMyArray[newRow][0] = timePeriodStartMinutes;
                        newMyArray[newRow][1] = timeBeforeMinutes;
                        newMyArray[newRow][2] = neverNotify;
                        newMyArray[newRow][3] = waitForPrevious;
                        newMyArray[newRow][4] = onlyWhenChangingRooms;
                        newMyArray[newRow][5] = sName;
                        newMyArray[newRow][6] = sAbbrev;
                        newMyArray[newRow][7] = periods;
                        newMyArray[newRow][8] = roomsArray[p1];
                        newMyArray[newRow][9] = tName;
                        newMyArray[newRow][10] = tAbbrev;
                        newMyArray[newRow][11] = timeStart;
                        newMyArray[newRow][12] = timeEnd;
                        newMyArray[newRow][13] = timeEndMinutes;

                        // insert entries after new line
                        for (int after = newRow; after < Rows; after++){
                            for (int c = 0; c < Cols; c++){
                                newMyArray[after+1][c] = myArray[after][c];
                            }
                        }

                        // make newMyArray the new myArray
                        myArray = newMyArray;
                        Rows++;
                    }
                }
            }
        }
        //Toast.makeText(context, "AllTt: "+myArray[0][0]+", "+myArray[0][1], Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, "AllTt Rows Print: "+String.valueOf(Rows), Toast.LENGTH_SHORT).show();
        writeToCSVFile(context, file, myArray, Rows, Cols, "editAllTtNotificationsTxtSubject");
        updateActiveTtNotificationsTxt(context);
    }

    public static void deleteSubjectFromAllTtNotifications(Context context, String sName){
        File file = new File(context.getExternalFilesDir(null), "AllTtNotifications.txt");
        String[][] myArray = toArray(context, "AllTtNotifications.txt");
        int Rows = nmbrRowsCols(context, "AllTtNotifications.txt")[0];
        int Cols = 14;

        //Delete all entries concerning this subject
        int countDel = 0;
        for (int r1 = 0; r1 < Rows; r1++){
            if(myArray[r1][5].equals(sName)) countDel++;
        }
        if(Rows-countDel <= 0){
            myArray = new String[0][0];
            Rows = 0;
        } else {
            String[][] deleteArray = new String[Rows-countDel][Cols];
            int countOut = 0;
            for (int r2 = 0; r2 < Rows; r2++){
                if(!myArray[r2][5].equals(sName)){
                    for (int c1 = 0; c1 < Cols; c1++){
                        deleteArray[r2][c1] = myArray[r2-countOut][c1];
                    }
                } else {
                    countOut++;
                }
            }
            myArray = deleteArray;
            Rows = Rows - countOut;
        }

        writeToCSVFile(context, file, myArray, Rows, Cols, "deleteSubjectFromAllTtNotifications");
        context.startService(new Intent(context, NotificationService.class));
    }

    public static void updateActiveTtNotificationsTxt(Context context){
        File file = new File(context.getExternalFilesDir(null), "ActiveTtNotifications.txt");
        String[][] AllTtNoifications = toArray(context, "AllTtNotifications.txt");
        int RowsAll = nmbrRowsCols(context, "AllTtNotifications.txt")[0];
        int ColsAll = nmbrRowsCols(context, "AllTtNotifications.txt")[1];
        String[][] myArray;
        int ColsMyArray = 9;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean never = prefs.getBoolean("pref_key_notification_never", false);
        boolean wait = prefs.getBoolean("pref_key_notification_wait_for_previous", false);
        boolean rooms = prefs.getBoolean("pref_key_notification_only_if_change_rooms", false);
        int timePref = prefs.getInt("pref_key_notification_time", 0);

        //Toast.makeText(context, "rowsAll: "+RowsAll, Toast.LENGTH_SHORT).show();
        int count = 0;
        for (int i = 0; i < RowsAll; i++){
            boolean dT = AllTtNoifications[i][2].equals("default") && never;
            boolean tT = AllTtNoifications[i][2].equals("true") && never;
            boolean tF = AllTtNoifications[i][2].equals("true") && !never;
            // TODO is this correct?? (tF instead of !tF)
            if (!dT && !tT && !tF) {
                count++;
            }
        }

        int count2 = 0;
        myArray = new String[count][ColsMyArray];

        for (int i = 0; i < RowsAll; i++){
            boolean dT = AllTtNoifications[i][2].equals("default") && never;
            boolean tT = AllTtNoifications[i][2].equals("true") && never;
            boolean tF = AllTtNoifications[i][2].equals("true") && !never;

            //Toast.makeText(context, "dT: "+String.valueOf(dT)+", tT: "+String.valueOf(tT)+", tF"+String.valueOf(tF), Toast.LENGTH_SHORT).show();
            if (!dT && !tT && !tF) {

                boolean roomsProblem = false;
                if (AllTtNoifications[i][4].equals("default")) {
                    if (rooms && AllTtNoifications[i][8].equals(AllTtNoifications[i-1][8])) roomsProblem = true;
                } else if (AllTtNoifications[i][4].equals("true")
                        && AllTtNoifications[i][8].equals(AllTtNoifications[i-1][8])) roomsProblem = true;

                if (!roomsProblem) {
                    String timeNotification;
                    String subject = AllTtNoifications[i][5];
                    String sAbbrev = AllTtNoifications[i][6];
                    String periods = AllTtNoifications[i][7];
                    String room = AllTtNoifications[i][8];
                    String teacher = AllTtNoifications[i][9];
                    String tAbbrev = AllTtNoifications[i][10];
                    String timeStart = AllTtNoifications[i][11];
                    String timeEnd = AllTtNoifications[i][12];

                    if (AllTtNoifications[i][1].equals("default")) {
                        timeNotification = String.valueOf(Integer.parseInt(AllTtNoifications[i][0]) - timePref);
                    } else if (isStringNumeric(AllTtNoifications[i][1])){
                        timeNotification = String.valueOf(Integer.parseInt(AllTtNoifications[i][0])
                                - Integer.parseInt(AllTtNoifications[i][1]));
                    } else {
                        timeNotification = String.valueOf(Integer.parseInt(AllTtNoifications[i][0]) - timePref);
                    }

                    boolean waitForPrevious = false;
                    if (AllTtNoifications[i][3].equals("true")){
                        waitForPrevious = true;
                    } else if (AllTtNoifications[i][3].equals("false")) {
                        waitForPrevious = false;
                    } else if (wait) {
                        waitForPrevious = true;
                    }

                    if (waitForPrevious) {
                        if (i > 0 && isStringNumeric(AllTtNoifications[i - 1][13])) {
                            if (Integer.parseInt(timeNotification) <= Integer.parseInt(AllTtNoifications[i - 1][13]))
                                timeNotification = AllTtNoifications[i - 1][13];
                        }
                    }

                    int newRow = 0;
                    for (int nr = 0; nr < count2; nr++){
                        if (Integer.parseInt(timeNotification) > Integer.parseInt(myArray[nr][0])) {
                            newRow++;
                        }
                    }

                    String[][] newMyArray = new String[count][ColsMyArray];
                    for (int before = 0; before < newRow; before++){
                        for (int c = 0; c < ColsMyArray; c++){
                            newMyArray[before][c] = myArray[before][c];
                        }
                    }
                    newMyArray[newRow][0] = timeNotification;
                    newMyArray[newRow][1] = subject;
                    newMyArray[newRow][2] = sAbbrev;
                    newMyArray[newRow][3] = periods;
                    newMyArray[newRow][4] = room;
                    newMyArray[newRow][5] = teacher;
                    newMyArray[newRow][6] = tAbbrev;
                    newMyArray[newRow][7] = timeStart;
                    newMyArray[newRow][8] = timeEnd;

                    for (int after = newRow+1; after < count; after++){
                        for (int c = 0; c < ColsMyArray; c++){
                            newMyArray[after][c] = myArray[after][c];
                        }
                    }

                    myArray = newMyArray;
                    count2++;
                    //Toast.makeText(context, myArray[0][0], Toast.LENGTH_SHORT).show();
                }
            }
        }

        writeToCSVFile(context, file, myArray, count2, ColsMyArray, "updateActiveTtNotificationsTxt");
        context.startService(new Intent(context, NotificationService.class));
        //Toast.makeText(context, "updateActiveTtNotifications", Toast.LENGTH_SHORT).show();
    }



    public static void changeNotificationMethod(Context context){
        changeNotificationMethod(context, false);
    }

    public static void changeNotificationMethod(Context context, boolean force){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String pref_NotificationMethodUpdated = "NotificationMethodUpdated";
        String pref_NotificationUpdate2016_05_20 = "NotificationUpdate20160520";

        String[][] myArray = toArray(context, "Subjects.txt");
        int rows = nmbrRowsCols(context, "Subjects.txt")[0];
        int cols = nmbrRowsCols(context, "Subjects.txt")[1];

        String[][] nArray = toArray(context, "AllTtNotifications.txt");
        int nRows = nmbrRowsCols(context, "AllTtNotifications.txt")[0];

        //Toast.makeText(context, "rows: " +String.valueOf(rows), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < rows; i++) {
            boolean already = false;
            for (int r = 0; r < nRows; r++){
                if (nArray[r][5].equals(myArray[i][0])) already = true;
            }
            if (!already || force) {
                editAllTtNotificationsTxtSubject(context, myArray[i][0], myArray[i][1], myArray[i][2], myArray[i][3],
                        myArray[i][4], myArray[i][5], myArray[i][6], myArray[i][7], myArray[i][8],
                        myArray[i][9], myArray[i][10], myArray[i][11], myArray[i][12], myArray[i][13],
                        myArray[i][14], myArray[i][15], myArray[i][16], myArray[i][17], myArray[i][18],
                        myArray[i][19], myArray[i][20], myArray[i][21], myArray[i][22], myArray[i][23],
                        myArray[i][24], myArray[i][25],
                        "default", "default", "default", "default");
            }
        }


        String[][] notifArray = toArray(context, "Notifications.txt");
        int notifRows = nmbrRowsCols(context, "Notifications.txt")[0];
        int notifCols = nmbrRowsCols(context, "Notifications.txt")[1];

        for (int i2 = 0; i2 < notifRows; i2++) {
            new ReminderManager(context).deleteReminder(Long.getLong(notifArray[i2][0]));
        }

        prefs.edit().putBoolean(pref_NotificationMethodUpdated, true).apply();
        prefs.edit().putBoolean(pref_NotificationUpdate2016_05_20, true).apply();
    }

    public static void checkTtFieldsTxt(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String pref_checkTtFieldsTxt2016_05_28 = "CheckTtFieldsTxt20160528";

        String[][] subjects = toArray(context, "Subjects.txt");
        String[][] fields = toArray(context, "TtFields.txt");
        int[] subjectsRC = nmbrRowsCols(context, "Subjects.txt");
        int[] fieldsRC = nmbrRowsCols(context, "TtFields.txt");
        File fFile = new File(context.getExternalFilesDir(null), "TtFields.txt");

        if (fieldsRC[1] >= 5){
            int deleted = 0;
            for (int i = 0; i < fieldsRC[0]; i++) {
                if (i < fieldsRC[0] - deleted) {
                    boolean doubleBooking = false;
                    for (int i1 = 0; i1 < fieldsRC[0]-deleted; i1++) {
                        if (fields[i][0].equals(fields[i1][0])) doubleBooking = true;
                    }
                    if (doubleBooking) {
                        deletePeriodContainingField(context, fields[i][0], fields[i][1]);
                        deleted++;
                    }

                    boolean foundSubject = false;
                    for (int s = 0; s < subjectsRC[0]; s++) {
                        if (fields[i][1].equals(subjects[s][0])) {
                            fields[i][2] = subjects[s][1];
                            fields[i][3] = subjects[s][24];
                            fields[i][4] = subjects[s][25];
                            foundSubject = true;
                        }
                    }
                    if (!foundSubject) {
                        if (fieldsRC[0] == 1) {
                            String[][] newFields = new String[0][0];
                            writeToCSVFile(context, fFile, newFields, 0, 0, "checkTtFilesTxt");
                        }
                        String[][] newFields = new String[fieldsRC[0] - 1][fieldsRC[1]];

                        for (int i1 = 0; i1 < i; i1++) {
                            for (int c = 0; c < fieldsRC[1]; c++) {
                                newFields[i1][c] = fields[i1][c];
                            }
                        }
                        for (int i2 = i + 1; i2 < fieldsRC[0]; i2++) {
                            for (int c = 0; c < fieldsRC[1]; c++) {
                                newFields[i2 - 1][c] = fields[i2][c];
                            }
                        }

                        writeToCSVFile(context, fFile, newFields, fieldsRC[0] - 1, fieldsRC[1], "checkTtFilesTxt");
                    }
                }
            }
        }

        prefs.edit().putBoolean(pref_checkTtFieldsTxt2016_05_28, true).apply();
    }

    public static void deletePeriodContainingField (Context context, String field, String subject){
        String[][] subjects = toArray(context, "Subjects.txt");
        String[][] periods = toArray(context, "Periods.txt");
        String[][] fields = toArray(context, "TtFields.txt");

        int[] subjectsRC = nmbrRowsCols(context, "Subjects.txt");
        int[] periodsRC = nmbrRowsCols(context, "Periods.txt");
        int[] fieldsRC = nmbrRowsCols(context, "TtFields.txt");

        boolean fail = false;
        if (isStringNumeric(field)) {
            int iField = Integer.parseInt(field);

            int day = -1;
            int period = -1;
            for (int d = 0; d < 7; d++) {
                if (d*12 +1 <= iField && iField <= d*12 +12){
                    day = d;
                    period = iField - d*12;
                }
            }

            if (day >= 0 && period >0){
                if (periodsRC[1] >= 3) {
                    for (int p = 0; p < periodsRC[0]; p++) {
                        if (periods[p][0].equals(String.valueOf(day))
                                && periods[p][1].equals(String.valueOf(period))
                                && periods[p][2].equals(subject)){
                            deletePeriodFromPeriodsTxt(context, p);
                        }
                    }
                }


                for (int s = 0; s < subjectsRC[0]; s++){
                    for (int s1 = 0; s1 < 5; s1++) {
                        if (subjects[s][0].equals((subject)) && subjects[s][4+s1].equals(String.valueOf(day))){
                            int from = -1;
                            int to = -1;
                            if (isStringNumeric(subjects[s][9+s1])) from = Integer.valueOf(subjects[s][9+s1]);
                            if (isStringNumeric(subjects[s][14+s1])) to = Integer.valueOf(subjects[s][14+s1]);

                            if (from > -1 && to > -1){
                                if (from <= period && period <= to){
                                    subjects[s][4+s1] = "-";
                                    subjects[s][9+s1] = "-";
                                    subjects[s][14+s1] = "-";
                                }
                            }
                        }
                    }
                }
                File sFile = new File(context.getExternalFilesDir(null), "Subjects.txt");
                writeToCSVFile(context, sFile, subjects, subjectsRC[0], subjectsRC[1], "deletePeriodContainingField");

            } else {
                fail = true;
            }
        } else {
            fail = true;
        }
    }

    public static void deletePeriodFromPeriodsTxt (Context context, int row){
        String[][] periods = toArray(context, "Periods.txt");
        int[] periodsRC = nmbrRowsCols(context, "Periods.txt");
        File file = new File(context.getExternalFilesDir(null), "Subjects.txt");

        if (periodsRC[0]>0 && periodsRC[1]>=3) {
            if (periodsRC[0] == 1){
                String newPArray[][] = new String[0][0];
                writeToCSVFile(context, file, newPArray, 0, 0, "deletePeriodFromPeriodsTxt");
            } else {
                String newPArray[][] = new String[periodsRC[0] - 1][periodsRC[1]];

                for (int i1 = 0; i1 < row; i1++) {
                    for (int c = 0; c < periodsRC[1]; c++) {
                        newPArray[i1][c] = periods[i1][c];
                    }
                }
                for (int i2 = row + 1; i2 < periodsRC[0]; i2++) {
                    for (int c = 0; c < periodsRC[1]; c++) {
                        newPArray[i2 - 1][c] = periods[i2][c];
                    }
                }

                writeToCSVFile(context, file, newPArray, periodsRC[0] - 1, periodsRC[1], "deletePeriodFromPeriodsTxt");
            }
        }
    }


    public static void writeToCSVFile(Context context, File file, String[][] myArray, int Rows, int Cols, String caller){
        //Toast.makeText(context, myArray[0][0], Toast.LENGTH_SHORT).show();
        // write data to file
        try{
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));

            for (int t = 0; t < Rows; t++){
                boolean dataCorrect = true;
                for (int c = 0; c < Cols; c++){
                    if (myArray[t][c] == null) dataCorrect = false;
                }

                if (dataCorrect){
                    String line = myArray[t][0];
                    for (int c1 = 1; c1 < Cols; c1++){
                        line = line + "," + myArray[t][c1];
                    }
                    //Toast.makeText(context, "write "+ line, Toast.LENGTH_SHORT).show();
                    buf.write(line);
                    buf.newLine();
                }else{
                    Log.e("Data saving error", "CANNOT save data:" + caller + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2]+". In: DataStorageHandler, writeToCSVFile");
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[][] toArray(Context context, String filename, boolean fromAssets){
        String[][] myArray = new String[0][0];

        if (!fromAssets){
            myArray = toArray(context, filename);
        } else if (fromAssets){
            myArray = toArrayAssets(context, filename);
        }

        return myArray;
    }

    public static String[][] toArray(Context context, String filename){

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

    public static int[] nmbrRowsCols(Context context, String filename){
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

    public static String[][] toArrayAssets(Context context, String filename){
        AssetManager assetManager = context.getAssets();
        InputStream input;
        String text = "[no text]";
        String [][] myArray = new String[0][0];

        final int[] RowsCols = nmbrRowsColsAssets(context, filename);
        int Rows = RowsCols[0];
        int Cols = RowsCols[1];

        if (Rows != 0 && Cols != 0) {
            myArray = new String[Rows][Cols];

            try {
                input = assetManager.open(filename);

                int size = input.available();
                byte[] buffer = new byte[size];

                input.read(buffer);
                input.close();

                text = new String(buffer);

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (text != null && text.length() > 0 && !text.equals("[no text]")) {
                String[] sRows = text.split(System.getProperty("line.separator"));

                for (int i = 0; i < sRows.length; i++) {
                    String[] sCols = sRows[i].split(",");
                    for (int c = 0; c < sCols.length; c++){
                        myArray[i][c] = sCols[c];
                    }
                }
            }
        }

        return myArray;
    }

    public static int[] nmbrRowsColsAssets(Context context, String filename){
        AssetManager assetManager = context.getAssets();
        InputStream input;
        String text = "[no text]";
        int Rows = 0;
        int Cols = 0;

        try {
            input = assetManager.open(filename);

            int size = input.available();
            byte[] buffer = new byte[size];

            input.read(buffer);
            input.close();

            text = new String(buffer);

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (text != null && text.length() > 0 && !text.equals("[no text]")){
            String[] sRows = text.split(System.getProperty("line.separator"));
            Rows = sRows.length;

            for (int i = 0; i < Rows; i++){
                String[] sCols = sRows[i].split(",");
                if (sCols.length > Cols) Cols = sCols.length;
            }
        }

        int[] RowsCols = new int[2];
        RowsCols[0] = Rows;
        RowsCols[1] = Cols;
        return RowsCols;
    }



    public static boolean isStringNumeric( String str ) {
        DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
        char localeMinusSign = currentLocaleSymbols.getMinusSign();

        if (str == null || str.length() <= 0) return false;
        if (!Character.isDigit( str.charAt( 0 ) ) && str.charAt( 0 ) != localeMinusSign ) return false;

        boolean isDecimalSeparatorFound = false;
        char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();

        for ( char c : str.substring( 1 ).toCharArray() )
        {
            if ( !Character.isDigit( c ) )
            {
                if ( c == localeDecimalSeparator && !isDecimalSeparatorFound )
                {
                    isDecimalSeparatorFound = true;
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable(Context context) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        Toast.makeText(context, "external storage currently NOT available", Toast.LENGTH_SHORT).show();
        return false;
    }

    public static String formatTime(String time){
        // Make 3:0 -> 03:00
        if (time.length() >= 3) {
            String h, m;
            String hour = time.split(":")[0];
            String minute = time.split(":")[1];
            if (hour.length() == 1) {
                h = "0" + hour;
            } else {
                h = hour;
            }
            if (minute.length() == 1) {
                m = "0" + minute;
            } else {
                m = minute;
            }
            String newTime = h + ":" + m;

            return newTime;
        } else {
            return "";
        }
    }

    public static String formatDateGeneralFormat(Context context, Calendar calendar){
        SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.date_formatter_general));

        String date = formatter.format(calendar.getTime());
        return date;
    }
}
