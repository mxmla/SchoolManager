package com.thinc_easy.schoolmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TimetableActivity extends ActionBarActivity
		implements DialogEditHomework.Communicator, DialogViewHomework.Communicator{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private Toolbar toolbar;
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
	private String[] mActivityTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
	private NavigationDrawerFragment1 drawerFragment;
    
	private Fragment mLessonFragment;
	private Fragment mSubjectsListFragment;

	public boolean isLessonFragmentActive;
	public boolean isSubjectsListFragmentActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		drawerFragment = (NavigationDrawerFragment1)
				getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_1);
		drawerFragment.setUp(R.id.fragment_navigation_drawer_1, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        Fragment mTimetableFragment = getSupportFragmentManager().findFragmentByTag
				(TimetableFragment.DEFAULT_EDIT_FRAGMENT_TAG);

		isLessonFragmentActive = false;

		mTitle = getResources().getString(R.string.title_activity_timetable);

		//FloatingActionButton();
        
        
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
            selectItem(1);
        } */

		// You can be pretty confident that the intent will not be null here.
		Intent intent = getIntent();
		boolean intentLesson = false;

		// Get the extras (if there are any)
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (extras.containsKey("action")) {
				if (getIntent().getExtras().getString("action").equals("lesson")){
					int dayInt = getIntent().getExtras().getInt("dayInt");
					int periodInt = getIntent().getExtras().getInt("periodInt");
					LessonFragment(dayInt, periodInt);
					intentLesson = true;
				}
			}
		}
		if (!intentLesson){
			if (mTimetableFragment == null) {
				mTimetableFragment = new TimetableFragment();

				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.add(R.id.container, mTimetableFragment,
						TimetableFragment.DEFAULT_EDIT_FRAGMENT_TAG);
				transaction.commit();
			}
		}
    }

    @Override
    public void onBackPressed(){
		// You can be pretty confident that the intent will not be null here.
		Intent intent = getIntent();
		boolean intentLesson = false;

		// Get the extras (if there are any)
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (extras.containsKey("caller") && extras.containsKey("action")) {
				if (getIntent().getExtras().getString("caller").equals("home") && getIntent().getExtras().getString("action").equals("lesson")){
					Intent i = new Intent(this, MainActivity.class);
					startActivityForResult(i, 0);
					intentLesson = true;
				}
			}
		}
		if (!intentLesson) {
			super.onBackPressed();
			// turn on the NavDrawer image. This Method is called in lower-level fragments.
			//mDrawerToggle.setDrawerIndicatorEnabled(true);
		}
    }

    public void LessonFragment(int dayInt, int periodInt){
    	mLessonFragment = getSupportFragmentManager().findFragmentByTag
				(LessonFragment.DEFAULT_EDIT_FRAGMENT_TAG);

		isLessonFragmentActive = true;
		drawerFragment.upArrow();
		
		//mDrawerToggle.setDrawerIndicatorEnabled(false);
		//mDrawerToggle.setHomeAsUpIndicator(getV7DrawerToggleDelegate().getThemeUpIndicator());

		mLessonFragment = new LessonFragment();
		Bundle args = new Bundle();
        args.putInt("dayInt", dayInt);
        args.putInt("periodInt", periodInt);
        args.putString("caller", "Timetable");
        mLessonFragment.setArguments(args);
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container, mLessonFragment,
				LessonFragment.DEFAULT_EDIT_FRAGMENT_TAG).addToBackStack(null);
		transaction.commit();
    }

	public void endLessonFragment(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		drawerFragment.noUpArrow();
		isLessonFragmentActive = false;

		getSupportActionBar().setTitle(mTitle);
	}

	public void SubjectsListFragment(){
		if (!isSubjectsListFragmentActive) {
			mSubjectsListFragment = getSupportFragmentManager().findFragmentByTag
					(SubjectsListFragment.DEFAULT_EDIT_FRAGMENT_TAG);

			mSubjectsListFragment = new SubjectsListFragment();
			Bundle args = new Bundle();
			args.putString("caller", "Timetable");
			mSubjectsListFragment.setArguments(args);

			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.container, mSubjectsListFragment,
					SubjectsListFragment.DEFAULT_EDIT_FRAGMENT_TAG).addToBackStack(null);
			transaction.commit();
			isSubjectsListFragmentActive = true;
		} else {
			mSubjectsListFragment = getSupportFragmentManager().findFragmentByTag
					(SubjectsListFragment.DEFAULT_EDIT_FRAGMENT_TAG);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.hide(mSubjectsListFragment).addToBackStack(null);
			transaction.commit();
			isSubjectsListFragmentActive = false;
		}
	}

	public void endSubjectsListFragment(){
		/*getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		drawerFragment.noUpArrow();*/
		isSubjectsListFragmentActive = false;

		getSupportActionBar().setTitle(mTitle);
	}

	private void FloatingActionButton(){
		final FloatingActionButton fabPlus = (FloatingActionButton) findViewById(R.id.fab);
		final View overlay = findViewById(R.id.overlay);
		final FloatingActionButton fabHomework = (FloatingActionButton) findViewById(R.id.fabHomework);
		final TextView tvFabHomework = (TextView) findViewById(R.id.fabHomeworkTextView);

		final float fabMargin = getResources().getDimension(R.dimen.fab_margin);
		final float fabNormalSize = getResources().getDimension(R.dimen.fab_normal_size);
		final float fabMiniSize = getResources().getDimension(R.dimen.fab_mini_size);
		final float oldY = -1f * (fabMargin + fabNormalSize);

		fabHomework.setX(-1f * (fabMargin + ((fabNormalSize - fabMiniSize) / 2f)));
		fabHomework.setY(oldY);
		tvFabHomework.setX(-1f * ((2f * fabMargin) + fabNormalSize + tvFabHomework.getWidth()));
		tvFabHomework.setY(oldY);
		tvFabHomework.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));

		overlay.setClickable(false);

		final boolean[] fabMenu = {true, false}; //available = true; open = false

		fabPlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (fabMenu[0] && !fabMenu[1]){
					fabMenu[0] = false;
					fabMenu[1] = true;
					fabPlus.setClickable(false);
					overlay.setClickable(false);

					overlay.setVisibility(View.VISIBLE);
					overlay.animate().alpha(0.95f).setDuration(250);
					fabPlus.animate().rotationBy(45).setDuration(125).setStartDelay(125);
					fabHomework.setVisibility(View.VISIBLE);
					tvFabHomework.setVisibility(View.VISIBLE);
					fabHomework.animate().alpha(1f).translationYBy(-1f * fabMargin).setDuration(125).setStartDelay(125);
					tvFabHomework.animate().alpha(1f).translationYBy(-1f * fabMargin).setDuration(125).setStartDelay(125);

					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							fabMenu[0] = true;
							fabPlus.setClickable(true);
							overlay.setClickable(true);
						}
					}, 500);
				} else if (fabMenu[0] && fabMenu[1]){
					fabMenu[0] = false;
					fabMenu[1] = false;
					fabPlus.setClickable(false);
					overlay.setClickable(false);

					overlay.animate().alpha(0f).setDuration(250);
					fabHomework.setVisibility(View.VISIBLE);
					tvFabHomework.setVisibility(View.VISIBLE);
					fabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
					tvFabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
					fabPlus.animate().rotationBy(-45).setDuration(250).setStartDelay(125);

					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							fabMenu[0] = true;
							fabPlus.setClickable(true);
							overlay.setVisibility(View.GONE);
						}
					}, 500);
				}
			}
		});

		fabHomework.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (fabMenu[0] && fabMenu[1]) {
					fabMenu[0] = false;
					fabMenu[1] = false;

					fabHomeworkClicked();

					overlay.animate().alpha(0f).setDuration(250);
					fabHomework.setVisibility(View.VISIBLE);
					tvFabHomework.setVisibility(View.VISIBLE);
					fabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
					tvFabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
					fabPlus.animate().rotationBy(-45).setDuration(250).setStartDelay(125);

					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							fabMenu[0] = true;
							fabPlus.setClickable(true);
							overlay.setVisibility(View.GONE);
						}
					}, 500);
				}
			}
		});

		overlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (fabMenu[0] && fabMenu[1]){
					fabMenu[0] = false;
					fabMenu[1] = false;
					fabPlus.setClickable(false);
					overlay.setClickable(false);

					overlay.animate().alpha(0f).setDuration(250);
					fabHomework.setVisibility(View.VISIBLE);
					tvFabHomework.setVisibility(View.VISIBLE);
					fabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
					tvFabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
					fabPlus.animate().rotationBy(-45).setDuration(250).setStartDelay(125);

					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							fabMenu[0] = true;
							fabPlus.setClickable(true);
							overlay.setVisibility(View.GONE);
						}
					}, 500);
				}
			}
		});
	}
    
    public String getPeriodName(String day, String period){		
		String[][] myArray = toArray(this, "Periods.txt");
		int Rows = nmbrRowsCols(this, "Periods.txt")[0];
		int Cols = nmbrRowsCols(this, "Periods.txt")[1];
		String sName = "-";
		File file = new File(getExternalFilesDir(null), "Periods.txt");
		
		if (file.length() > 0){
			for (int ab = 0; ab < Rows; ab++){
				if (myArray[ab][0].equals(day) & myArray[ab][1].equals(period)){
					sName = myArray[ab][2];
				}
			}
		}

		return sName;
	}
    
    public String getPeriodNameFromAbbrev(String abbrev){		
		String[][] myArray = toArray(this, "Periods.txt");
		int Rows = nmbrRowsCols(this, "Periods.txt")[0];
		int Cols = nmbrRowsCols(this, "Periods.txt")[1];
		String sName = "-";
		File file = new File(getExternalFilesDir(null), "Periods.txt");
		
		if (file.length() > 0){
			for (int ab = 0; ab < Rows; ab++){
				if (myArray[ab][0].equals(abbrev)){
					sName = myArray[ab][2];
				}
			}
		}

		return sName;
	}
    
    public String[] getSubjectInfoFromAbbrev(Context context, String sAbbrev){		
		String[][] myArray = toArray(context, "Subjects.txt");
		int Rows = nmbrRowsCols(context, "Subjects.txt")[0];
		int Cols = nmbrRowsCols(context, "Subjects.txt")[1];
		String[] sArray = new String[Cols];
		int row = 0;
		boolean existsSubject = false;
		
		for (int a = 0; a < Rows; a++){
			if (myArray[a][1].equals(sAbbrev)){
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
    
    public String getPeriodAbbrev(String sName){		
		String[][] myArray = toArray(this, "Subjects.txt");
		int Rows = nmbrRowsCols(this, "Subjects.txt")[0];
		int Cols = nmbrRowsCols(this, "Subjects.txt")[1];
		String sAbbrev = "-";
		File file = new File(getExternalFilesDir(null), "Subjects.txt");
		
		if (file.length() > 0){
			for (int ab = 0; ab < Rows; ab++){
				if (myArray[ab][0].equals(sName)){
					sAbbrev = myArray[ab][1];
				}
			}
		}

		return sAbbrev;
	}
    
    public String[] getPeriodColors(String sName){		
		String[][] myArray = toArray(this, "Subjects.txt");
		int Rows = nmbrRowsCols(this, "Subjects.txt")[0];
		int Cols = nmbrRowsCols(this, "Subjects.txt")[1];
		String[] sColor = {"-", "-"};
		File file = new File(getExternalFilesDir(null), "Subjects.txt");
		
		if (file.length() > 0){
			for (int ab = 0; ab < Rows; ab++){
				if (myArray[ab][0].equals(sName)){
					sColor[0] = myArray[ab][24];
					sColor[1] = myArray[ab][25];
				}
			}
		}

		return sColor;
	}

    public String[] getFieldInfo(String field){
        String[][] myArray = toArray(this, "TtFields.txt");
        int Rows = nmbrRowsCols(this, "TtFields.txt")[0];
        int Cols = nmbrRowsCols(this, "TtFields.txt")[1];
        String[] fieldInfo = new String[4];
        File file = new File(getExternalFilesDir(null), "TtFields.txt");

        if (file.length() > 0){
            for (int f = 0; f < Rows; f++){
                if (myArray[f][0].equals(field)){
                    fieldInfo[0] = myArray[f][1];
                    fieldInfo[1] = myArray[f][2];
                    fieldInfo[2] = myArray[f][3];
                    fieldInfo[3] = myArray[f][4];
                }
            }
        }

        return fieldInfo;
    }

	public void fabHomeworkClicked(){
		showEditHwDialog();
	}

	public void recyclerViewListClicked(String ID){
		showViewHwDialog(ID);
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
	public void onDialogMessageHomework(String title, String content, String subject, String date) {
		createHomework(this, subject, date, title, content);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, new MainFragment(), MainFragment.DEFAULT_EDIT_FRAGMENT_TAG);
		transaction.commit();
	}

	@Override
	public void onDialogMessageUpdateHomework(String ID, String title, String content, String subject, String date, String done) {
		updateHomework(this, ID, subject, date, title, content, done);

		/*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, new MainFragment(), MainFragment.DEFAULT_EDIT_FRAGMENT_TAG);
		transaction.commit();*/
	}

	public void onDialogMessageViewHomework(boolean edit, String ID){
		/*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, new MainFragment(), MainFragment.DEFAULT_EDIT_FRAGMENT_TAG);
		transaction.commit();
		if (edit){
			showEditHwDialogForExistingHw(ID);
		}*/
	}

	@Override
	public void onDialogMessageDeleteHomework(String ID) {

	}

	@Override
	public void onDialogMessageDoneClicked() {

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
        inflater.inflate(R.menu.timetable, menu);
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
			Toast.makeText(this, "drawerToggle", Toast.LENGTH_SHORT).show();
			return true;
		}*/
        // Handle action buttons
        switch(item.getItemId()) {
		case android.R.id.home:
			Toast.makeText(this, "Up clicked", Toast.LENGTH_SHORT).show();
			return false;
		case R.id.action_subjects_list:
			menuActionSubjectsList();
			return true;
        case R.id.action_new_timetable:
			menuActionNewTimetable();
            return true;
		case R.id.action_add_subject:
			menuActionAddSubject();
			return true;
        case R.id.action_settings:
			menuActionSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	public void menuActionSubjectsList(){
		SubjectsListFragment();
	}

	public void menuActionNewTimetable(){
		Intent i = new Intent(this,
				NewTimetableActivity.class);
		startActivityForResult(i, 0);
	}

	public void menuActionAddSubject(){
		Bundle args = new Bundle();
		args.putString("caller", "Timetable");
		args.putString("action", "add_subject");

		Intent i = new Intent(this, EditSubjectActivity.class);
		i.putExtras(args);
		startActivityForResult(i, 0);
	}

	public void menuActionSettings(){
		Intent i2 = new Intent(this,
				SettingsActivity.class);
		startActivityForResult(i2, 0);
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
	    		Intent i = new Intent(this,
						MainActivity.class);
				startActivityForResult(i, 0);
	    		break;
	    	case 1:
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

}
