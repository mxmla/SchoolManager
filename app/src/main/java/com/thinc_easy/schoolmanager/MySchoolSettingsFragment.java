package com.thinc_easy.schoolmanager;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wassm on 2016-07-03.
 */
public class MySchoolSettingsFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName;
    private SharedPreferences prefs;
    private Spinner sCountry, sState, sSchool;
    private String countryName, countryID, schoolName, schoolID, prefKeySchoolID, prefKeyCountryID,
            prefKeySchoolName, prefKeyCountryName, stateName, stateID, prefKeyStateName, prefKeyStateID;
    private String[][] allSchools, allStates;
    private String[] countryNames, countryIDs;
    private List<String> schools, schoolIDs, states, stateIDs;
    private boolean foundCountryPreference, foundStatePreference;
    int selCount, statesSelCount;
    private Button nitlButton, saveButton;
    private long NrUsersNewSchoolBefore, NrUsersOldSchoolBefore;

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
        sState = (Spinner) v.findViewById(R.id.sState);

        TextView tvCountry = (TextView) v.findViewById(R.id.tvCountry);
        TextView tvSchool = (TextView) v.findViewById(R.id.tvSchool);
        TextView tvState = (TextView) v.findViewById(R.id.tvState);
        tvCountry.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
        tvSchool.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
        tvState.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));

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

        // Sort names and IDs alphabetically (by name)
        String[][] countries = new String[countryNames.length][2];
        for (int c = 0; c < countryIDs.length && c < countryNames.length; c++){
            countries[c][0] = countryIDs[c];
            countries[c][1] = countryNames[c];
        }
        Arrays.sort(countries, new Comparator<String[]>() {
            @Override
            public int compare(final String[] entry1, final String[] entry2) {
                return entry1[1].compareTo(entry2[1]);
            }
        });
        countryNames = new String[countries.length];
        countryIDs = new String[countries.length];
        for (int c = 0; c < countries.length; c++){
            countryIDs[c] = countries[c][0];
            countryNames[c] = countries[c][1];
        }


        allSchools = DataStorageHandler.toArray(getActivity(), "schools/AllSchools.txt", true);
        allStates = DataStorageHandler.toArray(getActivity(), "schools/StatesRegions.txt", true);


        countryName = "[none]";
        countryID = "[none]";
        stateName = "[none]";
        stateID = "[none]";
        schoolName = "[none]";
        schoolID = "[none]";


        ArrayAdapter<String> adapterCountries = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, countryNames);
        adapterCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCountry.setAdapter(adapterCountries);

        selCount = 0;
        statesSelCount = 0;
        sCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
                countryName = countryNames[parentView.getSelectedItemPosition()];
                countryID = countryIDs[parentView.getSelectedItemPosition()];

                if (selCount >= 1) setUpStatesSpinner(countryID, "oISListener");
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

        setUpStatesSpinner(countryID, "setUp");

        foundStatePreference = false;
        if (foundCountryPreference) {
            if (prefs.contains(prefKeySchoolID)) {
                final String prefStateID = prefs.getString(prefKeySchoolID, "[none]").substring(0, 8);

                for (int c = 0; c < stateIDs.size(); c++) {
                    final String thisID = stateIDs.get(c);
                    if (prefStateID.equals(thisID)) {
                        stateID = prefStateID;
                        stateName = states.get(c);

                        sState.setSelection(c, false);
                        foundStatePreference = true;
                    }
                }
            }
        }

        setUpSchoolsSpinner(stateID, "setUp");

        if (foundStatePreference){
            if (prefs.contains(prefKeySchoolID)) {
                final String prefSchoolID = prefs.getString(prefKeySchoolID, "[none]");
                int position = 0;
                for (int s = 0; s < schoolIDs.size(); s++) {
                    if (prefSchoolID.equals(schoolIDs.get(s))) position = s;
                }

                if (schoolIDs.size() >= position){
                    sSchool.setSelection(position, false);
                }
            }
        }


        setUpNotInTheListButton();

    }

    private void setUpStatesSpinner(String newCountryID, String caller){
        states = new ArrayList<>();
        states.add(getActivity().getResources().getString(R.string.spinner_please_select));
        stateIDs = new ArrayList<>();
        stateIDs.add(getActivity().getResources().getString(R.string.spinner_please_select));


        for (int i = 0; i < allStates.length; i++){
            if (allStates[i][0].startsWith(String.valueOf(newCountryID))){
                states.add(allStates[i][1]);
                stateIDs.add(allStates[i][0]);
            }
        }

        // Sort names and IDs alphabetically (by name)
        String[][] sortStates = new String[states.size()][2];
        for (int c = 0; c < stateIDs.size() && c < states.size(); c++){
            sortStates[c][0] = stateIDs.get(c);
            sortStates[c][1] = states.get(c);
        }
        Arrays.sort(sortStates, new Comparator<String[]>() {
            @Override
            public int compare(final String[] entry1, final String[] entry2) {
                return entry1[1].compareTo(entry2[1]);
            }
        });
        states = new ArrayList<>();
        stateIDs = new ArrayList<>();
        for (int c = 0; c < sortStates.length; c++){
            stateIDs.add(sortStates[c][0]);
            states.add(sortStates[c][1]);
        }

        //if (schoolIDs.size()>0) schoolID = schoolIDs.get(0);


        ArrayAdapter<String> adapterStates = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, states);
        adapterStates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sState.setAdapter(adapterStates);
        sState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateName = states.get(parent.getSelectedItemPosition());
                stateID = stateIDs.get(parent.getSelectedItemPosition());

                if (statesSelCount >= 1) setUpSchoolsSpinner(stateID, "oISListener");
                statesSelCount++;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setUpSchoolsSpinner(String newStateID, String caller){
        schools = new ArrayList<>();
        schools.add(getActivity().getResources().getString(R.string.spinner_please_select));
        schoolIDs = new ArrayList<>();
        schoolIDs.add(getActivity().getResources().getString(R.string.spinner_please_select));


        for (int i = 0; i < allSchools.length; i++){
            if (allSchools[i][0].startsWith(String.valueOf(newStateID))){
                schools.add(allSchools[i][1]);
                schoolIDs.add(allSchools[i][0]);
            }
        }

        // Sort names and IDs alphabetically (by name)
        String[][] sortSchools = new String[schools.size()][2];
        for (int c = 0; c < schoolIDs.size() && c < schools.size(); c++){
            sortSchools[c][0] = schoolIDs.get(c);
            sortSchools[c][1] = schools.get(c);
        }
        Arrays.sort(sortSchools, new Comparator<String[]>() {
            @Override
            public int compare(final String[] entry1, final String[] entry2) {
                return entry1[1].compareTo(entry2[1]);
            }
        });
        schools = new ArrayList<>();
        schoolIDs = new ArrayList<>();
        for (int c = 0; c < sortSchools.length; c++){
            schoolIDs.add(sortSchools[c][0]);
            schools.add(sortSchools[c][1]);
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
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    System.out.println("connected");
                } else {
                    System.out.println("not connected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
        boolean checkDataOK = true;
        if (countryID == null || countryID.contains(getActivity().getResources().getString(R.string.spinner_please_select)) ||
                !DataStorageHandler.isStringNumeric(countryID))
            checkDataOK = false;

        if (schoolID == null || countryID.contains(getActivity().getResources().getString(R.string.spinner_please_select)) ||
                !DataStorageHandler.isStringNumeric(schoolID))
            checkDataOK = false;

        if (checkDataOK) {
            // Register user ID if not done so before. Also get user ID if existant
            final String keyUserIDRegistered = getActivity().getResources().getString(R.string.pref_key_user_id_registered);
            String userID = "[none]";
            final String keyUserID = getActivity().getResources().getString(R.string.pref_key_user_id);
            if (prefs.contains(keyUserID)) {
                userID = prefs.getString(keyUserID, "[none]");
            }
            if (userID == null || userID.equals("") || userID.equals("[none]")) {
                SecureRandom random = new SecureRandom();
                userID = new BigInteger(130, random).toString(32);
                prefs.edit().putString(keyUserID, userID).apply();

                FirebaseMessaging.getInstance().unsubscribeFromTopic("user_" + userID);
                Log.d("FCM", "Unsubscribed from user topic");
                prefs.edit().putBoolean(keyUserIDRegistered, false).apply();
            }


            // If user had selected a school before
            if (prefs.contains(prefKeySchoolID) && !prefs.getString(prefKeySchoolID, "-").equals(schoolID)
                    && prefs.contains(prefKeyCountryID)) {

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("MySchool - Select school - Selection changed")
                        .setAction("s(" + userID + ") S: " + schoolID + " (" + schoolName +
                                ") || OLD C: " + prefs.getString(prefKeyCountryID, "[none]") + ", S: " +
                                prefs.getString(prefKeySchoolID, "[none]"))
                        .build());


                FirebaseMessaging.getInstance().unsubscribeFromTopic("school_" + prefs.getString(prefKeySchoolID, "[none]"));
                Log.d("FCM", "Unsubscribed from school topic");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("country_" + prefs.getString(prefKeyCountryID, "[none]"));
                Log.d("FCM", "Unsubscribed from country topic");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("state_" + prefs.getString(prefKeySchoolID, "[none]").substring(0, 8));
                Log.d("FCM", "Unsubscribed from state topic");

                FirebaseMessaging.getInstance().subscribeToTopic("school_" + schoolID);
                Log.d("FCM", "Subscribed to school topic");
                FirebaseMessaging.getInstance().subscribeToTopic("country_" + countryID);
                Log.d("FCM", "Subscribed to country topic");
                FirebaseMessaging.getInstance().subscribeToTopic("state_" + schoolID.substring(0, 8));
                Log.d("FCM", "Subscribed to state topic");

                // Made a mistake here: Subscribed to Regions (North/South/...) instead of the actual states.
                // Should have been substring of 8, but was 6 only.
                FirebaseMessaging.getInstance().unsubscribeFromTopic("state_" + prefs.getString(prefKeySchoolID, "[none]").substring(0, 6));
                FirebaseMessaging.getInstance().unsubscribeFromTopic("state_" + schoolID.substring(0, 6));


                transactionsSchools(schoolID, prefs.getString(prefKeySchoolID, "[none]"), userID);

            } else if (!prefs.contains(prefKeySchoolID)) {
                // For first school selection by this user
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("MySchool - Select school - First selection")
                        .setAction("sn(" + userID + ") S: " + schoolID + " (" + schoolName + ")")
                        .build());

                FirebaseMessaging.getInstance().subscribeToTopic("school_" + schoolID);
                Log.d("FCM", "Subscribed to school topic");
                FirebaseMessaging.getInstance().subscribeToTopic("country_" + countryID);
                Log.d("FCM", "Subscribed to country topic");
                FirebaseMessaging.getInstance().subscribeToTopic("state_" + schoolID.substring(0, 6));
                Log.d("FCM", "Subscribed to state topic");

            }

            prefs.edit().putString(prefKeyCountryID, countryID).apply();
            prefs.edit().putString(prefKeySchoolID, schoolID).apply();
            prefs.edit().putString(prefKeyCountryName, countryName).apply();
            prefs.edit().putString(prefKeySchoolName, schoolName).apply();

            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.toast_school_selection_went_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    private void transactionsSchools(String schoolNew, String schoolOld, String user){
        final int triesStart = 0;
        final int maxTries = 10;
        transactionsNrUsersNew(triesStart, maxTries, schoolNew);
        transactionsNrUsersOld(triesStart, maxTries, schoolOld);
        transactionsUserIDNew(triesStart, maxTries, schoolNew, user);
        transactionsUserIDOld(triesStart, maxTries, schoolOld, user);
    }

    private void transactionsNrUsersNew(final int tries, final int maxTries, final String school){
        if (tries <= maxTries && school != null && !school.equals("[none]")) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Schools NrUsers/"+school);

            myRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    final Object obj1 = mutableData.getValue();
                    System.out.println("old: "+String.valueOf(obj1));

                    if (obj1 != null) {
                        final long l1 = (long) obj1;
                        mutableData.setValue(l1 + 1);
                        System.out.println("new: "+String.valueOf(l1+1));
                    } else {
                        mutableData.setValue(1);
                        System.out.println("new: created (1)");
                    }

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (databaseError != null){
                        Log.d("FBDB", "transactionsNrUsersNew:onComplete:Error: " + String.valueOf(tries) + databaseError);
                        transactionsNrUsersNew(tries+1, maxTries, school);
                    } else {
                        // Transaction completed
                        Log.d("FBDB", "transactionsNrUsersNew:onComplete: " + String.valueOf(tries) + databaseError);
                    }
                }
            });
        }
    }

    private void transactionsNrUsersOld(final int tries, final int maxTries, final String school){
        if (tries <= maxTries && school != null && !school.equals("[none]")) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Schools NrUsers/"+school);

            myRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    final Object obj1 = mutableData.getValue();
                    System.out.println("old: "+String.valueOf(obj1));

                    if (obj1 != null) {
                        final long l1 = (long) obj1;
                        if (l1 <= 1){
                            mutableData.setValue(null);
                            System.out.println("new: null");
                        } else {
                            mutableData.setValue(l1 - 1);
                            System.out.println("new: " + String.valueOf(l1 - 1));
                        }
                    }

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (databaseError != null){
                        Log.d("FBDB", "transactionsNrUsersOld:onComplete:Error: " + String.valueOf(tries) + databaseError);
                        transactionsNrUsersOld(tries+1, maxTries, school);
                    } else {
                        // Transaction completed
                        Log.d("FBDB", "transactionsNrUsersOld:onComplete: " + String.valueOf(tries) + databaseError);
                    }
                }
            });
        }
    }

    private void transactionsUserIDNew(final int tries, final int maxTries, final String school, final String user){
        if (tries <= maxTries && school != null && !school.equals("[none]")
                && user != null && !user.equals("[none]")) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Schools Users/"+school+"/"+user);

            myRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    mutableData.setValue(true);

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (databaseError != null){
                        Log.d("FBDB", "transactionsUserIDNew:onComplete:Error: " + String.valueOf(tries) + databaseError);
                        transactionsUserIDNew(tries+1, maxTries, school, user);
                    } else {
                        // Transaction completed
                        Log.d("FBDB", "transactionsUserIDNew:onComplete: " + String.valueOf(tries) + databaseError);
                    }
                }
            });
        }
    }

    private void transactionsUserIDOld(final int tries, final int maxTries, final String school, final String user){
        if (tries <= maxTries && school != null && !school.equals("[none]")
                && user != null && !user.equals("[none]")) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Schools Users/"+school+"/"+user);

            myRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    mutableData.setValue(null);

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (databaseError != null){
                        Log.d("FBDB", "transactionsUserIDOld:onComplete:Error: " + String.valueOf(tries) + databaseError);
                        transactionsUserIDOld(tries+1, maxTries, school, user);
                    } else {
                        // Transaction completed
                        Log.d("FBDB", "transactionsUserIDOld:onComplete: " + String.valueOf(tries) + databaseError);
                    }
                }
            });
        }
    }

    //private void addSchoolToFBDB(String

    @Override
    public void onResume(){
        super.onResume();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}