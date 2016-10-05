package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wassm on 2016-07-02.
 */
public class MySchoolActivity extends ActionBarActivity implements DialogAdInfo.AdInfoDialogListener{
    public static final int NOTIFICATION_ID = 1;
    Fragment mMySchoolFragment;

    private Toolbar toolbar;
    private CharSequence mTitle;
    private NavigationDrawerFragment1 drawerFragment;
    public static final String PREF_FILE_NAME = "testpref";
    private int mOpenMySchoolActivityCount;
    public final int mHowOftenUntilShareApp = 25;
    private String KEY_OPEN_MY_SCHOOL_ACTIVITY_COUNT = "open_my_school_activity_count";
    private Context context;
    private SharedPreferences prefs;
    private InterstitialAd mInterstitialAd;
    private final int daysAdFree = 14;
    boolean adClicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_school);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        drawerFragment = (NavigationDrawerFragment1)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_1);
        drawerFragment.setUp(R.id.fragment_navigation_drawer_1, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavigationDrawerFragment1 drawerFragment = (NavigationDrawerFragment1)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_1);
        drawerFragment.setUp(R.id.fragment_navigation_drawer_1, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        mMySchoolFragment = getSupportFragmentManager().findFragmentByTag
                (MySchoolFragment.DEFAULT_EDIT_FRAGMENT_TAG);


        context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mOpenMySchoolActivityCount = Integer.valueOf(readFromPreferences(this, KEY_OPEN_MY_SCHOOL_ACTIVITY_COUNT, "0"));
        saveToPreferences(this, KEY_OPEN_MY_SCHOOL_ACTIVITY_COUNT, String.valueOf(mOpenMySchoolActivityCount + 1));

        //prefs.edit().putString("last_time_ad_clicked", "[none]").apply();
        showAdIfNecessary();

        if (mMySchoolFragment == null) {
            mMySchoolFragment = new MySchoolFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mMySchoolFragment,
                    MySchoolFragment.DEFAULT_EDIT_FRAGMENT_TAG);
            transaction.commit();
        }
    }

    private void showAdIfNecessary(){
        boolean showAd = false;
        adClicked = false;
        if (prefs.contains("open_main_activity_count") && Integer.parseInt(prefs.getString("open_main_activity_count", "0"))>10
                && (!prefs.contains("first_registered_use_date") || !prefs.getString("first_registered_use_date", "[none]").equals(DataStorageHandler.formatDateGeneralFormat(context, Calendar.getInstance())))) {

            if (prefs.contains("last_time_ad_clicked")) {
                String dateOld = prefs.getString("last_time_ad_clicked", "[none]");
                Date dOld = DataStorageHandler.getDateFromGeneralDateFormat(context, dateOld);
                if (dOld != null) {
                    Calendar cTimeAgo = Calendar.getInstance();
                    cTimeAgo.add(Calendar.DAY_OF_YEAR, -daysAdFree);
                    Date dTimeAgo = cTimeAgo.getTime();
                    if (dOld.before(dTimeAgo)) showAd = true;
                } else {
                    showAd = true;
                }
            } else {
                showAd = true;
            }
        }

        if (showAd) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-9138088263014683/8547242853");

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLeftApplication() {
                    System.out.println("onAdLeftApplication");
                    super.onAdLeftApplication();
                    String date = DataStorageHandler.formatDateGeneralFormat(context, Calendar.getInstance());
                    prefs.edit().putString("last_time_ad_clicked", date).apply();
                    adClicked = true;
                }
                @Override
                public void onAdLoaded(){
                    super.onAdLoaded();
                    DialogFragment dialogAdInfo = new DialogAdInfo();
                    dialogAdInfo.show(getSupportFragmentManager(), "adInfo");
                }
                @Override
                public void onAdClosed(){
                    super.onAdClosed();
                    if (adClicked) Toast.makeText(context, getResources().getString(R.string.toast_ads_removed), Toast.LENGTH_LONG).show();
                }
            });

            requestNewInterstitial();
        }
    }

    @Override
    public void onDialogPause(DialogFragment dialog) {
        showInterstitialAd();
    }

    public void showInterstitialAd() {
        mInterstitialAd.show();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_school, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_example).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        /*if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_select_school:
                Intent i1 = new Intent(this,
                        MySchoolSettingsActivity.class);
                startActivityForResult(i1, 0);
                return true;
            case R.id.action_settings:
                Intent i2 = new Intent(this,
                        SettingsActivity.class);
                startActivityForResult(i2, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }
    public static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }
}