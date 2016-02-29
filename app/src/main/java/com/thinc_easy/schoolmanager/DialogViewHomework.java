package com.thinc_easy.schoolmanager;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
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
    private TextView tvTitle, tvContent, tvSubject, tvDeadline;
    private Button bEdit, bDelete, bStatus;

    private String title, content, subject, date, done, isDone;
    private String[] mDayNames;
    private int[] dayInts = {2, 3, 4, 5, 6, 7, 1};


    public void onAttach(Activity activity){
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_view_homework, null);
        builder.setView(view);

        mDayNames = getResources().getStringArray(R.array.DayNames);

        ID = getArguments().getString("ID");

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvContent = (TextView) view.findViewById(R.id.tvContent);
        tvSubject = (TextView) view.findViewById(R.id.tvSubject);
        tvDeadline = (TextView) view.findViewById(R.id.tvDeadline);
        bDelete = (Button) view.findViewById(R.id.buttonDelete);
        bEdit = (Button) view.findViewById(R.id.buttonEdit);
        bStatus = (Button) view.findViewById(R.id.buttonStatus);

        String[][] hwArray = toArray(getActivity(), "Homework.txt");
        int hwCols = nmbrRowsCols(getActivity(), "Homework.txt")[0];
        for (int h = 0; h < hwCols; h++){
            if (hwArray[h][0].equals(ID)){
                subject = hwArray[h][1];
                date = hwArray[h][2];
                title = hwArray[h][3];
                content = hwArray[h][4];
                done = hwArray[h][5];
            }
        }

        content = content.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",");
        tvContent.setText(content);
        tvTitle.setText(title.replace("[comma]", ","));
        tvSubject.setText(subject.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));

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
        File file = new File(getActivity().getExternalFilesDir(null), "Homework.txt");
        String[][] hwArray = toArray(getActivity(), "Homework.txt");
        int hwRows = nmbrRowsCols(getActivity(), "Homework.txt")[0];
        int thisRow = -1;
        for (int r = 0; r < hwRows; r++){
            if (ID.equals(hwArray[r][0])) thisRow = r;
        }
        //Toast.makeText(getActivity(), "thisRow: "+String.valueOf(thisRow), Toast.LENGTH_SHORT).show();
        if (thisRow > -1){
            if (done) hwArray[thisRow][5] = "yes";
            if (!done) hwArray[thisRow][5] = "no";

            // write data to file
            try{
                BufferedWriter buf = new BufferedWriter(new FileWriter(file));
                for (int t = 0; t < hwRows; t++){
                    if (hwArray[t][0].equals(null) == false & hwArray[t][1].equals(null) == false & hwArray[t][2].equals(null) == false & hwArray[t][3].equals(null) == false){
                        buf.write(hwArray[t][0] + "," + hwArray[t][1] + "," + hwArray[t][2] + "," + hwArray[t][3] + "," + hwArray[t][4] + "," + hwArray[t][5]);
                        buf.newLine();
                    }else{
                        Toast.makeText(getActivity(), "CANNOT save data:" + hwArray[t][0] + "," + hwArray[t][1] + "," + hwArray[t][2] + "," + hwArray[t][3], Toast.LENGTH_SHORT).show();
                    }
                }
                buf.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
