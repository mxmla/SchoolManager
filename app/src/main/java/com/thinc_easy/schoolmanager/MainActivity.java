package com.thinc_easy.schoolmanager;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends ActionBarActivity implements DialogEditHomework.Communicator, DialogViewHomework.Communicator,  HomeworkAdapter.Communicator {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	//private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
     * A numeric value that identifies the notification that we'll be sending.
     * This value needs to be unique within this app, but it doesn't need to be
     * unique system-wide.
     */
    public static final int NOTIFICATION_ID = 1;
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
    private Toolbar toolbar;
	private CharSequence mTitle;
    private CharSequence mDrawerTitle;
	private String[] mActivityTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Fragment mShareAppFragment;
    private NavigationDrawerFragment1 drawerFragment;
    public static final String PREF_FILE_NAME = "testpref";
    private int mOpenMainActivityCount;
    public final int mHowOftenUntilShareApp = 25;
    private String KEY_OPEN_MAIN_ACTIVITY_COUNT = "open_main_activity_count";
    private String ttFolder;
    private CoordinatorLayout cl_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        drawerFragment = (NavigationDrawerFragment1)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_1);
        drawerFragment.setUp(R.id.fragment_navigation_drawer_1, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavigationDrawerFragment1 drawerFragment = (NavigationDrawerFragment1)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_1);
        drawerFragment.setUp(R.id.fragment_navigation_drawer_1, (DrawerLayout)findViewById(R.id.drawer_layout), toolbar);

        cl_main = (CoordinatorLayout) findViewById(R.id.cl_main);
		
		Fragment mMainFragment = getSupportFragmentManager().findFragmentByTag
                (MainFragment.DEFAULT_EDIT_FRAGMENT_TAG);

        mShareAppFragment = getSupportFragmentManager().findFragmentByTag
                (ShareAppFragment.DEFAULT_EDIT_FRAGMENT_TAG);

        startService(new Intent(this, NotificationService.class));


		/*mTitle = mDrawerTitle = getTitle();
		mActivityTitles = getResources().getStringArray(R.array.activity_titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mActivityTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  // host Activity
                mDrawerLayout,         // DrawerLayout object
                toolbar,  // nav drawer image to replace 'Up' caret
                R.string.drawer_open,  // "open drawer" description for accessibility
                R.string.drawer_close  // "close drawer" description for accessibility
                ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
		
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String pref_NotificationMethodUpdated = "NotificationMethodUpdated";
        String pref_NotificationUpdate2016_05_20 = "NotificationUpdate20160520";
        String pref_checkTtFieldsTxt2016_05_28 = "CheckTtFieldsTxt20160528";
        String pref_strorage_method_updated_2016_09_13 = "storage_method_updated_20160913";

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        mOpenMainActivityCount = Integer.valueOf(readFromPreferences(this, KEY_OPEN_MAIN_ACTIVITY_COUNT, "0"));

        /*if (!prefs.getBoolean(pref_NotificationMethodUpdated, false) || mOpenMainActivityCount%10 == 0) {
            DataStorageHandler.changeNotificationMethod(this);
        }

        if (!prefs.getBoolean(pref_NotificationUpdate2016_05_20, false)){
            DataStorageHandler.changeNotificationMethod(this, true);
        }

        if (!prefs.getBoolean(pref_checkTtFieldsTxt2016_05_28, false)){
            DataStorageHandler.checkTtFieldsTxt(this);
        }*/

        if (!prefs.getBoolean(pref_strorage_method_updated_2016_09_13, false)){
            File fSubjectsOld = new File(getExternalFilesDir(null), "Subjects.txt");
            File folderTimetablesNew  = new File(getExternalFilesDir(null), getResources().getString(R.string.folder_name_timetables));
            if (fSubjectsOld.exists() && !folderTimetablesNew.exists()){
                DataStorageHandler.updateStorageMethod20160913(this);
                prefs.edit().putBoolean(pref_strorage_method_updated_2016_09_13, true);
            } else {
                final String dismissedNewFeatureABWeeksKey = "dismissed_new_feature_ab_weeks";
                prefs.edit().putBoolean(dismissedNewFeatureABWeeksKey, true).apply();
            }
        }

        saveToPreferences(this, KEY_OPEN_MAIN_ACTIVITY_COUNT, String.valueOf(mOpenMainActivityCount + 1));
        /*if (mOpenMainActivityCount < mHowOftenUntilShareApp){
            saveToPreferences(this, KEY_OPEN_MAIN_ACTIVITY_COUNT, String.valueOf(mOpenMainActivityCount + 1));
        } else if (mOpenMainActivityCount == mHowOftenUntilShareApp){
            saveToPreferences(this, KEY_OPEN_MAIN_ACTIVITY_COUNT, "0");
        }*/

        updateDayNamesToIDs();

        ttFolder = prefs.getString(getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        //DataStorageHandler.RefreshTtAttributes(this, ttFolder);

        
		
		if (mMainFragment == null){
			mMainFragment = new MainFragment();
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.container, mMainFragment, 
					MainFragment.DEFAULT_EDIT_FRAGMENT_TAG);
			transaction.commit();
		}

    }

    private void updateDayNamesToIDs(){
        String isUpdated = readFromPreferences(this, "pref_are_daynames_saved_as_ids", "false");
        if (!isUpdated.equals("true")){
            DataStorageHandler.updateDayNamesToIDs(this);
            saveToPreferences(this, "pref_are_daynames_saved_as_ids", "true");
        }
    }

    public void refreshMainFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new MainFragment(),
                MainFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();
    }

    public void fabHomeworkClicked(){
        showEditHwDialog();
    }

    public void recyclerViewListClicked(String ID){
        showViewHwDialog(ID);
    }

    @Override
    public void recyclerViewDoneClicked(String ID) {
        refreshMainFragment();
    }

    public void showEditHwDialogForExistingHw(String ID){
        Bundle args = new Bundle();
        args.putString("caller", "Timetable");
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
        args.putString("caller", "Timetable");
        args.putString("ID", ID);
        FragmentManager manager = getSupportFragmentManager();
        DialogViewHomework myDialog = new DialogViewHomework();
        myDialog.setArguments(args);
        myDialog.show(manager, "myDialog");
    }

    public void showEditHwDialog(){
        Bundle args = new Bundle();
        args.putString("caller", "Timetable");
        args.putBoolean("existing", false);
        args.putString("subject", "-");
        args.putString("ID", "-");
        FragmentManager manager = getSupportFragmentManager();
        DialogEditHomework myDialog = new DialogEditHomework();
        myDialog.setArguments(args);
        myDialog.show(manager, "myDialog");
    }

    public void showEditHwDialog(String subjectID, String date){
        Bundle args = new Bundle();
        args.putString("caller", "Timetable");
        args.putBoolean("existing", false);
        args.putString("subject", subjectID);
        args.putString("date", date);
        args.putString("ID", "-");
        FragmentManager manager = getSupportFragmentManager();
        DialogEditHomework myDialog = new DialogEditHomework();
        myDialog.setArguments(args);
        myDialog.show(manager, "myDialog");
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
                .make(cl_main, R.string.snackbar_homework_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_homework_deleted_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteUndo(fContext, fID, fSubject, fDate, fTitle, fContent, fDone, fLessonID);
                    }
                })
                .show();
    }

    public void deleteUndo(Context context, String ID, String subject, String date, String hTitle, String hContent, String done, String lessonID){
        final String homeworkFilepath = ttFolder + "/" + getResources().getString(R.string.file_name_homework);
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

        DataStorageHandler.writeToCSVFile(this, file, myArray, Rows, Cols, "TimetableActivity deleteUndo");


        refreshMainFragment();
        //Snackbar.make(fabCoordinator, R.string.snackbar_homework_deleted_restored, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogMessageHomework(String title, String content, String subject, String date, String lessonID) {
        DataStorageHandler.createHomework(this, ttFolder, subject, date, title, content, lessonID);
        refreshMainFragment();
        /*createHomework(this, subject, date, title, content);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new MainFragment(), MainFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();*/
    }

    @Override
    public void onDialogMessageUpdateHomework(String ID, String title, String content, String subject, String date, String done, String lessonID) {
        DataStorageHandler.updateHomework(this, ttFolder, ID, subject, date, title, content, done, lessonID);
        refreshMainFragment();
        /*updateHomework(this, ID, subject, date, title, content, done);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new MainFragment(), MainFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();*/
    }

    public void onDialogMessageViewHomework(boolean edit, String ID){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new MainFragment(), MainFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();
        if (edit){
            showEditHwDialogForExistingHw(ID);
        }
    }

    @Override
    public void onDialogMessageDeleteHomework(String ID) {
        deleteHomework(this, ID);
        refreshMainFragment();
    }

    @Override
    public void onDialogMessageDoneClicked() {
        refreshMainFragment();
    }

    public void createHomework(Context context, String subject, String date, String hTitle, String hContent){
        String[][] myArray = toArray(context, "Homework.txt");
        int Rows = nmbrRowsCols(context, "Homework.txt")[0];
        int Cols = 6;

        File file = new File(getExternalFilesDir(null), "Homework.txt");

        // get the new ID
        int maxID = -1;
        for (int i1 = 0; i1 < Rows; i1++){
            int thisID = Integer.valueOf(myArray[i1][0]);
            if (thisID > maxID) maxID = thisID;
        }
        int ID = maxID + 1;

        // Save data to myArray
        String[][] myArray2 = new String[Rows + 1][Cols];
        for (int r = 0; r < Rows; r++) {
            for (int c = 0; c < Cols; c++) {
                myArray2[r][c] = myArray[r][c];
            }
        }
        myArray2[Rows][0] = String.valueOf(ID);
        myArray2[Rows][1] = subject;
        myArray2[Rows][2] = date;
        myArray2[Rows][3] = hTitle;
        myArray2[Rows][4] = hContent;
        myArray2[Rows][5] = "no";
        myArray = myArray2;
        Rows++;

        // write data to file
        try{
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            for (int t = 0; t < Rows; t++){
                if (myArray[t][0] != null & myArray[t][1] != null & myArray[t][2] != null & myArray[t][3] != null){
                    buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4] + "," + myArray[t][5]);
                    buf.newLine();
                }else{
                    Toast.makeText(this, "CANNOT save data:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateHomework(Context context, String ID, String subject, String date, String hTitle, String hContent, String done) {
        String[][] myArray = toArray(context, "Homework.txt");
        int Rows = nmbrRowsCols(context, "Homework.txt")[0];
        int Cols = 6;

        File file = new File(getExternalFilesDir(null), "Homework.txt");


        // Save data to myArray
        for (int r = 0; r < Rows; r++) {
            if (myArray[r][0].equals(ID)) {
                myArray[r][1] = subject;
                myArray[r][2] = date;
                myArray[r][3] = hTitle;
                myArray[r][4] = hContent;
                myArray[r][5] = done;
            }
        }

        // write data to file
        try{
            BufferedWriter buf = new BufferedWriter(new FileWriter(file));
            for (int t = 0; t < Rows; t++){
                if (myArray[t][0] != null & myArray[t][1] != null & myArray[t][2] != null & myArray[t][3] != null){
                    buf.write(myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3] + "," + myArray[t][4] + "," + myArray[t][5]);
                    buf.newLine();
                }else{
                    Toast.makeText(this, "CANNOT save data:" + myArray[t][0] + "," + myArray[t][1] + "," + myArray[t][2] + "," + myArray[t][3], Toast.LENGTH_SHORT).show();
                }
            }
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shareApp(){
        drawerFragment.upArrow();
        mShareAppFragment = new ShareAppFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, mShareAppFragment,
                ShareAppFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        transaction.commit();
    }
    public void endShareAppFragment(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawerFragment.noUpArrow();

        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        if (mShareAppFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(mShareAppFragment);
            transaction.commit();
        }
    }

    public String getSubjectFromPeriod(Context context, String day, String period){
        String[][] myArray = toArray(context, "Periods.txt");
        int Rows = nmbrRowsCols(context, "Periods.txt")[0];
        int Cols = nmbrRowsCols(context, "Periods.txt")[1];
        String[] sArray = new String[Cols];
        int row = 0;
        boolean existsSubject = false;

        for (int a = 0; a < Rows; a++){
            if (myArray[a][0].equals(day) & myArray[a][1].equals(period)){
                row = a;
                existsSubject = true;
            }
        }
        if (existsSubject == true){
            for (int b = 0; b < Cols; b++){
                sArray[b] = myArray[row][b];
            }
            return sArray[2];
        } else {
            return "-";
        }
    }

    public String[] getSubjectInfo(Context context, String sName){
        String[][] myArray = toArray(context, "Subjects.txt");
        int Rows = nmbrRowsCols(context, "Subjects.txt")[0];
        int Cols = nmbrRowsCols(context, "Subjects.txt")[1];
        String[] sArray = new String[Cols];
        int row = 0;
        boolean existsSubject = false;

        for (int a = 0; a < Rows; a++){
            if (myArray[a][0].equals(sName)){
                row = a;
                existsSubject = true;
            }
        }
        if (existsSubject = true){
            for (int b = 0; b < Cols; b++){
                sArray[b] = myArray[row][b];
            }
        }

        return sArray;
    }

    public String[][] ADaysSubjects(Context context, String day){
        String[][] myArray = toArray(context, "Subjects.txt");
        int Rows = nmbrRowsCols(context, "Subjects.txt")[0];
        String[][] sArray = new String[12][5];
        int counter = 0;
        String[] dayNames = getResources().getStringArray(R.array.DayNames);

        for (int a = 0; a < Rows; a++) {
            for (int b = 0; b < 5; b++) {
                if (myArray[a][4+b].equals(day)){
                    sArray[counter][0] = myArray[a][0];
                    sArray[counter][1] = myArray[a][1];
                    sArray[counter][2] = myArray[a][24];
                    sArray[counter][3] = myArray[a][25];
                    sArray[counter][4] = myArray[a][9+b];
                    counter = counter + 1;
                }
            }
        }

        String[][] newArray = new String[counter][5];
        for (int i = 0; i < counter; i++){
            newArray[i][0] = sArray[i][0];
            newArray[i][1] = sArray[i][1];
            newArray[i][2] = sArray[i][2];
            newArray[i][3] = sArray[i][3];
            newArray[i][4] = sArray[i][4];
        }

        return newArray;
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

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
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
        case R.id.action_settings:
        	Intent i2 = new Intent(this,
					SettingsActivity.class);
			startActivityForResult(i2, 0);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
    	//Start the selected Activity
	    switch (position){
	    	case 0:
	    		break;
	    	case 1:
	    		Intent i = new Intent(this,
						TimetableActivity.class);
				startActivityForResult(i, 0);
				break;
	    	case 2:
	    		Intent i2 = new Intent(this,
						SettingsActivity.class);
				startActivityForResult(i2, 0);
				break;
	    }
	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(position, true);
	    setTitle(mActivityTitles[position]);
	    mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
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

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        //mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	
	/**
     * Send a sample notification using the NotificationCompat API.
     */
    public void sendNotification(View view) {
 
        /** Create an intent that will be fired when the user clicks the notification.
         * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
         * notification service can fire it on our behalf.
         */
        Intent intent = new Intent(this, TimetableActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
 
        /**
         * Use NotificationCompat.Builder to set up our notification.
         */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
 
        /** Set the icon that will appear in the notification bar. This icon also appears
         * in the lower right hand corner of the notification itself.
         *
         * Important note: although you can use any drawable as the small icon, Android
         * design guidelines state that the icon should be simple and monochrome. Full-color
         * bitmaps or busy images don't render well on smaller screens and can end up
         * confusing the user.
         */
        builder.setSmallIcon(R.drawable.ic_launcher);
 
        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);
 
        // Set the notification to auto-cancel. This means that the notification will disappear
        // after the user taps it, rather than remaining until it's explicitly dismissed.
        builder.setAutoCancel(true);
 
        /**
         *Build the notification's appearance.
         * Set the large icon, which appears on the left of the notification. In this
         * sample we'll set the large icon to be the same as our app icon. The app icon is a
         * reasonable default if you don't have anything more compelling to use as an icon.
         */
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
 
        /**
         * Set the text of the notification. This sample sets the three most commononly used
         * text areas:
         * 1. The content title, which appears in large type at the top of the notification
         * 2. The content text, which appears in smaller text below the title
         * 3. The subtext, which appears under the text on newer devices. Devices running
         *    versions of Android prior to 4.2 will ignore this field, so don't use it for
         *    anything vital!
         */
        builder.setContentTitle("BasicNotifications Sample");
        builder.setContentText("Time to learn about notifications!");
        builder.setSubText("Tap to view documentation about notifications.");
 
 
        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
