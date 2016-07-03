package com.thinc_easy.schoolmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wassm on 2016-07-03.
 */
public class MySchoolSettingsFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private SharedPreferences prefs;
    private Spinner sCountry, sSchool;
    private String countryName, countryID, schoolName, schoolID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_school_settings, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        sCountry = (Spinner) v.findViewById(R.id.sCountry);
        sSchool = (Spinner) v.findViewById(R.id.sSchool);

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
    }

    private void initialSetup(){
        final String usa = getActivity().getResources().getString(R.string.country_united_states_of_america);
        final String germany = getActivity().getResources().getString(R.string.country_germany);
        final String[] countries = {usa, germany};
        final String[] countryIDs = {"0101", "0201"};

        List<String> usaSchools = new ArrayList<>();
        List<String> usaSchoolIDs = new ArrayList<>();
        List<String> germanySchools = new ArrayList<>();
        List<String> germanySchoolIDs = new ArrayList<>();

        String[][] allSchools = DataStorageHandler.toArray(getActivity(), "schools/AllSchools.txt", true);

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
            }
            public void onNothingSelected(AdapterView<?> parentView) {
                countryName = countries[0];
                countryID = countryIDs[0];
            }
        });
    }

}