package com.thinc_easy.schoolmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.InputStream;

/**
 * Created by wassm on 2016-07-02.
 */
public class MySchoolFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName;
    private SharedPreferences prefs;
    private WebView webView;
    private TextView tvNoSchool;
    private Button goToSettings;
    private String url, schoolID, prefKeySchoolID;
    private int whichWebPage;
    private ProgressDialog progDailog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_school, container, false);

        fragmentName = "MySchoolFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        int hColor = getActivity().getResources().getColor(R.color.color_myschool_appbar);
        ((MySchoolActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(hColor));

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        prefKeySchoolID = getActivity().getResources().getString(R.string.pref_key_my_school_school_id);

        whichWebPage = 1;

        tvNoSchool = (TextView) v.findViewById(R.id.tvNoSchool);
        goToSettings = (Button) v.findViewById(R.id.selectSchoolButton);

        webView = (WebView) v.findViewById(R.id.webView);
        loadURL();

        return v;
    }

    private void loadURL(){
        String[][] AllSchoolsURLs = DataStorageHandler.toArray(getActivity(), "schools/AllSchoolsURLs.txt", true);

        boolean foundURL = false;
        if (prefs.contains(prefKeySchoolID)){
            schoolID = prefs.getString(prefKeySchoolID, "[none]");
            if (schoolID != null && !schoolID.equals("[none]")){
                for (int i = 0; i < AllSchoolsURLs.length; i++){
                    if (AllSchoolsURLs[i][0].equals(schoolID) && AllSchoolsURLs[i][1].equals(String.valueOf(whichWebPage))){
                        url = AllSchoolsURLs[i][2];

                        tvNoSchool.setVisibility(View.GONE);
                        goToSettings.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        webView.setWebViewClient(new WebViewClient());
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setBuiltInZoomControls(true);
                        webView.getSettings().setDisplayZoomControls(false);
                        webView.getSettings().setSupportMultipleWindows(true);
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setUseWideViewPort(true);

                        webView.loadUrl(url);
                        foundURL = true;
                    }
                }
            }
        }

        if (!foundURL){
            webView.setVisibility(View.GONE);
            tvNoSchool.setVisibility(View.VISIBLE);
            goToSettings.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                goToSettings.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.my_school_button_color_state_list));
                goToSettings.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
            } else {
                goToSettings.setTextColor(getActivity().getResources().getColor(R.color.color_myschool));
            }

            goToSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(getActivity(),
                            MySchoolSettingsActivity.class);
                    startActivityForResult(i1, 0);
                }
            });
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        String newSchoolID = prefs.getString(prefKeySchoolID, "[none]");
        if (!newSchoolID.equals(schoolID)) loadURL();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    /*@Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Toast.makeText(getActivity(), "onKey", Toast.LENGTH_SHORT).show();

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    Toast.makeText(getActivity(), String.valueOf(webView.canGoBack()), Toast.LENGTH_SHORT).show();
                    if (webView.canGoBack()){
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
    }*/
}


