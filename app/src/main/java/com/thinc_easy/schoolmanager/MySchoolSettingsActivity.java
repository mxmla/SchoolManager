package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

/**
 * Created by wassm on 2016-07-03.
 */
public class MySchoolSettingsActivity extends ActionBarActivity {
    public SharedPreferences.OnSharedPreferenceChangeListener spChanged;

    private AppCompatDelegate mDelegate;
    private Toolbar toolbar;
    private NavigationDrawerFragment1 drawerFragment;

    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        final Context mContext = this;

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int sColor = getResources().getColor(R.color.color_settings_appbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(sColor));

        drawerFragment = (NavigationDrawerFragment1)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_1);
        drawerFragment.setUp(R.id.fragment_navigation_drawer_1, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


        Fragment mMySchoolSettingsFragment = getSupportFragmentManager().findFragmentByTag
                (MySchoolFragment.DEFAULT_EDIT_FRAGMENT_TAG);

        if (mMySchoolSettingsFragment == null){
            mMySchoolSettingsFragment = new MySchoolSettingsFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mMySchoolSettingsFragment,
                    MySchoolSettingsFragment.DEFAULT_EDIT_FRAGMENT_TAG);
            transaction.commit();
        }
    }

    public void showAddSchoolFragment(){
        Fragment mMySchoolAddSchoolFragment = getSupportFragmentManager().findFragmentByTag
                (MySchoolAddSchoolFragment.DEFAULT_EDIT_FRAGMENT_TAG);


        mMySchoolAddSchoolFragment = new MySchoolAddSchoolFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mMySchoolAddSchoolFragment,
                MySchoolAddSchoolFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();
    }
}