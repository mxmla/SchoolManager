package com.thinc_easy.schoolmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import java.util.Date;
import java.util.Scanner;

/**
 * Created by M on 02.02.2016.
 */

public class DialogDeleteSubjectConfirm extends DialogFragment {
    Communicator communicator;
    private TextView tvTitle;
    private Button bYes, bNo;

    private String title;
    private String subject;


    public void onAttach(Activity activity){
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_subject_confirm, null);
        builder.setView(view);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        bYes = (Button) view.findViewById(R.id.buttonYes);
        bNo = (Button) view.findViewById(R.id.buttonNo);

        final String subjectID = getArguments().getString("subject");
        final String ttFolder = getArguments().getString("ttFolder");
        final String[] subjectInfo = DataStorageHandler.SubjectInfo(getActivity(), ttFolder, subjectID);
        subject = subjectInfo[0];
        title = subject.replace("[comma]", ",").replace("[none]", "").replace("[null]", "") + " " + getResources().getString(R.string.dialog_delete_subject_confirm_title);

        tvTitle.setText(title.replace("[comma]", ","));

        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.onDialogMessageDeleteConfirm(true, subjectID);
                dismiss();
            }
        });
        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.onDialogMessageDeleteConfirm(false, subjectID);
                dismiss();
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }

    interface Communicator{
        public void onDialogMessageDeleteConfirm(boolean delete, String subject);
    }
}
