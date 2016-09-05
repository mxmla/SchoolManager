package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.nearby.connection.dev.Strategy;
import com.google.android.gms.vision.text.Line;

import java.util.ArrayList;

public class CustomSubjectFragment extends Fragment{
	
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";
	private SharedPreferences prefs;
	private Tracker mTracker;
	private String fragmentName;

	private int mSubjectInt;
	private String[] mDayNames;
	private String[] mPeriodNumbers;
	private TextView tvSubjectName, tvSmallSubjectName, tvSmallSubjectAbbrev;
	private LinearLayout header1;
	private FrameLayout flTitle;
	private int hTextSize;
	private EditText etSubjectName, etSubjectAbbrev;
	private String[] mSubjectNames, mSubjectAbbrevs, mSubjectArray;
	private String mSubjectName, mSubjectAbbrev, mTeacherName, mTeacherAbbrev;
	private String Day1, Day2, Day3, Day4, Day5;
	private String Period11, Period12, Period21, Period22, Period31, Period32, Period41, Period42, Period51, Period52;
	private String Room1, Room2, Room3, Room4, Room5;
	private String Color, Textcolor;
	private EditText etTeacherName, etTeacherAbbrev, etRoom1, etRoom2, etRoom3, etRoom4, etRoom5;
	private View le2, le3, le4, le5;
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
	private String ttFolder;
	private float dp;
	private int cardPadding, cardMargin;
	private LinearLayout llLessonsHolder;
	private ArrayList<Integer> alLessonInts = new ArrayList<>();
	private int lessonCounter;
	private ArrayList<LinearLayout> alLessonList = new ArrayList<>();
	private ArrayList<String> alDayList = new ArrayList<>();
	private ArrayList<ArrayList<CheckBox>> alABsList = new ArrayList<>();
	private ArrayList<Boolean> alCustomTimeList = new ArrayList<>();
	private ArrayList<EditText> alPeriodFList = new ArrayList<>();
	private ArrayList<EditText> alPeriodTList = new ArrayList<>();
	private ArrayList<EditText> alTimeStartHList = new ArrayList<>();
	private ArrayList<EditText> alTimeStartMList = new ArrayList<>();
	private ArrayList<EditText> alTimeEndHList = new ArrayList<>();
	private ArrayList<EditText> alTimeEndMList = new ArrayList<>();
	private ArrayList<EditText> alPlaceList = new ArrayList<>();
	private ArrayList<String> allABs = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_custom_subject, container, false);

        ((EditSubjectActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_fragment_edit_subject));
		int ttColor = getActivity().getResources().getColor(R.color.color_timetable);
		((EditSubjectActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ttColor));


		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		fragmentName = "CustomSubjectFragment";
		// Obtain the shared Tracker instance.
		SchoolManager application = (SchoolManager) getActivity().getApplication();
		mTracker = application.getDefaultTracker();


		getTtFolderName();
		getAllABs();

        setUpTvSubjectName(v);
		setUpSubjectInfo(v);
		setUpTeacherInfo(v);
		setUPColorCircles(v);
		setUpLessonSpinnerAdapters(v);
		//setUpLessonCard1(v);
		//setUpOtherLessonCards(v);
		setUpLessonsCard(v);
		setUpNextButton(v);

		if (getArguments() != null && getArguments().containsKey("action") &&
				getArguments().getString("action").equals("edit_subject") && getArguments().containsKey("subjectID")){
			loadData(getArguments().getString("subjectID"));

		} else if (getArguments() != null && getArguments().containsKey("action") &&
				getArguments().getString("action").equals("new_timetable")){
			setUpFirstLesson();

		} else {
			//setUpFirstLesson();
			loadData("0001");
		}

		Color = "light_green";
		Textcolor = "black";
        
        return v;
    }

	@Override
	public void onResume(){
		super.onResume();

		Log.i("Analytics", "Setting screen name: " + fragmentName);
		mTracker.setScreenName("Image~" + fragmentName);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	private void setUpTvSubjectName(View v){
		tvSubjectName = (TextView) v.findViewById(R.id.tvSubjectName);
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

	private void setUpSubjectInfo(View v){
		mSubjectNames = getResources().getStringArray(R.array.SubjectNames);
		mSubjectAbbrevs = getResources().getStringArray(R.array.SubjectAbbreviations);

		etSubjectName = (EditText) v.findViewById(R.id.etSubjectName);
		etSubjectAbbrev = (EditText) v.findViewById(R.id.etSubjectAbbrev);
		tvSmallSubjectName = (TextView) v.findViewById(R.id.TextView06);
		tvSmallSubjectAbbrev = (TextView) v.findViewById(R.id.TextView05);

		etSubjectName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				tvSubjectName.setText(etSubjectName.getText().toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {}
		});

		if (getArguments().getString("caller").equals("NewTimetable")){
			String sName = getArguments().getString("subject_name");
			etSubjectName.setText(sName.replace("[null]", ""));
		}
	}

	private void setUpTeacherInfo(View v){
		etTeacherName = (EditText) v.findViewById(R.id.teacherName);
		etTeacherAbbrev = (EditText) v.findViewById(R.id.teacherAbbrev);
	}

	private void setUPColorCircles(View v){
		/*
		android.app.ActionBar actionBar = getActivity().getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
		*/

		Color = "light_green";
		Textcolor = "black";

		bColorChooserBg = (Button) v.findViewById(R.id.bColorChooserBg);
		bColorChooserT = (Button) v.findViewById(R.id.bColorChooserT);

		GradientDrawable bgd = (GradientDrawable) getResources().getDrawable(R.drawable.color_circle);
		bgd.setColor(getResources().getColor(R.color.light_green));
		bgd.setStroke(1, getResources().getColor(R.color.color_circle_stroke_color));

		bColorChooserBg.setBackgroundDrawable(bgd);

		GradientDrawable bgt = (GradientDrawable) getResources().getDrawable(R.drawable.color_circle);
		bgt.setColor(getResources().getColor(R.color.black));
		bgt.setStroke(1, getResources().getColor(R.color.color_circle_stroke_color));

		bColorChooserT.setBackgroundDrawable(bgt.mutate());

		bColorChooserBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditSubjectActivity) getActivity()).showDialog(v, "background", "CustomSubjectFragment");
			}
		});
		bColorChooserT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditSubjectActivity) getActivity()).showDialog(v, "text", "CustomSubjectFragment");
			}
		});
	}

	private void setUpLessonSpinnerAdapters(View v){
		mDayNames = getResources().getStringArray(R.array.DayIDs);
		mPeriodNumbers = getResources().getStringArray(R.array.PeriodNumbers);


		// Create an ArrayAdapter using the string array and a default spinner layout
		adapterDay = ArrayAdapter.createFromResource(getActivity(),
				R.array.DayNames, android.R.layout.simple_spinner_item);

		// Specify the layout to use when the list of choices appears
		adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


		adapterLesson = ArrayAdapter.createFromResource(getActivity(),
				R.array.PeriodNumbers, android.R.layout.simple_spinner_item);

		adapterLesson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	private void setUpLessonCard1(View v){
		mDaySpinner1 = (Spinner) v.findViewById(R.id.spinner1);
		mPeriodSpinner01 = (Spinner) v.findViewById(R.id.spinner2);
		mPeriodSpinner02 = (Spinner) v.findViewById(R.id.spinner3);
		etRoom1 = (EditText) v.findViewById(R.id.editText3);

		// Apply the adapter to the spinner
		mDaySpinner1.setAdapter(adapterDay);
		mDaySpinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Day1 = mDayNames[parentView.getSelectedItemPosition()];
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				Day1 = mDayNames[0];
			}
		});

		mPeriodSpinner01.setAdapter(adapterLesson);
		mPeriodSpinner02.setAdapter(adapterLesson);
		mPeriodSpinner01.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Period11 = mPeriodNumbers[parentView.getSelectedItemPosition()];
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				Period11 = mPeriodNumbers[0];
			}
		});
		mPeriodSpinner02.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Period12 = mPeriodNumbers[parentView.getSelectedItemPosition()];
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				Period12 = mPeriodNumbers[0];
			}
		});
	}

	private void setUpOtherLessonCards(View v){
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
		initLE2Card(v);
	}

	private void setUpNextButton(View v){
		mNextButton = (Button) v.findViewById(R.id.confirm);
		if (getArguments() != null && getArguments().containsKey("caller")
				&& getArguments().getString("caller").equals("NewTimetable")){
			mNextButton.setText(getActivity().getResources().getString(R.string.Next));
		} else if (getArguments() != null && getArguments().containsKey("caller")
				&& getArguments().getString("caller").equals("Timetable")){
			mNextButton.setText(getActivity().getResources().getString(R.string.save));
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mNextButton.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
			mNextButton.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
		} else {
			mNextButton.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
		}

		mNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SaveData();
			}
		});
	}

	private void getTtFolderName(){
		if (getArguments() != null && getArguments().containsKey("tt_folder")){
			ttFolder = getArguments().getString("tt_folder");
		} else {
			ttFolder = prefs.getString(getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
		}
	}

	private void getAllABs(){
		String tt_attr_filepath = ttFolder + "/" + getActivity().getResources().getString(R.string.file_name_timetable_attributes);
		String[][] attrArray = DataStorageHandler.toArray(getActivity(), tt_attr_filepath);

		for (int i = 0; i < attrArray.length; i++){
			allABs.add(attrArray[i][0]);
		}
	}


    public void saveChosenColor(String color, String which){
    	if (which.equals("background")){
        	int colorInt = 0xffFFFFFF;
			boolean isChosen = false;
        	for (int i = 0; i < colorNames.length; i++){
        		if (colorNames[i].equals(color)){
        			colorInt = colorInts[i];
					isChosen = true;
        		}
        	}
			if (isChosen) {
				Color = color;

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
        	int colorInt = 0xff000000;
			boolean isChosen = false;
        	for (int i = 0; i < colorNames.length; i++){
        		if (colorNames[i].equals(color)){
        			colorInt = colorInts[i];
					isChosen = true;
        		}
        	}
			if (isChosen) {
				Textcolor = color;

				((GradientDrawable) bColorChooserT.getBackground()).setColor(colorInt);
				if (color.equals("white")) {
					((GradientDrawable) bColorChooserT.getBackground()).setStroke(1, 0xff000000);
				}
				tvSubjectName.setTextColor(colorInt);
			}
    	}
    }

	public void loadData(String subjectID){
		String[] subjectInfo = DataStorageHandler.SubjectInfo(getActivity(), ttFolder, subjectID);

		if (subjectInfo!=null && !subjectInfo.equals(null) & !subjectInfo.equals("") & !subjectInfo.equals("-")){

			if (subjectInfo[0]!=null && !subjectInfo[0].equals("") && !subjectInfo[0].equals("-") && !subjectInfo[0].equals("[null]")){
				System.out.println("subjectInfo[0]: "+subjectInfo[0]);
				etSubjectName.setText(subjectInfo[0].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", "").replace("[none]", ""));
			}
			if (subjectInfo[1]!=null && !subjectInfo[1].equals("") && !subjectInfo[1].equals("-") && !subjectInfo[1].equals("[null]")){
				etSubjectAbbrev.setText(subjectInfo[1].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", "").replace("[none]", ""));
			}
			if (subjectInfo[2]!=null && !subjectInfo[2].equals("") && !subjectInfo[2].equals("-") && !subjectInfo[2].equals("[null]")){
				etTeacherName.setText(subjectInfo[2].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", "").replace("[none]", ""));
			}
			if (subjectInfo[3]!=null && !subjectInfo[3].equals("") && !subjectInfo[3].equals("-") && !subjectInfo[3].equals("[null]")){
				etTeacherAbbrev.setText(subjectInfo[3].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", "").replace("[none]", ""));
			}

			if (subjectInfo[4]!=null && !subjectInfo[4].equals("") && !subjectInfo[4].equals("-")){
				Color = subjectInfo[4];
				int bColorInt = getActivity().getResources().getColor(R.color.color_timetable);
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
				((EditSubjectActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(bColorInt));
				//flTitle.setBackgroundColor(bColorInt);
			}

			if (subjectInfo[5]!=null && !subjectInfo[5].equals("") && !subjectInfo[5].equals("-")){
				Textcolor = subjectInfo[5];
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

			final String[][] lessons = DataStorageHandler.SubjectLessons(getActivity(), ttFolder, subjectID);
			for (int l = 0; l < lessons.length; l++){
				if (lessons[l].length >= 9) {
					final String[] abs = lessons[l][1].split("/");
					final String day = lessons[l][2];
					final String custom = lessons[l][3];
					final String periodF = lessons[l][4];
					final String periodT = lessons[l][5];
					final String timeS = lessons[l][6];
					final String timeE = lessons[l][7];
					final String room = lessons[l][8];

					llLessonsHolder.addView(lessonLayout(lessonCounter, true, abs, day, custom, periodF, periodT, timeS, timeE, room));
					llLessonsHolder.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				}
			}
		}
	}

	public void setUpLessonsCard(View v){
		llLessonsHolder = (LinearLayout) v.findViewById(R.id.lessons_holder);
		LinearLayout llAddLesson = (LinearLayout) v.findViewById(R.id.llAddLesson);

		dp = 1 / getActivity().getResources().getDisplayMetrics().density;
		cardPadding = Math.round(getActivity().getResources().getDimension(R.dimen.card_padding) /** dp*/);
		cardMargin = Math.round(getActivity().getResources().getDimension(R.dimen.card_margin) /** dp*/);

		lessonCounter = 0;

		llAddLesson.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				llLessonsHolder.addView(lessonLayout(lessonCounter));
				llLessonsHolder.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		});
	}

	private void setUpFirstLesson(){
		llLessonsHolder.addView(lessonLayout(lessonCounter));
		llLessonsHolder.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	private LinearLayout lessonLayout(final int _intID){
		LinearLayout ll = lessonLayout(_intID, false, new String[] {"[none]"}, "[none]", "[none]", "[none]", "[none]",
				"[none]", "[none]", "[none]");

		return ll;
	}

	private LinearLayout lessonLayout(final int _intID, final boolean existing, final String[] abs, final String day,
									  final String custom, final String periodF, final String periodT,
									  final String timeS, final String timeE, final String room){
		Button delete = new Button(getActivity(), null, android.R.attr.buttonBarButtonStyle);
		delete.setId(_intID);
		delete.setTextColor(getActivity().getResources().getColor(R.color.color_button_delete_light));
		delete.setText(getActivity().getResources().getString(R.string.lesson_delete));
		delete.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		RelativeLayout rlDelete = new RelativeLayout(getActivity());
		rlDelete.setId(_intID);
		rlDelete.setGravity(Gravity.RIGHT);
		rlDelete.addView(delete);
		LinearLayout.LayoutParams lpDelete = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpDelete.setMargins(cardPadding, 0, cardPadding,0);
		rlDelete.setLayoutParams(lpDelete);

		View lesson_divider = new View(getActivity(), null, R.style.DividersStyle);
		lesson_divider.setId(_intID);
		lesson_divider.setBackgroundColor(getActivity().getResources().getColor(R.color.black));
		lesson_divider.setAlpha(0.12f);
		LinearLayout.LayoutParams lpDIV = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) getActivity().getResources().getDimension(R.dimen.divider_height));
		lpDIV.setMargins(0, 0, 0, (int) (cardPadding * 2));
		lesson_divider.setLayoutParams(lpDIV);

		final LinearLayout ll = new LinearLayout(getActivity(), null, R.style.CardContent);
		ll.setId(_intID);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(lesson_divider);
		ll.addView(linearLayout_day_week(_intID, existing, abs, day));
		ll.addView(linearLayout_Time(_intID, existing, custom, periodF, periodT, timeS, timeE));
		ll.addView(linearLayout_Place(_intID, existing, room));
		ll.addView(rlDelete);
		ll.setPadding(0, 0, 0, 0);
		ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));


		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				llLessonsHolder.removeView(ll);
				alLessonInts.remove(alLessonInts.indexOf(_intID));
			}
		});

		if (alLessonList.size() > _intID){
			alLessonList.set(_intID, ll);
		} else {
			alLessonList.add(ll);
		}
		if (!alLessonInts.contains(_intID)) alLessonInts.add(_intID);

		lessonCounter ++;
		return ll;
	}

	private LinearLayout linearLayout_day_week(int _intID, final boolean existing, final String[] abs, final String day){
		View div = new View(getActivity()/*, null, R.style.DividersStyle*/);
		div.setBackgroundColor(getActivity().getResources().getColor(R.color.black));
		div.setAlpha(0.12f);
		LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams((int) getActivity().getResources().getDimension(R.dimen.divider_height), LayoutParams.MATCH_PARENT);
		dlp.setMargins(0, cardMargin, 0, cardMargin);
		div.setLayoutParams(dlp);

		LinearLayout ll = new LinearLayout(getActivity());
		ll.setId(_intID);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.addView(linearLayout_day(_intID, existing, day));
		ll.addView(div);
		ll.addView(linearLayout_week(_intID, existing, abs));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(cardPadding, 0, cardPadding, (int) (cardPadding*2));
		ll.setLayoutParams(layoutParams);

		return ll;
	}
	private LinearLayout linearLayout_day(int _intID, final boolean existing, final String day){
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setId(_intID);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(textView_Day(_intID));
		ll.addView(spinner_day(_intID, existing, day));

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
		layoutParams.setMargins(0, 0, cardPadding, 0);
		ll.setLayoutParams(layoutParams);

		return ll;
	}
	private TextView textView_Day(int _intID){
		TextView tv = new TextView(getActivity(), null, R.style.CardContentText);
		tv.setId(_intID);
		tv.setText(getActivity().getResources().getString(R.string.lesson_day));
		tv.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return tv;
	}
	private Spinner spinner_day(final int _intID, final boolean existing, final String day){
		Spinner s = new Spinner(getActivity());
		s.setId(_intID);

		// Apply the adapter to the spinner
		s.setAdapter(adapterDay);
		s.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String day = mDayNames[parentView.getSelectedItemPosition()];
				if (alDayList.size() > _intID){
					alDayList.set(_intID, day);
				} else {
					alDayList.add(day);
				}
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				String day = mDayNames[0];
				if (alDayList.size() > _intID){
					alDayList.set(_intID, day);
				} else {
					alDayList.add(day);
				}
			}
		});

		if (existing && day != null){
			for (int d = 0; d < mDayNames.length; d++){
				if (mDayNames[d].equals(day)) s.setSelection(d);
			}
		}


		final int margin = (int) (-1 * (getActivity().getResources().getDimension(R.dimen.spinner_padding)));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(margin, (int) getActivity().getResources().getDimension(R.dimen.checkbox_padding_top), margin, 0);
		s.setLayoutParams(lp);

		return s;
	}
	private LinearLayout linearLayout_week(int _intID, final boolean existing, final String[] abs){
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setId(_intID);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(textView_Week(_intID));
		ll.addView(horizontalScrollView_ABs(_intID, existing, abs));

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
		layoutParams.setMargins(cardPadding, 0, 0, 0);
		ll.setLayoutParams(layoutParams);

		return ll;
	}
	private TextView textView_Week(int _intID){
		TextView tv = new TextView(getActivity(), null, R.style.CardContentText);
		tv.setId(_intID);
		tv.setText(getActivity().getResources().getString(R.string.lesson_week));
		tv.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return tv;
	}
	private HorizontalScrollView horizontalScrollView_ABs(int _intID, final boolean existing, final String[] abs){
		HorizontalScrollView hsv = new HorizontalScrollView(getActivity());
		hsv.setId(_intID);

		LinearLayout ll = new LinearLayout(getActivity());
		ll.setId(_intID);
		ll.setOrientation(LinearLayout.HORIZONTAL);

		ArrayList<CheckBox> cbs = new ArrayList<>();

		for (int i = 0; i < allABs.size(); i++){
			CheckBox cb = new CheckBox(getActivity());
			cb.setId(10000 + _intID * 100 + i);
			cb.setText(allABs.get(i));

			if (existing && abs != null){
				cb.setChecked(false);
				for (int a = 0; a < abs.length; a++){
					if (allABs.get(i).equals(abs[a])){
						cb.setChecked(true);
					}
				}
			} else {
				cb.setChecked(true);
			}

			final int margin = (int) (-1 * (getActivity().getResources().getDimension(R.dimen.checkbox_padding)));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(margin, 0, 0, 0);
			cb.setLayoutParams(lp);

			cbs.add(cb);

			if (i != 0) {
				TextView tv = new TextView(getActivity(), null, R.style.CardContentText);
				tv.setText("|");
				tv.setGravity(Gravity.CENTER_VERTICAL);
				tv.setPadding(cardMargin, 0, cardMargin, 0);
				tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				ll.addView(tv);
			}
			ll.addView(cb);
		}
		if (alABsList.size() > _intID){
			alABsList.set(_intID, cbs);
		} else {
			alABsList.add(cbs);
		}

		ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		hsv.addView(ll);
		hsv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return hsv;
	}

	private LinearLayout linearLayout_Time(int _intID, final boolean existing, final String custom, final String periodF, final String periodT,
										   final String timeS, final String timeE){
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setId(_intID);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.addView(textView_Time(_intID));
		ll.addView(relativeLayout_Time(_intID, existing, custom, periodF, periodT, timeS, timeE));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(cardPadding,0,cardPadding, cardMargin);
		ll.setLayoutParams(layoutParams);

		return ll;
	}
	private TextView textView_Time(int _intID){
		TextView tv = new TextView(getActivity(), null, R.style.CardContentText);
		tv.setId(_intID);
		tv.setText(getActivity().getResources().getString(R.string.lesson_time));
		tv.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) getActivity().getResources().getDimension(R.dimen.lesson_card_title_width), LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, (int) (getActivity().getResources().getDimension(R.dimen.radio_button_content_margin_top)), cardMargin, 0);
		tv.setLayoutParams(lp);

		return tv;
	}
	private RelativeLayout relativeLayout_Time(final int _intID, final boolean existing, final String custom, final String periodF, final String periodT,
											   final String timeS, final String timeE){
		RelativeLayout rl = new RelativeLayout(getActivity());
		rl.setId(_intID);

		RadioGroup rg = new RadioGroup(getActivity());
		rg.setId(_intID);
		final RadioButton rbPeriod = new RadioButton(getActivity());
		rbPeriod.setId(_intID + 2000);
		final RadioButton rbCustom = new RadioButton(getActivity());
		rbCustom.setId(_intID + 3000);

		LinearLayout llPeriod = new LinearLayout(getActivity());
		llPeriod.setId(_intID + 5000);
		final TextView tvPeriod = new TextView(getActivity(), null, R.style.CardContentText);
		tvPeriod.setId(_intID);
		final Spinner sPeriodT = new Spinner(getActivity());
		sPeriodT.setId(_intID);
		final Spinner sPeriodF = new Spinner(getActivity());
		sPeriodF.setId(_intID);
		final TextView tvPDivider = textView_PeriodDivider(_intID);
		final EditText etPeriodFrom = new EditText(getActivity());
		etPeriodFrom.setId(_intID);
		final EditText etPeriodTo = new EditText(getActivity());
		etPeriodTo.setId(_intID);

		final LinearLayout llCustom = new LinearLayout(getActivity());
		llCustom.setId(_intID);
		final EditText etCSH = et_timeStartHours(_intID, existing, timeS);
		final TextView tvHMDiv = textView_HourMinuteDivider(_intID);
		final EditText etCSM = et_timeStartMinutes(_intID, existing, timeS);
		final TextView tvTimesDiv = textView_TimesDivider(_intID);
		final EditText etCEH = et_timeEndHours(_intID, existing, timeE);
		final TextView tvHMDiv2 = textView_HourMinuteDivider(_intID);
		final EditText etCEM = et_timeEndMinutes(_intID, existing, timeE);


		// RelativeLayout
		// rl.setGravity(Gravity.T);
		// Set LayoutParams for the RelativeLayout
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		rl.setLayoutParams(layoutParams);
		rl.setPadding(0, 0, 0, 0);


		// LinearLayout (Period)
		llPeriod.setOrientation(LinearLayout.HORIZONTAL);
		// Add the LinearLayout (Period) to the RelativeLayout
		RelativeLayout.LayoutParams lpLlPeriod = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpLlPeriod.setMargins((int) (getActivity().getResources().getDimension(R.dimen.radio_button_content_margin_left)), (int) (-1 * getActivity().getResources().getDimension(R.dimen.edit_text_content_margin_top)), 0, 0);
		lpLlPeriod.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		llPeriod.setLayoutParams(lpLlPeriod);
		rl.addView(llPeriod);

		// TextView Period
		//tvPeriod.setTextColor(getActivity().getResources().getColor(R.color.Text));
		tvPeriod.setText(getActivity().getResources().getString(R.string.lesson_period));
		LinearLayout.LayoutParams lpPeriod = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpPeriod.setMargins(0, 0, cardMargin, 0);
		tvPeriod.setLayoutParams(lpPeriod);
		// Add the TextView to the LinearLayout
		llPeriod.addView(tvPeriod);

		/*// Spinner Period From
		sPeriodF.setAdapter(adapterLesson);
		sPeriodF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String period = mPeriodNumbers[parent.getSelectedItemPosition()];
				if (alPeriodFList.size() > _intID){
					alPeriodFList.set(_intID, period);
				} else {
					alDayList.add(period);
				}
				ArrayList<String> pfArray = new ArrayList<>();
				for (int i = parent.getSelectedItemPosition(); i < mPeriodNumbers.length; i++) pfArray.add(mPeriodNumbers[i]);
				sPeriodT.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pfArray));
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				String period = mPeriodNumbers[0];
				if (alPeriodFList.size() > _intID){
					alPeriodFList.set(_intID, period);
				} else {
					alDayList.add(period);
				}
				sPeriodT.setAdapter(adapterLesson);
			}
		});
		sPeriodF.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		// Add the Spinner to the LinearLayout
		llPeriod.addView(sPeriodF);*/

		// EditText Period From
		etPeriodFrom.setEms(2);
		etPeriodFrom.setInputType(InputType.TYPE_CLASS_NUMBER);
		etPeriodFrom.setGravity(Gravity.CENTER_HORIZONTAL);
		etPeriodFrom.setFilters(new InputFilter[]{new InputFilterMinMax("1", "12")});
		etPeriodFrom.setText("1");
		if (existing && periodF != null) etPeriodFrom.setText(periodF);
		etPeriodFrom.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		if (alPeriodFList.size() > _intID){
			alPeriodFList.set(_intID, etPeriodFrom);
		} else {
			alPeriodFList.add(etPeriodFrom);
		}
		// Add the EditText to the LinearLayout
		llPeriod.addView(etPeriodFrom);

		// Add the TextView Period Divider "-" to the LinearLayout
		llPeriod.addView(tvPDivider);

		/*// Spinner Period To
		sPeriodT.setAdapter(adapterLesson);
		sPeriodT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String period = mPeriodNumbers[parent.getSelectedItemPosition() + (mPeriodNumbers.length - parent.getAdapter().getCount())];
				if (alPeriodTList.size() > _intID){
					alPeriodTList.set(_intID, period);
				} else {
					alPeriodTList.add(period);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				String period = mPeriodNumbers[mPeriodNumbers.length - parent.getAdapter().getCount()];
				if (alPeriodTList.size() > _intID){
					alPeriodTList.set(_intID, period);
				} else {
					alPeriodTList.add(period);
				}
			}
		});
		sPeriodT.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		// Add the Spinner to the LinearLayout
		llPeriod.addView(sPeriodT);*/


		// EditText Period To
		etPeriodTo.setEms(2);
		etPeriodTo.setInputType(InputType.TYPE_CLASS_NUMBER);
		etPeriodTo.setGravity(Gravity.CENTER_HORIZONTAL);
		etPeriodTo.setFilters(new InputFilter[]{new InputFilterMinMax("1", "12")});
		etPeriodTo.setText("1");
		if (existing && periodT != null) etPeriodTo.setText(periodT);
		etPeriodTo.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		if (alPeriodTList.size() > _intID){
			alPeriodTList.set(_intID, etPeriodTo);
		} else {
			alPeriodTList.add(etPeriodTo);
		}
		// Add the EditText to the LinearLayout
		llPeriod.addView(etPeriodTo);


		// LinearLayout Custom Time
		llCustom.setOrientation(LinearLayout.HORIZONTAL);
		// Add the LinearLayout to the RelativeLayout
		RelativeLayout.LayoutParams lpLlCustom = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lpLlCustom.setMargins((int) (getActivity().getResources().getDimension(R.dimen.radio_button_content_margin_left)), (int) (-1 * getActivity().getResources().getDimension(R.dimen.edit_text_content_margin_top)), 0, 0);
		lpLlCustom.addRule(RelativeLayout.BELOW, llPeriod.getId());
		llCustom.setLayoutParams(lpLlCustom);
		rl.addView(llCustom);

		//Add views
		llCustom.addView(etCSH);
		llCustom.addView(tvHMDiv);
		llCustom.addView(etCSM);
		llCustom.addView(tvTimesDiv);
		llCustom.addView(etCEH);
		llCustom.addView(tvHMDiv2);
		llCustom.addView(etCEM);

		// Radio Group
		rg.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		// Add the RadioGroup to the RelativeLayout
		rl.addView(rg);

		// RadioButton Period
		rbPeriod.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		getActivity().setTheme(R.style.AppTheme);
		// Add the RadioButton to the RadioGroup
		rg.addView(rbPeriod);

		//RadioButton Custom
		RelativeLayout.LayoutParams lprbc = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lprbc.setMargins(0, (int) getActivity().getResources().getDimension(R.dimen.edit_text_padding_top), 0, 0);
		rbCustom.setLayoutParams(lprbc);
		// Add the RadioButton to the RadioGroup
		rg.addView(rbCustom);

		// RadioGroup Listener
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				if (i == rbPeriod.getId()){
					tvPeriod.setTextColor(getResources().getColor(R.color.Text));
					//sPeriodF.setEnabled(true);
					etPeriodFrom.setEnabled(true);
					etPeriodFrom.setTextColor(getResources().getColor(R.color.Text));
					tvPDivider.setTextColor(getResources().getColor(R.color.Text));
					//sPeriodT.setEnabled(true);
					etPeriodTo.setEnabled(true);
					etPeriodTo.setTextColor(getResources().getColor(R.color.Text));

					etCSH.setEnabled(false);
					etCSH.setTextColor(getResources().getColor(R.color.Disabled));
					etCSM.setEnabled(false);
					etCSM.setTextColor(getResources().getColor(R.color.Disabled));
					etCEH.setEnabled(false);
					etCEH.setTextColor(getResources().getColor(R.color.Disabled));
					etCEM.setEnabled(false);
					etCEM.setTextColor(getResources().getColor(R.color.Disabled));
					tvHMDiv.setTextColor(getResources().getColor(R.color.Disabled));
					tvTimesDiv.setTextColor(getResources().getColor(R.color.Disabled));
					tvHMDiv.setTextColor(getResources().getColor(R.color.Disabled));
					tvHMDiv2.setTextColor(getResources().getColor(R.color.Disabled));
					if (alCustomTimeList.size() > _intID){
						alCustomTimeList.set(_intID, true);
					} else {
						alCustomTimeList.add(true);
					}
				} else if (i == rbCustom.getId()){
					tvPeriod.setTextColor(getResources().getColor(R.color.Disabled));
					//sPeriodF.setEnabled(true);
					etPeriodFrom.setEnabled(false);
					etPeriodFrom.setTextColor(getResources().getColor(R.color.Disabled));
					tvPDivider.setTextColor(getResources().getColor(R.color.Disabled));
					//sPeriodT.setEnabled(true);
					etPeriodTo.setEnabled(false);
					etPeriodTo.setTextColor(getResources().getColor(R.color.Disabled));

					etCSH.setEnabled(true);
					etCSH.setTextColor(getResources().getColor(R.color.Text));
					etCSM.setEnabled(true);
					etCSM.setTextColor(getResources().getColor(R.color.Text));
					etCEH.setEnabled(true);
					etCEH.setTextColor(getResources().getColor(R.color.Text));
					etCEM.setEnabled(true);
					etCEM.setTextColor(getResources().getColor(R.color.Text));
					tvHMDiv.setTextColor(getResources().getColor(R.color.Text));
					tvTimesDiv.setTextColor(getResources().getColor(R.color.Text));
					tvHMDiv.setTextColor(getResources().getColor(R.color.Text));
					tvHMDiv2.setTextColor(getResources().getColor(R.color.Text));
					if (alCustomTimeList.size() > _intID){
						alCustomTimeList.set(_intID, false);
					} else {
						alCustomTimeList.add(false);
					}
				}
			}
		});

		if (existing && custom != null && custom.equals("true")) {
			rg.check(rbCustom.getId());
		} else {
			rg.check(rbPeriod.getId());
		}


		return rl;
	}
	private TextView textView_PeriodDivider(int _intID){
		TextView tv = new TextView(getActivity());
		tv.setId(_intID);
		tv.setText("-");
		tv.setGravity(Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(cardMargin, 0, cardMargin, 0);
		tv.setLayoutParams(lp);

		return tv;
	}
	private EditText et_timeStartHours(int _intID, final boolean existing, final String timeS){
		final EditText et = new EditText(getActivity());
		et.setId(_intID);
		et.setMaxEms(2);
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		et.setGravity(Gravity.RIGHT);
		et.setFilters(new InputFilter[]{new InputFilterMinMax("0", "23")});
		/*et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			@Override
			public void afterTextChanged(Editable editable) {
				final String text = et.getText().toString();
				if (text.length() == 1) et.setText("0"+text);
				if (text.length() <= 0) et.setText("00");
			}
		});*/
		et.setText("13");
		if (existing && timeS != null && timeS.split(":").length >= 2 && timeS.split(":")[0] != null)
			et.setText(timeS.split(":")[0]);
		et.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		if (alTimeStartHList.size() > _intID){
			alTimeStartHList.set(_intID, et);
		} else {
			alTimeStartHList.add(et);
		}
		return et;
	}
	private EditText et_timeStartMinutes(int _intID, final boolean existing, final String timeS){
		final EditText et = new EditText(getActivity());
		et.setId(_intID);
		et.setMaxEms(2);
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		et.setGravity(Gravity.LEFT);
		et.setFilters(new InputFilter[]{new InputFilterMinMax("0", "59")});
		/*et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			@Override
			public void afterTextChanged(Editable editable) {
				final String text = et.getText().toString();
				if (text.length() == 1) et.setText("0"+text);
				if (text.length() <= 0) et.setText("00");
			}
		});*/
		et.setText("30");
		if (existing && timeS != null && timeS.split(":").length >= 2 && timeS.split(":")[1] != null)
			et.setText(timeS.split(":")[1]);
		et.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		if (alTimeStartMList.size() > _intID){
			alTimeStartMList.set(_intID, et);
		} else {
			alTimeStartMList.add(et);
		}
		return et;
	}
	private EditText et_timeEndHours(int _intID, final boolean existing, final String timeE){
		final EditText et = new EditText(getActivity());
		et.setId(_intID);
		et.setMaxEms(2);
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		et.setGravity(Gravity.RIGHT);
		et.setFilters(new InputFilter[]{new InputFilterMinMax("0", "23")});
		/*et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			@Override
			public void afterTextChanged(Editable editable) {
				final String text = et.getText().toString();
				if (text.length() == 1) et.setText("0"+text);
				if (text.length() <= 0) et.setText("00");
			}
		});*/
		et.setText("14");
		if (existing && timeE != null && timeE.split(":").length >= 2 && timeE.split(":")[0] != null)
			et.setText(timeE.split(":")[0]);
		et.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		if (alTimeEndHList.size() > _intID){
			alTimeEndHList.set(_intID, et);
		} else {
			alTimeEndHList.add(et);
		}
		return et;
	}
	private EditText et_timeEndMinutes(int _intID, final boolean existing, final String timeE){
		final EditText et = new EditText(getActivity());
		et.setId(_intID);
		et.setMaxEms(2);
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		et.setGravity(Gravity.LEFT);
		et.setFilters(new InputFilter[]{new InputFilterMinMax("0", "59")});
		/*et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}
			@Override
			public void afterTextChanged(Editable editable) {
				final String text = et.getText().toString();
				if (text.length() == 1) et.setText("0"+text);
				if (text.length() <= 0) et.setText("00");
			}
		});*/
		et.setText("30");
		if (existing && timeE != null && timeE.split(":").length >= 2 && timeE.split(":")[1] != null)
			et.setText(timeE.split(":")[1]);
		et.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		if (alTimeEndMList.size() > _intID){
			alTimeEndMList.set(_intID, et);
		} else {
			alTimeEndMList.add(et);
		}
		return et;
	}
	private TextView textView_HourMinuteDivider(int _intID){
		TextView tv = new TextView(getActivity());
		tv.setId(_intID);
		tv.setText(":");
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		return tv;
	}
	private TextView textView_TimesDivider(int _intID){
		TextView tv = new TextView(getActivity());
		tv.setId(_intID);
		tv.setText("-");
		tv.setGravity(Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(cardMargin, 0, cardMargin, 0);
		tv.setLayoutParams(lp);

		return tv;
	}
	private LinearLayout linearLayout_Place(int _intID, final boolean existing, final String room){
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setId(_intID);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.addView(textView_Place(_intID));
		ll.addView(editText_Place(_intID, existing, room));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(cardPadding,0,cardPadding,cardMargin);
		ll.setLayoutParams(layoutParams);

		return ll;
	}
	private TextView textView_Place(int _intID){
		TextView tv = new TextView(getActivity(), null, R.style.CardContentText);
		tv.setId(_intID);
		tv.setText(getActivity().getResources().getString(R.string.lesson_place));
		tv.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
		tv.setLayoutParams(new LayoutParams((int) getActivity().getResources().getDimension(R.dimen.lesson_card_title_width), LayoutParams.WRAP_CONTENT));

		return tv;
	}
	private EditText editText_Place(int _intID, final boolean existing, final String room){
		EditText et = new EditText(getActivity());
		et.setId(_intID);
		et.setSingleLine(true);
		et.setHint(R.string.HintRoom);
		if (existing && room != null && !room.equals("") && !room.equals("[none]"))
			et.setText(room.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", "").replace("[none]", ""));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(cardMargin,0,0,0);
		et.setLayoutParams(layoutParams);

		if (alPlaceList.size() > _intID){
			alPlaceList.set(_intID, et);
		} else {
			alPlaceList.add(et);
		}
		return et;
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
			        Day2 = mDayNames[parentView.getSelectedItemPosition()]; }
			    public void onNothingSelected(AdapterView<?> parentView) {
			        Day2 = mDayNames[0]; }
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
				 if (isChecked) {
					 initLE4Card(view);
					 //vs31.showNext();
					 expand(vs31);
					 l3active = true;
				 } else {
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
			 public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				 Day3 = mDayNames[parentView.getSelectedItemPosition()];
			 }

			 public void onNothingSelected(AdapterView<?> parentView) {
				 Day3 = mDayNames[0];
			 }
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
			 public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				 Period32 = mPeriodNumbers[parentView.getSelectedItemPosition()];
			 }

			 public void onNothingSelected(AdapterView<?> parentView) {
				 Period32 = mPeriodNumbers[0];
			 }
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
				 if (isChecked) {
					 initLE5Card(view);
					 //vs41.showNext();
					 expand(vs41);
					 l4active = true;
				 } else {
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
			 public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				 Day4 = mDayNames[parentView.getSelectedItemPosition()];
			 }

			 public void onNothingSelected(AdapterView<?> parentView) {
				 Day4 = mDayNames[0];
			 }
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
			 public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				 Period42 = mPeriodNumbers[parentView.getSelectedItemPosition()];
			 }

			 public void onNothingSelected(AdapterView<?> parentView) {
				 Period42 = mPeriodNumbers[0];
			 }
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
			 public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				 Day5 = mDayNames[parentView.getSelectedItemPosition()];
			 }

			 public void onNothingSelected(AdapterView<?> parentView) {
				 Day5 = mDayNames[0];
			 }
		 });
		 mPeriodSpinner09 = (Spinner) v.findViewById(R.id.sl51);
		 mPeriodSpinner09.setAdapter(adapterLesson);
		 mPeriodSpinner10 = (Spinner) v.findViewById(R.id.sl52);
		 mPeriodSpinner10.setAdapter(adapterLesson);
		 mPeriodSpinner09.setOnItemSelectedListener(new OnItemSelectedListener() {
			 public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				 Period51 = mPeriodNumbers[parentView.getSelectedItemPosition()];
			 }

			 public void onNothingSelected(AdapterView<?> parentView) {
				 Period51 = mPeriodNumbers[0];
			 }
		 });
		 mPeriodSpinner10.setOnItemSelectedListener(new OnItemSelectedListener() {
			 public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				 Period52 = mPeriodNumbers[parentView.getSelectedItemPosition()];
			 }

			 public void onNothingSelected(AdapterView<?> parentView) {
				 Period52 = mPeriodNumbers[0];
			 }
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
	    a.setDuration((int)(targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
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
		/*if (l2active == false){
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
		}*/
		mTeacherName = etTeacherName.getText().toString();
		mTeacherAbbrev = etTeacherAbbrev.getText().toString();
        //Room1 = etRoom1.getText().toString();
        mSubjectName = etSubjectName.getText().toString();
        mSubjectAbbrev = etSubjectAbbrev.getText().toString();

		mSubjectName = mSubjectName.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		mSubjectAbbrev = mSubjectAbbrev.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		mTeacherName = mTeacherName.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		mTeacherAbbrev = mTeacherAbbrev.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		/*Room1 = Room1.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		Room2 = Room2.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		Room3 = Room3.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		Room4 = Room4.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");
		Room5 = Room5.replace(System.getProperty("line.separator"), "[newline]").replace(",", "[comma]");*/

		boolean proceed = true;
		if (mSubjectName==null||mSubjectName.equals("")){
			proceed=false;
			tvSmallSubjectName.setTextColor(getResources().getColor(R.color.red));
		}
		if (mSubjectAbbrev==null||mSubjectAbbrev.equals("")){
			proceed=false;
			tvSmallSubjectAbbrev.setTextColor(getResources().getColor(R.color.red));
		}
		if (mTeacherName==null) mTeacherName="[null]";
		if (mTeacherAbbrev==null) mTeacherAbbrev="[null]";

		if (!proceed){
			Toast.makeText(getActivity(), getResources().getString(R.string.toast_type_in_subject_name_and_abbreviation), Toast.LENGTH_LONG).show();
		} else {
			//TODO something here still doesn't work...
			if (mTeacherName==null) mTeacherName="[null]";
			if (mTeacherAbbrev==null) mTeacherAbbrev="[null]";
			/*if (Room1==null) Room1="";
			if (Room2==null) Room2="";
			if (Room3==null) Room3="";
			if (Room4==null) Room4="";
			if (Room5==null) Room5="";*/



			/*DataStorageHandler.EditSubject(getActivity(), mSubjectName, mSubjectAbbrev, mTeacherName, mTeacherAbbrev,
					Day1, Day2, Day3, Day4, Day5,
					Period11, Period21, Period31, Period41, Period51,
					Period12, Period22, Period32, Period42, Period52,
					Room1, Room2, Room3, Room4, Room5,
					Color, Textcolor,
					"default", "default", "default", "default");*/

			ArrayList<String[]> LessonsList = new ArrayList<>();

			for (int lesson = 0; lesson < alLessonInts.size(); lesson++){
				int thisLessonInt = alLessonInts.get(lesson);

				String sABs = "";
				for (CheckBox checkBox : alABsList.get(thisLessonInt)){
					if (checkBox.isChecked()){
						if (sABs == null || sABs.equals("")){
							sABs = checkBox.getText().toString().replace(" ","");
						} else {
							sABs = sABs + "/" + checkBox.getText().toString().replace(" ","");
						}
					}
				}

				String sDay = alDayList.get(thisLessonInt);

				boolean bCustomTime = (boolean) alCustomTimeList.get(thisLessonInt);
				String sCustomTime = String.valueOf(bCustomTime);

				String sPeriodFrom = alPeriodFList.get(thisLessonInt).getText().toString().replace(" ", "");
				if (sPeriodFrom == null || sPeriodFrom.equals("")) sPeriodFrom = "[null]";

				String sPeriodTo = alPeriodTList.get(thisLessonInt).getText().toString().replace(" ", "");
				if (sPeriodTo == null || sPeriodTo.equals("")) sPeriodTo = "[null]";

				if (DataStorageHandler.isStringNumeric(sPeriodFrom) && DataStorageHandler.isStringNumeric(sPeriodTo) &&
						Integer.parseInt(sPeriodFrom) > Integer.parseInt(sPeriodTo)){
					final String sFOld = sPeriodFrom;
					sPeriodFrom = sPeriodTo;
					sPeriodTo = sFOld;
				}

				String sTimeStartH = alTimeStartHList.get(thisLessonInt).getText().toString().replace(" ", "");
				if (sTimeStartH == null || sTimeStartH.equals("")) sTimeStartH = "[null]";

				String sTimeStartM = alTimeStartMList.get(thisLessonInt).getText().toString().replace(" ", "");
				if (sTimeStartM == null || sTimeStartM.equals("")) sTimeStartM = "[null]";

				String sTimeEndH = alTimeEndHList.get(thisLessonInt).getText().toString().replace(" ", "");
				if (sTimeEndH == null || sTimeEndH.equals("")) sTimeEndH = "[null]";

				String sTimeEndM = alTimeEndMList.get(thisLessonInt).getText().toString().replace(" ", "");
				if (sTimeEndM == null || sTimeEndM.equals("")) sTimeEndM = "[null]";

				if (DataStorageHandler.isStringNumeric(sTimeStartH) && DataStorageHandler.isStringNumeric(sTimeStartM) &&
						DataStorageHandler.isStringNumeric(sTimeEndH) && DataStorageHandler.isStringNumeric(sTimeEndM) &&
						(Integer.parseInt(sTimeStartH) > Integer.parseInt(sTimeEndH) ||
								(Integer.parseInt(sTimeStartH) == Integer.parseInt(sTimeEndH) && Integer.parseInt(sTimeStartM) > Integer.parseInt(sTimeEndM)))){
					final String sTSHOld = sTimeStartH;
					final String sTSMOld = sTimeStartM;
					sTimeStartH = sTimeEndH;
					sTimeStartM = sTimeEndM;
					sTimeEndH = sTSHOld;
					sTimeEndM = sTSMOld;
				}

				String sTimeStart = sTimeStartH + ":" + sTimeStartM;
				String sTimeEnd = sTimeEndH + ":" + sTimeEndM;

				String sPlace = alPlaceList.get(thisLessonInt).getText().toString().replace(",", "[comma]");
				if (sPlace == null || sPlace.equals("")) sPlace = "[null]";

				LessonsList.add(new String[] {sABs, sDay, sCustomTime, sPeriodFrom, sPeriodTo, sTimeStart, sTimeEnd, sPlace});
			}

			//LessonsList.add(new String[] {"A", Day1, "false", Period11, Period12, "-", "-", Room1});
			String[][] LessonsArray = new String[LessonsList.size()][];
			for (int i = 0; i < LessonsList.size(); i++) {
				String[] row = LessonsList.get(i);
				LessonsArray[i] = row;
			}

			System.out.println("ttFolder: " + ttFolder);

			// Register the subject
			if (getArguments() != null && getArguments().containsKey("action") &&
					getArguments().getString("action").equals("edit_subject") && getArguments().containsKey("subjectID")){
				DataStorageHandler.EditSubject(getActivity(), ttFolder, getArguments().getString("subjectID", "[none]"),
						mSubjectName, mSubjectAbbrev, mTeacherName, mTeacherAbbrev, Color, Textcolor, LessonsArray);
			} else {
				DataStorageHandler.RegisterSubject(getActivity(), ttFolder, mSubjectName, mSubjectAbbrev,
						mTeacherName, mTeacherAbbrev, Color, Textcolor, LessonsArray);
			}

			if (getArguments() != null && getArguments().containsKey("action") && getArguments().getString("action").equals("add_subject")){
				if (getArguments().containsKey("caller") && getArguments().getString("caller").equals("Timetable")){
					Intent i = new Intent(getActivity(),
							TimetableActivity.class);
					startActivityForResult(i, 0);
				} else {
					Intent i = new Intent(getActivity(),
							TimetableActivity.class);
					startActivityForResult(i, 0);
				}
			} else {
				((EditSubjectActivity)getActivity()).NextSubject();
			}
		}
	}

	public class InputFilterMinMax implements InputFilter {

		private int min, max;

		public InputFilterMinMax(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public InputFilterMinMax(String min, String max) {
			this.min = Integer.parseInt(min);
			this.max = Integer.parseInt(max);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			try {
				int input = Integer.parseInt(dest.toString() + source.toString());
				if (isInRange(min, max, input))
					return null;
			} catch (NumberFormatException nfe) { }
			return "";
		}

		private boolean isInRange(int a, int b, int c) {
			return b > a ? c >= a && c <= b : c >= b && c <= a;
		}
	}
}
