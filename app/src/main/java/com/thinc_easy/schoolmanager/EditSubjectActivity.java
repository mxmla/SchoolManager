package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

//import java.nio.file.Files;


public class EditSubjectActivity extends ActionBarActivity implements DialogColorChooser.Communicator, DialogDeleteSubjectConfirm.Communicator{

    private Toolbar toolbar;
    private Fragment mEditSubjectFragment;
	private Fragment mCustomSubjectsOrNotFragment;
	private Fragment mCustomSubjectFragment;
	private Fragment mAnotherCustomSubjectFragment;
	private Fragment mNewTtDoneFragment;
	private Fragment mSubjectFragment;
	private int firstSubject;
	private int thisSubject;
	private ArrayList<Integer> subjectsToAdd;
	ArrayList<String> subjectsToAddNames;
	private Drawable mActionBarBackgroundDrawable;
	private int headerHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_subject);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

		if (getIntent().getExtras().getString("caller").equals("NewTimetable") && getIntent().getExtras().getString("subjects").equals("true")){
			mCustomSubjectFragment = getSupportFragmentManager().findFragmentByTag(CustomSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);

			subjectsToAddNames = getIntent().getExtras().getStringArrayList("subjects_to_add_names");
			thisSubject = 0;

			if (getIntent().hasExtra("first_subject") && getIntent().getExtras().getInt("first_subject") >= 0){
				firstSubject = getIntent().getExtras().getInt("first_subject");
			} else {
				firstSubject = 0;
			}

			if (mCustomSubjectFragment == null){
				mCustomSubjectFragment = new CustomSubjectFragment();
				Bundle args = new Bundle();
				args.putString("subject_name", subjectsToAddNames.get(thisSubject));
				args.putString("caller", "NewTimetable");
				args.putString("action", "new_timetable");
				mCustomSubjectFragment.setArguments(args);

				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.add(R.id.container, mCustomSubjectFragment, CustomSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
				transaction.commit();
			}


			/*mEditSubjectFragment = getSupportFragmentManager().findFragmentByTag
					(EditSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
			
			if (mEditSubjectFragment == null){
				mEditSubjectFragment = new EditSubjectFragment();
				Bundle args = new Bundle();
		        args.putInt("subject_int", subjectsToAdd.get(firstSubject));
		        args.putString("caller", "NewTimetable");
		        mEditSubjectFragment.setArguments(args);
				
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.add(R.id.container, mEditSubjectFragment, 
						EditSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
				transaction.commit();
			}*/
		}

		if (getIntent().getExtras().getString("caller").equals("NewTimetable") && getIntent().getExtras().getString("subjects").equals("false")){
			if (mCustomSubjectsOrNotFragment == null){
				mCustomSubjectsOrNotFragment = new CustomSubjectsOrNotFragment();

				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.add(R.id.container, mCustomSubjectsOrNotFragment, CustomSubjectsOrNotFragment.DEFAULT_EDIT_FRAGMENT_TAG);
				transaction.commit();
			}
		}

		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("action")){
			if (extras.getString("action").equals("add_subject")){
				mCustomSubjectFragment = new CustomSubjectFragment();
				Bundle args = new Bundle();
				args.putString("caller", getIntent().getExtras().getString("caller"));
				args.putString("action", getIntent().getExtras().getString("action"));
				mCustomSubjectFragment.setArguments(args);

				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.container, mCustomSubjectFragment, CustomSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
				transaction.commit();
			}
		} else if (getIntent().getExtras().getString("caller").equals("Timetable")){
			mSubjectFragment = getSupportFragmentManager().findFragmentByTag
					(SubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
			
			String abbrev = getIntent().getExtras().getString("abbrev");
			if (mSubjectFragment == null){
				mSubjectFragment = new SubjectFragment();
				Bundle args = new Bundle();
		        args.putString("abbrev", abbrev);
		        args.putString("caller", "Timetable");
		        mSubjectFragment.setArguments(args);
				
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.add(R.id.container, mSubjectFragment, 
						SubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
				transaction.commit();
			}
		}
		
		
		/*
		headerHeight = findViewById(R.id.header1).getHeight() - getActionBar().getHeight();
		
		mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.ab_background);
		mActionBarBackgroundDrawable.setAlpha(0);
		getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);
		((NotifyingScrollView) findViewById(R.id.ScrollView1)).setOnScrollChangedListener(mOnScrollChangedListener);
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
		    mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
		}
		*/
		
	}
    
    public void showDialog(View v, String which, String whichFragment){
    	Bundle args = new Bundle();
    	args.putString("which", which);
    	args.putString("whichFragment", whichFragment);
		FragmentManager manager = getSupportFragmentManager();
		DialogColorChooser myDialog = new DialogColorChooser();
		myDialog.setArguments(args);
		myDialog.show(manager, "myDialog");
	}

    @Override
	public void onDialogMessage(String message, String which, String whichFragment) {
    	if (whichFragment == "EditSubjectFragment"){
    		Fragment fragment = getSupportFragmentManager().findFragmentByTag(EditSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        	((EditSubjectFragment) fragment).saveChosenColor(message, which);
    	}
    	if (whichFragment == "CustomSubjectFragment"){
    		Fragment fragment = getSupportFragmentManager().findFragmentByTag(CustomSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
        	((CustomSubjectFragment) fragment).saveChosenColor(message, which);
    	}
	}
	
    // when called by NewTimetable
	public void NextSubject(){
		thisSubject = thisSubject + 1;
		if (thisSubject < subjectsToAddNames.size()){
			mCustomSubjectFragment = new CustomSubjectFragment();
			Bundle args = new Bundle();
			args.putString("subject_name", subjectsToAddNames.get(thisSubject));
			args.putString("caller", "NewTimetable");
			mCustomSubjectFragment.setArguments(args);

			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container, mCustomSubjectFragment, CustomSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
			transaction.commit();
		}else{
			Completed();
		}
	}

	/*public void NextSubject(){
		thisSubject = thisSubject + 1;
		if (thisSubject < subjectsToAdd.size()){
			mEditSubjectFragment = new EditSubjectFragment();
			Bundle args = new Bundle();
	        args.putInt("subject_int", subjectsToAdd.get(thisSubject));
	        args.putString("caller", "NewTimetable");
	        mEditSubjectFragment.setArguments(args);
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container, mEditSubjectFragment, EditSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
			transaction.commit();
		}else{
			mCustomSubjectsOrNotFragment = new CustomSubjectsOrNotFragment();
			
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.container, mCustomSubjectsOrNotFragment, CustomSubjectsOrNotFragment.DEFAULT_EDIT_FRAGMENT_TAG);
			transaction.commit();
		}
	}*/
	
	public void EditCustom(){
		mCustomSubjectFragment = new CustomSubjectFragment();
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, mCustomSubjectFragment, CustomSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
		transaction.commit();
	}
	
	public void AnotherCustom(){
		mAnotherCustomSubjectFragment = new AnotherCustomSubjectFragment();
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, mAnotherCustomSubjectFragment, AnotherCustomSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
		transaction.commit();
	}
	
	public void Completed(){
		mNewTtDoneFragment = new NewTtDoneFragment();
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, mNewTtDoneFragment, NewTtDoneFragment.DEFAULT_EDIT_FRAGMENT_TAG);
		transaction.commit();
	}
	
	// when called by SubjectFragment
	public void CallEditSubjectFragment(String sName){
		/*mEditSubjectFragment = new EditSubjectFragment();
		Bundle args = new Bundle();
        args.putString("caller", "SubjectFragment");
        args.putString("subject", sName);
        mEditSubjectFragment.setArguments(args);
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container, mEditSubjectFragment, EditSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG).addToBackStack(null);
		transaction.commit();*/

		mCustomSubjectFragment = getSupportFragmentManager().findFragmentByTag(CustomSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);

		String subjectID = "[none]";
		if (getIntent().getExtras().containsKey("subjectID"))
			subjectID = getIntent().getExtras().getString("subjectID", "[none]");

		if (mCustomSubjectFragment == null){
			mCustomSubjectFragment = new CustomSubjectFragment();
			Bundle args = new Bundle();
			args.putString("caller", "SubjectFragment");
			args.putString("action", "edit_subject");
			args.putString("subjectID", subjectID);
			mCustomSubjectFragment.setArguments(args);

			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.container, mCustomSubjectFragment, CustomSubjectFragment.DEFAULT_EDIT_FRAGMENT_TAG);
			transaction.commit();
		}
	}
	




	public void menuActionDeleteSubject(String subject){
		showDeleteSubjectConfirmDialog(subject);
	}

	@Override
	public void onDialogMessageDeleteConfirm(boolean delete, String subject) {
		if (delete){
			deleteSubject(this, subject);
		}
	}

	public void showDeleteSubjectConfirmDialog(String subject){
		Bundle args = new Bundle();
		args.putString("subject", subject);
		FragmentManager manager = getSupportFragmentManager();
		DialogDeleteSubjectConfirm myDialog = new DialogDeleteSubjectConfirm();
		myDialog.setArguments(args);
		myDialog.show(manager, "myDialog");
	}



	public void deleteSubject(Context context, String subject){
		DataStorageHandler.deleteSubject(context, subject);

		Intent i = new Intent(this,
				TimetableActivity.class);
		startActivityForResult(i, 0);
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
	
	
	
	public String mathsTeacher(){
			if (DataStorageHandler.isExternalStorageWritable(this)){
			String mT = getSubjectInfo(this, "Mathematik")[2];
			return mT;
		} else {
			return "external storage currently not available";
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_subject, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		/*int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}*/
		return super.onOptionsItemSelected(item);
	}


	public void menuActionSettings(){
		Intent i2 = new Intent(this,
				SettingsActivity.class);
		startActivityForResult(i2, 0);
	}

	/*
	private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }
    };
    */
    
    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getSupportActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };
	
}
