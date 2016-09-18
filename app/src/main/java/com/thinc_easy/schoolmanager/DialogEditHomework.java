package com.thinc_easy.schoolmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;


public class DialogEditHomework extends DialogFragment{
    Communicator communicator;
    private Tracker mTracker;
    private String fragmentName;
    private EditText etTitle, etContent;
    private Button bOK, bCancel;
    private ArrayAdapter<String> adapterSubject, adapterDate;
    private Spinner mSubjectSpinner, mDateSpinner;

    private String title, content, subject, date;
    private boolean exists;
    private String eID, eTitle, eContent, eSubject, eDate, eDone;
    private String[] mDayNames, dates, datesSpinner;
    private int[] dayInts = {2, 3, 4, 5, 6, 7, 1};


    public void onAttach(Activity activity){
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_homework, null);
        builder.setView(view);

        fragmentName = "DialogEditHomework";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        final String currentTimetable = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getActivity().getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        final String homeworkFilename = currentTimetable + "/" + getActivity().getResources().getString(R.string.file_name_homework);
        final String subjectsFilename = currentTimetable + "/" + getActivity().getResources().getString(R.string.file_name_subjects);



        mDayNames = getResources().getStringArray(R.array.DayNames);
        title = "-";
        content = "-";
        subject = "-";
        date = "-";

        etTitle = (EditText) view.findViewById(R.id.etTitle);
        etContent = (EditText) view.findViewById(R.id.etContent);

        etTitle.setTypeface(Typeface.createFromAsset(getActivity().getResources().getAssets(), "Roboto-Medium.ttf"));



        // get subject names and put them into sNames
        int sRows = nmbrRowsCols(getActivity(), subjectsFilename)[0];
        String[][] sArray = toArray(getActivity(), subjectsFilename);
        final String[] sNames = new String[sRows];
        final String[] sIDs = new String[sRows];
        for (int r = 0; r < sRows; r++){
            sNames[r] = sArray[r][1].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",");
            sIDs[r] = sArray[r][0].replace("[comma]", ",");
        }

        mSubjectSpinner = (Spinner) view.findViewById(R.id.spinnerSubject);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterSubject = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, sNames);
        // Specify the layout to use when the list of choices appears
        adapterSubject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSubjectSpinner.setAdapter(adapterSubject);
        mSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
                subject = sIDs[parentView.getSelectedItemPosition()]; }
            public void onNothingSelected(AdapterView<?> parentView) {
                subject = sIDs[0]; }
        });


        SimpleDateFormat formatterSpinner = new SimpleDateFormat(getActivity().getResources().getString(R.string.date_formatter_local));
        SimpleDateFormat formatter = new SimpleDateFormat(getActivity().getResources().getString(R.string.date_formatter_general));


        dates = new String[17];
        dates[0] = "nextPeriod";
        datesSpinner = new String[17];
        datesSpinner[0] = getActivity().getResources().getString(R.string.next_lesson);

        for (int d = 0; d < 6; d++){
            Calendar then = Calendar.getInstance();
            int doyNow = then.get(Calendar.DAY_OF_YEAR);
            then.set(Calendar.DAY_OF_YEAR, doyNow + d + 1);

            int thenDay = then.get(Calendar.DAY_OF_WEEK);
            String thenFormS = formatterSpinner.format(then.getTime());
            String thenForm = formatter.format(then.getTime());
            dates[1+d] = thenForm;
            for (int i3 = 0; i3 < dayInts.length; i3++) {
                if (thenDay == dayInts[i3]) datesSpinner[1+d] = mDayNames[i3] + ", " + thenFormS;
            }
        }

        for (int d = 0; d < 10; d++){
            Calendar then = Calendar.getInstance();
            int doyNow = then.get(Calendar.DAY_OF_YEAR);
            int dayDiff = (d+1) * 7;
            then.set(Calendar.DAY_OF_YEAR, doyNow + dayDiff);

            String thenForm = formatter.format(then.getTime());
            dates[7+d] = thenForm;
            datesSpinner[7+d] = getActivity().getResources().getStringArray(R.array.date_spinner_weeks)[d];
        }





        if(getArguments().containsKey("existing") && getArguments().getBoolean("existing")
                && getArguments().containsKey("ID")) {
            exists = true;

            eID = getArguments().getString("ID");
            String[][] hwArray = toArray(getActivity(), homeworkFilename);
            int hwRows = nmbrRowsCols(getActivity(), homeworkFilename)[0];
            for (int h = 0; h < hwRows; h++){
                if (eID.equals(hwArray[h][0])){
                    eSubject = hwArray[h][1];
                    eDate = hwArray[h][2];
                    eTitle = hwArray[h][3];
                    eContent = hwArray[h][4];
                    eDone = hwArray[h][5];
                }
            }


            etTitle.setText(eTitle.replace("[comma]", ","));
            etContent.setText(eContent.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));

            for (int n = 0; n < sNames.length; n++){
                if (eSubject.equals(sIDs[n])){
                    mSubjectSpinner.setSelection(n);
                }
            }

            boolean alreadyInList = false;
            for (int d = 0; d < datesSpinner.length; d++){
                if (eDate.equals(dates[d])) alreadyInList = true;
            }
            if (!alreadyInList){
                String[] datesNew = new String[dates.length + 1];
                String[] datesSpinnerNew = new String[datesSpinner.length + 1];
                datesNew[0] = eDate;
                Date convertedDate = new Date();
                String dateLocal = "-";
                try {
                    convertedDate = formatter.parse(eDate);
                    dateLocal = formatterSpinner.format(convertedDate);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                datesSpinnerNew[0] = dateLocal;
                for (int s = 0; s < datesSpinner.length; s++) datesSpinnerNew[s+1] = datesSpinner[s];
                for (int s1 = 0; s1 < dates.length; s1++) datesNew[s1 + 1] = dates[s1];
                dates = datesNew;
                datesSpinner = datesSpinnerNew;
            }


            setUpDateSpinner(view);

            for (int d = 0; d < datesSpinner.length && d < dates.length; d++){
                if (eDate.equals(dates[d])){
                    mDateSpinner.setSelection(d);
                }
            }
        } else {
            if (getArguments().containsKey("subject")){
                final String sID = getArguments().getString("subject");
                for (int n = 0; n < sNames.length; n++){
                    if (sID.equals(sIDs[n])){
                        mSubjectSpinner.setSelection(n);
                    }
                }
            }

            if (getArguments().containsKey("date")) {
                final String nowDate = getArguments().getString("date");

                boolean alreadyInList = false;
                int position = -1;
                for (int d = 0; d < dates.length; d++) {
                    if (nowDate.equals(dates[d])) {
                        alreadyInList = true;
                        position = d;
                    }
                }
                if (!alreadyInList) {
                    String[] datesNew = new String[dates.length + 1];
                    String[] datesSpinnerNew = new String[datesSpinner.length + 1];
                    datesNew[0] = nowDate;
                    Date convertedDate = new Date();
                    String dateLocal = "-";
                    try {
                        convertedDate = formatter.parse(nowDate);
                        dateLocal = formatterSpinner.format(convertedDate);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    datesSpinnerNew[0] = dateLocal;
                    for (int s = 0; s < datesSpinner.length; s++)
                        datesSpinnerNew[s + 1] = datesSpinner[s];
                    for (int s1 = 0; s1 < dates.length; s1++) datesNew[s1 + 1] = dates[s1];
                    dates = datesNew;
                    datesSpinner = datesSpinnerNew;
                    position = 0;
                }

                setUpDateSpinner(view);

                if (position >= 0) {
                    mDateSpinner.setSelection(position);
                }

            } else {
                setUpDateSpinner(view);
            }
        }





        bOK = (Button) view.findViewById(R.id.buttonOK);
        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = etTitle.getText().toString();
                title = title.replace(",", "[comma]");
                content = etContent.getText().toString();
                content = content.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");

                String lessonID = "[none]";
                if (date.equals("nextPeriod")){
                    date = subjectNextLesson(subject)[0];
                    lessonID = subjectNextLesson(subject)[1];
                }
                if(subject!=null && !subject.equals("") && !subject.equals("-")) {
                    if (exists) {
                        communicator.onDialogMessageUpdateHomework(eID, title, content, subject, date, eDone, lessonID);
                    } else {
                        communicator.onDialogMessageHomework(title, content, subject, date, lessonID);
                    }
                    dismiss();
                }
            }
        });
        bCancel = (Button) view.findViewById(R.id.buttonCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }

    interface Communicator{
        public void onDialogMessageHomework(String title, String content, String subject, String date, String lessonID);
        public void onDialogMessageUpdateHomework(String ID, String title, String content, String subject, String date, String done, String lessonID);
    }

    private void setUpDateSpinner(View view){
        mDateSpinner = (Spinner) view.findViewById(R.id.spinnerDeadline);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterDate = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, datesSpinner);
        // Specify the layout to use when the list of choices appears
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mDateSpinner.setAdapter(adapterDate);
        final String[] sDates = dates;
        mDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
                date = sDates[parentView.getSelectedItemPosition()]; }
            public void onNothingSelected(AdapterView<?> parentView) {
                date = sDates[0]; }
        });
    }

    public String[] subjectNextLesson(String sID){
        final String currentTimetable = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getActivity().getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        final String lessonsFilename = currentTimetable + "/" + getActivity().getResources().getString(R.string.file_name_lessons);

        final String[][] sArray = toArray(getActivity(), lessonsFilename);
        final int sRows = nmbrRowsCols(getActivity(), lessonsFilename)[0];
        SimpleDateFormat formatter = new SimpleDateFormat(getActivity().getResources().getString(R.string.date_formatter_general));
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        String nextLessonTime = formatter.format(tomorrow.getTime());
        String nextLessonID = "[none]";

        final String[] allAbs = DataStorageHandler.AllABs(getActivity(), currentTimetable);
        final int currentABint = DataStorageHandler.getCurrentAB(getActivity(), currentTimetable, Calendar.getInstance());

        int nowDay = 0;
        Calendar now = Calendar.getInstance();
        int iNowDay = now.get(Calendar.DAY_OF_WEEK);
        for (int i2 = 0; i2 < dayInts.length; i2++) {
            if (iNowDay == dayInts[i2]) nowDay = i2;
        }
        int dayDif = -1;

        for (int r = 0; r < sRows; r++){
            if (sID.equals(sArray[r][0])){

                int dInt = -1;
                for (int d = 0; d < mDayNames.length; d++){
                    if (sArray[r][2].equals(mDayNames[d])){
                        dInt = d;
                    }
                }
                if (dInt <= 0) dInt = 0;
                int dDif = dInt - nowDay;

                int weekDifference = 0;
                String[] thisAbs = sArray[r][1].split("/");
                for (int s = 0; s < thisAbs.length; s++){
                    int thisABint = 0;
                    for (int a = 0; a < allAbs.length; a++) {
                        if (thisAbs[r].replace(" ", "").toUpperCase().equals(allAbs[a])){
                            thisABint = a;
                        }
                    }

                    int wdif = currentABint - thisABint;
                    int dif = wdif * 7 + dDif;
                    if (dif <= 0) dif = allAbs.length + dif;

                    if (dayDif < 0 || dif < dayDif) dayDif = dif;
                }

                Calendar then = Calendar.getInstance();
                if (dayDif < 0) dayDif = 0;
                then.add(Calendar.DAY_OF_YEAR, dayDif);
                nextLessonTime = formatter.format(then.getTime());
            }
        }
        return new String[] {nextLessonTime, nextLessonID};
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


        File file = new File(getActivity().getExternalFilesDir(null), filename);
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

        File file = new File(getActivity().getExternalFilesDir(null), filename);
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

    @Override
    public void onResume(){
        super.onResume();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
