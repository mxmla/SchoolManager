package com.thinc_easy.schoolmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.DecimalFormatSymbols;

/**
 * Created by M on 19.02.2016.
 */
public class NotificationSettingsFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName;
    private SharedPreferences prefs;
    private RadioGroup rg1;
    private RadioButton rb1, rb2;
    private EditText et1;
    private CheckBox cb1, cb2;
    private TextView tv1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification_settings, container, false);

        fragmentName = "NotificationSettingsFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        rg1 = (RadioGroup) v.findViewById(R.id.rg1);
        rb1 = (RadioButton) v.findViewById(R.id.rb1);
        rb2 = (RadioButton) v.findViewById(R.id.rb2);
        et1 = (EditText) v.findViewById(R.id.et1);
        cb1 = (CheckBox) v.findViewById(R.id.cb1);
        cb2 = (CheckBox) v.findViewById(R.id.cb2);
        tv1 = (TextView) v.findViewById(R.id.tv1);

        initialSetup();

        return v;
    }

    @Override
    public void onPause(){
        saveData();
        super.onPause();
    }

    private void saveData(){
        Toast.makeText(getActivity().getApplicationContext(), getActivity().getResources().getString(R.string.toast_saved), Toast.LENGTH_LONG).show();
        String timeString = et1.getText().toString();
        boolean notify = true;
        int beforeTime = 0;
        boolean oWChRooms = false;
        boolean waitFPrev = false;

        if (rb1.isChecked()) {
            prefs.edit().putBoolean("pref_key_notification_never", true).apply();
            notify = false;
        } else if (rb2.isChecked()) {
            prefs.edit().putBoolean("pref_key_notification_never", false).apply();

            if (timeString == null || timeString.equals("")) timeString = "0";

            if (DataStorageHandler.isStringNumeric(timeString)) {
                prefs.edit().putInt("pref_key_notification_time", Integer.parseInt(timeString)).apply();
                beforeTime = Integer.parseInt(timeString);
            }
            notify = true;
        }

        if (cb1.isChecked()) {
            prefs.edit().putBoolean("pref_key_notification_wait_for_previous", true).apply();
            waitFPrev = true;
        } else {
            prefs.edit().putBoolean("pref_key_notification_wait_for_previous", false).apply();
            waitFPrev = false;
        }
        if (cb2.isChecked()) {
            prefs.edit().putBoolean("pref_key_notification_only_if_change_rooms", true).apply();
            oWChRooms = true;
        } else {
            prefs.edit().putBoolean("pref_key_notification_only_if_change_rooms", false).apply();
            oWChRooms = false;
        }

        //DataStorageHandler.updateActiveTtNotificationsTxt(getActivity().getApplicationContext());
        String currentTtFolder = prefs.getString(getActivity().getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        if (!currentTtFolder.equals("[none]"))
            DataStorageHandler.NotificationsPrefChange(getActivity(), currentTtFolder, notify, beforeTime, oWChRooms, waitFPrev);
    }

    private void initialSetup(){
        //Toast.makeText(getActivity(), "initialSetup", Toast.LENGTH_SHORT).show();
        int notifTime = prefs.getInt("pref_key_notification_time", 0);
        boolean notifNever = prefs.getBoolean("pref_key_notification_never", false);
        //Toast.makeText(getActivity(), "notifTime: " + String.valueOf(notifTime) + "; NotifNever: " + String.valueOf(notifNever), Toast.LENGTH_SHORT).show();
        et1.setText(String.valueOf(notifTime));
        if (notifNever){
            rg1.check(rb1.getId());
            et1.setEnabled(false);
            tv1.setTextColor(getResources().getColor(R.color.Disabled));
        } else {
            rg1.check(rb2.getId());
        }
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == rb1.getId()){
                    et1.setEnabled(false);
                    tv1.setTextColor(getResources().getColor(R.color.Disabled));
                } else if (i == rb2.getId()){
                    et1.setEnabled(true);
                    tv1.setTextColor(getResources().getColor(R.color.Text));
                }
            }
        });

        boolean notifWaitForPrevious = prefs.getBoolean("pref_key_notification_wait_for_previous", false);
        boolean notifOnlyIfChangeRooms = prefs.getBoolean("pref_key_notification_only_if_change_rooms", false);

        if (notifWaitForPrevious) {
            cb1.setChecked(true);
        } else {
            cb1.setChecked(false);
        }

        if (notifOnlyIfChangeRooms) {
            cb2.setChecked(true);
        } else {
            cb2.setChecked(false);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
