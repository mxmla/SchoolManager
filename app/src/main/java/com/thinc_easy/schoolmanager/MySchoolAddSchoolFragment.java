package com.thinc_easy.schoolmanager;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by wassm on 2016-07-04.
 */
public class MySchoolAddSchoolFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName, country, school, url, linkText, added, shared, city, state, fbSchoolName;
    private SharedPreferences prefs;
    private TextView tvError, tvCountry, tvSchool, tvURL, tvCity, tvState;
    private ScrollView svAddSchool, svShare;
    private Button bAddSchool, bShare;
    private EditText etCountry, etSchool, etURL, etCity, etState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_school_add_school, container, false);

        fragmentName = "MySchoolAddSchoolFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        //tvIntro = (TextView) v.findViewById(R.id.introText);
        tvError = (TextView) v.findViewById(R.id.tvError);
        tvCountry = (TextView) v.findViewById(R.id.tvCountry);
        tvSchool = (TextView) v.findViewById(R.id.tvSchool);
        tvCity = (TextView) v.findViewById(R.id.tvCity);
        tvURL = (TextView) v.findViewById(R.id.tvURL);
        tvState = (TextView) v.findViewById(R.id.tvState);
        svAddSchool = (ScrollView) v.findViewById(R.id.svAddSchool);
        svShare = (ScrollView) v.findViewById(R.id.svShare);
        bAddSchool = (Button) v.findViewById(R.id.bAddSchoolSave);
        bShare = (Button) v.findViewById(R.id.bShare);
        etCountry = (EditText) v.findViewById(R.id.etCountry);
        etSchool = (EditText) v.findViewById(R.id.etSchool);
        etCity = (EditText) v.findViewById(R.id.etCity);
        etURL = (EditText) v.findViewById(R.id.etURL);
        etState = (EditText) v.findViewById(R.id.etState);

        linkText = getActivity().getResources().getString(R.string.link_to_play_store_page);

        added = "false";
        shared = "false";

        setUpAddSchool();

        return v;
    }

    private void setUpAddSchool(){
        //tvIntro.setVisibility(View.VISIBLE);
        svAddSchool.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.INVISIBLE);

        svShare.setVisibility(View.GONE);

        tvCountry.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf"));
        tvSchool.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf"));
        tvCity.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf"));
        tvURL.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf"));
        tvState.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bAddSchool.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
            bAddSchool.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
        } else {
            bAddSchool.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
        }

        bAddSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                country = etCountry.getText().toString();
                school = etSchool.getText().toString();
                city = etCity.getText().toString();
                url = etURL.getText().toString();
                state = etState.getText().toString();


                if (country != null && !country.equals("") && !country.equals(" ") &&
                        state != null && !state.equals("") && !state.equals(" ") &&
                        school != null && !school.equals("") && !school.equals(" ") &&
                        city != null && !city.equals("") && !city.equals(" ")){
                    tvError.setVisibility(View.INVISIBLE);

                    if (url == null) url = "[none]";


                    String userID = "[none]";
                    final String keyUserID = getActivity().getResources().getString(R.string.pref_key_user_id);
                    if (prefs.contains(keyUserID)){
                        userID = prefs.getString(keyUserID, "[none]");
                    }
                    if (userID == null || userID.equals("") || userID.equals("[none]")){
                        userID = registerAddSchool(country, school, url);
                        prefs.edit().putString(keyUserID, userID).apply();
                    }

                    transactionsFBAddSchool(country, state, city, school, url, userID);

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("MySchool - Add school")
                            .setAction("+("+userID+") "+country+" | "+city+" | "+school+" | "+url)
                            .setLabel("+"+school+" ("+userID+")")
                            .build());

                    Calendar calendar = Calendar.getInstance();
                    String date = DataStorageHandler.formatDateGeneralFormat(getActivity(), calendar);

                    FirebaseMessaging.getInstance().subscribeToTopic("addded_school--"+date);
                    Log.d("FCM", "Subscribed to added_school topic");

                    added = "true";
                    setUpShare();

                } else {
                    country = "[none]";
                    school = "[none]";
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String registerAddSchool(String cName, String sName, String mURL){
        final String registerFilename = getActivity().getResources().
                getString(R.string.filename_my_school_add_school_my_suggestions);
        final int[] registerRowsColsOld = DataStorageHandler.nmbrRowsCols(getActivity(), registerFilename);
        final int registerRowsOld = registerRowsColsOld[0];
        final int registerColsOld = registerRowsColsOld[1];

        int registerColsNew = 5;
        String[][] registerArray = DataStorageHandler.toArray(getActivity(), registerFilename);

        String[][] newRegisterArray;
        if (registerColsNew >= registerColsOld) {
            newRegisterArray = new String[registerRowsOld + 1][registerColsNew];
        } else {
            newRegisterArray = new String[registerRowsOld + 1][registerColsOld];
        }

        for (int i = 0; i < registerRowsOld; i++){
            for (int c = 0; c < registerColsOld; c++){
                newRegisterArray[i][c] = registerArray[i][c];
            }
        }

        SecureRandom random = new SecureRandom();
        final String nextSessionId = new BigInteger(130, random).toString(32);

        newRegisterArray[registerRowsOld][0] = nextSessionId;
        newRegisterArray[registerRowsOld][1] = DataStorageHandler.formatDateGeneralFormat(getActivity(), Calendar.getInstance());
        newRegisterArray[registerRowsOld][2] = cName;
        newRegisterArray[registerRowsOld][3] = sName;
        newRegisterArray[registerRowsOld][4] = mURL;

        File file = new File(getActivity().getExternalFilesDir(null), registerFilename);
        DataStorageHandler.writeToCSVFile(getActivity(), file, newRegisterArray, registerRowsOld+1, registerColsNew, "MySchoolAddSchoolFragment");

        return nextSessionId;
    }

    private void setUpShare(){
        //tvIntro.setVisibility(View.GONE);
        svAddSchool.setVisibility(View.GONE);

        svShare.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bShare.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
            bShare.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
        } else {
            bShare.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
        }

        bShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, linkText);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

                int nrShares = 0;
                if (prefs.contains(getActivity().getResources().getString(R.string.pref_key_number_shares))){
                    nrShares = prefs.getInt(getActivity().getResources().getString(R.string.pref_key_number_shares), 0);
                }
                nrShares ++;
                prefs.edit().putInt(getActivity().getResources().getString(R.string.pref_key_number_shares), nrShares).apply();

                shared = "true";

                if (fbSchoolName != null) transactionsFBClickedShare(fbSchoolName);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("MySchool - Share app (after Add school)")
                        .setAction("C: "+country+" || S: "+school+" || W: "+url)
                        .build());
            }
        });
    }

    private void transactionsFBAddSchool(String mCountry, String mState, String mCity, String mSchool, String mURL, String mUserID){
        final int triesStart = 0;
        final int maxTries = 10;
        transactionAddSchool(triesStart, maxTries, mCountry, mState, mCity, mSchool, mURL, mUserID);
    }

    private void transactionAddSchool(final int tries, final int maxTries, final String mCountry,
                                      final String mState, final String mCity, final String mSchool,
                                      final String mURL, final String mUserID){
        if (tries <= maxTries && mSchool != null && !mSchool.equals("[none]")) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("AddSchool Requested Schools");

            myRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    int count = 0;
                    String value_name = mSchool;
                    while (count < 50){
                        if (mutableData.child(value_name).getValue() == null && !mutableData.child(value_name).hasChildren()){
                            mutableData.child(value_name).child("user_id").setValue(mUserID);
                            mutableData.child(value_name).child("country").setValue(mCountry);
                            mutableData.child(value_name).child("state").setValue(mState);
                            mutableData.child(value_name).child("city").setValue(mCity);
                            mutableData.child(value_name).child("url").setValue(mURL);

                            fbSchoolName = value_name;
                            break;
                        } else {
                            count ++;
                            value_name = mSchool + "-" + String.valueOf(count);
                        }
                    }

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (databaseError != null){
                        Log.d("FBDB", "transactionAddSchool:onComplete:Error: " + String.valueOf(tries) + databaseError);
                        transactionAddSchool(tries+1, maxTries, mCountry, mState, mCity, mSchool, mURL, mUserID);
                    } else {
                        // Transaction completed
                        Log.d("FBDB", "transactionAddSchool:onComplete: " + String.valueOf(tries) + databaseError);
                    }
                }
            });
        }
    }

    private void transactionsFBClickedShare(String mSchoolValueName){
        final int triesStart = 0;
        final int maxTries = 10;
        transactionShare(triesStart, maxTries, mSchoolValueName);
    }

    private void transactionShare(final int tries, final int maxTries, final String mSchoolValueName){
        if (tries <= maxTries && mSchoolValueName != null && !mSchoolValueName.equals("[none]")) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("AddSchool Requested Schools");

            myRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    if (mutableData.child(mSchoolValueName).getValue() != null){
                        final Object obj1 = mutableData.child(mSchoolValueName).child("shared").getValue();
                        System.out.println("old: "+String.valueOf(obj1));

                        if (obj1 != null) {
                            final long l1 = (long) obj1;
                            mutableData.child(mSchoolValueName).child("shared").setValue(l1 + 1);
                            System.out.println("shared: "+String.valueOf(l1+1));
                        } else {
                            mutableData.child(mSchoolValueName).child("shared").setValue(1);
                            System.out.println("shared: created (1)");
                        }
                    }

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (databaseError != null){
                        Log.d("FBDB", "transactionAddSchool:onComplete:Error: " + String.valueOf(tries) + databaseError);
                        transactionShare(tries+1, maxTries, mSchoolValueName);
                    } else {
                        // Transaction completed
                        Log.d("FBDB", "transactionAddSchool:onComplete: " + String.valueOf(tries) + databaseError);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("MySchool - AddSchool/ShareApp - onDestroy")
                .setAction("Added: "+added+" || Shared: "+shared+" || C: "+country+" || S: "+school+" || W: "+url)
                .build());
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
