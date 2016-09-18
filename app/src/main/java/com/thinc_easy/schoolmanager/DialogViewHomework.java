package com.thinc_easy.schoolmanager;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogViewHomework extends DialogFragment {
    Communicator communicator;
    String ID;
    private Tracker mTracker;
    private String fragmentName;
    private TextView tvTitle, tvContent, tvSubject, tvDeadline;
    private Button bEdit, bDelete, bStatus;

    private String title, content, subject, date, done, isDone;
    private String[] mDayNames;
    private int[] dayInts = {2, 3, 4, 5, 6, 7, 1};
    private String ttFolder, homeworkFilepath;


    public void onAttach(Activity activity){
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_homework, null);
        builder.setView(view);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ttFolder = prefs.getString(getActivity().getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        homeworkFilepath = ttFolder + "/" + getActivity().getResources().getString(R.string.file_name_homework);

        fragmentName = "DialogViewHomework";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        mDayNames = getResources().getStringArray(R.array.DayNames);

        ID = getArguments().getString("ID");

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvContent = (TextView) view.findViewById(R.id.tvContent);
        tvSubject = (TextView) view.findViewById(R.id.tvSubject);
        tvDeadline = (TextView) view.findViewById(R.id.tvDeadline);
        bDelete = (Button) view.findViewById(R.id.buttonDelete);
        bEdit = (Button) view.findViewById(R.id.buttonEdit);
        bStatus = (Button) view.findViewById(R.id.buttonStatus);

        subject = "-";
        date = "-";
        title = "-";
        content = "-";
        done = "-";

        String[][] hwArray = toArray(getActivity(), homeworkFilepath);
        int hwCols = nmbrRowsCols(getActivity(), homeworkFilepath)[0];
        for (int h = 0; h < hwCols; h++){
            if (hwArray[h][0].equals(ID)){
                subject = hwArray[h][1];
                date = hwArray[h][2];
                title = hwArray[h][3];
                content = hwArray[h][4];
                done = hwArray[h][5];
            }
        }

        final String[] subjectInfo = DataStorageHandler.SubjectInfo(getActivity(), ttFolder, subject);
        final String subjectName = subjectInfo[0];

        content = content.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",");
        tvContent.setText(content);
        tvTitle.setText(title.replace("[comma]", ","));
        tvSubject.setText(subjectName.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));

        SimpleDateFormat dateFormatL = new SimpleDateFormat(getResources().getString(R.string.date_formatter_local));
        SimpleDateFormat dateFormatG = new SimpleDateFormat(getResources().getString(R.string.date_formatter_general));
        Date convertedDate = new Date();
        String dateLocal = "";
        try {
            convertedDate = dateFormatG.parse(date);
            dateLocal = dateFormatL.format(convertedDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        tvDeadline.setText(dateLocal);

        isDone = "no";
        if(done.equals("yes")){
            isDone = "yes";
            bStatus.setText(getActivity().getResources().getString(R.string.homework_status_done));
            bStatus.setTextColor(getActivity().getResources().getColor(R.color.color_homework_done));
        } else if(done.equals("no")) {
            isDone = "no";
            bStatus.setText(getActivity().getResources().getString(R.string.homework_status_pending));
            bStatus.setTextColor(getActivity().getResources().getColor(R.color.color_homework_pending));
        }
        bStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean nowDone = false;
                if (isDone.equals("yes")){
                    isDone = "no";
                    bStatus.setText(getActivity().getResources().getString(R.string.homework_status_pending));
                    bStatus.setTextColor(getActivity().getResources().getColor(R.color.color_homework_pending));
                    nowDone = false;
                } else if (isDone.equals("no")){
                    isDone = "yes";
                    bStatus.setText(getActivity().getResources().getString(R.string.homework_status_done));
                    bStatus.setTextColor(getActivity().getResources().getColor(R.color.color_homework_done));
                    nowDone = true;
                }
                hwDone(ID, nowDone);
            }
        });

        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.onDialogMessageViewHomework(true, ID);
                dismiss();
            }
        });
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.onDialogMessageDeleteHomework(ID);
                dismiss();
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }

    interface Communicator{
        public void onDialogMessageViewHomework(boolean edit, String ID);
        public void onDialogMessageDeleteHomework(String ID);
        public void onDialogMessageDoneClicked();
    }


    public void hwDone(String ID, boolean done){
        //Toast.makeText(getActivity(), "hwDone: "+ID+", "+String.valueOf(done), Toast.LENGTH_SHORT).show();
        File file = new File(getActivity().getExternalFilesDir(null), homeworkFilepath);
        String[][] hwArray = toArray(getActivity(), homeworkFilepath);
        final int[] rc = nmbrRowsCols(getActivity(), homeworkFilepath);
        final int hwRows = rc[0];
        final int hwCols = rc[1];
        int thisRow = -1;
        for (int r = 0; r < hwRows; r++){
            if (ID.equals(hwArray[r][0])) thisRow = r;
        }
        //Toast.makeText(getActivity(), "thisRow: "+String.valueOf(thisRow), Toast.LENGTH_SHORT).show();
        if (thisRow > -1){
            if (done) hwArray[thisRow][5] = "yes";
            if (!done) hwArray[thisRow][5] = "no";

            DataStorageHandler.writeToCSVFile(getActivity(), file, hwArray, hwRows, hwCols, "DialogViewHomework");
        }
        communicator.onDialogMessageDoneClicked();
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
