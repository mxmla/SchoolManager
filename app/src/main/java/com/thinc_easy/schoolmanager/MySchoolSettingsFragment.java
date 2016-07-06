package com.thinc_easy.schoolmanager;

import android.content.SharedPreferences;
import android.graphics.Typeface;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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
    List<String> usaSchools, usaSchoolIDs, germanySchools, germanySchoolIDs;
    private String[][] allSchools;
    private Button nitlButton;

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

        initialSetup();

        return v;
    }

    @Override
    public void onPause(){
        saveData();
        super.onPause();
    }

    private void saveData(){
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.toast_saved), Toast.LENGTH_LONG).show();


        if (prefs.contains(prefKeySchoolID) && !prefs.getString(prefKeySchoolID, "-").equals(schoolID)
                && prefs.contains(prefKeyCountryID)){
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("MySchool - Select school - Selection changed")
                    .setAction("NEW C: "+countryID+" ("+countryName+"), S: "+schoolID+" ("+schoolName+
                            ") || OLD C: "+prefs.getString(prefKeyCountryID, "[none]")+", S: "+
                            prefs.getString(prefKeySchoolID, "[none]"))
                    .build());
        } else if (!prefs.contains(prefKeySchoolID)) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("MySchool - Select school - First selection")
                    .setAction("SELECTED C: "+countryID+" ("+countryName+") || S: "+schoolID+" ("+schoolName+")")
                    .build());
        }

        prefs.edit().putString(prefKeyCountryID, countryID).apply();
        prefs.edit().putString(prefKeySchoolID, schoolID).apply();
        prefs.edit().putString(prefKeyCountryName, countryName).apply();
        prefs.edit().putString(prefKeySchoolName, schoolName).apply();
    }

    private void initialSetup(){
        final String usa = getActivity().getResources().getString(R.string.country_united_states_of_america);
        final String germany = getActivity().getResources().getString(R.string.country_germany);
        final String[] countries = {getActivity().getResources().getString(R.string.spinner_please_select), usa, germany};
        final String[] countryIDs = {getActivity().getResources().getString(R.string.spinner_please_select), "0101", "0201"};

        usaSchools = new ArrayList<>();
        usaSchoolIDs = new ArrayList<>();
        germanySchools = new ArrayList<>();
        germanySchoolIDs = new ArrayList<>();

        usaSchools.add(getActivity().getResources().getString(R.string.spinner_please_select));
        usaSchoolIDs.add(getActivity().getResources().getString(R.string.spinner_please_select));
        germanySchools.add(getActivity().getResources().getString(R.string.spinner_please_select));
        germanySchoolIDs.add(getActivity().getResources().getString(R.string.spinner_please_select));

        allSchools = DataStorageHandler.toArray(getActivity(), "schools/AllSchools.txt", true);

        for (int i = 0; i < allSchools.length; i++){
            if (allSchools[i][0].startsWith("0101")){
                usaSchools.add(allSchools[i][1]);
                usaSchoolIDs.add(allSchools[i][0]);
            } else if (allSchools[i][0].startsWith("0201")){
                germanySchools.add(allSchools[i][1]);
                germanySchoolIDs.add(allSchools[i][0]);
            }
        }

        countryName = getActivity().getResources().getString(R.string.country_united_states_of_america);
        countryID = "0101";
        schoolName = "[none]";
        schoolID = "[none]";


        ArrayAdapter<String> adapterCountries = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, countries);
        adapterCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCountry.setAdapter(adapterCountries);
        sCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
                countryName = countries[parentView.getSelectedItemPosition()];
                countryID = countryIDs[parentView.getSelectedItemPosition()];
                setUpSchoolsSpinner(countryID, "[none]");
            }
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        boolean schoolsSpinnerSetUp = false;
        if (prefs.contains(prefKeyCountryID)){
            String prefCountryID = prefs.getString(prefKeyCountryID, "[none]");
            for (int c = 0; c < countryIDs.length; c++){
                if (countryIDs[c].equals(prefCountryID)){
                    countryID = prefCountryID;
                    countryName = countries[c];

                    sCountry.setSelection(c);

                    if (prefs.contains(prefKeySchoolID)){
                        String prefSchoolID = prefs.getString(prefKeySchoolID, "[none]");
                        for (int s = 0; s < allSchools.length; s++){
                            if (allSchools[s][0].equals(prefSchoolID)){
                                schoolID = prefSchoolID;
                                schoolName = allSchools[s][1];

                                setUpSchoolsSpinner(countryID, schoolID);
                                schoolsSpinnerSetUp = true;
                            }
                        }
                    }
                }
            }
        }

        if (!schoolsSpinnerSetUp) setUpSchoolsSpinner(countryID, "[none]");

        setUpNotInTheListButton();

    }

    private void setUpSchoolsSpinner(String countryID, String selectedSchoolID){
        List<String> schools;
        List<String> schoolIDs;
        if (countryID.equals("0101")){
            schools = usaSchools;
            schoolIDs = usaSchoolIDs;
        } else if (countryID.equals("0201")) {
            schools = germanySchools;
            schoolIDs = germanySchoolIDs;
        } else {
            schools = new ArrayList<>();
            schools.add(getActivity().getResources().getString(R.string.spinner_please_select));
            schoolIDs = new ArrayList<>();
            schoolIDs.add(getActivity().getResources().getString(R.string.spinner_please_select));
        }

        if (schoolIDs.size()>0) schoolID = schoolIDs.get(0);

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

        if (selectedSchoolID != null && !selectedSchoolID.equals("[none]") && !selectedSchoolID.equals("")){
            for (int i = 0; i < fSchoolIDs.size(); i++){
                if (fSchoolIDs.get(i).equals(selectedSchoolID)) sSchool.setSelection(i);
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

    @Override
    public void onResume(){
        super.onResume();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}