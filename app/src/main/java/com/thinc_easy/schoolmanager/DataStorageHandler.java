package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by M on 02.02.2016.
 */
public class DataStorageHandler {

    public static void RegisterSubject(Context context, String ttFolderName,
                                   String sName, String sAbbrev,
                                   String tName, String tAbbrev, String color1, String color2,
                                   String[][] Lessons){
        if (isExternalStorageWritable(context)) {
            System.out.println("Storage is writable.");
            String subjectID = RegisterSubjectEntry(context, ttFolderName, sName, sAbbrev, tName, tAbbrev, color1, color2);
            System.out.println("ID: " + subjectID);

            // create entry in lessons.txt, timetable_a(/b..).txt, timetable_attributes.txt for all subject lessons
            AddLessons(context, ttFolderName, subjectID, Lessons);

            // TODO create all tt notifications for lessons
        }
    }

    public static String RegisterSubjectEntry(Context context, String ttFolderName,
                                   String sName, String sAbbrev,
                                   String tName, String tAbbrev, String color1, String color2){

        String subjectID = "[null]";
        String[] newDataArray = {sName, sAbbrev, tName, tAbbrev, color1, color2};
        File ttFolder = new File(context.getExternalFilesDir(null), ttFolderName);

        if (ttFolder.isDirectory()){
            String subjectsFileName = context.getResources().getString(R.string.file_name_subjects);
            String subjectsFilePath = ttFolderName + "/" + subjectsFileName;

            File subjectsFile = new File(ttFolder, subjectsFileName);

            if (subjectsFile.exists()){
                String[][] subjectsArray = toArray(context, subjectsFilePath);
                int Rows = nmbrRowsCols(context, subjectsFilePath)[0];
                int Cols = nmbrRowsCols(context, subjectsFilePath)[1];

                if (newDataArray.length + 1 > Cols) {
                    String[][] SubjectsArray2 = new String[Rows][newDataArray.length + 1];
                    for (int r1 = 0; r1 < Rows; r1++){
                        for (int c1 = 0; c1 < Cols; c1++){
                            SubjectsArray2[r1][c1] = (subjectsArray[r1][c1] == null || subjectsArray[r1][c1].equals("")) ? "[none]" : subjectsArray[r1][c1];
                        }
                        for (int c2 = 0; c2 < (newDataArray.length + 1 - Cols); c2++){
                            SubjectsArray2[r1][Cols + c2] = "";
                        }
                    }
                    subjectsArray = SubjectsArray2;
                    Cols = newDataArray.length + 1;
                }

                int maxID = 0;
                for (int r = 0; r < Rows; r++){
                    String sThisID = subjectsArray[r][0];
                    if (isStringNumeric(sThisID)){
                        int thisID = Integer.parseInt(sThisID);
                        if (thisID > maxID) maxID = thisID;
                    }
                }
                int idInt = maxID + 1;
                String ID = String.valueOf(idInt);
                final int length = ID.length();
                for (int s = 0; s < (4 - length); s++){
                    ID = "0" + ID;
                }

                String[][] SubjectsArray3 = new String[Rows + 1][Cols];
                for (int r2 = 0; r2 < Rows; r2++){
                    for (int c3 = 0; c3 < Cols; c3++){
                        SubjectsArray3[r2][c3] = (subjectsArray[r2][c3] == null || subjectsArray[r2][c3].equals("")) ? "[none]" : subjectsArray[r2][c3];
                    }
                }

                SubjectsArray3[Rows][0] = ID;
                for (int c4 = 0; c4 < newDataArray.length; c4++){
                    SubjectsArray3[Rows][c4 + 1] = (newDataArray[c4] == null || newDataArray[c4].equals("")) ? "[none]" : newDataArray[c4];
                }
                subjectsArray = SubjectsArray3;
                Rows++;

                subjectID = ID;
                writeToCSVFile(context, subjectsFile, subjectsArray, Rows, Cols, "New RegisterSubjectEntry");
            } else {

                subjectID = "0001";
                int Rows = 1;
                int Cols = newDataArray.length + 1;
                String[][] subjectsArray = new String[Rows][Cols];
                subjectsArray[0][0] = subjectID;
                for (int d = 0; d < newDataArray.length; d++)
                    subjectsArray[0][d + 1] = (newDataArray[d] == null || newDataArray[d].equals("")) ? "[none]" : newDataArray[d];

                writeToCSVFile(context, subjectsFile, subjectsArray, Rows, Cols, "New RegisterSubjectEntry");
            }
        }

        return subjectID;
    }

    public static void EditSubject(Context context, String ttFolderName,
                            String subjectID, String sName, String sAbbrev,
                            String tName, String tAbbrev, String color1, String color2,
                            String[][] Lessons){
        if (isExternalStorageWritable(context)) {
            EditSubjectEntry(context, ttFolderName, subjectID, sName, sAbbrev, tName, tAbbrev, color1, color2);

            // delete all subject lessons from lessons.txt, timetable_a(/b..).txt, timetable_attributes.txt
            RemoveSubjectFromLessonsTxt(context, ttFolderName, subjectID, false);
            RefreshTtAttributes(context, ttFolderName);

            // create entry in lessons.txt, timetable_a(/b..).txt, timetable_attributes.txt for all subject lessons
            AddLessons(context, ttFolderName, subjectID, Lessons);

            // TODO edit all tt notifications for lessons
        }
    }

    public static void EditSubjectEntry(Context context, String ttFolderName,
                                   String subjectID, String sName, String sAbbrev,
                                   String tName, String tAbbrev, String color1, String color2){

        String[] newDataArray = {subjectID, sName, sAbbrev, tName, tAbbrev, color1, color2};
        File ttFolder = new File(context.getExternalFilesDir(null), ttFolderName);

        if (ttFolder.isDirectory()){
            String subjectsFileName = context.getResources().getString(R.string.file_name_subjects);
            String subjectsFilePath = ttFolderName + "/" + subjectsFileName;

            File subjectsFile = new File(ttFolder, subjectsFileName);
            String[][] subjectsArray = {{""}};
            int fileRows = 0;
            int fileCols = 0;
            int subjectRow = -1;

            if (subjectsFile.exists()){
                subjectsArray = toArray(context, subjectsFilePath);
                final int[] rc = nmbrRowsCols(context, subjectsFilePath);
                fileRows = rc[0];
                fileCols = rc[1];

                for (int r = 0; r < fileRows; r++){
                    if (subjectsArray[r][0].equals(subjectID)) subjectRow = r;
                }
            } else {
                subjectsArray = new String[1][newDataArray.length];
                fileRows = 1;
                fileCols = newDataArray.length;
                subjectRow = 0;
            }

            if (subjectRow < 0){
                int cDif = 0;
                if (fileCols < newDataArray.length) cDif = newDataArray.length - fileCols;

                String[][] subjectsArray2 = new String[fileRows+1][fileCols + cDif];

                for (int r = 0; r < fileRows; r++){
                    for (int c = 0; c < fileCols; c++)
                        subjectsArray2[r][c] = (subjectsArray[r][c] == null || subjectsArray[r][c].equals("")) ? "[none]" : subjectsArray[r][c];
                    for (int cd = 0; cd < cDif; cd++) subjectsArray[r][fileCols+cd] = "";
                }

                subjectsArray = subjectsArray2;
                subjectRow = fileRows;

                fileRows++;
                fileCols = fileCols + cDif;
            }

            for (int cn = 0; cn < newDataArray.length; cn++) subjectsArray[subjectRow][cn] = (newDataArray[cn] == null || newDataArray[cn].equals("")) ? "[none]" : newDataArray[cn];

            writeToCSVFile(context, subjectsFile, subjectsArray, fileRows, fileCols, "new EditSubjectEntry");
        }
    }

    public static void DeleteSubject(Context context, String ttFolder, String sujectID){
        RemoveSubjectfromSubjectsTxt(context, ttFolder, sujectID);
        RemoveSubjectFromLessonsTxt(context, ttFolder, sujectID);
    }

    public static void RemoveSubjectfromSubjectsTxt(Context context, String ttFolderName, String subjectID){
        File ttFolder = new File(context.getExternalFilesDir(null), ttFolderName);

        if (ttFolder.isDirectory()) {
            String subjectsFileName = context.getString(R.string.file_name_subjects);
            String subjectsFilePath = ttFolderName + "/" + subjectsFileName;

            String[][] myArray = toArray(context, subjectsFilePath);
            int Rows = nmbrRowsCols(context, subjectsFilePath)[0];
            int Cols = nmbrRowsCols(context, subjectsFilePath)[1];
            File file = new File(context.getExternalFilesDir(null), subjectsFilePath);

            if (file.exists()) {
                int countA = 0;
                String[][] newArray = new String[0][0];
                if (Rows > 0) {
                    for (int is = 0; is < Rows; is++) {
                        String thisSID = myArray[is][0];
                        if (!thisSID.equals(subjectID)) {
                            String[][] transitionArray = new String[countA + 1][Cols];
                            for (int r = 0; r < countA; r++) {
                                for (int c = 0; c < Cols; c++) {
                                    transitionArray[r][c] = newArray[r][c];
                                }
                            }
                            for (int c = 0; c < Cols; c++) {
                                transitionArray[countA][c] = (myArray[is][c] == null || myArray[is][c].equals("")) ? "[none]" : myArray[is][c];
                            }
                            newArray = transitionArray;
                            countA++;
                        }
                    }
                }

                myArray = newArray;
                Rows = countA;

                writeToCSVFile(context, file, myArray, Rows, Cols, "RemoveSubjectfromSubjectsTxt");
            }
        }
    }

    public static void AddLessons(Context context, String ttFolderName, String subjectID, String[][] Lessons){
        File ttFolder = new File(context.getExternalFilesDir(null), ttFolderName);

        if (ttFolder.isDirectory()){
            String lessonsFileName = context.getResources().getString(R.string.file_name_lessons);
            String lessonsFilePath = ttFolderName + "/" + lessonsFileName;

            for (int i = 0; i < Lessons.length; i++){
                String lessonID = "[none]";
                String[] newDataArray = new String[Lessons[i].length];

                for (int c = 0; c < Lessons[i].length; c++){
                    newDataArray[c] = (Lessons[i][c] == null || Lessons[i][c].equals("")) ? "[none]" : Lessons[i][c];
                }

                File lessonsFile = new File(ttFolder, lessonsFileName);

                if (lessonsFile.exists()){
                    String[][] lessonsArray = toArray(context, lessonsFilePath);
                    int Rows = nmbrRowsCols(context, lessonsFilePath)[0];
                    int Cols = nmbrRowsCols(context, lessonsFilePath)[1];
                    System.out.println("Rows: "+Rows+", Cols: "+Cols);
                    System.out.println("Rows - 1, 0: "+lessonsArray[Rows - 1][0]);

                    if (newDataArray.length + 1 > Cols) {
                        System.out.println("newDataArray.length + 1 > Cols");
                        String[][] LessonsArray2 = new String[Rows][newDataArray.length + 1];
                        for (int r1 = 0; r1 < Rows; r1++){
                            for (int c1 = 0; c1 < Cols; c1++){
                                LessonsArray2[r1][c1] = lessonsArray[r1][c1];
                            }
                            for (int c2 = 0; c2 < (newDataArray.length - Cols); c2++){
                                LessonsArray2[r1][Cols + c2] = "[none]";
                            }
                        }
                        lessonsArray = LessonsArray2;
                        Cols = newDataArray.length + 1;
                    }

                    // go through array and find largest lesson id of this subject
                    int maxID = 0;
                    for (int r = 0; r < Rows; r++){
                        String sThisID = lessonsArray[r][0];

                        for (int c = 0; c < Cols; c++)
                            System.out.println("lessonsArray: "+r+", "+c+": "+((lessonsArray[r][c] == null) ? "null!!" : lessonsArray[r][c]));

                        if (sThisID.length() >= 8) {
                            String sTSubjectID = sThisID.substring(0, 4);
                            if (sTSubjectID.equals(subjectID)) {

                                String sTLessonID = sThisID.substring(4, 8);
                                System.out.println("sThisID!!!! = "+sThisID);
                                System.out.println("sTLessonID!!!! = "+sTLessonID);
                                if (isStringNumeric(sTLessonID)) {
                                    int thisID = Integer.parseInt(sTLessonID);
                                    if (thisID > maxID) maxID = thisID;
                                }
                            }
                        }
                    }
                    int idInt = maxID + 1;
                    System.out.println("maxID!!!! = "+maxID);
                    System.out.println("idInt!!!! = "+idInt);
                    String ID = String.valueOf(idInt);
                    final int length = ID.length();
                    for (int s = 0; s < (4 - length); s++){
                        ID = "0" + ID;
                    }
                    ID = subjectID + ID;
                    System.out.println("subjectID!!!! = "+subjectID);
                    System.out.println("ID!!!! = "+ID);

                    String[][] LessonsArray3 = new String[Rows + 1][Cols];
                    for (int r2 = 0; r2 < Rows; r2++){
                        for (int c3 = 0; c3 < Cols; c3++){
                            LessonsArray3[r2][c3] = (lessonsArray[r2][c3] == null || lessonsArray[r2][c3].equals("")) ? "[none]" : lessonsArray[r2][c3];
                        }
                    }

                    LessonsArray3[Rows][0] = ID;
                    for (int c4 = 0; c4 < newDataArray.length; c4++){
                        LessonsArray3[Rows][c4 + 1] = (newDataArray[c4] == null || newDataArray[c4].equals("")) ? "[none]" : newDataArray[c4];
                    }
                    lessonsArray = LessonsArray3;
                    Rows++;

                    lessonID = ID;
                    writeToCSVFile(context, lessonsFile, lessonsArray, Rows, Cols, "New AddLesson");
                } else {
                    String[][] lessonsArray = new String[1][newDataArray.length + 1];

                    lessonID = subjectID + "0001";
                    lessonsArray[0][0] = lessonID;
                    for (int d = 0; d < newDataArray.length; d++)
                        lessonsArray[0][d+1] = (newDataArray[d] == null || newDataArray[d].equals("")) ? "[none]" : newDataArray[d];

                    writeToCSVFile(context, lessonsFile, lessonsArray, 1, newDataArray.length+1, "New AddLesson");
                }

                if (!lessonID.equals("[none]")){
                    String sTime = "[none]";
                    String eTime = "[none]";
                    if (newDataArray[2].equals("true")){
                        sTime = newDataArray[5];
                        eTime = newDataArray[6];
                    } else {
                        String sPeriod = newDataArray[3];
                        String ePeriod = newDataArray[4];

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                        if (isStringNumeric(sPeriod)) sTime = prefs.getString("pref_key_period"+sPeriod+"_start", "[none]");
                        if (isStringNumeric(ePeriod)) eTime = prefs.getString("pref_key_period"+ePeriod+"_end", "[none]");
                    }
                    if (!sTime.equals("[none]") && !eTime.equals("[none]")){
                        // make timetable_attributes entry if necessary
                        String[] sSplit = sTime.split(":");
                        String[] eSplit  = eTime.split(":");
                        if (sSplit.length>=2 && isStringNumeric(sSplit[0]) && isStringNumeric(sSplit[1]) &&
                                eSplit.length>=2 && isStringNumeric(eSplit[0]) && isStringNumeric(eSplit[1])) {

                            String tsmin = String.valueOf((Integer.parseInt(sSplit[0]) *60) + Integer.parseInt(sSplit[1]));
                            String temin = String.valueOf((Integer.parseInt(eSplit[0]) *60) + Integer.parseInt(eSplit[1]));

                            LessonCheckTtAttributes(context, ttFolderName, newDataArray[0], newDataArray[1], tsmin, temin);
                        }

                        // get subject abbrev
                        final String[] subjectInfo = SubjectInfo(context, ttFolderName, subjectID);
                        String subjectName = subjectInfo[0];
                        String subjectAbbrev = subjectInfo[1];
                        String teacherName = subjectInfo[2];
                        String teacherAbbrev = subjectInfo[3];
                        String subjectColor1 = subjectInfo[4];
                        String subjectColor2 = subjectInfo[5];

                        // make timetable_a/b/.. entry
                        if (subjectInfo != null && !subjectAbbrev.equals("[none]")
                                && !subjectColor1.equals("[none]") && !subjectColor2.equals("[none]")) {

                            AddLessonToTimetables(context, ttFolderName, newDataArray[0], lessonID, newDataArray[1], sTime, eTime,
                                    subjectAbbrev, subjectColor1, subjectColor2, newDataArray[7], newDataArray[2]);
                        }
                        // make Notifications entry
                        LessonRegisterNotification(context, ttFolderName, "default", "default", "default", "default",
                                lessonID, newDataArray[0], newDataArray[1], sTime, eTime, newDataArray[2],
                                newDataArray[3], newDataArray[4], newDataArray[7],
                                subjectName, subjectAbbrev, teacherName, teacherAbbrev);
                    }
                }
            }
        }
    }

    public static void RemoveSubjectFromLessonsTxt(Context context, String ttFolderName, String subjectID){
        RemoveSubjectFromLessonsTxt(context, ttFolderName, subjectID, true);
    }

    public static void RemoveSubjectFromLessonsTxt(Context context, String ttFolderName, String subjectID, boolean refreshTtAttr){
        File ttFolder = new File(context.getExternalFilesDir(null), ttFolderName);

        if (ttFolder.isDirectory()) {
            String lessonsFileName = context.getString(R.string.file_name_lessons);
            String lessonsFilePath = ttFolderName + "/" + lessonsFileName;

            String[][] myArray = toArray(context, lessonsFilePath);
            int Rows = nmbrRowsCols(context, lessonsFilePath)[0];
            int Cols = nmbrRowsCols(context, lessonsFilePath)[1];
            File file = new File(context.getExternalFilesDir(null), lessonsFilePath);

            if (file.exists()) {
                int countA = 0;
                String[][] newArray = new String[0][0];
                if (Rows > 0) {
                    for (int is = 0; is < Rows; is++) {
                        String thisSID = myArray[is][0].substring(0, 4);
                        if (!thisSID.equals(subjectID)) {
                            String[][] transitionArray = new String[countA + 1][Cols];
                            for (int r = 0; r < countA; r++) {
                                for (int c = 0; c < Cols; c++) {
                                    transitionArray[r][c] = newArray[r][c];
                                }
                            }
                            for (int c = 0; c < Cols; c++) {
                                transitionArray[countA][c] = myArray[is][c];
                            }
                            newArray = transitionArray;
                            countA++;
                        }
                    }
                }

                myArray = newArray;
                Rows = countA;

                writeToCSVFile(context, file, myArray, Rows, Cols, "deleteSubjectFromLessonsTxt");
                RemoveSubjectFromTimetables(context, ttFolderName, subjectID, refreshTtAttr);
                SubjectDeleteNotifications(context, ttFolderName, subjectID);
            }
        }
    }

    public static void RemoveLessonFromLessonsTxt(Context context, String ttFolderName, String lessonID){
        RemoveLessonFromLessonsTxt(context, ttFolderName, lessonID, true);
    }

    public static void RemoveLessonFromLessonsTxt(Context context, String ttFolderName, String lessonID, boolean refreshTtAttr){
        File ttFolder = new File(context.getExternalFilesDir(null), ttFolderName);

        if (ttFolder.isDirectory()) {
            String lessonsFileName = context.getString(R.string.file_name_lessons);
            String lessonsFilePath = ttFolderName + "/" + lessonsFileName;

            String[][] myArray = toArray(context, lessonsFilePath);
            int Rows = nmbrRowsCols(context, lessonsFilePath)[0];
            int Cols = nmbrRowsCols(context, lessonsFilePath)[1];
            File file = new File(context.getExternalFilesDir(null), lessonsFilePath);

            if (file.exists()) {
                int countA = 0;
                String[][] newArray = new String[0][0];
                if (Rows > 0) {
                    for (int is = 0; is < Rows; is++) {
                        if (!myArray[is][0].equals(lessonID)) {
                            String[][] transitionArray = new String[countA + 1][Cols];
                            for (int r = 0; r < countA; r++) {
                                for (int c = 0; c < Cols; c++) {
                                    transitionArray[r][c] = newArray[r][c];
                                }
                            }
                            for (int c = 0; c < Cols; c++) {
                                transitionArray[countA][c] = myArray[is][c];
                            }
                            newArray = transitionArray;
                            countA++;
                        }
                    }
                }

                myArray = newArray;
                Rows = countA;

                writeToCSVFile(context, file, myArray, Rows, Cols, "RemoveLessonFromLessonsTxt");
                RemoveLessonFromTimetables(context, ttFolderName, lessonID, refreshTtAttr);
                LessonDeleteNotification(context, ttFolderName, lessonID);
            }
        }
    }

    public static void LessonCheckTtAttributes(Context context, String ttFolderName, String lAB, String day, String timeStart, String timeEnd){
        final String ttAttrFileName = ttFolderName+"/"+context.getResources().getString(R.string.file_name_timetable_attributes);
        File ttAttr = new File(context.getExternalFilesDir(null), ttAttrFileName);
        System.out.println("LessonCheckTtAttributes "+day+", "+timeStart+", "+timeEnd);

        if (ttAttr.exists()){
            String[][] attrArray = toArray(context, ttAttrFileName);
            final int[] fileRowsCols = nmbrRowsCols(context, ttAttrFileName);
            int fileRows = fileRowsCols[0];
            int fileCols = fileRowsCols[1];

            String[] ABs = lAB.split("/");
            for (int ab = 0; ab < ABs.length; ab++) {
                String thisAB = ABs[ab];
                boolean foundAB = false;
                String timeDif = "[none]";

                for (int r = 0; r < fileRows; r++) {
                    if (attrArray[r][0].equals(thisAB)) {
                        foundAB = true;

                        // register day if necessary
                        if (isStringNumeric(day)) {
                            int dayThis = Integer.parseInt(day);

                            if (isStringNumeric(attrArray[r][5])) {
                                int firstDay = Integer.parseInt(attrArray[r][5]);

                                // if this day is earlier than the registered first day, register this one as first day
                                if (dayThis < firstDay) {
                                    attrArray[r][5] = day;
                                    attrArray[r][6] = "1";

                                } else if (dayThis == firstDay) {
                                    // if day equals the existing first day, it is refered to by 1 more lesson
                                    if (isStringNumeric(attrArray[r][6])) {
                                        int ref = Integer.parseInt(attrArray[r][6]);
                                        ref++;
                                        attrArray[r][6] = String.valueOf(ref);
                                    } else {
                                        attrArray[r][6] = "1";
                                    }
                                }
                            } else {
                                attrArray[r][5] = day;
                                attrArray[r][6] = "1";
                            }

                            // do the same for the last day of week
                            if (isStringNumeric(attrArray[r][7])) {
                                int lastDay = Integer.parseInt(attrArray[r][7]);

                                // if this day is later than the registered last day, register this one as first day
                                if (dayThis > lastDay) {
                                    attrArray[r][7] = day;
                                    attrArray[r][8] = "1";

                                } else if (dayThis == lastDay) {
                                    // if day equals the existing last day, it is refered to by 1 more lesson
                                    if (isStringNumeric(attrArray[r][8])) {
                                        int ref = Integer.parseInt(attrArray[r][8]);
                                        ref++;
                                        attrArray[r][8] = String.valueOf(ref);
                                    } else {
                                        attrArray[r][8] = "1";
                                    }
                                }
                            } else {
                                attrArray[r][7] = day;
                                attrArray[r][8] = "1";
                            }
                        }

                        // register start time if necessary
                        /*String[] sSplit = timeStart.split(":");
                        String sHour = "[none]";
                        String sMinute = "[none]";
                        if (sSplit.length >= 2){
                            System.out.println("sSplit  >= 2: "+sSplit[0]+", "+sSplit[1]);
                            sHour = sSplit[0];
                            sMinute = sSplit[1];
                        }*/

                        if (isStringNumeric(timeStart)){
                            int time = Integer.parseInt(timeStart);
                            System.out.println("time = "+time);

                            String sAttr = attrArray[r][1];

                            if (isStringNumeric(sAttr)) {
                                int aTime = Integer.parseInt(sAttr);

                                // if this time is earlier than the registered first time, register this one as first time
                                if (time < aTime) {
                                    attrArray[r][1] = String.valueOf(time);
                                    attrArray[r][2] = "1";

                                } else if (time == aTime) {
                                    // if time equals the existing earliest time, it is refered to by 1 more lesson
                                    if (isStringNumeric(attrArray[r][2])) {
                                        int ref = Integer.parseInt(attrArray[r][2]);
                                        ref++;
                                        attrArray[r][2] = String.valueOf(ref);
                                    } else {
                                        attrArray[r][2] = "1";
                                    }
                                }
                            } else {
                                attrArray[r][1] = String.valueOf(time);
                                attrArray[r][2] = "1";
                            }
                        }

                        // register end time if necessary
                        /*String[] eSplit = timeEnd.split(":");
                        String eHour = "[none]";
                        String eMinute = "[none]";
                        if (eSplit.length >= 2){
                            eHour = eSplit[0];
                            eMinute = eSplit[1];
                        }*/

                        if (isStringNumeric(timeEnd)){
                            int time = Integer.parseInt(timeEnd);

                            String eAttr = attrArray[r][3];
                            if (isStringNumeric(eAttr)) {
                                int aTime = Integer.parseInt(eAttr);

                                // if this time is later than the registered latest time, register this one as latest time
                                if (time > aTime) {
                                    attrArray[r][3] = String.valueOf(time);
                                    attrArray[r][4] = "1";

                                } else if (time == aTime) {
                                    // if time equals the existing earliest time, it is refered to by 1 more lesson
                                    if (isStringNumeric(attrArray[r][4])) {
                                        int ref = Integer.parseInt(attrArray[r][4]);
                                        ref++;
                                        attrArray[r][4] = String.valueOf(ref);
                                    } else {
                                        attrArray[r][4] = "1";
                                    }
                                }
                            } else {
                                attrArray[r][3] = String.valueOf(time);
                                attrArray[r][4] = "1";
                            }
                        }

                        // register minimum and maximum time length if necessary
                        if (isStringNumeric(timeStart) && isStringNumeric(timeEnd)){
                            int timeS = Integer.parseInt(timeStart);

                            int timeE = Integer.parseInt(timeEnd);

                            int time = ((timeE - timeS) >= 0) ? (timeE - timeS) : (timeS - timeE);
                            timeDif = String.valueOf(time);

                            String minAttr = attrArray[r][9];
                            System.out.println(timeS+", "+timeE+", "+timeDif+", "+minAttr);

                            if (isStringNumeric(minAttr)) {
                                System.out.println("String is numeric: minAttr");
                                int aTime = Integer.parseInt(minAttr);

                                // if this time is earlier than the registered first time, register this one as first time
                                if (time < aTime || aTime == 0) {
                                    attrArray[r][9] = timeDif;
                                    attrArray[r][10] = "1";

                                } else if (time == aTime) {
                                    // if time equals the existing earliest time, it is refered to by 1 more lesson
                                    if (isStringNumeric(attrArray[r][10])) {
                                        int ref = Integer.parseInt(attrArray[r][10]);
                                        ref++;
                                        attrArray[r][10] = String.valueOf(ref);
                                    } else {
                                        attrArray[r][10] = "1";
                                    }
                                }
                            } else {
                                attrArray[r][9] = timeDif;
                                attrArray[r][10] = "1";
                            }

                            // for max time
                            String maxAttr = attrArray[r][11];

                            if (isStringNumeric(maxAttr)) {
                                int aTime = Integer.parseInt(maxAttr);

                                if (time > aTime || aTime == 0) {
                                    attrArray[r][11] = timeDif;
                                    attrArray[r][12] = "1";

                                } else if (time == aTime) {
                                    if (isStringNumeric(attrArray[r][12])) {
                                        int ref = Integer.parseInt(attrArray[r][12]);
                                        ref++;
                                        attrArray[r][12] = String.valueOf(ref);
                                    } else {
                                        attrArray[r][12] = "1";
                                    }
                                }
                            } else {
                                attrArray[r][11] = timeDif;
                                attrArray[r][12] = "1";
                            }
                        }
                    }
                }
                if (!foundAB) {
                    String[] alphabet = context.getResources().getStringArray(R.array.alphabet);
                    boolean isInAlphabet = false;
                    for (int abc = 0; abc < alphabet.length; abc++) {
                        if (thisAB.equals(alphabet[abc])) isInAlphabet = true;
                    }

                    if (isInAlphabet) {
                        int colsDiff = 0;
                        if (fileCols < 9) colsDiff = 9 - fileCols;
                        String[][] attrArray2 = new String[fileRows + 1][fileCols + colsDiff];

                        for (int rows = 0; rows < fileRows; rows++) {
                            for (int c = 0; c < fileCols; c++) {
                                attrArray2[rows][c] = (attrArray[rows][c] == null || attrArray[rows][c].equals("")) ? "[none]" : attrArray[rows][c];
                            }
                            for (int c = 0; c < colsDiff; c++) {
                                attrArray2[rows][fileCols + c] = "";
                            }
                        }
                        if (isStringNumeric(timeStart) && isStringNumeric(timeEnd)) {
                            int ts = Integer.parseInt(timeStart);
                            int te = Integer.parseInt(timeEnd);
                            timeDif = String.valueOf((te-ts >= 0) ? te-ts : ts-te);
                        }
                        attrArray2[fileRows][0] = thisAB;
                        attrArray2[fileRows][1] = timeStart;
                        attrArray2[fileRows][2] = "1";
                        attrArray2[fileRows][3] = timeEnd;
                        attrArray2[fileRows][4] = "1";
                        attrArray2[fileRows][5] = day;
                        attrArray2[fileRows][6] = "1";
                        attrArray2[fileRows][7] = day;
                        attrArray2[fileRows][8] = "1";
                        attrArray2[fileRows][9] = timeDif;
                        attrArray2[fileRows][10] = "1";
                        attrArray2[fileRows][11] = timeDif;
                        attrArray2[fileRows][12] = "1";

                        attrArray = attrArray2;
                        fileRows++;
                        fileCols = fileCols + colsDiff;
                    }
                }
            }
            writeToCSVFile(context, ttAttr, attrArray, fileRows, fileCols, "new LessonCheckTtAttributes");
        }
    }

    public static void RefreshTtAttributes(Context context, String ttFolderName){
        File topFolder = new File(context.getExternalFilesDir(null), ttFolderName);
        String timetableFilePrefix = context.getResources().getString(R.string.file_name_timetable);

        String attrFilePath = ttFolderName + "/" + context.getResources().getString(R.string.file_name_timetable_attributes);
        File attrFile = new File(context.getExternalFilesDir(null), attrFilePath);

        if (topFolder.isDirectory()){
            // go through all files in tt folder and search for timetable files
            for (File file : topFolder.listFiles()){
                String fName = file.getName();
                if (fName.startsWith(timetableFilePrefix)){
                    String[] split = fName.split("_");
                    if (split.length >= 2 && split[0].equals(context.getResources().getString(R.string.file_name_timetable))
                            && split[1].replace(" ", "").replace(".txt", "").length() == 1){

                        String AB = split[1].replace(" ", "").replace(".txt", "").toUpperCase();
                        String[][] attrArray = toArray(context, attrFilePath);
                        int attrCols = nmbrRowsCols(context, attrFilePath)[1];

                        // clear the attributes for this timetable
                        for (int i = 0; i < attrArray.length; i++){
                            if (attrArray[i][0].equals(AB)) {
                                for (int c = 1; c < attrArray[i].length; c++){
                                    attrArray[i][c] = "[none]";
                                }
                            }
                        }
                        writeToCSVFile(context, attrFile, attrArray, attrArray.length, attrCols, "RefreshTtAttributes");

                        // extract the important data from the timetable file and rebuild the attributes
                        String[][] fileArray = toArray(context, ttFolderName+"/"+fName);
                        for (int r = 0; r < fileArray.length; r++){
                            if (fileArray[r].length >=3) {
                                LessonCheckTtAttributes(context, ttFolderName, AB, fileArray[r][1], fileArray[r][2], fileArray[r][3]);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void AddLessonToTimetables(Context context, String ttFolderName, String lAB, String lessonID,
                                             String day, String timeStart, String timeEnd, String Abbrev,
                                             String color1, String color2, String Room, String custom){

        File topFolder = new File(context.getExternalFilesDir(null), ttFolderName);
        int numberColumns = 9;

        if (topFolder.isDirectory()){
            String[] ABs = lAB.split("/");

            for (String thisAB : ABs) {
                String AB = thisAB.replace(" ","").toLowerCase();
                String ttFileName = ttFolderName + "/" + context.getResources().getString(R.string.file_name_timetable) + "_" + AB + ".txt";

                File ttFile = new File(context.getExternalFilesDir(null), ttFileName);
                String[][] ttArray = {{""}};
                int fileRows = 0;
                int fileCols = 0;
                int lessonRow = -1;

                if (ttFile.exists()){
                    System.out.println("tt_file exists");
                    ttArray = toArray(context, ttFileName);
                    final int[] rc = nmbrRowsCols(context, ttFileName);
                    fileRows = rc[0];
                    fileCols = rc[1];

                    for (int r = 0; r < fileRows; r++){
                        if (ttArray[r][0].substring(0, 8).equals(lessonID)) lessonRow = r;
                    }
                } else {
                    System.out.println("tt_file des not exist");
                    ttArray = new String[1][9];
                    fileRows = 1;
                    fileCols = 9;
                    lessonRow = 0;
                }

                if (lessonRow < 0){
                    System.out.println("lesson row < 0");
                    int cDif = 0;
                    if (fileCols < numberColumns) cDif = numberColumns - fileCols;

                    String[][] ttArray2 = new String[fileRows+1][fileCols + cDif];

                    for (int r = 0; r < fileRows; r++){
                        for (int c = 0; c < fileCols; c++) ttArray2[r][c] = (ttArray[r][c] == null || ttArray[r][c].equals("")) ? "[none]" : ttArray[r][c];
                        for (int cd = 0; cd < cDif; cd++) ttArray2[r][fileCols+cd] = "";
                    }

                    ttArray = ttArray2;
                    lessonRow = fileRows;

                    fileRows++;
                    fileCols = fileCols + cDif;
                }
                System.out.println("tt_file assign "+fileRows+", "+fileCols);
                final String[] tSArray = timeStart.split(":");
                final String[] tEArray = timeEnd.split(":");
                String timeStartMinutes = "0";
                String timeEndMinutes = "0";
                if (tSArray.length>=2 && isStringNumeric(tSArray[0]) && isStringNumeric(tSArray[1]))
                    timeStartMinutes = String.valueOf((Integer.parseInt(tSArray[0]) * 60) + Integer.parseInt(tSArray[1]));

                if (tEArray.length>=2 && isStringNumeric(tEArray[0]) && isStringNumeric(tEArray[1]))
                    timeEndMinutes = String.valueOf((Integer.parseInt(tEArray[0]) * 60) + Integer.parseInt(tEArray[1]));

                ttArray[lessonRow][0] = lessonID;
                ttArray[lessonRow][1] = day;
                ttArray[lessonRow][2] = timeStartMinutes;
                ttArray[lessonRow][3] = timeEndMinutes;
                ttArray[lessonRow][4] = Abbrev;
                ttArray[lessonRow][5] = color1;
                ttArray[lessonRow][6] = color2;
                ttArray[lessonRow][7] = Room;
                ttArray[lessonRow][8] = custom;

                writeToCSVFile(context, ttFile, ttArray, fileRows, fileCols, "new AddLessonToTimetables");
            }
        }
    }

    public static void AddRemoveABs(Context context, String ttFolder, int newNumberABs){
        final String lessons_filepath = ttFolder + "/" + context.getResources().getString(R.string.file_name_lessons);
        final String timetable_file_prefix = ttFolder + "/" + context.getResources().getString(R.string.file_name_timetable);
        final String tt_attributes_filepath = ttFolder + "/" + context.getResources().getString(R.string.file_name_timetable_attributes);
        String[][] lessons_array = toArray(context, lessons_filepath);
        String[][] tt_attr_array = toArray(context, tt_attributes_filepath);
        File lessons_file = new File(context.getExternalFilesDir(null), lessons_filepath);
        File tt_attr_file = new File(context.getExternalFilesDir(null), tt_attributes_filepath);

        final String[] alphabet = context.getResources().getStringArray(R.array.alphabet);
        final String[] oldABs = AllABs(context, ttFolder);

        if (newNumberABs > oldABs.length) {
            String[] addABs = new String[newNumberABs - oldABs.length];
            String newABs = "";
            for (int n = 0; n < addABs.length; n++) {
                addABs[n] = alphabet[oldABs.length + n];
                newABs = newABs+"/"+alphabet[oldABs.length + n];
            }

            // lessons
            for (int l = 0; l < lessons_array.length; l++){
                if (lessons_array[l][1].toLowerCase().contains(alphabet[0].toLowerCase())) {
                    lessons_array[l][1] = lessons_array[l][1]+newABs.toUpperCase();
                    LessonDeleteNotification(context, ttFolder, lessons_array[l][0]);
                    LessonRegisterNotificationLessonArray(context, ttFolder, lessons_array[l]);
                }
            }
            writeToCSVFile(context, lessons_file, lessons_array, lessons_array.length, lessons_array[0].length, "AddRemoveABs - lessons");

            // tt_attributes
            String[][] newTtAttributes = new String[newNumberABs][];
            for (int o = 0; o < oldABs.length; o++){
                String[] row = new String[tt_attr_array[o].length];
                for (int c = 0; c < tt_attr_array[o].length; c++) row[c] = tt_attr_array[o][c];
                newTtAttributes[o] = row;
            }
            for (int n = oldABs.length; n < newNumberABs; n++){
                String[] row = new String[tt_attr_array[0].length];
                row[0] = addABs[n - oldABs.length].toUpperCase();
                for (int c = 1; c < tt_attr_array[0].length; c++) row[c] = tt_attr_array[0][c];
                newTtAttributes[n] = row;
            }
            tt_attr_array = newTtAttributes;
            writeToCSVFile(context, tt_attr_file, tt_attr_array, tt_attr_array.length, tt_attr_array[0].length, "AddRemoveABs - tt_attributes");


            // timetable
            final String tt_a_file_path = timetable_file_prefix+"_a.txt";
            final String[][] timetable_a = toArray(context, tt_a_file_path);
            final int[] tt_a_rc = nmbrRowsCols(context, tt_a_file_path);
            for (int n = 0; n < addABs.length; n++){
                File fNew = new File(context.getExternalFilesDir(null), timetable_file_prefix+"_"+addABs[n].toLowerCase()+".txt");
                writeToCSVFile(context, fNew, timetable_a, tt_a_rc[0], tt_a_rc[1], "AddRemoveABs - tt_file: "+addABs[n]);
            }

        } else if (newNumberABs < oldABs.length) {
            String[] removeABs = new String[oldABs.length - newNumberABs];
            String rABs = "";
            for (int r = newNumberABs; r < oldABs.length; r++) {
                removeABs[r-newNumberABs] = alphabet[r];
                rABs = rABs+alphabet[r];
            }

            // lessons
            for (int l = 0; l < lessons_array.length; l++){
                String[] thisabc = lessons_array[l][1].split("/");
                String newabc = "";
                for (int a = 0; a < thisabc.length; a++) {
                    if (!rABs.toLowerCase().contains(thisabc[a].toLowerCase())){
                        newabc = (newabc.length()>0) ? newabc+"/"+thisabc[a].toUpperCase() : thisabc[a].toUpperCase();
                    }
                }
                lessons_array[l][1] = newabc;
                LessonDeleteNotification(context, ttFolder, lessons_array[l][0]);
                LessonRegisterNotificationLessonArray(context, ttFolder, lessons_array[l]);
            }
            writeToCSVFile(context, lessons_file, lessons_array, lessons_array.length, lessons_array[0].length, "AddRemoveABs - remove lessons");


            // tt_attributes
            String[][] newTtAttributes = new String[newNumberABs][];
            int currrow = 0;
            for (int n = 0; n < tt_attr_array.length; n++){
                if (!rABs.toLowerCase().contains(tt_attr_array[n][0].toLowerCase())) {
                    newTtAttributes[currrow] = tt_attr_array[n];
                    currrow++;
                }
            }
            tt_attr_array = newTtAttributes;
            writeToCSVFile(context, tt_attr_file, tt_attr_array, tt_attr_array.length, tt_attr_array[0].length, "AddRemoveABs - remove tt_attributes");


            // timetable
            for (int n = 0; n < removeABs.length; n++){
                File fNew = new File(context.getExternalFilesDir(null), timetable_file_prefix+"_"+removeABs[n].toLowerCase()+".txt");
                if (fNew.exists()) fNew.delete();
            }
        }
    }

    public static void RemoveSubjectFromTimetables(Context context, String ttFolderName, String subjectID){
        RemoveSubjectFromTimetables(context, ttFolderName, subjectID, true);
    }

    public static void RemoveSubjectFromTimetables(Context context, String ttFolderName, String subjectID, boolean refreshTtAttr){
        File topFolder = new File(context.getExternalFilesDir(null), ttFolderName);

        if (topFolder.isDirectory()){
            for (File file : topFolder.listFiles()){
                String fileName = file.getName();
                String[] split = fileName.split("_");
                if (split.length >= 2 && split[0].equals(context.getResources().getString(R.string.file_name_timetable))){
                    String[][] ttArray = toArray(context, ttFolderName + "/" + fileName);
                    final int[] rc = nmbrRowsCols(context, ttFolderName + "/" + fileName);
                    int Rows = rc[0];
                    int Cols = rc[1];

                    for (int r = 0; r < Rows; r++){
                        if (ttArray[r][0].startsWith(subjectID)){
                             String[][] ttArray2 = new String[Rows - 1][Cols];

                            for (int nr = 0; nr < r; nr++){
                                for (int c = 0; c < Cols; c++) ttArray2[nr][c] = (ttArray[nr][c] == null || ttArray[nr][c].equals("")) ? "[none]" : ttArray[nr][c];
                            }

                            for (int nr2 = r; nr2 < Rows - 1; nr2++){
                                for (int c = 0; c < Cols; c++) ttArray2[nr2][c] = (ttArray[nr2+1][c] == null || ttArray[nr2+1][c].equals("")) ? "[none]" : ttArray[nr2+1][c];
                            }

                            ttArray = ttArray2;
                            Rows = Rows - 1;
                        }
                    }

                    writeToCSVFile(context, file, ttArray, Rows, Cols, "RemoveSubjectFromTimetables");
                    if (refreshTtAttr) RefreshTtAttributes(context, ttFolderName);
                }
            }
        }
    }

    public static void RemoveLessonFromTimetables(Context context, String ttFolderName, String lessonID){
        RemoveLessonFromTimetables(context, ttFolderName, lessonID, true);
    }

    public static void RemoveLessonFromTimetables(Context context, String ttFolderName, String lessonID, boolean refreshTtAttr){
        File topFolder = new File(context.getExternalFilesDir(null), ttFolderName);

        if (topFolder.isDirectory()){
            for (File file : topFolder.listFiles()){
                String fileName = file.getName();
                String[] split = fileName.split("_");
                if (split.length >= 2 && split[0].equals(context.getResources().getString(R.string.file_name_timetable))){
                    String[][] ttArray = toArray(context, ttFolderName + "/" + fileName);
                    final int[] rc = nmbrRowsCols(context, ttFolderName + "/" + fileName);
                    int Rows = rc[0];
                    int Cols = rc[1];

                    for (int r = 0; r < Rows; r++){
                        if (ttArray[r][0].equals(lessonID)){
                            String[][] ttArray2 = new String[Rows - 1][Cols];

                            for (int nr = 0; nr < r; nr++){
                                for (int c = 0; c < Cols; c++) ttArray2[nr][c] = (ttArray[nr][c] == null || ttArray[nr][c].equals("")) ? "[none]" : ttArray[nr][c];
                            }

                            for (int nr2 = r; nr2 < Rows - 1; nr2++){
                                for (int c = 0; c < Cols; c++) ttArray2[nr2][c] = (ttArray[nr2+1][c] == null || ttArray[nr2+1][c].equals("")) ? "[none]" : ttArray[nr2+1][c];
                            }

                            ttArray = ttArray2;
                            Rows = Rows - 1;
                        }
                    }

                    writeToCSVFile(context, file, ttArray, Rows, Cols, "RemoveLessonFromTimetables");
                    if (refreshTtAttr) RefreshTtAttributes(context, ttFolderName);
                }
            }
        }
    }

    public static String[] LessonInfo(Context context, String ttFolderName, String lessonID){
        String lessonsFilePath = ttFolderName+"/"+context.getResources().getString(R.string.file_name_lessons);
        File lessonsFile = new File(context.getExternalFilesDir(null), lessonsFilePath);

        String lAB = "[none]";
        String day = "[none]";
        String custom = "[none]";
        String periodFrom = "[none]";
        String periodTo = "[none]";
        String timeStart = "[none]";
        String timeEnd = "[none]";
        String place = "[none]";

        if (lessonsFile.exists()) {
            final String[][] lessons  = toArray(context, lessonsFilePath);
            for (int i = 0; i < lessons.length; i++){
                if (lessons[i][0].equals(lessonID)){
                    lAB = lessons[i][1];
                    day = lessons[i][2];
                    custom = lessons[i][3];
                    periodFrom = lessons[i][4];
                    periodTo = lessons[i][5];
                    timeStart = lessons[i][6];
                    timeEnd = lessons[i][7];
                    place = lessons[i][8];
                }
            }
        }

        return new String[] {lAB, day, custom, periodFrom, periodTo, timeStart, timeEnd, place};
    }

    public static String[] SubjectInfo(Context context, String ttFolderName, String subjectID){
        String subjectName = "[none]";
        String subjectAbbrev = "[none]";
        String teacherName = "[none]";
        String teacherAbbrev = "[none]";
        String color1 = "[none]";
        String color2 = "[none]";

        String subjectsFilePath = ttFolderName+"/"+context.getResources().getString(R.string.file_name_subjects);
        System.out.println(subjectsFilePath);
        File subjectsFile = new File(context.getExternalFilesDir(null), subjectsFilePath);

        if (subjectsFile.exists()) {
            String[][] subjectsArray = toArray(context, subjectsFilePath);
            final int[] rc = nmbrRowsCols(context, subjectsFilePath);
            int Rows = rc[0];
            int Cols = rc[1];

            if (Cols >= 7) {
                for (int r = 0; r < Rows; r++) {
                    if (subjectsArray[r][0].equals(subjectID)){
                        subjectName = subjectsArray[r][1];
                        subjectAbbrev = subjectsArray[r][2];
                        teacherName = subjectsArray[r][3];
                        teacherAbbrev = subjectsArray[r][4];
                        color1 = subjectsArray[r][5];
                        color2 = subjectsArray[r][6];
                    }
                }
            }
        }

        String[] subjectInfo = {subjectName, subjectAbbrev, teacherName, teacherAbbrev, color1, color2};
        return subjectInfo;
    }

    public static String[][] SubjectLessons(Context context, String ttFolderName, String subjectID){
        String lessonsFilePath = ttFolderName+"/"+context.getResources().getString(R.string.file_name_lessons);
        File lessonsFile = new File(context.getExternalFilesDir(null), lessonsFilePath);

        String[][] lessons = new String[][] {{"[none]"}};

        if (lessonsFile.exists()) {
            String[][] lessonsArray = toArray(context, lessonsFilePath);
            final int[] rc = nmbrRowsCols(context, lessonsFilePath);
            int Rows = rc[0];
            int Cols = rc[1];

            ArrayList<String[]> subjectLessons = new ArrayList<>();
            for (int i = 0; i < Rows; i++){
                if (lessonsArray[i][0].length() >= 4 && lessonsArray[i][0].substring(0, 4).equals(subjectID)) {
                    subjectLessons.add(lessonsArray[i]);
                }
            }

            String[][] lArray2 = new String[subjectLessons.size()][];
            for (int i = 0; i < subjectLessons.size(); i++) {
                String[] row = subjectLessons.get(i);
                lArray2[i] = row;
            }

            lessons = lArray2;
        }

        return lessons;
    }

    public static String[] TimetableAttributes(Context context, String ttFolderName, String AB){
        String attributesFilePath = ttFolderName+"/"+context.getResources().getString(R.string.file_name_timetable_attributes);
        File attributesFile = new File(context.getExternalFilesDir(null), attributesFilePath);

        String[] attributes = new String[] {"[none]"};

        if (attributesFile.exists()) {
            String[][] attrArray = toArray(context, attributesFilePath);

            for (int r = 0; r < attrArray.length; r++){
                if (attrArray[r][0].equals(AB)){
                    attributes = attrArray[r];
                }
            }
        }

        return attributes;
    }

    public static String[] AllABs(Context context, String ttFolderName){
        String attributesFilePath = ttFolderName+"/"+context.getResources().getString(R.string.file_name_timetable_attributes);
        File attributesFile = new File(context.getExternalFilesDir(null), attributesFilePath);

        String[] ABs = new String[] {"[none]"};

        if (attributesFile.exists()) {
            String[][] attrArray = toArray(context, attributesFilePath);
            ArrayList<String> newArray = new ArrayList<>();
            final String[] alphabet = context.getResources().getStringArray(R.array.alphabet);

            for (int r = 0; r < attrArray.length; r++){
                for (int abc = 0; abc < alphabet.length; abc++) {
                    if (attrArray[r][0].equals(alphabet[abc])) {
                        newArray.add(attrArray[r][0]);
                        break;
                    }
                }
            }

            ABs = new String[newArray.size()];
            for (int s = 0; s < newArray.size(); s++) ABs[s] = newArray.get(s);
        }

        return ABs;
    }

    public static String[][] TimetableLessons(Context context, String ttFolderName, String AB){
        String timetableFilePath = ttFolderName+"/"+context.getResources().getString(R.string.file_name_timetable)+"_"+AB+".txt";
        File timetableFile = new File(context.getExternalFilesDir(null), timetableFilePath);

        String[][] timetable = new String[][] {{"[none]"}};

        if (timetableFile.exists()) {
            timetable = toArray(context, timetableFilePath);
        }

        return timetable;
    }

    // Register lesson in lesson_notifications.txt
    public static void LessonRegisterNotificationLessonArray(Context context, String ttFolder, String[] lesson){
        String sTime = "[none]";
        String eTime = "[none]";
        if (lesson[3].equals("true")){
            sTime = lesson[6];
            eTime = lesson[7];
        } else {
            String sPeriod = lesson[4];
            String ePeriod = lesson[5];

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (isStringNumeric(sPeriod)) sTime = prefs.getString("pref_key_period"+sPeriod+"_start", "[none]");
            if (isStringNumeric(ePeriod)) eTime = prefs.getString("pref_key_period"+ePeriod+"_end", "[none]");
        }

        if (!sTime.equals("[none]") && !eTime.equals("[none]")) {
            final String lessonID = lesson[0];
            final String AB = lesson[1];
            final String day = lesson[2];
            final String timeStart = sTime;
            final String timeEnd = eTime;
            final String customTime = lesson[3];
            final String periodFrom = lesson[4];
            final String periodTo = lesson[5];
            final String place = lesson[8];

            LessonRegisterNotificationDefault(context, ttFolder, lessonID, AB, day, timeStart, timeEnd,
                    customTime, periodFrom, periodTo, place);
        }
    }

    public static void LessonRegisterNotificationDefault(Context context, String ttFolder, String lessonID, String AB,
                                                         String day, String timeStart, String timeEnd, String customTime,
                                                         String periodFrom, String periodTo, String place){
        if (lessonID.length() >= 4){
            final String[] subjectInfo = SubjectInfo(context, ttFolder, lessonID.substring(0,4));
            final String subjectName = subjectInfo[0];
            final String subjectAbbrev = subjectInfo[1];
            final String teacherName = subjectInfo[2];
            final String teacherAbbrev = subjectInfo[3];
            LessonRegisterNotification(context, ttFolder, "default", "default", "default", "default",
                    lessonID, AB, day, timeStart, timeEnd, customTime, periodFrom, periodTo, place,
                    subjectName, subjectAbbrev, teacherName, teacherAbbrev);
        }
    }

    public static void LessonRegisterNotification(Context context, String ttFolder, String notifyPref,
                                                  String notifybeforePref, String notifyWhenChangingRoomsPref,
                                                  String waitForPreviousPref, String lessonID, String AB,
                                                  String day, String timeStart, String timeEnd,
                                                  String customTime, String periodFrom, String periodTo, String place,
                                                  String subjectName, String subjectAbbrev, String teacherName, String teacherAbbrev){
        final int fileCols = 14;

        final String[] tSArray = timeStart.split(":");
        final String[] tEArray = timeEnd.split(":");
        int timeStartMinutes = 0;
        int timeEndMinutes = 0;
        boolean foundTimeStart = false;
        boolean foundTimeEnd = false;
        if (tSArray.length>=2 && isStringNumeric(tSArray[0]) && isStringNumeric(tSArray[1])) {
            timeStartMinutes = (Integer.parseInt(tSArray[0]) * 60) + Integer.parseInt(tSArray[1]);
            foundTimeStart = true;
        }

        if (tEArray.length>=2 && isStringNumeric(tEArray[0]) && isStringNumeric(tEArray[1])) {
            timeEndMinutes = (Integer.parseInt(tEArray[0]) * 60) + Integer.parseInt(tEArray[1]);
            foundTimeEnd = true;
        }


        if(isStringNumeric(day) && foundTimeStart && foundTimeEnd) {
            final String filepath = ttFolder + "/" + context.getResources().getString(R.string.file_name_lesson_notifications);

            File file = new File(context.getExternalFilesDir(null), filepath);
            String[][] myArray = toArray(context, filepath);
            final int[] rc = nmbrRowsCols(context, filepath);
            final int Rows = rc[0];
            final int Cols = rc[1];

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            String notify, textName, textTime, textPlace, textTeacher;
            int notifTime, lessonSMinutes, lessonEMinutes;

            if (notifyPref.equals("true") || notifyPref.equals("false")) {
                notify = notifyPref;
            } else {
                if (prefs.getBoolean(context.getResources().getString(R.string.notifications_pref_never_notify), false)) {
                    notify = "false";
                } else {
                    notify = "true";
                }
            }

            final String[] allABs = AllABs(context, ttFolder);
            final String[] thisABs = AB.split("/");
            for (int abc = 0; abc < thisABs.length; abc++) {
                int ab = 0;
                for (int a = 0; a < allABs.length; a++) {
                    if (allABs[a].toLowerCase().equals(thisABs[abc].toLowerCase())) ab = a;
                }

                final int d = Integer.parseInt(day);
                lessonSMinutes = ab * (7 * 24 * 60) + d * (24 * 60) + timeStartMinutes;
                lessonEMinutes = ab * (7 * 24 * 60) + d * (24 * 60) + timeEndMinutes;

                int before = 0;
                if (isStringNumeric(notifybeforePref)) {
                    before = Integer.parseInt(notifybeforePref);
                } else {
                    before = prefs.getInt(context.getResources().getString(R.string.notifications_pref_time_before), 0);
                }
                notifTime = lessonSMinutes - before;

                textName = subjectName;
                if (!subjectAbbrev.replace("[none]", "").replace("[null]", "").equals(""))
                    textName = textName + " (" + subjectAbbrev + ")";

                timeStart = formatTime(timeStart);
                timeEnd = formatTime(timeEnd);
                textTime = timeStart + "-" + timeEnd;
                if (customTime.equals("false")) textTime = textTime + " (" +
                        context.getResources().getString(R.string.period)+" "+periodFrom+" - "+periodTo+")";

                textPlace = place;
                textTeacher = teacherName;
                if (!teacherAbbrev.replace("[none]", "").replace("[null]", "").equals(""))
                    textTeacher = textTeacher + teacherAbbrev;

                // find row to put this lesson in
                int row = -1;
                int checkRow = 0;
                while (checkRow < Rows && row < 0) {
                    if (myArray[checkRow].length >= 2 && isStringNumeric(myArray[checkRow][1]) && notifTime < Integer.parseInt(myArray[checkRow][1]))
                        row = checkRow;

                    checkRow++;
                }

                String[][] newArray = new String[Rows + 1][fileCols];
                if (row <= 0) row = Rows;
                for (int r = 0; r < row; r++) {
                    for (int c = 0; c < myArray[r].length; c++) newArray[r][c] = myArray[r][c];
                    for (int c = myArray.length; c < fileCols; c++) newArray[r][c] = "[none]";
                }
                for (int r = row; r < Rows; r++) {
                    for (int c = 0; c < myArray[r].length; c++) newArray[r + 1][c] = myArray[r][c];
                    for (int c = myArray.length; c < fileCols; c++) newArray[r + 1][c] = "[none]";
                }

                // handle Wait for previous option
                boolean wait = false;
                if (waitForPreviousPref.equals("true") || waitForPreviousPref.equals("false")) {
                    wait = Boolean.valueOf(waitForPreviousPref);
                } else {
                    wait = prefs.getBoolean(context.getString(R.string.notifications_pref_wait_for_previous), false);
                }
                if (wait && row > 0 && isStringNumeric(newArray[row - 1][13]) && Integer.valueOf(newArray[row - 1][13]) > notifTime)
                    notifTime = Integer.valueOf(newArray[row - 1][13]);

                newArray[row][0] = notify;
                newArray[row][1] = String.valueOf(notifTime);
                newArray[row][2] = lessonID;
                newArray[row][3] = notifyPref;
                newArray[row][4] = notifybeforePref;
                newArray[row][5] = notifyWhenChangingRoomsPref;
                newArray[row][6] = waitForPreviousPref;
                newArray[row][7] = customTime;
                newArray[row][8] = textName;
                newArray[row][9] = textTime;
                newArray[row][10] = textPlace;
                newArray[row][11] = textTeacher;
                newArray[row][12] = String.valueOf(lessonSMinutes);
                newArray[row][13] = String.valueOf(lessonEMinutes);

                myArray = newArray;
            }


            writeToCSVFile(context, file, myArray, myArray.length, fileCols, "LessonRegisterNotification");
            context.startService(new Intent(context, NotificationService.class));
        }
    }

    public static void LessonDeleteNotification(Context context, String ttFolder, String lessonID){
        final int fileCols = 14;
        final String filepath = ttFolder+"/"+context.getResources().getString(R.string.file_name_lesson_notifications);

        File file = new File(context.getExternalFilesDir(null), filepath);
        String[][] myArray = toArray(context, filepath);
        final int[] rc = nmbrRowsCols(context, filepath);
        final int Rows = rc[0];
        final int Cols = rc[1];

        for (int r = 0; r < Rows; r++){
            if (myArray[r].length >= 3 && myArray[r][2].equals(lessonID)){
                String[][] newArray = new String[Rows-1][fileCols];
                for (int n = 0; n < r; n++){
                    for (int c = 0; c < myArray[n].length; c++) newArray[n][c] = myArray[n][c];
                    for (int c = myArray[n].length; c < fileCols; c++) newArray[n][c] = "";
                }
                for (int n = r; n < Rows - 1; n++){
                    for (int c = 0; c < myArray[n].length; c++) newArray[n][c] = myArray[n+1][c];
                    for (int c = myArray[n].length; c < fileCols; c++) newArray[n][c] = "";
                }

                writeToCSVFile(context, file, myArray, Rows, Cols, "LessonDeleteNotification");
                context.startService(new Intent(context, NotificationService.class));
            }
        }
    }

    public static void SubjectDeleteNotifications(Context context, String ttFolder, String subjectID){
        final int fileCols = 14;
        final String filepath = ttFolder+"/"+context.getResources().getString(R.string.file_name_lesson_notifications);

        File file = new File(context.getExternalFilesDir(null), filepath);
        String[][] myArray = toArray(context, filepath);
        final int[] rc = nmbrRowsCols(context, filepath);
        final int Rows = rc[0];
        final int Cols = rc[1];

        for (int r = 0; r < Rows; r++){
            if (myArray[r].length >= 3 && myArray[r][2].substring(0,4).equals(subjectID)){
                String[][] newArray = new String[Rows-1][fileCols];
                for (int n = 0; n < r; n++){
                    for (int c = 0; c < myArray[n].length; c++) newArray[n][c] = myArray[n][c];
                    for (int c = myArray[n].length; c < fileCols; c++) newArray[n][c] = "";
                }
                for (int n = r; n < Rows - 1; n++){
                    for (int c = 0; c < myArray[n].length; c++) newArray[n][c] = myArray[n+1][c];
                    for (int c = myArray[n].length; c < fileCols; c++) newArray[n][c] = "";
                }

                writeToCSVFile(context, file, myArray, Rows, Cols, "SubjetDeleteNotifications");
                context.startService(new Intent(context, NotificationService.class));
            }
        }
    }

    public static void NotificationsPrefChange(Context context, String ttFolder, boolean notify,
                                               int beforeTime, boolean oWChangeRooms, boolean waitFPrev){
        final int fileCols = 14;
        final String filepath = ttFolder+"/"+context.getResources().getString(R.string.file_name_lesson_notifications);

        File file = new File(context.getExternalFilesDir(null), filepath);
        String[][] myArray = toArray(context, filepath);
        final int[] rc = nmbrRowsCols(context, filepath);
        final int Rows = rc[0];
        final int Cols = rc[1];

        for (int r = 0; r < Rows; r++){
            if (myArray[r].length >= 7){
                if(!myArray[r][3].equals("true") && !myArray[r][3].equals("false")) myArray[r][0] = String.valueOf(notify);

                if(!myArray[r][4].equals("true") && !myArray[r][4].equals("false") && isStringNumeric(myArray[r][12])){
                    int lessonStart = Integer.parseInt(myArray[r][12]);
                    myArray[r][1] = String.valueOf(lessonStart - beforeTime);
                }

                if(!myArray[r][5].equals("true") && !myArray[r][5].equals("false")) myArray[r][5] = String.valueOf(oWChangeRooms);

                if(!myArray[r][6].equals("true") && !myArray[r][6].equals("false") && isStringNumeric(myArray[r][1])){
                    int notifTime = Integer.parseInt(myArray[r][1]);
                    if (waitFPrev && r > 0 && isStringNumeric(myArray[r-1][13]) && Integer.parseInt(myArray[r-1][13]) > notifTime){
                        myArray[r][1] = myArray[r-1][13];
                    }
                }
            }
        }

        writeToCSVFile(context, file, myArray, Rows, Cols, "NotificationsPrefChange");
        context.startService(new Intent(context, NotificationService.class));
    }

    public static void PeriodTimingChange(Context context, String ttFolder){
        System.out.println("PeriodTimingChange Called!!");
        final int numberPeriods = 12;
        final int fileCols = 14;
        final String filepath = ttFolder+"/"+context.getResources().getString(R.string.file_name_lessons);

        File file = new File(context.getExternalFilesDir(null), filepath);
        String[][] myArray = toArray(context, filepath);
        final int[] rc = nmbrRowsCols(context, filepath);
        final int Rows = rc[0];
        final int Cols = rc[1];

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        ArrayList<String> pStartTimes = new ArrayList<>();
        ArrayList<String> pEndTimes = new ArrayList<>();
        for (int p = 1; p <= numberPeriods; p++){
            pStartTimes.add(prefs.getString("pref_key_period"+p+"_start", "08:00"));
            pEndTimes.add(prefs.getString("pref_key_period"+p+"_end", "09:00"));
        }

        for (int r = 0; r < Rows; r++){
            if (myArray[r].length >= 9){
                if (!myArray[r][3].equals("true")){
                    RemoveLessonFromTimetables(context, ttFolder, myArray[r][0], false);

                    if (!myArray[r][0].equals("[none]")) {
                        String sTime = "[none]";
                        String eTime = "[none]";
                        if (!myArray[r][3].equals("true")) {
                            String sPeriod = myArray[r][4];
                            String ePeriod = myArray[r][5];

                            if (isStringNumeric(sPeriod) && isStringNumeric(ePeriod)) {
                                int p = Integer.parseInt(sPeriod);
                                if (p > 0 && p <= pStartTimes.size())
                                    sTime = pStartTimes.get(p - 1);

                                int pe = Integer.parseInt(ePeriod);
                                if (pe > 0 && pe <= pEndTimes.size()) eTime = pEndTimes.get(pe - 1);

                                // make timetable_attributes entry if necessary
                                // get subject abbrev
                                String subjectID = myArray[r][0].substring(0, 4);
                                final String[] subjectInfo = SubjectInfo(context, ttFolder, subjectID);
                                String subjectName = subjectInfo[0];
                                String subjectAbbrev = subjectInfo[1];
                                String teacherName = subjectInfo[2];
                                String teacherAbbrev = subjectInfo[3];
                                String subjectColor1 = subjectInfo[4];
                                String subjectColor2 = subjectInfo[5];

                                // make timetable_a/b/.. entry
                                if (subjectInfo != null && !subjectAbbrev.equals("[none]")
                                        && !subjectColor1.equals("[none]") && !subjectColor2.equals("[none]")) {

                                    AddLessonToTimetables(context, ttFolder, myArray[r][1], myArray[r][0], myArray[r][2], sTime, eTime,
                                            subjectAbbrev, subjectColor1, subjectColor2, myArray[r][8], myArray[r][3]);

                                    final String lessonID = myArray[r][0];
                                    final String AB = myArray[r][1];
                                    final String day = myArray[r][2];
                                    final String custom = myArray[r][3];
                                    final String periodF = myArray[r][4];
                                    final String periodT = myArray[r][5];
                                    final String place = myArray[r][8];
                                    // TODO check if actually everything really is "default"
                                    LessonRegisterNotification(context, ttFolder, "default", "default", "default", "default",
                                            lessonID, AB, day, sTime, eTime, custom,
                                            periodF, periodT, place,
                                            subjectName, subjectAbbrev, teacherName, teacherAbbrev);
                                }
                            }
                        }
                    }
                }
            }
        }

        writeToCSVFile(context, file, myArray, Rows, Cols, "NotificationsPrefChange");
        RefreshTtAttributes(context, ttFolder);
        context.startService(new Intent(context, NotificationService.class));
    }

    public static int getCurrentAB(Context context, String ttFolder, Calendar cal){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();
		/*if (!prefs.contains("first_day_of_week")){
			prefs.edit().putInt("first_day_of_week", c.getFirstDayOfWeek()).apply();
		}
		int firstDayOfWeek = prefs.getInt("first_day_of_week", c.getFirstDayOfWeek());*/

        String sWeekARef = "[none]";
        if (prefs.contains("week_a_reference")){
            sWeekARef = prefs.getString("week_a_reference", "[none]");
        }
        if (sWeekARef.equals("[none]")){
            String newRef = DataStorageHandler.formatDateGeneralFormat(context, Calendar.getInstance());
            sWeekARef = newRef;
            prefs.edit().putString("week_a_reference", newRef);
        }

        Calendar calRef = new GregorianCalendar();
        calRef.setFirstDayOfWeek(firstDayOfWeek);
        calRef.setTime(DataStorageHandler.getDateFromGeneralDateFormat(context, sWeekARef));
        calRef.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        Date dRef = calRef.getTime();

        cal.setFirstDayOfWeek(firstDayOfWeek);
        cal.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        Date dNow = cal.getTime();

        int weekDifference = DataStorageHandler.getWeeksBetween(dRef, dNow);

        final String[] allABs = AllABs(context, ttFolder);
        String currentAB;
        int cABint;
        if (weekDifference >= 0){
            cABint = weekDifference % allABs.length;
            currentAB = allABs[cABint];
        } else {
            cABint = allABs.length + (weekDifference % allABs.length);
            currentAB = allABs[cABint];
        }

        return cABint;
    }

    public static String[][] LessonHomework(Context context, String ttFolder, String lessonID){
        final String homeworkFilePath = ttFolder + "/" + context.getResources().getString(R.string.file_name_homework);
        final String[][] myArray = toArray(context, homeworkFilePath);

        ArrayList<String[]> hList = new ArrayList<>();

        for (int r = 0; r < myArray.length; r++){
            if (myArray[r][6].equals(lessonID)){
                final String homeworkID = myArray[r][0];
                final String date = myArray[r][2];
                final String hTitle = myArray[r][3];
                final String hContent = myArray[r][4];
                final String Done = myArray[r][5];

                hList.add(new String[] {homeworkID, date, hTitle, hContent, Done});
            }
        }

        String[][] lessonHomework = new String[hList.size()][];
        for (int i = 0; i < hList.size(); i++){
            lessonHomework[i] = hList.get(i);
        }

        return lessonHomework;
    }

    public static String[][] SubjectHomework(Context context, String ttFolder, String subjectID){
        final String homeworkFilePath = ttFolder + "/" + context.getResources().getString(R.string.file_name_homework);
        final String[][] myArray = toArray(context, homeworkFilePath);

        ArrayList<String[]> hList = new ArrayList<>();

        for (int r = 0; r < myArray.length; r++){
            if (myArray[r][1].equals(subjectID)){
                final String homeworkID = myArray[r][0];
                final String date = myArray[r][2];
                final String hTitle = myArray[r][3];
                final String hContent = myArray[r][4];
                final String Done = myArray[r][5];
                final String lessonID = myArray[r][6];

                hList.add(new String[] {homeworkID, date, hTitle, hContent, Done, lessonID});
            }
        }

        String[][] lessonHomework = new String[hList.size()][];
        for (int i = 0; i < hList.size(); i++){
            lessonHomework[i] = hList.get(i);
        }

        return lessonHomework;
    }

    public static void createHomework(Context context, String ttFolder, String subject, String date, String hTitle, String hContent, String lessonID){
        final String homeworkFilepath = ttFolder + "/" + context.getResources().getString(R.string.file_name_homework);
        String[][] myArray = DataStorageHandler.toArray(context, homeworkFilepath);
        int Rows = nmbrRowsCols(context, homeworkFilepath)[0];
        int Cols = 7;

        File file = new File(context.getExternalFilesDir(null), homeworkFilepath);

        // get the new ID
        int maxID = -1;
        for (int i1 = 0; i1 < Rows; i1++){
            if (isStringNumeric(myArray[i1][0])) {
                int thisID = Integer.parseInt(myArray[i1][0]);
                if (thisID > maxID) maxID = thisID;
            }
        }
        int ID = maxID + 1;

        // Save data to myArray
        String[][] myArray2 = new String[Rows + 1][Cols];
        for (int r = 0; r < Rows; r++) {
            for (int c = 0; c < myArray[r].length; c++) myArray2[r][c] = myArray[r][c];
            for (int c = myArray[r].length; c < Cols; c++) myArray2[r][c] = "[none]";
        }
        myArray2[Rows][0] = String.valueOf(ID);
        myArray2[Rows][1] = subject;
        myArray2[Rows][2] = date;
        myArray2[Rows][3] = hTitle;
        myArray2[Rows][4] = hContent;
        myArray2[Rows][5] = "no";
        myArray2[Rows][6] = lessonID;
        myArray = myArray2;
        Rows++;

        DataStorageHandler.writeToCSVFile(context, file, myArray, Rows, Cols, "createHomework");
    }

    public static void updateHomework(Context context, String ttFolder, String ID, String subject, String date, String hTitle, String hContent, String done, String lessonID){
        final String homeworkFilepath = ttFolder + "/" + context.getResources().getString(R.string.file_name_homework);
        String[][] myArray = DataStorageHandler.toArray(context, homeworkFilepath);
        int Rows = nmbrRowsCols(context, homeworkFilepath)[0];
        int Cols = 7;

        File file = new File(context.getExternalFilesDir(null), homeworkFilepath);


        // Save data to myArray
        for (int r = 0; r < Rows; r++) {
            if (myArray[r][0].equals(ID)){
                myArray[r][1] = subject;
                myArray[r][2] = date;
                myArray[r][3] = hTitle;
                myArray[r][4] = hContent;
                myArray[r][5] = done;
                myArray[r][6] = lessonID;
            } else {
                for (int c = myArray[r].length; c < Cols; c++) myArray[r][c] = "[none]";
            }
        }

        DataStorageHandler.writeToCSVFile(context, file, myArray, Rows, Cols, "updateHomework");
    }

    public static String[] deleteHomeworkGetBackupArray(Context context, String ttFolder, String ID){
        final String homeworkFilepath = ttFolder + "/" + context.getResources().getString(R.string.file_name_homework);
        String[][] myArray = DataStorageHandler.toArray(context, homeworkFilepath);
        int Rows = nmbrRowsCols(context, homeworkFilepath)[0];
        int Cols = 7;

        File file = new File(context.getExternalFilesDir(null), homeworkFilepath);

        //Generate temporary backup array
        String[] backupArray = new String[Cols];

        //Remove data from myArray
        int removableRow = -1;
        for (int r = 0; r < Rows; r++) {
            if (myArray[r][0].equals(ID)){
                removableRow = r;
                for (int c = 0; c < Cols; c++) backupArray[c] = myArray[r][c];
            }
        }
        if (removableRow >= 0) {
            Rows = Rows - 1;
            String[][] newArray = new String[myArray.length - 1][Cols];
            for (int i1 = 0; i1 < removableRow; i1++){
                for (int c = 0; c < myArray[i1].length; c++) newArray[i1][c] = myArray[i1][c];
                for (int c = myArray[i1].length; c < Rows; c++) newArray[i1][c] = "[none]";
            }
            for (int i2 = removableRow + 1; i2 < myArray.length; i2++){
                for (int c = 0; c < myArray[i2].length; c++) newArray[i2-1][c] = myArray[i2][c];
                for (int c = myArray[i2].length; c < Rows; c++) newArray[i2-1][c] = "[none]";
            }
            myArray = newArray;
        }

        DataStorageHandler.writeToCSVFile(context, file, myArray, Rows, Cols, "deleteHomework");

        final Context fContext = context;
        final String fID = backupArray[0];
        final String fSubject = backupArray[1];
        final String fDate = backupArray[2];
        final String fTitle = backupArray[3];
        final String fContent = backupArray[4];
        final String fDone = backupArray[5];
        final String fLessonID = backupArray[6];

        return new String[] {fID, fSubject, fDate, fTitle, fContent, fDone, fLessonID};
    }

    public static void updateStorageMethod20160913(Context context) {
        final String[] alphabet = context.getResources().getStringArray(R.array.alphabet);
        final String ttFolder = setUpNewTimetableReturnTtFolderPath(context, 1);

        final String[][] subjects_old = toArray(context, "Subjects.txt");

        for (int s = 0; s < subjects_old.length; s++){
            if (subjects_old[s].length >= 26 && subjects_old[s][0] != null &&
                    !subjects_old[s][0].replace(" ", "").replace("[none]", "").equals("")){

                final String subjectName = subjects_old[s][0];
                final String subjectAbbrev = subjects_old[s][1];
                final String teacherName = subjects_old[s][2];
                final String teacherAbbrev = subjects_old[s][3];
                final String day1 = subjects_old[s][4];
                final String day2 = subjects_old[s][5];
                final String day3 = subjects_old[s][6];
                final String day4 = subjects_old[s][7];
                final String day5 = subjects_old[s][8];
                final String period11 = subjects_old[s][9];
                final String period21 = subjects_old[s][10];
                final String period31 = subjects_old[s][11];
                final String period41 = subjects_old[s][12];
                final String period51 = subjects_old[s][13];
                final String period12 = subjects_old[s][14];
                final String period22 = subjects_old[s][15];
                final String period32 = subjects_old[s][16];
                final String period42 = subjects_old[s][17];
                final String period52 = subjects_old[s][18];
                final String room1 = subjects_old[s][19];
                final String room2 = subjects_old[s][20];
                final String room3 = subjects_old[s][21];
                final String room4 = subjects_old[s][22];
                final String room5 = subjects_old[s][23];
                final String color = (subjects_old[s][24].equals("ornge")) ? "orange" : subjects_old[s][24];
                final String textColor = (subjects_old[s][25].equals("ornge") ? "orange" : subjects_old[s][25]);

                final String[] days = new String[] {day1, day2, day3, day4, day5};
                final String[] periodsF = new String[] {period11, period21, period31, period41, period51};
                final String[] periodsT = new String[] {period12, period22, period32, period42, period52};
                final String[] rooms = new String[] {room1, room2, room3, room4, room5};

                final String A = alphabet[0].toUpperCase();
                final String custom = "false";
                final String none = "[none]";

                ArrayList<String[]> lList = new ArrayList<>();
                for (int i = 0; i < 5; i++){
                    if (!days[i].contains("-")&&isStringNumeric(days[i]) && !periodsF[i].contains("-")&&isStringNumeric(periodsF[i])
                            && !periodsT[i].contains("-")&&isStringNumeric(periodsT[i])){
                        lList.add(new String[] {A, days[i], custom, periodsF[i], periodsT[i], none, none, rooms[i]});
                    }
                }

                String[][] lessons = new String[lList.size()][];
                for (int l = 0; l < lList.size(); l++){
                    lessons[l] = lList.get(l);
                }


                RegisterSubject(context, ttFolder, subjectName, subjectAbbrev, teacherName, teacherAbbrev, color, textColor, lessons);
            }
        }
    }

    public static String setUpNewTimetableReturnTtFolderPath(Context context, int numbrAB) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String[] alphabet = context.getResources().getStringArray(R.array.alphabet);

        int maxVal = 0;
        String topFolder = context.getResources().getString(R.string.folder_name_timetables);
        File top = new File(context.getExternalFilesDir(null), topFolder);
        if (top.isDirectory()) {
            System.out.println("top.isDirectory");
            for (File file : top.listFiles()) {
                System.out.println("file");
                if (file.isDirectory()) {
                    String name = file.getName();
                    System.out.println("file.isDirectory: "+name);
                    String[] nSplit = name.split("_");
                    if (nSplit.length >= 2) {
                        final String sNumber = nSplit[1];
                        System.out.println("sNumber = "+sNumber);
                        if (DataStorageHandler.isStringNumeric(sNumber)) {
                            int thisValue = 0;
                            try {
                                thisValue = Integer.parseInt(sNumber);
                            } catch (NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }

                            if (thisValue > maxVal) maxVal = thisValue;
                        }
                    }
                }
            }
        }

        Calendar now = Calendar.getInstance();
        String date = formatDateGeneralFormat(context, now);

        String ttid = String.valueOf(maxVal+1);
        while (ttid.length() < 4){
            ttid = "0"+ttid;
        }
        final String folder = topFolder + "/" + context.getResources().getString(R.string.folder_name_timetable)
                + "_" + ttid + "_" + date;
        File ttFolder = new File(context.getExternalFilesDir(null), folder);
        ttFolder.mkdirs();

        String filename = context.getResources().getString(R.string.file_name_timetable_attributes);

        File attrFile = new File(ttFolder, filename);
        int fileRows = numbrAB;
        int fileCols = 13;
        String[][] ttAtr = new String[fileRows][fileCols];

        for (int f = 0; f < fileRows; f++){
            ttAtr[f][0] = alphabet[f];
            ttAtr[f][1] = "[none]";
            ttAtr[f][2] = "[none]";
            ttAtr[f][3] = "[none]";
            ttAtr[f][4] = "[none]";
            ttAtr[f][5] = "[none]";
            ttAtr[f][6] = "[none]";
            ttAtr[f][7] = "[none]";
            ttAtr[f][8] = "[none]";
            ttAtr[f][9] = "[none]";
            ttAtr[f][10] = "[none]";
            ttAtr[f][11] = "[none]";
            ttAtr[f][12] = "[none]";
        }

        writeToCSVFile(context, attrFile, ttAtr, fileRows, fileCols, "NewTimetableFragment");
        prefs.edit().putString(context.getResources().getString(R.string.pref_key_current_timetable_filename), folder).apply();
        return folder;
    }


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
                    if (myArray[t][c] == null) {
                        dataCorrect = false;
                        Log.e("Null error", "This value == null: " + caller + ". Row: " + t + ", Col: " + c);
                    }
                }

                if (dataCorrect){
                    String line = myArray[t][0];
                    for (int c1 = 1; c1 < Cols; c1++){
                        line = line + "," + myArray[t][c1];
                    }
                    System.out.println("write to "+file.getName()+" by "+caller+": "+line);
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

    public static Date getDateFromGeneralDateFormat(Context context, String dateString){
        SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.date_formatter_general));

        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String formatDateLocalFormat(Context context, Calendar calendar){
        SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.date_formatter_local));

        String date = formatter.format(calendar.getTime());
        return date;
    }

    public static int getWeeksBetween(Date a, Date b){
        if (b.before(a)){
            return -getWeeksBetween(b, a);
        }
        a = resetTime(a);
        b = resetTime(b);

        Calendar cal = new GregorianCalendar();
        cal.setTime(a);
        int weeks = 0;
        while (cal.getTime().before(b)){
            // add another week
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            weeks++;
        }

        return weeks;
    }

    public static Date resetTime (Date d){
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
