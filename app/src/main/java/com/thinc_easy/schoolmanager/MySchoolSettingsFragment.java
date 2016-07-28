package com.thinc_easy.schoolmanager;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.internal.request.StringParcel;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.messaging.FirebaseMessaging;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wassm on 2016-07-03.
 */
public class MySchoolSettingsFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName;
    private SharedPreferences prefs;
    private Spinner sCountry, sSchool;
    private String countryName, countryID, schoolName, schoolID, prefKeySchoolID, prefKeyCountryID,
            prefKeySchoolName, prefKeyCountryName;
    private String[][] allSchools;
    private String[] countryNames, countryIDs;
    private List<String> schools, schoolIDs;
    private boolean foundCountryPreference;
    int selCount;
    private Button nitlButton, saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_school_settings, container, false);

        fragmentName = "MySchoolSettingsFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        prefKeyCountryID = getActivity().getResources().getString(R.string.pref_key_my_school_country_id);
        prefKeySchoolID = getActivity().getResources().getString(R.string.pref_key_my_school_school_id);
        prefKeyCountryName = getActivity().getResources().getString(R.string.pref_key_my_school_country_name);
        prefKeySchoolName = getActivity().getResources().getString(R.string.pref_key_my_school_school_name);

        sCountry = (Spinner) v.findViewById(R.id.sCountry);
        sSchool = (Spinner) v.findViewById(R.id.sSchool);

        TextView tvCountry = (TextView) v.findViewById(R.id.tvCountry);
        TextView tvSchool = (TextView) v.findViewById(R.id.tvSchool);
        tvCountry.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
        tvSchool.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));

        nitlButton = (Button) v.findViewById(R.id.bNotInTheList);
        saveButton = (Button) v.findViewById(R.id.bSaveSchoolSelection);

        initialSetup();
        setUpSaveButton();

        return v;
    }

    @Override
    public void onPause(){
        //saveData();
        super.onPause();
    }



    private void initialSetup(){
        countryNames = getActivity().getResources().getStringArray(R.array.my_school_countries);
        final String countryIDs2Dim[][] = DataStorageHandler.toArray(getActivity(), "schools/Countries.txt", true);
        countryIDs = new String[countryIDs2Dim.length+1];
        countryIDs[0] = getActivity().getResources().getString(R.string.spinner_please_select);
        for (int r1 = 0; r1 < countryIDs2Dim.length; r1++){
            countryIDs[r1+1] = countryIDs2Dim[r1][0];
        }

        allSchools = DataStorageHandler.toArray(getActivity(), "schools/AllSchools.txt", true);


        countryName = "[none]";
        countryID = "[none]";
        schoolName = "[none]";
        schoolID = "[none]";


        ArrayAdapter<String> adapterCountries = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, countryNames);
        adapterCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCountry.setAdapter(adapterCountries);

        selCount = 0;
        sCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
                countryName = countryNames[parentView.getSelectedItemPosition()];
                countryID = countryIDs[parentView.getSelectedItemPosition()];

                if (selCount >= 1) setUpSchoolsSpinner(countryID, "oISListener");
                selCount++;
            }
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });



        foundCountryPreference = false;
        if (prefs.contains(prefKeyCountryID)){
            String prefCountryID = prefs.getString(prefKeyCountryID, "[none]");
            for (int c = 0; c < countryIDs.length; c++){
                if (countryIDs[c].equals(prefCountryID)){
                    countryID = prefCountryID;
                    countryName = countryNames[c];

                    sCountry.setSelection(c, false);
                    foundCountryPreference = true;
                }
            }
        }

        setUpSchoolsSpinner(countryID, "setUp");

        if (foundCountryPreference){
            if (prefs.contains(prefKeySchoolID)) {
                final String prefSchoolID = prefs.getString(prefKeySchoolID, "[none]");
                int position = 0;
                for (int s = 0; s < schoolIDs.size(); s++) {
                    if (prefSchoolID.equals(schoolIDs.get(s))) position = s;
                }

                if (schoolIDs.size() > 0){
                    sSchool.setSelection(position, false);
                }
            }
        }


        setUpNotInTheListButton();

    }

    private void setUpSchoolsSpinner(String newCountryID, String caller){
        schools = new ArrayList<>();
        schools.add(getActivity().getResources().getString(R.string.spinner_please_select));
        schoolIDs = new ArrayList<>();
        schoolIDs.add(getActivity().getResources().getString(R.string.spinner_please_select));


        for (int i = 0; i < allSchools.length; i++){
            if (allSchools[i][0].startsWith(String.valueOf(newCountryID))){
                schools.add(allSchools[i][1]);
                schoolIDs.add(allSchools[i][0]);
            }
        }

        //if (schoolIDs.size()>0) schoolID = schoolIDs.get(0);


        ArrayAdapter<String> adapterSchools = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, schools);
        adapterSchools.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSchool.setAdapter(adapterSchools);
        sSchool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                schoolName = schools.get(parent.getSelectedItemPosition());
                schoolID = schoolIDs.get(parent.getSelectedItemPosition());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void schoolSpinnerFirstSetUp(String prefCountry){
        setUpSchoolsSpinner(prefCountry, "ssFirstSetup");

        List<String> schools = new ArrayList<>();
        schools.add(getActivity().getResources().getString(R.string.spinner_please_select));
        List <String> schoolIDs = new ArrayList<>();
        schoolIDs.add(getActivity().getResources().getString(R.string.spinner_please_select));

        for (int i = 0; i < allSchools.length; i++){
            if (allSchools[i][0].startsWith(String.valueOf(prefCountry))){
                schools.add(allSchools[i][1]);
                schoolIDs.add(allSchools[i][0]);
            }
        }


        if (prefs.contains(prefKeySchoolID)){
            String prefSchoolID = prefs.getString(prefKeySchoolID, "[none]");
            for (int s = 0; s < schoolIDs.size(); s++){
                if (schoolIDs.get(s).equals(prefSchoolID)){
                    schoolID = prefSchoolID;
                    schoolName = schoolIDs.get(s);


                    final List<String> fSchools = schools;
                    final List<String> fSchoolIDs = schoolIDs;

                    ArrayAdapter<String> adapterSchools = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, fSchools);
                    adapterSchools.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sSchool.setAdapter(adapterSchools);
                    sSchool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            schoolName = fSchools.get(parent.getSelectedItemPosition());
                            schoolID = fSchoolIDs.get(parent.getSelectedItemPosition());
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    sSchool.setSelection(s, true);
                }
            }
        }
    }

    private void setUpNotInTheListButton(){
        nitlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MySchoolSettingsActivity) getActivity()).showAddSchoolFragment();
            }
        });
    }

    private void setUpSaveButton(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            saveButton.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
            saveButton.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
        } else {
            saveButton.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData(){
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();

        if (prefs.contains(prefKeySchoolID) && !prefs.getString(prefKeySchoolID, "-").equals(schoolID)
                && prefs.contains(prefKeyCountryID)){

            final String keyUserIDRegistered = getActivity().getResources().getString(R.string.pref_key_user_id_registered);

            String userID = "[none]";
            final String keyUserID = getActivity().getResources().getString(R.string.pref_key_user_id);
            if (prefs.contains(keyUserID)){
                userID = prefs.getString(keyUserID, "[none]");
            }
            if (userID == null || userID.equals("") || userID.equals("[none]")){
                SecureRandom random = new SecureRandom();
                userID = new BigInteger(130, random).toString(32);
                prefs.edit().putString(keyUserID, userID).apply();

                FirebaseMessaging.getInstance().unsubscribeFromTopic("user_"+userID);
                Log.d("FCM", "Unsubscribed from user topic");
                prefs.edit().putBoolean(keyUserIDRegistered, false).apply();
            }

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("MySchool - Select school - Selection changed")
                    .setAction("s("+userID+") S: "+schoolID+" ("+schoolName+
                            ") || OLD C: "+prefs.getString(prefKeyCountryID, "[none]")+", S: "+
                            prefs.getString(prefKeySchoolID, "[none]"))
                    .build());


            FirebaseMessaging.getInstance().unsubscribeFromTopic("school_"+prefs.getString(prefKeySchoolID, "[none]"));
            Log.d("FCM", "Unsubscribed from school topic");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("country_"+prefs.getString(prefKeyCountryID, "[none]"));
            Log.d("FCM", "Unsubscribed from country topic");
            FirebaseMessaging.getInstance().unsubscribeFromTopic("state_"+prefs.getString(prefKeySchoolID, "[none]").substring(0,6));
            Log.d("FCM", "Unsubscribed from state topic");

            FirebaseMessaging.getInstance().subscribeToTopic("school_"+schoolID);
            Log.d("FCM", "Subscribed to school topic");
            FirebaseMessaging.getInstance().subscribeToTopic("country_"+countryID);
            Log.d("FCM", "Subscribed to country topic");
            FirebaseMessaging.getInstance().subscribeToTopic("state_"+schoolID.substring(0,6));
            Log.d("FCM", "Subscribed to state topic");



        } else if (!prefs.contains(prefKeySchoolID)) {
            final String keyUserIDRegistered = getActivity().getResources().getString(R.string.pref_key_user_id_registered);

            String userID = "[none]";
            final String keyUserID = getActivity().getResources().getString(R.string.pref_key_user_id);
            if (prefs.contains(keyUserID)){
                userID = prefs.getString(keyUserID, "[none]");
            }
            if (userID == null || userID.equals("") || userID.equals("[none]")){
                SecureRandom random = new SecureRandom();
                userID = new BigInteger(130, random).toString(32);
                prefs.edit().putString(keyUserID, userID).apply();

                FirebaseMessaging.getInstance().unsubscribeFromTopic("user_"+userID);
                Log.d("FCM", "Unsubscribed from user topic");
                prefs.edit().putBoolean(keyUserIDRegistered, false).apply();
            }

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("MySchool - Select school - First selection")
                    .setAction("sn("+userID+") S: "+schoolID+" ("+schoolName+")")
                    .build());

            FirebaseMessaging.getInstance().subscribeToTopic("school_"+schoolID);
            Log.d("FCM", "Subscribed to school topic");
            FirebaseMessaging.getInstance().subscribeToTopic("country_"+countryID);
            Log.d("FCM", "Subscribed to country topic");
            FirebaseMessaging.getInstance().subscribeToTopic("state_"+schoolID.substring(0,6));
            Log.d("FCM", "Subscribed to state topic");
        }

        prefs.edit().putString(prefKeyCountryID, countryID).apply();
        prefs.edit().putString(prefKeySchoolID, schoolID).apply();
        prefs.edit().putString(prefKeyCountryName, countryName).apply();
        prefs.edit().putString(prefKeySchoolName, schoolName).apply();
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}