package com.thinc_easy.schoolmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
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

public class TimetableActivity extends ActionBarActivity
		implements DialogEditHomework.Communicator, DialogViewHomework.Communicator, DialogAdInfo.AdInfoDialogListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private Toolbar toolbar;
    private CharSequence mTitle;
	private int tt_app_bar_color;
    private CharSequence mDrawerTitle;
	private String[] mActivityTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
	private NavigationDrawerFragment1 drawerFragment;
    
	private Fragment mLessonFragment;
	private Fragment mSubjectsListFragment;
	private CoordinatorLayout cl_timetable;

	public boolean isLessonFragmentActive;
	public boolean isSubjectsListFragmentActive;
	private String ttFolder, homeworkFilepath;
	private File homeworkFile;
	private String currentLessonID, currentDateWeek;
	private View bottom_sheet_shader;

	private Context context;
	private SharedPreferences prefs;
	private InterstitialAd mInterstitialAd;
	private final int daysAdFree = 14;
	boolean adClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

		cl_timetable = (CoordinatorLayout) findViewById(R.id.cl_timetable);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

		tt_app_bar_color = getResources().getColor(R.color.color_timetable_appbar);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		drawerFragment = (NavigationDrawerFragment1)
				getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_1);
		drawerFragment.setUp(R.id.fragment_navigation_drawer_1, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


		context = this;
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		ttFolder = prefs.getString(getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
		homeworkFilepath = ttFolder + "/" + getResources().getString(R.string.file_name_homework);
		homeworkFile = new File(getExternalFilesDir(null), homeworkFilepath);

		showAdIfNecessary();

		bottom_sheet_shader = (View) findViewById(R.id.bottom_sheet_shader);

        Fragment mTimetableFragment = getSupportFragmentManager().findFragmentByTag
				(TimetableFragment.DEFAULT_EDIT_FRAGMENT_TAG);

		isLessonFragmentActive = false;

		mTitle = getResources().getString(R.string.title_activity_timetable);

		//FloatingActionButton();

		// You can be pretty confident that the intent will not be null here.
		Intent intent = getIntent();
		boolean intentLesson = false;

		// Get the extras (if there are any)
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (extras.containsKey("action") && extras.containsKey("lessonID") && extras.containsKey("date_week")) {
				if (getIntent().getExtras().getString("action").equals("lesson")){
					String lessonID = getIntent().getExtras().getString("lessonID");
					String dateWeek = getIntent().getExtras().getString("date_week");
					LessonFragment(lessonID, dateWeek);
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

	private void showAdIfNecessary(){
		boolean showAd = false;
		adClicked = false;
		if (prefs.contains("open_main_activity_count") && prefs.getInt("open_main_activity_count", 0)>5
				&& prefs.getInt("open_main_activity_count", 0)%5 == 0
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
			mInterstitialAd.setAdUnitId("ca-app-pub-9138088263014683/2500709250");

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
					if (adClicked) Toast.makeText(context.getApplicationContext(), getResources().getString(R.string.toast_ads_removed), Toast.LENGTH_LONG).show();
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
    public void onBackPressed(){
		if (isLessonFragmentActive) {
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(tt_app_bar_color));


			// You can be pretty confident that the intent will not be null here.
			Intent intent = getIntent();
			boolean intentLesson = false;

			// Get the extras (if there are any)
			Bundle extras = intent.getExtras();
			if (extras != null) {
				if (extras.containsKey("caller") && extras.containsKey("action")) {
					if (getIntent().getExtras().getString("caller").equals("home") && getIntent().getExtras().getString("action").equals("lesson")) {
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
		} else {
			super.onBackPressed();
			// turn on the NavDrawer image. This Method is called in lower-level fragments.
			//mDrawerToggle.setDrawerIndicatorEnabled(true);
		}
    }

    public void LessonFragment(String lessonID, String date_week){
		/*BottomSheetDialogFragment bottomSheetDialogFragment = new LessonFragment();
		bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());*/
		currentLessonID = lessonID;
		currentDateWeek = date_week;

    	mLessonFragment = getSupportFragmentManager().findFragmentByTag
				(LessonFragment.DEFAULT_EDIT_FRAGMENT_TAG);

		isLessonFragmentActive = true;
		//drawerFragment.upArrow();
		
		//mDrawerToggle.setDrawerIndicatorEnabled(false);
		//mDrawerToggle.setHomeAsUpIndicator(getV7DrawerToggleDelegate().getThemeUpIndicator());

		mLessonFragment = new LessonFragment();
		Bundle args = new Bundle();
        args.putString("lessonID", lessonID);
        args.putString("date_week", date_week);
        args.putString("caller", "Timetable");
        mLessonFragment.setArguments(args);
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container_bottom_sheet_lesson, mLessonFragment,
				LessonFragment.DEFAULT_EDIT_FRAGMENT_TAG).addToBackStack(null);
		transaction.commit();

		bottom_sheet_shader.setVisibility(View.VISIBLE);

		/*FrameLayout containerBS = (FrameLayout) findViewById(R.id.container_bottom_sheet_lesson);
		float bs_elevation = getResources().getDimension(R.dimen.lesson_bottom_sheet_televation);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			containerBS.setElevation(bs_elevation);
		}

		View bottomSheet = findViewById(R.id.container_bottom_sheet_lesson);
		BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

		int peek_height = (int) (getResources().getDimension(R.dimen.lesson_bottom_sheet_peek_height) + 0.5f);
		bottomSheetBehavior.setPeekHeight(peek_height);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		bottomSheetBehavior.setHideable(true);

		final View v_appbar_shadow = (View) findViewById(R.id.v_appbar_shadow);

		bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				if (newState == BottomSheetBehavior.STATE_EXPANDED){
					// update the actionbar to show the up carat/affordance
					getSupportActionBar().setDisplayHomeAsUpEnabled(true);
					getSupportActionBar().setTitle(getString(R.string.title_fragment_lesson));
					v_appbar_shadow.setVisibility(View.VISIBLE);

				} else {
					getSupportActionBar().setDisplayHomeAsUpEnabled(false);
					drawerFragment.noUpArrow();
					getSupportActionBar().setTitle(mTitle);
					v_appbar_shadow.setVisibility(View.GONE);
				}
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {

			}
		});*/
    }

	public void endLessonFragment(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		drawerFragment.noUpArrow();
		isLessonFragmentActive = false;

		getSupportActionBar().setTitle(mTitle);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(tt_app_bar_color));
		bottom_sheet_shader.setVisibility(View.GONE);
	}

	public void extendLessonFragment(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		drawerFragment.upArrow();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		isLessonFragmentActive = true;
		bottom_sheet_shader.setVisibility(View.VISIBLE);
	}

	public void collapseLessonFragment(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		drawerFragment.noUpArrow();
		isLessonFragmentActive = true;

		getSupportActionBar().setTitle(mTitle);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(tt_app_bar_color));
		bottom_sheet_shader.setVisibility(View.VISIBLE);
	}

	public void resumeLessonFragment(){
		isLessonFragmentActive = true;
		bottom_sheet_shader.setVisibility(View.VISIBLE);
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

	/*private void FloatingActionButton(){
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
	}*/

	public void fabHomeworkClicked(){
		showEditHwDialog();
	}

	public void recyclerViewListClicked(String ID){
		showViewHwDialog(ID);
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

	@Override
	public void onDialogMessageHomework(String title, String content, String subject, String date, String lessonID) {
		DataStorageHandler.createHomework(this, ttFolder, subject, date, title, content, lessonID);

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
				.make(cl_timetable, R.string.snackbar_homework_deleted, Snackbar.LENGTH_LONG)
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

		DataStorageHandler.writeToCSVFile(this, file, myArray, Rows, Cols, "TimetableActivity deleteUndo");


		updateViews();
		//Snackbar.make(fabCoordinator, R.string.snackbar_homework_deleted_restored, Snackbar.LENGTH_SHORT).show();
	}

	private void updateViews(){
		if (isLessonFragmentActive){
			LessonFragment(currentLessonID, currentDateWeek);
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
			//Toast.makeText(this, "Up clicked", Toast.LENGTH_SHORT).show();
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
