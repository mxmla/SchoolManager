package com.thinc_easy.schoolmanager;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

public class EditSubjectFragment extends Fragment{
	
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";
	private int mSubjectInt;
	private String[] mDayNames;
	private String[] mDayIDs;
	private String[] mPeriodNumbers;
	private TextView tvSubjectName;
	private LinearLayout header1;
	private FrameLayout flTitle;
	private int hTextSize;
	private String[] mSubjectNames, mSubjectAbbrevs, mSubjectArray;
	private String mSubjectName, mSubjectAbbrev, mTeacherName, mTeacherAbbrev;
	private String Day1, Day2, Day3, Day4, Day5;
	private String Period11, Period12, Period21, Period22, Period31, Period32, Period41, Period42, Period51, Period52;
	private String Room1, Room2, Room3, Room4, Room5;
	private String Color, Textcolor;
	private EditText etTeacherName, etTeacherAbbrev, etRoom1, etRoom2, etRoom3, etRoom4, etRoom5;
	private View le2, le3, le4, le5;
	private FrameLayout mInsertPoint1, mInsertPoint2, mInsertPoint3, mInsertPoint4;
	private CardView CardLesson1, CardLesson2, CardLesson3, CardLesson4, CardLesson5;
	private ToggleButton tb1, tb31, tb41, tb51;
	private ViewSwitcher vs1, vs31, vs41, vs51;
	private ArrayAdapter<CharSequence> adapterDay;
	private ArrayAdapter<CharSequence> adapterLesson;
	private Button mNextButton, bColorChooserBg, bColorChooserT;
	private Spinner mDaySpinner1, mDaySpinner2, mDaySpinner3, mDaySpinner4, mDaySpinner5;
	private Spinner mPeriodSpinner01, mPeriodSpinner02, mPeriodSpinner03, mPeriodSpinner04, mPeriodSpinner05, 
						mPeriodSpinner06, mPeriodSpinner07, mPeriodSpinner08, mPeriodSpinner09, mPeriodSpinner10;
	private OnItemSelectedListener listener;
	private boolean l2active, l3active, l4active, l5active;
	private int[] colorIntsOld = {0xff33B5E5, 0xff0099CC, 0xffAA66CC, 0xff9933CC, 0xff99CC00, 0xff669900,
			0xffFFBB33, 0xffFF8800, 0xffFF4444, 0xffCC0000, 0xff000000, 0xffFFFFFF};
	private int[] sColorIntsOld = {0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000,
			0xff000000, 0xff000000, 0xff000000, 0xffFF8800, 0xffFF8800};
	private String[] colorNamesOld = {"blue_light", "blue_dark", "purple_light", "purple_dark", "green_light", "green_dark",
			"orange_light", "orange_dark", "red_light", "red_dark", "black", "white", "gray_light", "gray_dark"};
    private String[] colorNames = {"red", "pink", "purple", "deep_purple", "indigo", "blue",
            "light_blue", "cyan", "teal", "green", "light_green", "lime", "yellow", "amber",
            "ornge", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
    private int[] colorInts = {0xffF44336, 0xffE91E63, 0xff9C27B0, 0xff673AB7, 0xff3F51B5, 0xff2196F3,
            0xff03A9F4, 0xff00BCD4, 0xff009688, 0xff4CAF50, 0xff8BC34A, 0xffCDDC39,
            0xffFFEB3B, 0xffFFC107, 0xffFF9800, 0xffFF5722, 0xff795548, 0xff9E9E9E, 0xff607D8B, 0xff000000, 0xffFFFFFF};
    private int[] sColorInts = {0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000,
            0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000,
            0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xffFF8800, 0xffFF8800};

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_subject, container, false);

        mSubjectNames = getResources().getStringArray(R.array.SubjectNames);
        mSubjectAbbrevs = getResources().getStringArray(R.array.SubjectAbbreviations);
        mDayNames = getResources().getStringArray(R.array.DayNames);
		mDayIDs =getResources().getStringArray(R.array.DayIDs);
        mPeriodNumbers = getResources().getStringArray(R.array.PeriodNumbers);
        mDaySpinner1 = (Spinner) v.findViewById(R.id.spinner1);
        mPeriodSpinner01 = (Spinner) v.findViewById(R.id.spinner2);
        mPeriodSpinner02 = (Spinner) v.findViewById(R.id.spinner3);

        if (getArguments().getString("caller").equals("NewTimetable")){
	        mSubjectInt = getArguments().getInt("subject_int");
	        mSubjectName = mSubjectNames[mSubjectInt];
	        mSubjectAbbrev = mSubjectAbbrevs[mSubjectInt];
        }
        if (getArguments().getString("caller").equals("SubjectFragment")){
	        mSubjectName = getArguments().getString("subject");
	        mSubjectArray = ((EditSubjectActivity) getActivity()).getSubjectInfo(getActivity(), mSubjectName);
	        mSubjectAbbrev = mSubjectArray[1];
        }

		((EditSubjectActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_fragment_edit_subject));
		int abColor;
		abColor = getActivity().getResources().getColor(R.color.color_timetable);
		if (getArguments().getString("caller").equals("SubjectFragment") && !mSubjectArray[24].equals("-")) {
			for (int c = 0; c < colorNames.length; c++){
				if (mSubjectArray[24].equals(colorNames[c])) abColor = colorInts[c];
			}
		}
		((EditSubjectActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(abColor));
        
        tvSubjectName = (TextView) v.findViewById(R.id.tvSubjectName);
        tvSubjectName.setText(mSubjectName);
        setUpTvSubjectName(v);
        //flTitle = (FrameLayout) v.findViewById(R.id.FrameLayoutTitle);
        
        etRoom1 = (EditText) v.findViewById(R.id.editText3);
        
        etTeacherName = (EditText) v.findViewById(R.id.teacherName);
        etTeacherAbbrev = (EditText) v.findViewById(R.id.teacherAbbrev);

		mInsertPoint1 = (FrameLayout) v.findViewById(R.id.insert_point_1);
		mInsertPoint2 = (FrameLayout) v.findViewById(R.id.insert_point_2);
		mInsertPoint3 = (FrameLayout) v.findViewById(R.id.insert_point_3);
		mInsertPoint4 = (FrameLayout) v.findViewById(R.id.insert_point_4);

		CardLesson1 = (CardView) v.findViewById(R.id.card_lesson_edit);
		CardLesson2 = (CardView) v.findViewById(R.id.card_lesson_edit_2);
		CardLesson3 = (CardView) v.findViewById(R.id.card_lesson_edit_3);
		CardLesson4 = (CardView) v.findViewById(R.id.card_lesson_edit_4);
		CardLesson5 = (CardView) v.findViewById(R.id.card_lesson_edit_5);

		tb1 = (ToggleButton) v.findViewById(R.id.tb1);
		tb31 = (ToggleButton) v.findViewById(R.id.tb31);
		tb41 = (ToggleButton) v.findViewById(R.id.tb41);
		tb51 = (ToggleButton) v.findViewById(R.id.tb51);

		vs1 = (ViewSwitcher) v.findViewById(R.id.vs1);
		vs31 = (ViewSwitcher) v.findViewById(R.id.vs31);
		vs41 = (ViewSwitcher) v.findViewById(R.id.vs41);
		vs51 = (ViewSwitcher) v.findViewById(R.id.vs51);
		
		l2active = false;
		l3active = false;
		l4active = false;
		l5active = false;

		vs1.showNext();
		vs31.showNext();
		vs41.showNext();
		vs51.showNext();
		closeLE3Card(v);

		Color = "white";
		Textcolor = "black";
        
		 // Create an ArrayAdapter using the string array and a default spinner layout
		 adapterDay = ArrayAdapter.createFromResource(getActivity(),
		         R.array.DayNames, android.R.layout.simple_spinner_item);
		 // Specify the layout to use when the list of choices appears
		 adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 // Apply the adapter to the spinner
		 mDaySpinner1.setAdapter(adapterDay);
		 mDaySpinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			        Day1 = mDayIDs[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			        Day1 = mDayIDs[0]; }
			});
		 
		 
		 adapterLesson = ArrayAdapter.createFromResource(getActivity(),
		         R.array.PeriodNumbers, android.R.layout.simple_spinner_item);
		 adapterLesson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 
		 mPeriodSpinner01.setAdapter(adapterLesson);
		 mPeriodSpinner02.setAdapter(adapterLesson);
		 mPeriodSpinner01.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			    	Period11 = mPeriodNumbers[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	Period11 = mPeriodNumbers[0]; }
			});
		 mPeriodSpinner02.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			    	Period12 = mPeriodNumbers[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	Period12 = mPeriodNumbers[0]; }
			});
		 
		 
		 initLE2Card(v);
		 
		/*
		android.app.ActionBar actionBar = getActivity().getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED)); 
		*/
		
		bColorChooserBg = (Button) v.findViewById(R.id.bColorChooserBg);
		GradientDrawable bgd = (GradientDrawable) getResources().getDrawable(R.drawable.color_circle);
		bgd.setColor(getResources().getColor(R.color.white));
		bgd.setStroke(1, getResources().getColor(R.color.color_circle_stroke_color));
		bColorChooserBg.setBackgroundDrawable(bgd);
		bColorChooserT = (Button) v.findViewById(R.id.bColorChooserT);
		GradientDrawable bgt = (GradientDrawable) getResources().getDrawable(R.drawable.color_circle);
		bgt.setColor(getResources().getColor(R.color.black));
		bgt.setStroke(1, getResources().getColor(R.color.color_circle_stroke_color));
		bColorChooserT.setBackgroundDrawable(bgt.mutate());
		bColorChooserBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditSubjectActivity) getActivity()).showDialog(v, "background", "EditSubjectFragment");
			}
		});
		bColorChooserT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditSubjectActivity) getActivity()).showDialog(v, "text", "EditSubjectFragment");
			}
		});

		mNextButton = (Button) v.findViewById(R.id.confirm);
		if (getArguments().getString("caller").equals("NewTimetable")) mNextButton.setText(getResources().getString(R.string.Next));
		if (getArguments().getString("caller").equals("SubjectFragment")){
			mNextButton.setText(getResources().getString(R.string.save));
			loadData(v);
		}

		mNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SaveData();
				if (getArguments().getString("caller").equals("NewTimetable")) ((EditSubjectActivity)getActivity()).NextSubject();
				if (getArguments().getString("caller").equals("SubjectFragment")){
					Intent i = new Intent(getActivity(), TimetableActivity.class);
					startActivityForResult(i, 0);
				}
				//notification();
			}
		});

		// indicate that the fragment would like to add items to the OptionsMenu
		setHasOptionsMenu(true);
        
        return v;
    }

    private void setUpTvSubjectName(View v){
		tvSubjectName.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Regular.ttf"));
    	//tvSubjectName.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Thin.ttf"));
    	//hTextSize = 64;
        //tvSubjectName.setTextSize(hTextSize);
        /*
        header1 = (LinearLayout) v.findViewById(R.id.header1);
        while (tvSubjectName.getWidth() < v.getWidth()){
        	hTextSize = hTextSize + 1;
        	tvSubjectName.setTextSize(hTextSize);
        }*/
    }
	
    public void saveChosenColor(String color, String which){
    	if (which.equals("background")){
    		Color = color;
        	int colorInt = 0xffFFFFFF;
			boolean isChosen = false;
        	for (int i = 0; i < colorNames.length; i++){
        		if (colorNames[i].equals(color)){
        			colorInt = colorInts[i];
					isChosen = true;
        		}
        	}
			if (isChosen) {
				((GradientDrawable) bColorChooserBg.getBackground()).setColor(colorInt);
				if (color.equals("white")) {
					((GradientDrawable) bColorChooserBg.getBackground()).setStroke(1, 0xff000000);
				}
				tvSubjectName.setBackgroundColor(colorInt);
				//flTitle.setBackgroundColor(colorInt);
				((EditSubjectActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorInt));
			}
    	}
    	if (which.equals("text")){
    		Textcolor = color;
        	int colorInt = 0xff000000;
			boolean isChosen = false;
        	for (int i = 0; i < colorNames.length; i++){
        		if (colorNames[i].equals(color)){
        			colorInt = colorInts[i];
					isChosen = true;
        		}
        	}
			if (isChosen) {
				((GradientDrawable) bColorChooserT.getBackground()).setColor(colorInt);
				if (color.equals("white")) {
					((GradientDrawable) bColorChooserT.getBackground()).setStroke(1, 0xff000000);
				}
				tvSubjectName.setTextColor(colorInt);
			}
    	}
    }
    
    public void loadData(View v){
    	String[] subjectInfo = ((EditSubjectActivity) getActivity()).getSubjectInfo(getActivity(), mSubjectName);
    	if (!subjectInfo.equals(null) & !subjectInfo.equals("") & !subjectInfo.equals("-")){

			for (int i = 0; i < mDayNames.length; i++){
				if (mDayIDs[i].equals(subjectInfo[4])){
					mDaySpinner1.setSelection(i);
				}
				if (mDayIDs[i].equals(subjectInfo[5])){
					initLE2Card(v);
					tb1.performClick();
					mDaySpinner2.setSelection(i);
				}
				if (mDayIDs[i].equals(subjectInfo[6])){
					initLE3Card(v);
					tb31.performClick();
					mDaySpinner3.setSelection(i);
				}
				if (mDayIDs[i].equals(subjectInfo[7])){
					initLE4Card(v);
					tb41.performClick();
					mDaySpinner4.setSelection(i);
				}
				if (mDayIDs[i].equals(subjectInfo[8])){
					initLE5Card(v);
					tb51.performClick();
					mDaySpinner5.setSelection(i);
				}
			}

    		if (!subjectInfo[2].equals(null) & !subjectInfo[2].equals("") & !subjectInfo[2].equals("-") & !subjectInfo[2].equals("[null]")){
    			etTeacherName.setText(subjectInfo[2].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", ""));
    		}
    		if (!subjectInfo[3].equals(null) & !subjectInfo[3].equals("") & !subjectInfo[3].equals("-") & !subjectInfo[3].equals("[null]")){
    			etTeacherAbbrev.setText(subjectInfo[3].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", ""));
    		}
    		if (!subjectInfo[4].equals(null) & !subjectInfo[4].equals("") & !subjectInfo[4].equals("-")){
    			etRoom1.setText(subjectInfo[19].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
    		}
    		if (!subjectInfo[5].equals(null) & !subjectInfo[5].equals("") & !subjectInfo[5].equals("-")){
    			etRoom2.setText(subjectInfo[20].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
    		}
    		if (!subjectInfo[6].equals(null) & !subjectInfo[6].equals("") & !subjectInfo[6].equals("-")){
    			etRoom3.setText(subjectInfo[21].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
    		}
    		if (!subjectInfo[7].equals(null) & !subjectInfo[7].equals("") & !subjectInfo[7].equals("-")){
    			etRoom4.setText(subjectInfo[22].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
    		}
    		if (!subjectInfo[8].equals(null) & !subjectInfo[8].equals("") & !subjectInfo[8].equals("-")){
    			etRoom5.setText(subjectInfo[23].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
    		}
    		
    		if (!subjectInfo[24].equals(null) & !subjectInfo[24].equals("") & !subjectInfo[24].equals("-")){
    			Color = subjectInfo[24];
    			int bColorInt = 0xffFFFFFF;
            	for (int i = 0; i < colorNames.length; i++){
            		if (colorNames[i].equals(Color)){
            			bColorInt = colorInts[i];
            		}
            	}
            	((GradientDrawable)bColorChooserBg.getBackground()).setColor(bColorInt);
            	if (Color.equals("white")){
                	((GradientDrawable)bColorChooserBg.getBackground()).setStroke(1, 0xff000000);
            	}
				tvSubjectName.setBackgroundColor(bColorInt);
            	//flTitle.setBackgroundColor(bColorInt);
    		}
    		
    		if (!subjectInfo[25].equals(null) & !subjectInfo[25].equals("") & !subjectInfo[25].equals("-")){
    			Textcolor = subjectInfo[25];
    			int tColorInt = 0xff000000;
            	for (int i = 0; i < colorNames.length; i++){
            		if (colorNames[i].equals(Textcolor)){
            			tColorInt = colorInts[i];
            		}
            	}
            	((GradientDrawable)bColorChooserT.getBackground()).setColor(tColorInt);
            	if (Textcolor.equals("white")){
                	((GradientDrawable)bColorChooserT.getBackground()).setStroke(1, 0xff000000);
            	}
            	tvSubjectName.setTextColor(tColorInt);
    		}

    		for (int i = 0; i < mPeriodNumbers.length; i++){
    			if (mPeriodNumbers[i].equals(subjectInfo[9])){
    	    		mPeriodSpinner01.setSelection(i);
    			}
    			if (mPeriodNumbers[i].equals(subjectInfo[14])){
    	    		mPeriodSpinner02.setSelection(i);
    			}
    			if (mPeriodNumbers[i].equals(subjectInfo[10])){
    	    		mPeriodSpinner03.setSelection(i);
    			}
    			if (mPeriodNumbers[i].equals(subjectInfo[15])){
    	    		mPeriodSpinner04.setSelection(i);
    			}
    			if (mPeriodNumbers[i].equals(subjectInfo[11])){
    	    		mPeriodSpinner05.setSelection(i);
    			}
    			if (mPeriodNumbers[i].equals(subjectInfo[16])){
    	    		mPeriodSpinner06.setSelection(i);
    			}
    			if (mPeriodNumbers[i].equals(subjectInfo[12])){
    	    		mPeriodSpinner07.setSelection(i);
    			}
    			if (mPeriodNumbers[i].equals(subjectInfo[17])){
    	    		mPeriodSpinner08.setSelection(i);
    			}
    			if (mPeriodNumbers[i].equals(subjectInfo[13])){
    	    		mPeriodSpinner09.setSelection(i);
    			}
    			if (mPeriodNumbers[i].equals(subjectInfo[18])){
    	    		mPeriodSpinner10.setSelection(i);
    			}
    		}
    	}
    }

    public void initLE2Card(View v){
		TextView tvh = (TextView) v.findViewById(R.id.tv1);
		tvh.setText(getResources().getString(R.string.PeriodNr2));

        final View view = v;
		tb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			 @Override
			 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			 if(isChecked) {
				 initLE3Card(view);
				 //vs1.showNext();
				 expand(vs1);
				 l2active = true;
			 }else{
				 closeLE3Card(view);
				 closeLE4Card(view);
				 closeLE5Card(view);
				 //vs1.showPrevious();
				 collapse(vs1);
				 l2active = false;
			 }
		 }
		 });
		 
		 mDaySpinner2 = (Spinner) v.findViewById(R.id.sd);
		 mDaySpinner2.setAdapter(adapterDay);
		 mDaySpinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			        Day2 = mDayIDs[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			        Day2 = mDayIDs[0]; }
			});
		 mPeriodSpinner03 = (Spinner) v.findViewById(R.id.sl1);
		 mPeriodSpinner03.setAdapter(adapterLesson);
		 mPeriodSpinner04 = (Spinner) v.findViewById(R.id.sl2);
		 mPeriodSpinner04.setAdapter(adapterLesson);
		 mPeriodSpinner03.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			    	Period21 = mPeriodNumbers[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	Period21 = mPeriodNumbers[0]; }
			});
		 mPeriodSpinner04.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			    	Period22 = mPeriodNumbers[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	Period22 = mPeriodNumbers[0]; }
			});
		 
		 etRoom2 = (EditText) v.findViewById(R.id.et1);
	}
		 
	 public void initLE3Card(View v){
		 CardLesson3.setVisibility(View.VISIBLE);
		 TextView tvh3 = (TextView) v.findViewById(R.id.tv31);
		 tvh3.setText(getResources().getString(R.string.PeriodNr3));

         final View view = v;
		 tb31.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			 @Override
			 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			     if(isChecked) {
					 initLE4Card(view);
			    	 //vs31.showNext();
			    	 expand(vs31);
					 l3active = true;
			     }else{
					 closeLE4Card(view);
			    	 //vs31.showPrevious();
			    	 collapse(vs31);
					 l3active = false;
			     }
			 }
			 });
		 
		 mDaySpinner3 = (Spinner) v.findViewById(R.id.sd3);
		 mDaySpinner3.setAdapter(adapterDay);
		 mDaySpinner3.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			        Day3 = mDayIDs[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			        Day3 = mDayIDs[0]; }
			});
		 mPeriodSpinner05 = (Spinner) v.findViewById(R.id.sl31);
		 mPeriodSpinner05.setAdapter(adapterLesson);
		 mPeriodSpinner06 = (Spinner) v.findViewById(R.id.sl32);
		 mPeriodSpinner06.setAdapter(adapterLesson);
		 mPeriodSpinner05.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			    	Period31 = mPeriodNumbers[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	Period31 = mPeriodNumbers[0]; }
			});
		 mPeriodSpinner06.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			    	Period32 = mPeriodNumbers[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	Period32 = mPeriodNumbers[0]; }
			});
		 
		 etRoom3 = (EditText) v.findViewById(R.id.et31);
	 }
	 

	 public void initLE4Card(View v){
		 CardLesson4.setVisibility(View.VISIBLE);
		 TextView tvh4 = (TextView) v.findViewById(R.id.tv41);
		 tvh4.setText(getResources().getString(R.string.PeriodNr4));

         final View view = v;
		 tb41.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			 @Override
			 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			     if(isChecked) {
					 initLE5Card(view);
			    	 //vs41.showNext();
			    	 expand(vs41);
					 l4active = true;
			     }else{
					 closeLE5Card(view);
			    	 //vs41.showPrevious();
			    	 collapse(vs41);
					 l4active = false;
			     }
			 }
			 });
		 
		 mDaySpinner4 = (Spinner) v.findViewById(R.id.sd4);
		 mDaySpinner4.setAdapter(adapterDay);
		 mDaySpinner4.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			        Day4 = mDayIDs[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			        Day4 = mDayIDs[0]; }
			});
		 mPeriodSpinner07 = (Spinner) v.findViewById(R.id.sl41);
		 mPeriodSpinner07.setAdapter(adapterLesson);
		 mPeriodSpinner08 = (Spinner) v.findViewById(R.id.sl42);
		 mPeriodSpinner08.setAdapter(adapterLesson);
		 mPeriodSpinner07.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			    	Period41 = mPeriodNumbers[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	Period41 = mPeriodNumbers[0]; }
			});
		 mPeriodSpinner08.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			    	Period42 = mPeriodNumbers[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	Period42 = mPeriodNumbers[0]; }
			});
		 
		 etRoom4 = (EditText) v.findViewById(R.id.et41);
	 }
	 
	 
	 public void initLE5Card(View v){
		 CardLesson5.setVisibility(View.VISIBLE);
		 TextView tvh5 = (TextView) v.findViewById(R.id.tv51);
		 tvh5.setText(getResources().getString(R.string.PeriodNr5));

		 tb51.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			 @Override
			 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			     if(isChecked) {
			    	 //vs51.showNext();
			    	 expand(vs51);
					 l5active = true;
			     }else{
			    	 //vs51.showPrevious();
			    	 collapse(vs51);
					 l5active = false;
			     }
			 }
			 });
		 
		 mDaySpinner5 = (Spinner) v.findViewById(R.id.sd5);
		 mDaySpinner5.setAdapter(adapterDay);
		 mDaySpinner5.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			        Day5 = mDayIDs[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			        Day5 = mDayIDs[0]; }
			});
		 mPeriodSpinner09 = (Spinner) v.findViewById(R.id.sl51);
		 mPeriodSpinner09.setAdapter(adapterLesson);
		 mPeriodSpinner10 = (Spinner) v.findViewById(R.id.sl52);
		 mPeriodSpinner10.setAdapter(adapterLesson);
		 mPeriodSpinner09.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			    	Period51 = mPeriodNumbers[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	Period51 = mPeriodNumbers[0]; }
			});
		 mPeriodSpinner10.setOnItemSelectedListener(new OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
			    	Period52 = mPeriodNumbers[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	Period52 = mPeriodNumbers[0]; }
			});
		 
		 etRoom5 = (EditText) v.findViewById(R.id.et51);
	 }
	 
	 public void closeLE3Card(View v){
		 //mInsertPoint2.removeAllViews();
		 //mInsertPoint2.setVisibility(View.GONE);
		 final ViewSwitcher vs31 = (ViewSwitcher) v.findViewById(R.id.vs31);
		 //vs31.showPrevious();
		 collapse(vs31);
		 tb31.setChecked(false);
		 tb41.setChecked(false);
		 tb51.setChecked(false);
		 CardLesson3.setVisibility(View.GONE);
		 closeLE4Card(v);
		 closeLE5Card(v);
		 l3active = false;
	 }
	 public void closeLE4Card(View v){
		 //mInsertPoint3.removeAllViews();
		 //mInsertPoint3.setVisibility(View.GONE);
		 final ViewSwitcher vs41 = (ViewSwitcher) v.findViewById(R.id.vs41);
		 //vs41.showPrevious();
		 collapse(vs41);
		 tb41.setChecked(false);
		 tb51.setChecked(false);
		 CardLesson4.setVisibility(View.GONE);
		 closeLE5Card(v);
		 l4active = false;
	 }
	 public void closeLE5Card(View v){
		 //mInsertPoint4.removeAllViews();
		 //mInsertPoint4.setVisibility(View.GONE);
		 final ViewSwitcher vs51 = (ViewSwitcher) v.findViewById(R.id.vs51);
		 //vs51.showPrevious();
		 collapse(vs51);
		 tb51.setChecked(false);
		 CardLesson5.setVisibility(View.GONE);
		 l5active = false;
	 }
	 

	public static void expand(final View v) {
	    v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    final int targtetHeight = v.getMeasuredHeight();

	    v.getLayoutParams().height = 0;
	    v.setVisibility(View.VISIBLE);
	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	            v.getLayoutParams().height = interpolatedTime == 1
	                    ? LayoutParams.WRAP_CONTENT
	                    : (int)(targtetHeight * interpolatedTime);
	            v.requestLayout();
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
	    v.startAnimation(a);
	}

	public static void collapse(final View v) {
	    final int initialHeight = v.getMeasuredHeight();

	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	            if(interpolatedTime == 1){
	                v.setVisibility(View.GONE);
	            }else{
	                v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
	                v.requestLayout();
	            }
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
	    v.startAnimation(a);
	}

	public void SaveData(){
		if (l2active == false){
			Day2 = "-";
			Period21 = "-";
			Period22 = "-";
			Room2 = "-";
		}else{
	        Room2 = etRoom2.getText().toString();
		}
		if (l3active == false){
			Day3 = "-";
			Period31 = "-";
			Period32 = "-";
			Room3 = "-";
		}else{
	        Room3 = etRoom3.getText().toString();
		}
		if (l4active == false){
			Day4 = "-";
			Period41 = "-";
			Period42 = "-";
			Room4 = "-";
		}else{
	        Room4 = etRoom4.getText().toString();
		}
		if (l5active == false){
			Day5 = "-";
			Period51 = "-";
			Period52 = "-";
			Room5 = "-";
		}else{
	        Room5 = etRoom5.getText().toString();
		}
		mTeacherName = etTeacherName.getText().toString();
		mTeacherAbbrev = etTeacherAbbrev.getText().toString();
        Room1 = etRoom1.getText().toString();

		mSubjectName = mSubjectName.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		mSubjectAbbrev = mSubjectAbbrev.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		mTeacherName = mTeacherName.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		mTeacherAbbrev = mTeacherAbbrev.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		Room1 = Room1.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		Room2 = Room2.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		Room3 = Room3.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		Room4 = Room4.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		Room5 = Room5.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");

		if (mTeacherName==null) mTeacherName="[null]";
		if (mTeacherAbbrev==null) mTeacherAbbrev="[null]";

		DataStorageHandler.EditSubject(getActivity(), mSubjectName, mSubjectAbbrev, mTeacherName, mTeacherAbbrev,
				Day1, Day2, Day3, Day4, Day5,
				Period11, Period21, Period31, Period41, Period51,
				Period12, Period22, Period32, Period42, Period52,
				Room1, Room2, Room3, Room4, Room5,
				Color, Textcolor,
				"default", "default", "default", "default");
	}
	
	public void notification(){
		
		if (!Period11.equals("") & !Period11.equals("-") & !Period11.equals(null) & 
				!Day1.equals("") & !Day1.equals("-") & !Day1.equals(null)){
			
			int period1start = Integer.valueOf(Period11);
			new ReminderManager(getActivity()).setLessonReminder((long)0, mSubjectName, Room1, mTeacherName, Color, Textcolor, period1start, Day1);
		}
		
		if (!Period21.equals("") & !Period21.equals("-") & !Period21.equals(null) & 
				!Day2.equals("") & !Day2.equals("-") & !Day2.equals(null)){
			
			int period2start = Integer.valueOf(Period21);
			new ReminderManager(getActivity()).setLessonReminder((long)0, mSubjectName, Room2, mTeacherName, Color, Textcolor, period2start, Day2);
		}
		
		if (!Period31.equals("") & !Period31.equals("-") & !Period31.equals(null) & 
				!Day3.equals("") & !Day3.equals("-") & !Day3.equals(null)){
			
			int period3start = Integer.valueOf(Period31);
			new ReminderManager(getActivity()).setLessonReminder((long)0, mSubjectName, Room3, mTeacherName, Color, Textcolor, period3start, Day3);
		}
		
		if (!Period41.equals("") & !Period41.equals("-") & !Period41.equals(null) & 
				!Day4.equals("") & !Day4.equals("-") & !Day4.equals(null)){
			
			int period4start = Integer.valueOf(Period41);
			new ReminderManager(getActivity()).setLessonReminder((long)0, mSubjectName, Room4, mTeacherName, Color, Textcolor, period4start, Day4);
		}
		
		if (!Period51.equals("") & !Period51.equals("-") & !Period51.equals(null) & 
				!Day5.equals("") & !Day5.equals("-") & !Day5.equals(null)){
			
			int period5start = Integer.valueOf(Period51);
			new ReminderManager(getActivity()).setLessonReminder((long)0, mSubjectName, Room5, mTeacherName, Color, Textcolor, period5start, Day5);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		MenuItem delete = menu.findItem(R.id.action_delete_subject);
		delete.setVisible(false);
		//inflater.inflate(R.menu.edit_subject, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
				// Handle action buttons
		switch(item.getItemId()) {
			case android.R.id.home:
				return false;
			case R.id.action_settings:
				((EditSubjectActivity) getActivity()).menuActionSettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
