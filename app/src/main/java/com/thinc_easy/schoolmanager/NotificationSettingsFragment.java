package com.thinc_easy.schoolmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormatSymbols;

/**
 * Created by M on 19.02.2016.
 */
public class NotificationSettingsFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

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
    }

    private void saveData(){
        String timeString = et1.getText().toString();

        if (rb1.isChecked()) {
            prefs.edit().putBoolean("pref_key_notification_never", true);
        } else if (rb2.isChecked()) {
            prefs.edit().putBoolean("pref_key_notification_never", false);

            if (DataStorageHandler.isStringNumeric(timeString)) {
                prefs.edit().putInt("pref_key_notification_time", Integer.parseInt(timeString));
            }
        }

        if (cb1.isChecked()) {
            prefs.edit().putBoolean("pref_key_notification_wait_for_previous", true);
        } else {
            prefs.edit().putBoolean("pref_key_notification_wait_for_previous", false);
        }
        if (cb2.isChecked()) {
            prefs.edit().putBoolean("pref_key_notification_only_if_change_rooms", true);
        } else {
            prefs.edit().putBoolean("pref_key_notification_only_if_change_rooms", false);
        }

        DataStorageHandler.updateActiveTtNotificationsTxt(getActivity().getApplicationContext());
    }

    private void initialSetup(){
        int notifTime = prefs.getInt("pref_key_notification_time", 0);
        boolean notifNever = prefs.getBoolean("pref_key_notification_never", false);
        et1.setText(String.valueOf(notifTime));
        if (notifNever){
            rb1.setSelected(true);
            et1.setEnabled(false);
            tv1.setTextColor(getResources().getColor(R.color.Disabled));
        } else {
            rb2.setSelected(true);
        }

        boolean notifWaitForPrevious = prefs.getBoolean("pref_key_notification_wait_for_previous", false);
        boolean notifOnlyIfChangeRooms = prefs.getBoolean("pref_key_notification_only_if_change_rooms", false);

        if (notifWaitForPrevious) {
            cb1.setSelected(true);
        } else {
            cb1.setSelected(false);
        }

        if (notifOnlyIfChangeRooms) {
            cb2.setSelected(true);
        } else {
            cb2.setSelected(false);
        }
    }

}
