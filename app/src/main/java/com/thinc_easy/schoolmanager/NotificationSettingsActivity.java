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
import android.widget.Toast;

/**
 * Created by M on 21.02.2016.
 */
public class NotificationSettingsActivity extends ActionBarActivity {
    public SharedPreferences.OnSharedPreferenceChangeListener spChanged;

    private AppCompatDelegate mDelegate;
    private Toolbar toolbar;
    private NavigationDrawerFragment1 drawerFragment;
    private SharedPreferences prefs;

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

        int sColor = getResources().getColor(R.color.color_settings);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(sColor));

        drawerFragment = (NavigationDrawerFragment1)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_1);
        drawerFragment.setUp(R.id.fragment_navigation_drawer_1, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


        Fragment mNotificationSettingsFragment = getSupportFragmentManager().findFragmentByTag
                (NotificationSettingsFragment.DEFAULT_EDIT_FRAGMENT_TAG);

        if (mNotificationSettingsFragment == null){
            mNotificationSettingsFragment = new NotificationSettingsFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mNotificationSettingsFragment,
                    NotificationSettingsFragment.DEFAULT_EDIT_FRAGMENT_TAG);
            transaction.commit();
        }


        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        /*spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                //UpdateNotificationsAfterSettingsChanged(mContext, key);
            }
        };*/
        //prefs.registerOnSharedPreferenceChangeListener(spChanged);
    }
}
