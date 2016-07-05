package com.thinc_easy.schoolmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wassm on 2016-07-04.
 */
public class MySchoolAddSchoolFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName, country, school, url, linkText, added, shared;
    private SharedPreferences prefs;
    private TextView tvIntro, tvError, tvCountry, tvSchool, tvURL;
    private ScrollView svAddSchool, svShare;
    private Button bAddSchool, bShare;
    private EditText etCountry, etSchool, etURL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_school_add_school, container, false);

        fragmentName = "MySchoolAddSchoolFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        tvIntro = (TextView) v.findViewById(R.id.introText);
        tvError = (TextView) v.findViewById(R.id.tvError);
        tvCountry = (TextView) v.findViewById(R.id.tvCountry);
        tvSchool = (TextView) v.findViewById(R.id.tvSchool);
        tvURL = (TextView) v.findViewById(R.id.tvURL);
        svAddSchool = (ScrollView) v.findViewById(R.id.svAddSchool);
        svShare = (ScrollView) v.findViewById(R.id.svShare);
        bAddSchool = (Button) v.findViewById(R.id.bAddSchoolSave);
        bShare = (Button) v.findViewById(R.id.bShare);
        etCountry = (EditText) v.findViewById(R.id.etCountry);
        etSchool = (EditText) v.findViewById(R.id.etSchool);
        etURL = (EditText) v.findViewById(R.id.etURL);

        linkText = getActivity().getResources().getString(R.string.link_to_play_store_page);

        added = "false";
        shared = "false";

        setUpAddSchool();

        return v;
    }

    private void setUpAddSchool(){
        tvIntro.setVisibility(View.VISIBLE);
        svAddSchool.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.INVISIBLE);

        svShare.setVisibility(View.GONE);

        tvCountry.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf"));
        tvSchool.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf"));
        tvURL.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf"));

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
                url = etURL.getText().toString();

                if (country != null && !country.equals("") && !country.equals(" ") &&
                        school != null && !school.equals("") && !school.equals(" ")){
                    tvError.setVisibility(View.INVISIBLE);

                    if (url == null) url = "[none]";

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("MySchool - Add school")
                            .setAction("C: "+country+" || S: "+school+" || W: "+url)
                            .build());

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

    private void setUpShare(){
        tvIntro.setVisibility(View.GONE);
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

                shared = "true";

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("MySchool - Share app (after Add school)")
                        .setAction("C: "+country+" || S: "+school+" || W: "+url)
                        .build());
            }
        });
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
