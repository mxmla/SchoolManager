package com.thinc_easy.schoolmanager;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class TimetableFragment extends Fragment {
	
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";
	
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
            "ornge", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
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
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timetable, container, false);

        ((TimetableActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_fragment_timetable));
        ((TimetableActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

		int ttColor = ((TimetableActivity) getActivity()).getResources().getColor(R.color.color_timetable);
		((TimetableActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ttColor));


        nmbrPeriods = 10;
        
        tableLayout = (TableLayout) v.findViewById(R.id.TableLayout);
//        tableLayout.setVisibility(4);
        findViews(v);
        setUpButtons(v);
        setUpDaysNumbers(v);
        setUpRowHeights(v);
        
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ((TimetableActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_fragment_timetable));
	}

    private void findViews(View v){
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
		/*SimpleDateFormat format = new SimpleDateFormat("HH:mm");

		Date p1s = null;
		Date p1e = null;
		Date p2s = null;
		Date p2e = null;
		Date p3s = null;
		Date p3e = null;
		Date p4s = null;
		Date p4e = null;
		Date p5s = null;
		Date p5e = null;
		Date p6s = null;
		Date p6e = null;
		Date p7s = null;
		Date p7e = null;
		Date p8s = null;
		Date p8e = null;
		Date p9s = null;
		Date p9e = null;
		Date p10s = null;
		Date p10e = null;
		Date p11s = null;
		Date p11e = null;
		Date p12s = null;
		Date p12e = null;

		try {
			p1s = format.parse(p1start);
			p1e = format.parse(p1end);
			p2s = format.parse(p2start);
			p2e = format.parse(p2end);
			p3s = format.parse(p3start);
			p3e = format.parse(p3end);
			p4s = format.parse(p4start);
			p4e = format.parse(p4end);
			p5s = format.parse(p5start);
			p5e = format.parse(p5end);
			p6s = format.parse(p6start);
			p6e = format.parse(p6end);
			p7s = format.parse(p7start);
			p7e = format.parse(p7end);
			p8s = format.parse(p8start);
			p8e = format.parse(p8end);
			p9s = format.parse(p9start);
			p9e = format.parse(p9end);
			p10s = format.parse(p10start);
			p10e = format.parse(p10end);
			p11s = format.parse(p11start);
			p11e = format.parse(p11end);
			p12s = format.parse(p12start);
			p12e = format.parse(p12end);*/

			//in milliseconds
			/*pLength1 = p1e.getTime() - p1s.getTime();
			pLength2 = p2e.getTime() - p2s.getTime();
			pLength3 = p3e.getTime() - p3s.getTime();
			pLength4 = p4e.getTime() - p4s.getTime();
			pLength5 = p5e.getTime() - p5s.getTime();
			pLength6 = p6e.getTime() - p6s.getTime();
			pLength7 = p7e.getTime() - p7s.getTime();
			pLength8 = p8e.getTime() - p8s.getTime();
			pLength9 = p9e.getTime() - p9s.getTime();
			pLength10 = p10e.getTime() - p10s.getTime();
			pLength11 = p11e.getTime() - p11s.getTime();
			pLength12 = p12e.getTime() - p12s.getTime();

			PeriodsAndBreaksLengthOverall = p12e.getTime() - p1s.getTime();

			bLength1 = p2e.getTime() - p1e.getTime();
			bLength2 = p3e.getTime() - p2e.getTime();
			bLength3 = p4e.getTime() - p3e.getTime();
			bLength4 = p5e.getTime() - p4e.getTime();
			bLength5 = p6e.getTime() - p5e.getTime();
			bLength6 = p7e.getTime() - p6e.getTime();
			bLength7 = p8e.getTime() - p7e.getTime();
			bLength8 = p9e.getTime() - p8e.getTime();
			bLength9 = p10e.getTime() - p9e.getTime();
			bLength10 = p11e.getTime() - p10e.getTime();
			bLength11 = p12e.getTime() - p11e.getTime();*/

//			long diffSeconds = diff / 1000 % 60;
//			long diffMinutes = diff / (60 * 1000) % 60;
//			long diffHours = diff / (60 * 60 * 1000) % 24;
//			long diffDays = diff / (24 * 60 * 60 * 1000);
		/*} catch (Exception e) {
			e.printStackTrace();
		}*/

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
    }
}