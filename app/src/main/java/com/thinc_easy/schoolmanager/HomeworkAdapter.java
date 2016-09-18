package com.thinc_easy.schoolmanager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by M on 28.08.2015.
 */
public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.MyViewHolder>{
    Communicator communicator;
    private LayoutInflater inflater;
    List<InformationHomeworkFull> data = Collections.emptyList();
    private Context context;
    private String listType;
    private String[] ctrlDone;
    private SharedPreferences prefs;
    private String ttFolder, homework_filepath;

    public HomeworkAdapter(Context context, List<InformationHomeworkFull> data, String listType){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.listType = listType;
    }

    public void delete(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ttFolder = prefs.getString(context.getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        homework_filepath = ttFolder + "/" + context.getResources().getString(R.string.file_name_homework);

        communicator = (Communicator) parent.getContext();
        if (listType.equals("hwFull")){
            view = inflater.inflate(R.layout.homework_row_full, parent, false);
        } else if (listType.equals("hwSmall")){
            view = inflater.inflate(R.layout.homework_row_small, parent, false);
        } else {
            view = inflater.inflate(R.layout.homework_row_full, parent, false);
        }
        ctrlDone = new String[data.size()];
        for (int d = 0; d < data.size(); d++){
            ctrlDone[d] = data.get(d).done;
        }

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        InformationHomeworkFull current = data.get(position);

        holder.lAvatar.setBackgroundDrawable(current.icon);
        holder.lAvatar.setText(current.sAbbrev.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
        holder.lAvatar.setTextSize(14);
        holder.lAvatar.setTypeface(Typeface.createFromAsset(context.getResources().getAssets(), "Roboto-Medium.ttf"));
        holder.lAvatar.setTextColor(current.sTextColor);

        holder.lText.setText(current.hTitle.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
        holder.lTextSec.setText(current.date);
        if (current.done.equals("yes")) holder.lIcon.setImageResource(R.drawable.ic_done_green_24dp);
        if (current.done.equals("no")) holder.lIcon.setImageResource(R.drawable.ic_done_black_24dp);

        final String fID = current.ID;
        final ImageButton flIcon = holder.lIcon;
        holder.lIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean nowDone = false;
                if (ctrlDone[position].equals("no")){
                    nowDone = true;
                    flIcon.setImageResource(R.drawable.ic_done_green_24dp);
                    ctrlDone[position] = "yes";
                } else if (ctrlDone[position].equals("yes")){
                    nowDone = false;
                    flIcon.setImageResource(R.drawable.ic_done_black_24dp);
                    ctrlDone[position] = "no";
                }
                hwDone(fID, nowDone);
                communicator.recyclerViewDoneClicked(fID);
            }
        });

        holder.lTouchTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.recyclerViewListClicked(fID);
            }
        });
    }

    public interface Communicator{
        public void recyclerViewListClicked(String ID);
        public void recyclerViewDoneClicked(String ID);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void hwDone(String ID, boolean done){
        File file = new File(context.getExternalFilesDir(null), homework_filepath);
        String[][] hwArray = toArray(context, homework_filepath);
        final int[] hwRowsCols = nmbrRowsCols(context, homework_filepath);
        int thisRow = -1;
        for (int r = 0; r < hwRowsCols[0]; r++){
            if (ID.equals(hwArray[r][0])) thisRow = r;
        }
        if (thisRow > -1){
            if (done) hwArray[thisRow][5] = "yes";
            if (!done) hwArray[thisRow][5] = "no";

            DataStorageHandler.writeToCSVFile(context, file, hwArray, hwRowsCols[0], hwRowsCols[1], "HomeworkAdapter");

            // write data to file
            /*try{
                BufferedWriter buf = new BufferedWriter(new FileWriter(file));
                for (int t = 0; t < hwRows; t++){
                    if (hwArray[t][0].equals(null) == false & hwArray[t][1].equals(null) == false & hwArray[t][2].equals(null) == false & hwArray[t][3].equals(null) == false){
                        buf.write(hwArray[t][0] + "," + hwArray[t][1] + "," + hwArray[t][2] + "," + hwArray[t][3] + "," + hwArray[t][4] + "," + hwArray[t][5]);
                        buf.newLine();
                    }else{
                        Toast.makeText(context, "CANNOT save data:" + hwArray[t][0] + "," + hwArray[t][1] + "," + hwArray[t][2] + "," + hwArray[t][3], Toast.LENGTH_SHORT).show();
                    }
                }
                buf.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
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

    class MyViewHolder extends RecyclerView.ViewHolder{
        Button lAvatar;
        TextView lText;
        TextView lTextSec;
        ImageButton lIcon;
        View lTouchTarget;


        public MyViewHolder(View itemView) {
            super(itemView);
            lAvatar = (Button) itemView.findViewById(R.id.avatar);
            lText = (TextView) itemView.findViewById(R.id.text);
            lTextSec = (TextView) itemView.findViewById(R.id.textSec);
            lIcon = (ImageButton) itemView.findViewById(R.id.icon);
            lTouchTarget = (View) itemView.findViewById(R.id.touchTarget);
        }
    }
}
