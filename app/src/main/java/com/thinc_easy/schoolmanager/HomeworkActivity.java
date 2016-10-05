package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;


public class HomeworkActivity extends ActionBarActivity implements DialogEditHomework.Communicator, DialogViewHomework.Communicator,
        HomeworkAdapter.Communicator, DialogAdInfo.AdInfoDialogListener{

    private Toolbar toolbar;
    private NavigationDrawerFragment1 drawerFragment;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private FloatingActionButton fab;
    private CoordinatorLayout fabCoordinator;
    private String ttFolder, homeworkFilepath;
    private File homeworkFile;
    private Context context;
    private SharedPreferences prefs;
    private InterstitialAd mInterstitialAd;
    private final int daysAdFree = 14;
    boolean adClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        drawerFragment = (NavigationDrawerFragment1)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_1);
        drawerFragment.setUp(R.id.fragment_navigation_drawer_1, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);


        context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ttFolder = prefs.getString(getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        homeworkFilepath = ttFolder + "/" + getResources().getString(R.string.file_name_homework);
        homeworkFile = new File(getExternalFilesDir(null), homeworkFilepath);

        showAdIfNecessary();

        /*Fragment mHomeworkFragment = getSupportFragmentManager().findFragmentByTag
                (HomeworkFragment.DEFAULT_EDIT_FRAGMENT_TAG);


        if (mHomeworkFragment == null){
            mHomeworkFragment = new HomeworkFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mHomeworkFragment,
                    HomeworkFragment.DEFAULT_EDIT_FRAGMENT_TAG);
            transaction.commit();
        }*/

                tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        pagerAdapter = new HomeworkPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getResources().getString(R.string.title_fragment_homework_pending).toString());
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.title_fragment_homework_done).toString());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.color_homework)}));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditHwDialog();
            }
        });

        fabCoordinator = (CoordinatorLayout) findViewById(R.id.fabCoordinator);
    }

    private void showAdIfNecessary(){
        boolean showAd = false;
        adClicked = false;
        if (prefs.contains("last_time_ad_clicked")) {
            String dateOld = prefs.getString("last_time_ad_clicked", "[none]");
            Date dOld = DataStorageHandler.getDateFromGeneralDateFormat(context, dateOld);
            if (dOld != null){
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

        if (showAd) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-9138088263014683/8826444457");

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homework, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent i2 = new Intent(this,
                        SettingsActivity.class);
                startActivityForResult(i2, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateViews(){
        int currentPage = viewPager.getCurrentItem();
        pagerAdapter = new HomeworkPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(getResources().getString(R.string.title_fragment_homework_pending).toString());
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.title_fragment_homework_done).toString());
        viewPager.setCurrentItem(currentPage);
    }

    public void recyclerViewListClicked(String ID){
        showViewHwDialog(ID);
    }
    public void recyclerViewDoneClicked(String ID) {
        updateViews();
    }


    public void onDialogMessageViewHomework(boolean edit, String ID){
        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeworkFragment(), HomeworkFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();*/
        updateViews();

        if (edit){
            showEditHwDialogForExistingHw(ID);
        }
    }

    @Override
    public void onDialogMessageDeleteHomework(String ID) {
        deleteHomework(this, ID);
        updateViews();
    }

    @Override
    public void onDialogMessageDoneClicked() {
        updateViews();
    }

    public void showEditHwDialogForExistingHw(String ID){
        Bundle args = new Bundle();
        args.putString("caller", "Homework");
        args.putBoolean("existing", true);
        args.putString("subject", "-");
        args.putString("ID", ID);
        FragmentManager manager = getSupportFragmentManager();
        DialogEditHomework myDialog = new DialogEditHomework();
        myDialog.setArguments(args);
        myDialog.show(manager, "myDialog");
    }

    public void showViewHwDialog(String ID){
        Bundle args = new Bundle();
        args.putString("caller", "Homework");
        args.putString("ID", ID);
        FragmentManager manager = getSupportFragmentManager();
        DialogViewHomework myDialog = new DialogViewHomework();
        myDialog.setArguments(args);
        myDialog.show(manager, "myDialog");
    }

    public void showEditHwDialog(){
        Bundle args = new Bundle();
        args.putString("caller", "Homework");
        args.putBoolean("existing", false);
        args.putString("subject", "-");
        args.putString("ID", "-");
        FragmentManager manager = getSupportFragmentManager();
        DialogEditHomework myDialog = new DialogEditHomework();
        myDialog.setArguments(args);
        myDialog.show(manager, "myDialog");
    }


    @Override
    public void onDialogMessageHomework(String title, String content, String subject, String date, String lessonID) {
        DataStorageHandler.createHomework(this, ttFolder, subject, date, title, content, lessonID);

        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeworkFragment(), HomeworkFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();*/

        updateViews();
    }

    @Override
    public void onDialogMessageUpdateHomework(String ID, String title, String content, String subject, String date, String done, String lessonID) {
        DataStorageHandler.updateHomework(this, ttFolder, ID, subject, date, title, content, done, lessonID);

        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeworkFragment(), HomeworkFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();*/

        updateViews();
    }

    public void deleteHomework(Context context, String ID){
        final String[] backupArray = DataStorageHandler.deleteHomeworkGetBackupArray(this, ttFolder, ID);

        final Context fContext = context;
        final String fID = backupArray[0];
        final String fSubject = backupArray[1];
        final String fDate = backupArray[2];
        final String fTitle = backupArray[3];
        final String fContent = backupArray[4];
        final String fDone = backupArray[5];
        final String fLessonID = backupArray[6];

        Snackbar
                .make(fabCoordinator, R.string.snackbar_homework_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_homework_deleted_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteUndo(fContext, fID, fSubject, fDate, fTitle, fContent, fDone, fLessonID);
                    }
                })
                .show();
    }

    public void deleteUndo(Context context, String ID, String subject, String date, String hTitle, String hContent, String done, String lessonID){
        String[][] myArray = DataStorageHandler.toArray(context, homeworkFilepath);
        int Rows = nmbrRowsCols(context, homeworkFilepath)[0];
        int Cols = 7;

        File file = new File(getExternalFilesDir(null), homeworkFilepath);


        // Save data to myArray
        String[][] newArray = new String[Rows+1][Cols];
        for (int r = 0; r < Rows; r++) {
            for (int c = 0; c < myArray[r].length; c++) newArray[r][c] = myArray[r][c];
            for (int c = myArray[r].length; c < Cols; c++) newArray[r][c] = "[none]";
        }
        newArray[Rows][0] = ID;
        newArray[Rows][1] = subject;
        newArray[Rows][2] = date;
        newArray[Rows][3] = hTitle;
        newArray[Rows][4] = hContent;
        newArray[Rows][5] = done;
        newArray[Rows][6] = lessonID;

        Rows = Rows + 1;

        myArray = newArray;

        DataStorageHandler.writeToCSVFile(this, file, myArray, Rows, Cols, "HomeworkActivity deleteUndo");


        updateViews();
        //Snackbar.make(fabCoordinator, R.string.snackbar_homework_deleted_restored, Snackbar.LENGTH_SHORT).show();
    }

    public String[][] toArray(Context context, String filename){

        int Rows = nmbrRowsCols(context, filename)[0];
        int Cols = nmbrRowsCols(context, filename)[1];
        String InputLine = "";
        Scanner scanIn = null;

        // Read existing file
        String[][] myArray = new String[Rows][Cols];
        int Rowc = 0;
        scanIn = null;
        InputLine = "";


        File file = new File(getExternalFilesDir(null), filename);
        try{
            scanIn = new Scanner(new BufferedReader(new FileReader(file)));

            while (scanIn.hasNextLine()){
                InputLine = scanIn.nextLine();
                String[] InArray = InputLine.split(",");
                for (int x = 0; x < InArray.length; x++){
                    myArray[Rowc][x] = String.valueOf(InArray[x]);
                }
                Rowc++;
            }
            scanIn.close();
        }catch (Exception e){
            System.out.println(e);
        }

        return myArray;
    }

    public int[] nmbrRowsCols(Context context, String filename){
        String InputLine = "";
        Scanner scanIn = null;

        // Get number of rows and columns
        int Rows = 0;
        int Cols = 0;

        File file = new File(getExternalFilesDir(null), filename);
        try{
            scanIn = new Scanner(new BufferedReader(new FileReader(file)));
            while (scanIn.hasNextLine()){
                InputLine = scanIn.nextLine();
                String[] InArray = InputLine.split(",");
                Rows++;
                Cols = InArray.length;
            }
            scanIn.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        int[] RowsCols = new int[2];
        RowsCols[0] = Rows;
        RowsCols[1] = Cols;
        return RowsCols;
    }
}
