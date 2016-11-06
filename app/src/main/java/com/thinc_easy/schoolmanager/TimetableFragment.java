package com.thinc_easy.schoolmanager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TimetableFragment extends Fragment {
	
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

	private Tracker mTracker;
	private String fragmentName;
	private TableLayout tableLayout; 
	private static int progress;
	private ProgressBar progressBar;
	private int progressStatus = 0;
	private Handler handler = new Handler();
	
	private Button bM1, bM2, bM3, bM4, bM5, bM6, bM7, bM8, bM9, bM10, 
					bTu1, bTu2, bTu3, bTu4, bTu5, bTu6, bTu7, bTu8, bTu9, bTu10,
					bW1, bW2, bW3, bW4, bW5, bW6, bW7, bW8, bW9, bW10,
					bTh1, bTh2, bTh3, bTh4, bTh5, bTh6, bTh7, bTh8, bTh9, bTh10,
					bF1, bF2, bF3, bF4, bF5, bF6, bF7, bF8, bF9, bF10;
	private String aM1, aM2, aM3, aM4, aM5, aM6, aM7, aM8, aM9, aM10, 
					aTu1, aTu2, aTu3, aTu4, aTu5, aTu6, aTu7, aTu8, aTu9, aTu10,
					aW1, aW2, aW3, aW4, aW5, aW6, aW7, aW8, aW9, aW10,
					aTh1, aTh2, aTh3, aTh4, aTh5, aTh6, aTh7, aTh8, aTh9, aTh10,
					aF1, aF2, aF3, aF4, aF5, aF6, aF7, aF8, aF9, aF10;
	private int[] colorIntsOld = {0xff33B5E5, 0xff0099CC, 0xffAA66CC, 0xff9933CC, 0xff99CC00, 0xff669900,
			0xffFFBB33, 0xffFF8800, 0xffFF4444, 0xffCC0000, 0xff000000, 0xffFFFFFF};
    private int[] colorInts = {0xffF44336, 0xffE91E63, 0xff9C27B0, 0xff673AB7, 0xff3F51B5, 0xff2196F3,
            0xff03A9F4, 0xff00BCD4, 0xff009688, 0xff4CAF50, 0xff8BC34A, 0xffCDDC39,
            0xffFFEB3B, 0xffFFC107, 0xffFF9800, 0xffFF5722, 0xff795548, 0xff9E9E9E, 0xff607D8B, 0xff000000, 0xffFFFFFF};
	private int[] sColorIntsOld = {0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000,
			0xff000000, 0xff000000, 0xff000000, 0xffFF8800, 0xffFF8800};
    private int[] sColorInts = {0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000,
            0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000,
            0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xffFF8800, 0xffFF8800};
	private String[] colorNamesOld = {"blue_light", "blue_dark", "purple_light", "purple_dark", "green_light", "green_dark",
			"orange_light", "orange_dark", "red_light", "red_dark", "black", "white", "gray_light", "gray_dark"};
    private String[] colorNames = {"red", "pink", "purple", "deep_purple", "indigo", "blue",
            "light_blue", "cyan", "teal", "green", "light_green", "lime", "yellow", "amber",
            "orange", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
	private String selected, which, whichFragment;
	private int[] dayInts = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
								3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
	private int[] periodInts = {0,1,2,3,4,5,6,7,8,9, 0,1,2,3,4,5,6,7,8,9, 0,1,2,3,4,5,6,7,8,9, 0,1,2,3,4,5,6,7,8,9, 0,1,2,3,4,5,6,7,8,9};
	private int nmbrPeriods;
	private TextView tvD1, tvD2, tvD3, tvD4, tvD5, tvN1, tvN2, tvN3, tvN4, tvN5, tvN6, tvN7, tvN8, tvN9, tvN10;
	private TableRow tr1, tr2, tr3, tr4, tr5, tr6, tr7, tr8, tr9, tr10;
	private TableRow[] TableRows = {tr1, tr2, tr3, tr4, tr5, tr6, tr7, tr8, tr9, tr10};
	private long pLength1, pLength2, pLength3, pLength4, pLength5, pLength6, pLength7, pLength8, pLength9, pLength10, pLength11, pLength12,
				bLength1, bLength2, bLength3, bLength4, bLength5, bLength6, bLength7, bLength8, bLength9, bLength10, bLength11, bLength12;
	private long[] periodLengths = {pLength1, pLength2, pLength3, pLength4, pLength5, pLength6, pLength7, pLength8, pLength9, pLength10, pLength11, pLength12};
	private long[] breakLengths = {bLength1, bLength2, bLength3, bLength4, bLength5, bLength6, bLength7, bLength8, bLength9, bLength10, bLength11, bLength12};
	private long PeriodsAndBreaksLengthOverall;
	private View viewBreak1, viewBreak2, viewBreak3, viewBreak4, viewBreak5, viewBreak6, viewBreak7, viewBreak8, viewBreak9;
	private View[] viewsBreaks = {viewBreak1, viewBreak2, viewBreak3, viewBreak4, viewBreak5, viewBreak6, viewBreak7, viewBreak8, viewBreak9};

	private SharedPreferences prefs;
	private String ttFolder, showAllDaysPref;
	private String[] dayAbbrevs, allABs;
	private ViewAnimator vaDays, vaTimetable;
	private ImageButton ibLeft, ibRight, ibGoToToday;
	private int column_division, column_min_width, lesson_min_height, height_days, abbrev_place_padding,
			timetable_period_time_height, timetable_period_time_lines_height, timetable_period_time_lines_left_part, timetable_period_time_lines_margin_right,
			timetable_period_divider_width, timetable_periods_padding_right;
	private int color_days_text, color_time_lines, color_divider_periods, color_timetable_periods_text, color_timetable_period_times_text;
	private float text_size_days, text_size_lesson_abbrev, text_size_lesson_place, text_size_lesson_time,
			timetable_periods_textsize, timetable_period_times_textsize;
	private int firstDayOfWeek, currentABPosition, number_of_periods;
	private boolean showPeriodDivider, showHoursInsteadOfPeriods;
	private Animation slide_in_left, slide_out_right, slide_in_right, slide_out_left;
	private Calendar active_week_day;
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timetable, container, false);

		fragmentName = "TimetableFragment";
		// Obtain the shared Tracker instance.
		SchoolManager application = (SchoolManager) getActivity().getApplication();
		mTracker = application.getDefaultTracker();

        ((TimetableActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_fragment_timetable));
        ((TimetableActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

		int ttColor = ((TimetableActivity) getActivity()).getResources().getColor(R.color.color_timetable_appbar);
		((TimetableActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ttColor));

		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*nmbrPeriods = 10;
        
        tableLayout = (TableLayout) v.findViewById(R.id.TableLayout);
//        tableLayout.setVisibility(4);
        findViews(v);
        setUpButtons(v);
        setUpDaysNumbers(v);
        setUpRowHeights(v);*/

		setUpTimetables(v);
        
        return v;
    }

	private void setUpTimetables(View v){

		getTtFolderName();
		getAllABs();
		getAllUniversalConstants();

		currentABPosition = DataStorageHandler.getCurrentAB(getActivity(), ttFolder, Calendar.getInstance());

		setUpAnimators(v);
		setUpWeekIndicator(v, Calendar.getInstance(), currentABPosition);
		addViewsToAnimators(currentABPosition);
	}

	private void getAllUniversalConstants(){
		final float dp = getActivity().getResources().getDisplayMetrics().density;
		column_division = (int) (getActivity().getResources().getDimension(R.dimen.timetable_column_division) + 0.5f);
		color_days_text = getActivity().getResources().getColor(R.color.color_timetable_days_text);
		text_size_days = getActivity().getResources().getDimension(R.dimen.timetable_days_textsize);
		color_timetable_periods_text = getActivity().getResources().getColor(R.color.color_timetable_periods_text);
		timetable_periods_textsize = getActivity().getResources().getDimension(R.dimen.timetable_periods_textsize);
		color_timetable_period_times_text = getActivity().getResources().getColor(R.color.color_timetable_period_times_text);
		timetable_period_times_textsize = getActivity().getResources().getDimension(R.dimen.timetable_period_times_textsize);
		height_days = (int) (getActivity().getResources().getDimension(R.dimen.timetable_days_height) + 0.5f);
		dayAbbrevs = getActivity().getResources().getStringArray(R.array.DayAbbreviations);
		column_min_width = (int) (getActivity().getResources().getDimension(R.dimen.timetable_column_min_width) + 0.5f);
		lesson_min_height = (int) (getActivity().getResources().getDimension(R.dimen.timetable_lesson_min_height) + 0.5f);

		showAllDaysPref = prefs.getString("timetable_show_all_days", "false");
		number_of_periods = 12;

		text_size_lesson_abbrev = getActivity().getResources().getDimension(R.dimen.timetable_lesson_title_textsize);
		text_size_lesson_place = getActivity().getResources().getDimension(R.dimen.timetable_lesson_place_textsize);
		text_size_lesson_time = getActivity().getResources().getDimension(R.dimen.timetable_lesson_time_textsize);

		abbrev_place_padding = (int) (getActivity().getResources().getDimension(R.dimen.timetable_abbrev_place_padding) + 0.5f);
		timetable_period_time_height = (int) (getActivity().getResources().getDimension(R.dimen.timetable_period_time_height) + 0.5f);

		color_time_lines = getActivity().getResources().getColor(R.color.color_time_lines);
		timetable_period_time_lines_height = (int) (getActivity().getResources().getDimension(R.dimen.timetable_period_time_lines_height) + 0.5f);
		timetable_period_time_lines_left_part = (int) (getActivity().getResources().getDimension(R.dimen.timetable_period_time_lines_left_part) + 0.5f);
		timetable_period_time_lines_margin_right = (int) (getActivity().getResources().getDimension(R.dimen.timetable_period_time_lines_left_part_margin_right) + 0.5f);

		color_divider_periods = getActivity().getResources().getColor(R.color.color_divider_periods);
		timetable_period_divider_width = (int) (getActivity().getResources().getDimension(R.dimen.timetable_period_divider_width) + 0.5f);
		showPeriodDivider = prefs.getBoolean("timetable_show_period_divider", false);

		timetable_periods_padding_right = (int) (getActivity().getResources().getDimension(R.dimen.timetable_periods_padding_right) + 0.5f);

		showHoursInsteadOfPeriods = prefs.getBoolean("timetable_show_hours_instead_of_periods", false);
	}

	private void getAllABs(){
		Calendar c = Calendar.getInstance();
		firstDayOfWeek = c.getFirstDayOfWeek();

		allABs = DataStorageHandler.AllABs(getActivity(), ttFolder);
	}

	private void getTtFolderName(){
		if (getArguments() != null && getArguments().containsKey("tt_folder")){
			ttFolder = getArguments().getString("tt_folder");
		} else {
			ttFolder = prefs.getString(getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
		}
	}

	private void setUpWeekIndicator(final View v, final Calendar cal, final int currentAB){
		active_week_day = (Calendar) cal.clone();

		TextView tvWeekDates = (TextView) v.findViewById(R.id.tvWeekDates);
		TextView tvWeekAB = (TextView) v.findViewById(R.id.tvWeekAB);
		LinearLayout llWeekIndicator = (LinearLayout) v.findViewById(R.id.llWeekIndicator);

		if (prefs.getBoolean("pref_key_show_week_selector", true)) {

			llWeekIndicator.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent i = new Intent(getActivity(), ABWeekSettingsActivity.class);
					i.putExtra("caller", "timetable");
					startActivityForResult(i, 0);
				}
			});

			tvWeekDates.setTypeface(Typeface.createFromAsset(getActivity().getResources().getAssets(), "Roboto-Bold.ttf"));
			tvWeekDates.setSingleLine(true);
			tvWeekDates.setEllipsize(TextUtils.TruncateAt.MARQUEE);
			tvWeekDates.setSelected(true);
			tvWeekAB.setSingleLine(true);
			tvWeekAB.setEllipsize(TextUtils.TruncateAt.MARQUEE);
			tvWeekAB.setSelected(true);
			tvWeekAB.setText(getActivity().getResources().getString(R.string.timetable_week_indicator_week) + " " + allABs[currentAB]);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				final ColorStateList cslIcon = getActivity().getResources().getColorStateList(R.color.color_state_list_icon);
				ibGoToToday.setImageTintList(cslIcon);
				ibLeft.setImageTintList(cslIcon);
				ibRight.setImageTintList(cslIcon);
			}

			final Calendar[] cals = firstAndLastDayOfWeek(cal);
			final Calendar calFirst = cals[0];
			final Calendar calLast = cals[1];

			String weekDates = DataStorageHandler.formatDateLocalFormat(getActivity(), calFirst) + " - "
					+ DataStorageHandler.formatDateLocalFormat(getActivity(), calLast);

			if (isCurrentWeek(calFirst, calLast)) {
				weekDates = weekDates + " (" + getActivity().getResources().getString(R.string.timetable_week_indicator_current_week) + ")";
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					ibGoToToday.setImageTintList(getActivity().getResources().getColorStateList(R.color.color_state_list_disabled));
				}
			}

			tvWeekDates.setText(weekDates);

			final int allABsLength = allABs.length;

			ibLeft.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					vaDays.setInAnimation(slide_in_left);
					vaDays.setOutAnimation(slide_out_right);
					vaTimetable.setInAnimation(slide_in_left);
					vaTimetable.setOutAnimation(slide_out_right);

					vaTimetable.showPrevious();
					vaDays.showPrevious();
					Calendar newC = cal;
					newC.add(Calendar.WEEK_OF_YEAR, -1);
					int nextAB = currentAB - 1;
					if (nextAB < 0) nextAB = allABsLength + nextAB;
					setUpWeekIndicator(v, newC, nextAB);
				}
			});

			ibRight.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					vaDays.setInAnimation(slide_in_right);
					vaDays.setOutAnimation(slide_out_left);
					vaTimetable.setInAnimation(slide_in_right);
					vaTimetable.setOutAnimation(slide_out_left);

					vaTimetable.showNext();
					vaDays.showNext();
					Calendar newC = cal;
					newC.add(Calendar.WEEK_OF_YEAR, 1);
					int nextAB = currentAB + 1;
					if (nextAB >= allABsLength) nextAB = 0 + (nextAB - allABsLength);
					setUpWeekIndicator(v, newC, nextAB);
				}
			});

			ibGoToToday.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					vaDays.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
					vaDays.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
					vaTimetable.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
					vaTimetable.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));

					vaTimetable.setDisplayedChild(0);
					vaDays.setDisplayedChild(0);
					Calendar newC = Calendar.getInstance();
					int nextAB = currentABPosition;
					setUpWeekIndicator(v, newC, nextAB);
				}
			});
		} else {
			llWeekIndicator.setVisibility(View.GONE);
		}
	}

	private Calendar[] firstAndLastDayOfWeek(Calendar calDay){
		Calendar calFirst = calDay;
		calFirst.set(Calendar.DAY_OF_WEEK, calDay.getFirstDayOfWeek());
		calFirst.set(Calendar.HOUR_OF_DAY, 0);
		calFirst.set(Calendar.MINUTE, 0);
		calFirst.set(Calendar.SECOND, 0);
		calFirst.set(Calendar.MILLISECOND, 0);

		Calendar calLast = (Calendar) calFirst.clone();
		calLast.add(Calendar.WEEK_OF_YEAR, 1);
		calLast.add(Calendar.MILLISECOND, -1);

		return new Calendar[] {calFirst, calLast};
	}

	boolean isCurrentWeek(Calendar calFirst, Calendar calLast){
		boolean isCW = false;

		Date now = Calendar.getInstance().getTime();
		Date first = calFirst.getTime();
		Date last = calLast.getTime();
		if (first.before(now) && now.before(last)) isCW = true;

		return isCW;
	}

	private void setUpAnimators(View v){
		vaDays = (ViewAnimator) v.findViewById(R.id.vaDays);
		vaTimetable = (ViewAnimator) v.findViewById(R.id.vaTimetable);

		// setMeasureAllChildren -> false so that va height will be adjusted for each child separately
		vaDays.setMeasureAllChildren(false);
		vaTimetable.setMeasureAllChildren(false);

		slide_in_left = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
		slide_out_right = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right);

		slide_in_right = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
		slide_out_left = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left);

		vaDays.setInAnimation(slide_in_left);
		vaDays.setOutAnimation(slide_out_right);

		vaTimetable.setInAnimation(slide_in_left);
		vaTimetable.setOutAnimation(slide_out_right);

		ibGoToToday = (ImageButton) v.findViewById(R.id.ib_go_to_today);
		ibLeft = (ImageButton) v.findViewById(R.id.ib_chevron_left);
		ibRight = (ImageButton) v.findViewById(R.id.ib_chevron_right);
	}

	private void addViewsToAnimators(final int currentAB){
		final float[] constantsNow = timetable_constants(currentAB);
		final int[] dayRange = getDayRange(constantsNow);

		vaDays.addView(rl_days(constantsNow, dayRange));
		vaTimetable.addView(rl_timetable(currentAB, constantsNow, dayRange));

		ibGoToToday.setEnabled(false);
		ibLeft.setEnabled(false);
		ibRight.setEnabled(false);

		Thread loadTimetables = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i1 = currentAB + 1; i1 < allABs.length; i1++){
					final float[] constantsThen = timetable_constants(i1);
					final int[] dayRangeThen = getDayRange(constantsThen);
					final RelativeLayout days = rl_days(constantsThen, dayRangeThen);
					final RelativeLayout tt = rl_timetable(i1, constantsThen, dayRangeThen);

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							vaDays.addView(days);
							vaTimetable.addView(tt);
						}
					});
				}

				for (int i2 = 0; i2 < currentAB; i2++){
					final float[] constantsThen = timetable_constants(i2);
					final int[] dayRangeThen = getDayRange(constantsThen);
					final RelativeLayout days = rl_days(constantsThen,dayRangeThen);
					final RelativeLayout tt = rl_timetable(i2, constantsThen, dayRangeThen);

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							vaDays.addView(days);
							vaTimetable.addView(tt);
						}
					});
				}

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ibGoToToday.setEnabled(true);
						ibLeft.setEnabled(true);
						ibRight.setEnabled(true);
					}
				});
			}
		});
		loadTimetables.start();
	}

	private float[] timetable_constants(int ABint){
		final String thisAB = allABs[ABint];
		final String[] tt_attributes = DataStorageHandler.TimetableAttributes(getActivity(), ttFolder, thisAB);

		// Get earliest day
		final String sMin_day = tt_attributes[5];
		int min_day = 0;
		if (showAllDaysPref.equals("show_all") || showAllDaysPref.equals("show_weekdays")){
			min_day = 0;
		} else if (DataStorageHandler.isStringNumeric(sMin_day)){
			min_day = Integer.parseInt(sMin_day);
		}

		// Get latest day
		final String sMax_day = tt_attributes[7];
		int max_day = 0;
		if (DataStorageHandler.isStringNumeric(sMax_day)) max_day = Integer.parseInt(sMax_day);
		if (showAllDaysPref.equals("show_all") && max_day < 6){
			max_day = 6;
		} else if (showAllDaysPref.equals("show_weekdays") && max_day < 4){
			max_day = 4;
		}

		// Get earliest time
		final String sMin_time = tt_attributes[1];
		int min_time = 0;
		if (DataStorageHandler.isStringNumeric(sMin_time)) min_time = Integer.parseInt(sMin_time);
		// Get latest time
		final String sMax_time = tt_attributes[3];
		int max_time = 0;
		if (DataStorageHandler.isStringNumeric(sMax_time)) max_time = Integer.parseInt(sMax_time);

		// Get longest lesson
		final String sMin_time_length = tt_attributes[9];
		int min_time_length = 0;
		if (DataStorageHandler.isStringNumeric(sMin_time_length)) min_time_length = Integer.parseInt(sMin_time_length);
		// Get shortest lesson
		final String sMax_time_length = tt_attributes[11];
		int max_time_length = 0;
		if (DataStorageHandler.isStringNumeric(sMax_time_length)) max_time_length = Integer.parseInt(sMax_time_length);

		// Get column_size
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int screen_width = size.x;
		int screen_height = size.y;
		int column_size = screen_width /(max_day - min_day + 2);
		if (column_size < column_min_width) column_size = column_min_width;
		int size_elements_except_timetable = (int) (getActivity().getResources().getDimension(R.dimen.timetable_height_other_elements)+0.5f);

		// Get height_time_ratio
		float height_time_ratio = lesson_min_height / 30;
		if (min_time_length != 0) height_time_ratio = lesson_min_height / min_time_length;
		// Set timetable height to minimum of 3/4 times the screen height
		if ((max_time - min_time) > 0) System.out.println("(max_time - min_time) > 0");
		if ((height_time_ratio * (max_time - min_time)) < (screen_height - size_elements_except_timetable))
			System.out.println("(height_time_ratio * (max_time - min_time)) < (screen_height - size_elements_except_timetable)");
		System.out.println("(height_time_ratio * (max_time - min_time): "+(height_time_ratio * (max_time - min_time)));
		System.out.println("screen_height: "+screen_height);
		System.out.println("size_elements_except_timetable: "+size_elements_except_timetable);

		if ((max_time - min_time) > 0 && (height_time_ratio * (max_time - min_time)) < (screen_height - size_elements_except_timetable)) {
			int elements = size_elements_except_timetable;
			if (!prefs.getBoolean("pref_key_show_week_selector", true)) elements = elements - (int) (getActivity().getResources().getDimension(R.dimen.timetable_week_indicator_height)+0.5f);
			height_time_ratio = (float) (screen_height - elements) / (float) (max_time - min_time);
		}

		System.out.println("height_time_ratio: "+height_time_ratio);


		// Get lesson_element_width
		int lesson_element_width = column_size - column_division;

		return new float[] {min_day, max_day, min_time, max_time, min_time_length, max_time_length, column_size, height_time_ratio, lesson_element_width};
	}

	private int[] getDayRange(float[] constants){
		final int min_day = (int) constants[0];
		final int max_day = (int) constants[1];

		int f = 0;
		int[] origRange = new int[] {2,3,4,5,6,7,1};
		for (int i = 0; i < origRange.length; i++){
			if (origRange[i] == firstDayOfWeek) f = i;
		}

		ArrayList<Integer> newRange = new ArrayList<>();
		for (int n = f; n < origRange.length; n++){
			newRange.add(n);
		}

		for (int n = 0; n < f; n++){
			newRange.add(n);
		}

		for (int r = 0; r < newRange.size(); r++){
			if (newRange.get(r) < min_day || newRange.get(r) > max_day){
				newRange.remove(r);
			}
		}

		int[] range = new int[newRange.size()];
		for (int a = 0; a < newRange.size(); a++) range[a] = newRange.get(a);

		return range;
	}

	private RelativeLayout rl_days(float[] constants, int[] dayRange){
		final float column_size = constants[6];

		RelativeLayout rl = new RelativeLayout(getActivity());
		rl.setId(1000 + 0);
		rl.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height_days));
		rl.setGravity(Gravity.BOTTOM | Gravity.LEFT);


		for (int d = 0; d < dayRange.length; d++){
			rl.addView(tv_day(dayRange[d], dayRange, column_size));
		}

		return rl;
	}

	private TextView tv_day(int day, int[] dayRange, float column_size){
		TextView tv = new TextView(getActivity());
		tv.setId(1001 + day);
		tv.setTextColor(color_days_text);
		// TODO
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_size_days);
		tv.setText(dayAbbrevs[day]);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) column_size, ViewGroup.LayoutParams.WRAP_CONTENT);
		int days_before = day;
		for (int d = 0; d < dayRange.length; d++){
			if (dayRange[d] == day) days_before = d;
		}
		lp.setMargins((int) ((days_before + 1) * column_size), 0, 0, 0);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		tv.setLayoutParams(lp);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		//tv.setTypeface(Typeface.createFromAsset(getActivity().getResources().getAssets(), "Roboto-Medium.ttf"));

		return tv;
	}

	private RelativeLayout rl_timetable(int ABint, float[] constants, int[] dayRange){
		final int min_time = (int) constants[2];
		final int max_time = (int) constants[3];
		final float height_time_ratio = constants[7];

		RelativeLayout rl = new RelativeLayout(getActivity());
		rl.setId(500 + ABint);
		final int timetable_height = (int) (height_time_ratio * (max_time-min_time));
		System.out.println("timetable_height: "+timetable_height);
		rl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, timetable_height));
		rl.setBackgroundColor(getActivity().getResources().getColor(R.color.color_timetable_area_background));


		if (!showHoursInsteadOfPeriods) {
			ArrayList<Integer> activeTimesS = new ArrayList<>();
			ArrayList<Integer> activeTimesE = new ArrayList<>();

			for (int p = 1; p <= number_of_periods; p++){
				String from = prefs.getString("pref_key_period"+p+"_start", "[none]");
				String to = prefs.getString("pref_key_period"+p+"_end", "[none]");

				String[] fsplit = from.split(":");
				String[] tsplit = to.split(":");
				if (fsplit.length >= 2 && tsplit.length >= 2){
					String fh = fsplit[0];
					String fm = fsplit[1];
					String th = tsplit[0];
					String tm = tsplit[1];

					if (DataStorageHandler.isStringNumeric(fh) && DataStorageHandler.isStringNumeric(fm)
							&& DataStorageHandler.isStringNumeric(th) && DataStorageHandler.isStringNumeric(tm)){{
						int fromH = Integer.parseInt(fh);
						int fromM = Integer.parseInt(fm);
						int toH = Integer.parseInt(th);
						int toM = Integer.parseInt(tm);

						int f = fromH * 60 + fromM;
						int t = toH * 60 + toM;
						if (t < f){
							int nT = f;
							f = t;
							t = nT;
						}

						if (min_time <= f && t <= max_time){
							rl.addView(tv_period(f, t, p, constants));
							activeTimesS.add(f);
							activeTimesE.add(t);
						}
					}}
				}
			}

			for (int times = 0; times < activeTimesS.size() && times < activeTimesE.size(); times++) {
				final int sThis = activeTimesS.get(times);
				final int eThis = activeTimesE.get(times);

				if (times > 0 && sThis <= activeTimesE.get(times - 1)) {
					// Do nothing! Do not add as it's the same time as the previous's end time

				} else {
					if (sThis > min_time) rl.addView(v_time_line(sThis, false, constants));
					rl.addView(tv_period_time(sThis, false, true, constants));
				}

				if (times < activeTimesE.size() - 1 && eThis >= activeTimesS.get(times + 1)) {
					rl.addView(v_time_line(eThis, true, constants));
					rl.addView(v_time_line_left_part(eThis, constants));
					rl.addView(tv_period_time(eThis, true, false, constants));

				} else {
					rl.addView(v_time_line(eThis, false, constants));
					rl.addView(tv_period_time(eThis, false, false, constants));
				}
			}
		} else {
			final int min_dist = 10;

			for (int hour = 0; hour < 24; hour++){
				final int minute = hour * 60;
				if (min_time <= hour && hour <= max_time){

					rl.addView(v_time_line(minute, true, constants));

					if (minute <= min_time + min_dist || minute >= max_time - min_dist) {
						rl.addView(tv_period_time(minute, false, true, constants));
					} else {
						rl.addView(tv_period_time(minute, true, true, constants));
					}
				}
			}
		}

		rl.addView(v_periods_divider(constants));

		String AB = allABs[ABint];
		final String[][] timetable_lessons = DataStorageHandler.TimetableLessons(getActivity(), ttFolder, AB);

		if (timetable_lessons != null) {
			for (int tl = 0; tl < timetable_lessons.length; tl++) {
				if (timetable_lessons[tl].length >= 9){
					boolean valid = true;
					for (int c = 0; c < 7; c++){
						if (timetable_lessons[tl][c] == null ||
								timetable_lessons[tl][c].replace("[none]", "").replace("[null", "").replace(" ", "").equals("")) valid = false;
					}
					if (!DataStorageHandler.isStringNumeric(timetable_lessons[tl][1]) ||
							!DataStorageHandler.isStringNumeric(timetable_lessons[tl][2]) ||
							!DataStorageHandler.isStringNumeric(timetable_lessons[tl][3])) valid = false;

					if (valid){
						rl.addView(lesson_element(timetable_lessons[tl], constants, dayRange, tl));
					}
				}
			}
		}

		return rl;
	}

	private TextView tv_period(int from, int to, int pNumber, float[] constants){
		final int min_time = (int) constants[2];
		final float height_time_ratio = constants[7];
		final int element_width = (int) constants[8];

		final int period_length = to - from;
		final int element_height = (int) (period_length * height_time_ratio);
		final int margin_top = (int) ((from - min_time) * height_time_ratio);
		final int width = element_width - timetable_periods_padding_right;

		TextView tv = new TextView(getActivity());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, element_height);
		lp.setMargins(0, margin_top, 0, 0);
		tv.setLayoutParams(lp);
		tv.setGravity(Gravity.CENTER);
		tv.setText(String.valueOf(pNumber));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, timetable_periods_textsize);
		tv.setTextColor(color_timetable_periods_text);

		return tv;
	}

    private TextView tv_period_time(int time, boolean shared, boolean startOfP, float[] constants){
        final int min_time = (int) constants[2];
        final float height_time_ratio = constants[7];
        final int element_width = (int) constants[8];

        int margin_top = (int) ((time - min_time) * height_time_ratio);

		TextView tv = new TextView(getActivity());

		if (shared) {
			margin_top = margin_top - (timetable_period_time_height /2);
			tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		} else if (!startOfP) {
			margin_top = margin_top - timetable_period_time_height;
			tv.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
		} else {
			tv.setGravity(Gravity.TOP | Gravity.RIGHT);
		}

		final int width = element_width - timetable_periods_padding_right;

		final int h = (int) (time / 60);
		final int m = time % 60;
		String hour = String.valueOf(h);
		//if (hour.length() < 2) hour = "0" + hour;
		String minute = String.valueOf(m);
		if (minute.length() < 2) minute = "0" + minute;
		final String sTime = hour + ":" + minute;

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, timetable_period_time_height);
		lp.setMargins(0, margin_top, 0, 0);
		tv.setLayoutParams(lp);
        tv.setText(String.valueOf(sTime));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, timetable_period_times_textsize);
        tv.setTextColor(color_timetable_period_times_text);
		tv.setAlpha(0.54f);

        return tv;
    }

	private View v_time_line(int time, boolean shared, float[] constants){
		final int min_time = (int) constants[2];
		final int column_size = (int) constants[6];
		final float height_time_ratio = constants[7];

		final int margin_top = (int) (height_time_ratio * (time - min_time));

		View v = new View(getActivity());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, timetable_period_time_lines_height);
		if (shared){
			final int margin_left = column_size - timetable_periods_padding_right;
			lp.setMargins(margin_left, margin_top, 0, 0);
		} else {
			lp.setMargins(0, margin_top, 0, 0);
		}
		v.setLayoutParams(lp);
		v.setBackgroundColor(color_time_lines);

		return v;
	}

	private View v_time_line_left_part(int time, float[] constants){
		final int min_time = (int) constants[2];
		final float height_time_ratio = constants[7];
		final int element_width = (int) constants[8];

		final int margin_top = (int) (height_time_ratio * (time - min_time));
		final int width = element_width - timetable_period_time_lines_margin_right;

		View v = new View(getActivity());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, timetable_period_time_lines_height);
		lp.setMargins(0, margin_top, 0, 0);
		v.setLayoutParams(lp);
		v.setBackgroundColor(color_time_lines);

		return v;
	}

	private View v_periods_divider(float[] constants){
		final float column_size = constants[6];
		final int margin_left = (int) (column_size - (timetable_period_divider_width/2) - (timetable_periods_padding_right/2));

		View v = new View(getActivity());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(timetable_period_divider_width, ViewGroup.LayoutParams.MATCH_PARENT);
		lp.setMargins(margin_left, 0, 0, 0);
		v.setLayoutParams(lp);
		v.setBackgroundColor(color_divider_periods);

		return v;
	}

	private RelativeLayout lesson_element(String[] lesson, float[] constants, int[] dayRange, int row){
		final int min_time = (int) constants[2];
		final float column_size = constants[6];
		final float height_time_ratio = constants[7];
		final int lesson_element_width = (int) constants[8];

		final String ID = lesson[0];
		final int day = Integer.parseInt(lesson[1]);
		final int timeStart = Integer.parseInt(lesson[2]);
		final int timeEnd = Integer.parseInt(lesson[3]);
		final String abbrev = lesson[4];
		final String color1 = lesson[5];
		final String color2 = lesson[6];
		final String place = lesson[7];
		final String custom = lesson[8];

		final int lesson_length = timeEnd - timeStart;
		final float element_height = (float) lesson_length * height_time_ratio;

		int days_before = 0;
		for (int d = 0; d < dayRange.length; d++){
			if (dayRange[d] == day) days_before = d;
		}
		final int margin_left = (int) ((days_before + 1) * (column_size + (column_division / 2)));

		final int margin_top = (int) ((timeStart - min_time) * height_time_ratio);

		RelativeLayout rl = new RelativeLayout(getActivity());
		rl.setId(500 + row);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(lesson_element_width, (int) element_height);
		lp.setMargins(margin_left, margin_top, 0, 0);
		rl.setLayoutParams(lp);
		rl.setClickable(true);
		rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final String date = DataStorageHandler.formatDateGeneralFormat(getActivity(), active_week_day);
				((TimetableActivity) getActivity()).LessonFragment(ID, date);
			}
		});

		int colorInt = 0xdd000000;
		int color2Int = 0xffFFFFFF;
		for (int c = 0; c < colorNames.length; c++){
			if (color1.equals(colorNames[c])) colorInt = colorInts[c];
			if (color2.equals(colorNames[c])) color2Int = colorInts[c];
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			/*Drawable bg = getActivity().getResources().getDrawable(R.drawable.custom_bg);
			bg.setColorFilter(colorInt, null);
			rl.setBackground(bg);
			*/
			StateListDrawable states = new StateListDrawable();
			states.addState(new int[] {android.R.attr.state_pressed}, new ColorDrawable(getResources().getColor(R.color.colorHightlight)));
			states.addState(new int[] {android.R.attr.state_selected}, new ColorDrawable(getResources().getColor(R.color.colorHightlight)));
			states.addState(new int[] { }, new ColorDrawable(colorInt));
			rl.setBackground(states);
		} else {
			rl.setBackgroundColor(colorInt);
		}

		if (custom.equals("true")){
			rl.addView(tv_time_start(timeStart, color2Int));
			rl.addView(tv_time_end(timeEnd, color2Int));
		}
		rl.addView(ll_lesson_abbrev_place(abbrev, place, color2Int));

		return rl;
	}

	private LinearLayout ll_lesson_abbrev_place(String abbrev, String place, int color2){
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		ll.setLayoutParams(lp);
		ll.setGravity(Gravity.CENTER_HORIZONTAL);

		ll.addView(tv_abbrev(abbrev, color2));
		ll.addView(tv_place(place, color2));

		return ll;
	}

	private TextView tv_abbrev(String abbrev, int color2){
		TextView tv = new TextView(getActivity());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_size_lesson_abbrev);
		tv.setTextColor(color2);
		tv.setText(abbrev.replace(" ", "").replace("[none]", "").replace("[comma]", ","));
		tv.setSingleLine(true);
		tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		tv.setSelected(true);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setTypeface(Typeface.createFromAsset(getActivity().getResources().getAssets(), "Roboto-Medium.ttf"));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(lp);

		return tv;
	}

	private TextView tv_place(String place, int color2){
		TextView tv = new TextView(getActivity());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_size_lesson_place);
		tv.setTextColor(color2);
		tv.setText(place.replace("[none]", "").replace("[null]", "").replace("[comma]", ","));
		tv.setSingleLine(true);
		tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		tv.setSelected(true);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, -1 * abbrev_place_padding, 0, 0);
		tv.setLayoutParams(lp);

		return tv;
	}

	private TextView tv_time_start(int timeStart, int color2){
		TextView tv = new TextView(getActivity());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		tv.setLayoutParams(lp);

		String h = String.valueOf((int) (timeStart / 60));
		String m = String.valueOf((int) (timeStart % 60));
		if (m.length() < 2) m = "0" + m;
		String time = h+":"+m;

		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_size_lesson_time);
		tv.setTextColor(color2);
		tv.setText(time);
		tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);

		return tv;
	}

	private TextView tv_time_end(int timeEnd, int color2){
		TextView tv = new TextView(getActivity());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		tv.setLayoutParams(lp);

		String h = String.valueOf((int) (timeEnd / 60));
		String m = String.valueOf((int) (timeEnd % 60));
		if (m.length() < 2) m = "0" + m;
		String time = h+":"+m;

		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_size_lesson_time);
		tv.setTextColor(color2);
		tv.setText(time);
		tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);

		return tv;
	}

    @Override
    public void onResume(){
        super.onResume();
        ((TimetableActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_fragment_timetable));

		Log.i("Analytics", "Setting screen name: " + fragmentName);
		mTracker.setScreenName("Image~" + fragmentName);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

    /*private void findViews(View v){
    	bM1 = (Button) v.findViewById(R.id.Button01);
    	bM2 = (Button) v.findViewById(R.id.Button06);
    	bM3 = (Button) v.findViewById(R.id.Button11);
    	bM4 = (Button) v.findViewById(R.id.Button16);
    	bM5 = (Button) v.findViewById(R.id.button21);
    	bM6 = (Button) v.findViewById(R.id.button26);
    	bM7 = (Button) v.findViewById(R.id.button31);
    	bM8 = (Button) v.findViewById(R.id.button36);
    	bM9 = (Button) v.findViewById(R.id.button41);
    	bM10 = (Button) v.findViewById(R.id.button46);

    	bTu1 = (Button) v.findViewById(R.id.Button02);
    	bTu2 = (Button) v.findViewById(R.id.Button07);
    	bTu3 = (Button) v.findViewById(R.id.Button12);
    	bTu4 = (Button) v.findViewById(R.id.Button17);
    	bTu5 = (Button) v.findViewById(R.id.button22);
    	bTu6 = (Button) v.findViewById(R.id.button27);
    	bTu7 = (Button) v.findViewById(R.id.button32);
    	bTu8 = (Button) v.findViewById(R.id.button37);
    	bTu9 = (Button) v.findViewById(R.id.button42);
    	bTu10 = (Button) v.findViewById(R.id.button47);

    	bW1 = (Button) v.findViewById(R.id.Button03);
    	bW2 = (Button) v.findViewById(R.id.Button08);
    	bW3 = (Button) v.findViewById(R.id.Button13);
    	bW4 = (Button) v.findViewById(R.id.Button18);
    	bW5 = (Button) v.findViewById(R.id.button23);
    	bW6 = (Button) v.findViewById(R.id.button28);
    	bW7 = (Button) v.findViewById(R.id.button33);
    	bW8 = (Button) v.findViewById(R.id.button38);
    	bW9 = (Button) v.findViewById(R.id.button43);
    	bW10 = (Button) v.findViewById(R.id.button48);

    	bTh1 = (Button) v.findViewById(R.id.Button04);
    	bTh2 = (Button) v.findViewById(R.id.Button09);
    	bTh3 = (Button) v.findViewById(R.id.Button14);
    	bTh4 = (Button) v.findViewById(R.id.Button19);
    	bTh5 = (Button) v.findViewById(R.id.button24);
    	bTh6 = (Button) v.findViewById(R.id.button29);
    	bTh7 = (Button) v.findViewById(R.id.button34);
    	bTh8 = (Button) v.findViewById(R.id.button39);
    	bTh9 = (Button) v.findViewById(R.id.button44);
    	bTh10 = (Button) v.findViewById(R.id.button49);

    	bF1 = (Button) v.findViewById(R.id.Button05_1);
    	bF2 = (Button) v.findViewById(R.id.Button10);
    	bF3 = (Button) v.findViewById(R.id.Button15);
    	bF4 = (Button) v.findViewById(R.id.Button20);
    	bF5 = (Button) v.findViewById(R.id.button25);
    	bF6 = (Button) v.findViewById(R.id.button30);
    	bF7 = (Button) v.findViewById(R.id.button35);
    	bF8 = (Button) v.findViewById(R.id.button40);
    	bF9 = (Button) v.findViewById(R.id.button45);
    	bF10 = (Button) v.findViewById(R.id.button50);
    	
    	
    	tvD1 = (TextView) v.findViewById(R.id.tvD1);
    	tvD2 = (TextView) v.findViewById(R.id.tvD2);
    	tvD3 = (TextView) v.findViewById(R.id.tvD3);
    	tvD4 = (TextView) v.findViewById(R.id.tvD4);
    	tvD5 = (TextView) v.findViewById(R.id.tvD5);

    	tvN1 = (TextView) v.findViewById(R.id.tvN1);
    	tvN2 = (TextView) v.findViewById(R.id.tvN2);
    	tvN3 = (TextView) v.findViewById(R.id.tvN3);
    	tvN4 = (TextView) v.findViewById(R.id.tvN4);
    	tvN5 = (TextView) v.findViewById(R.id.tvN5);
    	tvN6 = (TextView) v.findViewById(R.id.tvN6);
    	tvN7 = (TextView) v.findViewById(R.id.tvN7);
    	tvN8 = (TextView) v.findViewById(R.id.tvN8);
    	tvN9 = (TextView) v.findViewById(R.id.tvN9);
    	tvN10 = (TextView) v.findViewById(R.id.tvN10);
    	

    	tr1 = (TableRow) v.findViewById(R.id.tableRow1);
    	tr2 = (TableRow) v.findViewById(R.id.tableRow2);
    	tr3 = (TableRow) v.findViewById(R.id.tableRow3);
    	tr4 = (TableRow) v.findViewById(R.id.tableRow4);
    	tr5 = (TableRow) v.findViewById(R.id.tableRow5);
    	tr6 = (TableRow) v.findViewById(R.id.tableRow6);
    	tr7 = (TableRow) v.findViewById(R.id.tableRow7);
    	tr8 = (TableRow) v.findViewById(R.id.tableRow8);
    	tr9 = (TableRow) v.findViewById(R.id.tableRow9);
    	tr10 = (TableRow) v.findViewById(R.id.tableRow10);
    }
    
    private void setUpButtons(View v){
    	String[] dayNames = getResources().getStringArray(R.array.DayNames);
        String[] periodNumbers = getResources().getStringArray(R.array.PeriodNumbers);
    	final Button[] buttons = {bM1, bM2, bM3, bM4, bM5, bM6, bM7, bM8, bM9, bM10, 
				bTu1, bTu2, bTu3, bTu4, bTu5, bTu6, bTu7, bTu8, bTu9, bTu10,
				bW1, bW2, bW3, bW4, bW5, bW6, bW7, bW8, bW9, bW10,
				bTh1, bTh2, bTh3, bTh4, bTh5, bTh6, bTh7, bTh8, bTh9, bTh10,
				bF1, bF2, bF3, bF4, bF5, bF6, bF7, bF8, bF9, bF10};
    	TableRow[] tableRows = {tr1, tr2, tr3, tr4, tr5, tr6, tr7, tr8, tr9, tr10};
        String[] fields = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22",
                "25", "26", "27", "28", "29", "30", "31", "32", "33", "34",
                "37", "38", "39", "40", "41", "42", "43", "44", "45", "46",
                "49", "50", "51", "52", "53", "54", "55", "56", "57", "58"};
        int[] fieldInts = {1,2,3,4,5,6,7,8,9,10,13,14,15,16,17,18,19,20,21,22,25,26,27,28,29,30,31,32,33,34,
                37,38,39,40,41,42,43,44,45,46,49,50,51,52,53,54,55,56,57,58};
		boolean oneused = false;
        
        for (int i = nmbrPeriods - 1; i >= 0; i--){
    		boolean used = false;
    		
    		for (int i2 = 0; i2 < periodInts.length; i2++){
    			if (periodInts[i2] == i){
    				Button button = buttons[i2];
    				int dayInt = dayInts[i2];
    		        int periodInt = periodInts[i2];
    				//String sName = ((TimetableActivity)getActivity()).getPeriodName(dayNames[dayInt], periodNumbers[periodInt]);
    				//String sAbbrev = ((TimetableActivity) getActivity()).getPeriodAbbrev(sName);
                    String[] fieldInfo = ((TimetableActivity) getActivity()).getFieldInfo(fields[i2]);
                    String sName = fieldInfo[0];
                    String sAbbrev = fieldInfo[1];

    		        // Check if button is used
    				if (sName != null && !sName.equals("-") && !sName.equals(" ") && !sName.equals("") & sAbbrev != null && !sAbbrev.equals("-") && !sAbbrev.equals(" ") && !sAbbrev.equals("")){
    					used = true;
    					oneused = true;
						//Toast.makeText(getActivity(), String.valueOf(dayInt)+", "+String.valueOf(periodInt)+", "+sName, Toast.LENGTH_SHORT).show();

                        final float scale = getActivity().getResources().getDisplayMetrics().density;
    					int bounds = (int) (56 * scale + 0.5f);
    					
    					//String colorName = ((TimetableActivity) getActivity()).getPeriodColors(sName)[0];
    					//String textColorName = ((TimetableActivity) getActivity()).getPeriodColors(sName)[1];
    					String colorName = fieldInfo[2];
                        String textColorName = fieldInfo[3];

                        int colorInt = 0xffFFFFFF;
    					int textColorInt = 0xff000000;
    					
    					if (!colorName.equals("-") & !colorName.equals(null)){
    						for (int i1 = 0; i1 < colorNames.length; i1++){
    							if (colorNames[i1].equals(colorName)){
    								colorInt = colorInts[i1];
    							}
    						}
    					}
    					
    					if (!textColorName.equals("-") & !textColorName.equals(null)){
    						for (int i1 = 0; i1 < colorNames.length; i1++){
    							if (colorNames[i1].equals(textColorName)){
    								textColorInt = colorInts[i1];
    							}
    						}
    					}
    					
    					final GradientDrawable gd = new GradientDrawable();
    					gd.setShape(0);
    					gd.setColor(colorInt);
    					gd.setBounds(0, 0, bounds, bounds);
    					gd.setSize(bounds, bounds);
    					button.setBackgroundDrawable(gd);
    					
    					button.setOnTouchListener(new View.OnTouchListener() {
    						@Override
    						public boolean onTouch(View v, MotionEvent event) {
    							int iAction=event.getAction();

    					        if(iAction==MotionEvent.ACTION_DOWN){
    					            gd.setAlpha(150);
    					        }
    					        if(iAction==MotionEvent.ACTION_UP){
    					        	gd.setAlpha(255);
    					        }
    							return false;
    						}
    					});
    					
    					button.setText(sAbbrev.replace("[comma]", ","));
    					button.setTextColor(textColorInt);
    					
    					final int dayInt2 = dayInt;
    					final int periodInt2 = periodInt;
    					final String abbrev = sAbbrev;
    					button.setOnClickListener(new OnClickListener() {
    						@Override
    						public void onClick(View v) {
//    							ShowSubject(abbrev);
    							if (!abbrev.equals("-") && !abbrev.equals(null)){
    								((TimetableActivity) getActivity()).LessonFragment(dayInt2, periodInt2);
    							}
    						}
    					});
    				} else {
    					button.setVisibility(View.INVISIBLE);
    				}
    			}
    		}

    		if (used == false && oneused == false){
    			tableRows[i].setVisibility(View.GONE);
    		}
    	}
	}

    private void setUpDaysNumbers(View v){
    	TextView[] tvDs = {tvD1, tvD2, tvD3, tvD4, tvD5};
    	TextView[] tvNs = {tvN1, tvN2, tvN3, tvN4, tvN5, tvN6, tvN7, tvN8, tvN9, tvN10};

    	for(int i = 0; i < tvDs.length; i++){
    		String[] dayAbbrevs = getResources().getStringArray(R.array.DayAbbreviations);
            TextView tvD = tvDs[i];

            tvD.setText(dayAbbrevs[i]);
    	}

    	for(int i2 = 0; i2 < tvNs.length; i2++){
    		String[] periodNumbers = getResources().getStringArray(R.array.PeriodNumbers);
    		TextView tvN = tvNs[i2];

    		tvN.setText(periodNumbers[i2]);
    	}
    }

    private void setUpRowHeights(View v){
    	// set up Break views
    	viewBreak1 = (View) v.findViewById(R.id.viewBreak1);
    	viewBreak2 = (View) v.findViewById(R.id.viewBreak2);
    	viewBreak3 = (View) v.findViewById(R.id.viewBreak3);
    	viewBreak4 = (View) v.findViewById(R.id.viewBreak4);
    	viewBreak5 = (View) v.findViewById(R.id.viewBreak5);
    	viewBreak6 = (View) v.findViewById(R.id.viewBreak6);
    	viewBreak7 = (View) v.findViewById(R.id.viewBreak7);
    	viewBreak8 = (View) v.findViewById(R.id.viewBreak8);
    	viewBreak9 = (View) v.findViewById(R.id.viewBreak9);

    	// get Times from Preferences
    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String syncConnPref = sharedPref.getString("edit_text", "blub");

    	String p1start = (shared.getString("pref_key_period1_start", "07:55"));
    	String p1end = (shared.getString("pref_key_period1_end", "08:40"));
    	String p2start = (shared.getString("pref_key_period2_start", "8:40"));
    	String p2end = (shared.getString("pref_key_period2_end", "09:25"));
    	String p3start = (shared.getString("pref_key_period3_start", "09:45"));
    	String p3end = (shared.getString("pref_key_period3_end", "10:30"));
    	String p4start = (shared.getString("pref_key_period4_start", "10:35"));
    	String p4end = (shared.getString("pref_key_period4_end", "11:20"));
    	String p5start = (shared.getString("pref_key_period5_start", "11:40"));
    	String p5end = (shared.getString("pref_key_period5_end", "12:25"));
    	String p6start = (shared.getString("pref_key_period6_start", "12:25"));
    	String p6end = (shared.getString("pref_key_period6_end", "13:10"));
    	String p7start = (shared.getString("pref_key_period7_start", "13:45"));
    	String p7end = (shared.getString("pref_key_period7_end", "14:30"));
    	String p8start = (shared.getString("pref_key_period8_start", "14:30"));
    	String p8end = (shared.getString("pref_key_period8_end", "15:15"));
    	String p9start = (shared.getString("pref_key_period9_start", "15:15"));
    	String p9end = (shared.getString("pref_key_period9_end", "16:00"));
    	String p10start = (shared.getString("pref_key_period10_start", "16:00"));
    	String p10end = (shared.getString("pref_key_period10_end", "16:45"));
    	String p11start = (shared.getString("pref_key_period11_start", "16:45"));
    	String p11end = (shared.getString("pref_key_period11_end", "17:30"));
    	String p12start = (shared.getString("pref_key_period12_start", "17:30"));
    	String p12end = (shared.getString("pref_key_period12_end", "18:15"));

    	// c http://www.mkyong.com/java/how-to-calculate-date-time-difference-in-java/
		//HH converts hour in 24 hours format (0-23), day calculation
//		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//
//		Date p1s = null;
//		Date p1e = null;
//		Date p2s = null;
//		Date p2e = null;
//		Date p3s = null;
//		Date p3e = null;
//		Date p4s = null;
//		Date p4e = null;
//		Date p5s = null;
//		Date p5e = null;
//		Date p6s = null;
//		Date p6e = null;
//		Date p7s = null;
//		Date p7e = null;
//		Date p8s = null;
//		Date p8e = null;
//		Date p9s = null;
//		Date p9e = null;
//		Date p10s = null;
//		Date p10e = null;
//		Date p11s = null;
//		Date p11e = null;
//		Date p12s = null;
//		Date p12e = null;
//
//		try {
//			p1s = format.parse(p1start);
//			p1e = format.parse(p1end);
//			p2s = format.parse(p2start);
//			p2e = format.parse(p2end);
//			p3s = format.parse(p3start);
//			p3e = format.parse(p3end);
//			p4s = format.parse(p4start);
//			p4e = format.parse(p4end);
//			p5s = format.parse(p5start);
//			p5e = format.parse(p5end);
//			p6s = format.parse(p6start);
//			p6e = format.parse(p6end);
//			p7s = format.parse(p7start);
//			p7e = format.parse(p7end);
//			p8s = format.parse(p8start);
//			p8e = format.parse(p8end);
//			p9s = format.parse(p9start);
//			p9e = format.parse(p9end);
//			p10s = format.parse(p10start);
//			p10e = format.parse(p10end);
//			p11s = format.parse(p11start);
//			p11e = format.parse(p11end);
//			p12s = format.parse(p12start);
//			p12e = format.parse(p12end);
//
//			//in milliseconds
//			//pLength1 = p1e.getTime() - p1s.getTime();
//			pLength2 = p2e.getTime() - p2s.getTime();
//			pLength3 = p3e.getTime() - p3s.getTime();
//			pLength4 = p4e.getTime() - p4s.getTime();
//			pLength5 = p5e.getTime() - p5s.getTime();
//			pLength6 = p6e.getTime() - p6s.getTime();
//			pLength7 = p7e.getTime() - p7s.getTime();
//			pLength8 = p8e.getTime() - p8s.getTime();
//			pLength9 = p9e.getTime() - p9s.getTime();
//			pLength10 = p10e.getTime() - p10s.getTime();
//			pLength11 = p11e.getTime() - p11s.getTime();
//			pLength12 = p12e.getTime() - p12s.getTime();
//
//			PeriodsAndBreaksLengthOverall = p12e.getTime() - p1s.getTime();
//
//			bLength1 = p2e.getTime() - p1e.getTime();
//			bLength2 = p3e.getTime() - p2e.getTime();
//			bLength3 = p4e.getTime() - p3e.getTime();
//			bLength4 = p5e.getTime() - p4e.getTime();
//			bLength5 = p6e.getTime() - p5e.getTime();
//			bLength6 = p7e.getTime() - p6e.getTime();
//			bLength7 = p8e.getTime() - p7e.getTime();
//			bLength8 = p9e.getTime() - p8e.getTime();
//			bLength9 = p10e.getTime() - p9e.getTime();
//			bLength10 = p11e.getTime() - p10e.getTime();
//			bLength11 = p12e.getTime() - p11e.getTime();
//
//			long diffSeconds = diff / 1000 % 60;
//			long diffMinutes = diff / (60 * 1000) % 60;
//			long diffHours = diff / (60 * 60 * 1000) % 24;
//			long diffDays = diff / (24 * 60 * 60 * 1000);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		// c stackoverflow.com/questions/3224193/...
		//for (int i = 0; i < TableRows.length; i++){
			//TableRow tr = tr1;
			LayoutParams trparams1 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p1start, p1end)));
//			loparams.weight = (((int)periodLengths[i])/((int)PeriodsAndBreaksLengthOverall));
			tr1.setLayoutParams(trparams1);
		//}

			LayoutParams trparams2 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p2start, p2end)));
			LayoutParams trparams3 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p3start, p3end)));
			LayoutParams trparams4 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p4start, p4end)));
			LayoutParams trparams5 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p5start, p5end)));
			LayoutParams trparams6 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p6start, p6end)));
			LayoutParams trparams7 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p7start, p7end)));
			LayoutParams trparams8 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p8start, p8end)));
			LayoutParams trparams9 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p9start, p9end)));
			LayoutParams trparams10 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p10start, p10end)));

			tr2.setLayoutParams(trparams2);
			tr3.setLayoutParams(trparams3);
			tr4.setLayoutParams(trparams4);
			tr5.setLayoutParams(trparams5);
			tr6.setLayoutParams(trparams6);
			tr7.setLayoutParams(trparams7);
			tr8.setLayoutParams(trparams8);
			tr9.setLayoutParams(trparams9);
			tr10.setLayoutParams(trparams10);

		//for (int i2 = 0; i2 < viewsBreaks.length; i2++){
			//View viewBreak = viewsBreaks[i2];
			LayoutParams vbparams1 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p1end, p2start)));
			//vbparams1.weight = (breakLengths[i2]/PeriodsAndBreaksLengthOverall);
			viewBreak1.setLayoutParams(vbparams1);
		//}

			LayoutParams vbparams2 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p2end, p3start)));
			LayoutParams vbparams3 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p3end, p4start)));
			LayoutParams vbparams4 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p4end, p5start)));
			LayoutParams vbparams5 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p5end, p6start)));
			LayoutParams vbparams6 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p6end, p7start)));
			LayoutParams vbparams7 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p7end, p8start)));
			LayoutParams vbparams8 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p8end, p9start)));
			LayoutParams vbparams9 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, (minuteDifference(p9end, p10start)));
			
			viewBreak2.setLayoutParams(vbparams2);
			viewBreak3.setLayoutParams(vbparams3);
			viewBreak4.setLayoutParams(vbparams4);
			viewBreak5.setLayoutParams(vbparams5);
			viewBreak6.setLayoutParams(vbparams6);
			viewBreak7.setLayoutParams(vbparams7);
			viewBreak8.setLayoutParams(vbparams8);
			viewBreak9.setLayoutParams(vbparams9);
    }
    
    private int minuteDifference(String tStart, String tEnd){
    	String[] split1 = tStart.split(":");
    	String[] split2 = tEnd.split(":");
    	
    	int h1 = (Integer.valueOf(split1[0])) * 60;
    	int m1 = Integer.valueOf(split1[1]);
    	int g1 = h1 + m1;
    	
    	int h2 = (Integer.valueOf(split2[0])) * 60;
    	int m2 = Integer.valueOf(split2[1]);
    	int g2 = h2 + m2;
    	
    	int dif = g2 - g1;
    	
    	return dif;
    }*/

}