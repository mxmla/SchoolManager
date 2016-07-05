package com.thinc_easy.schoolmanager;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class LessonFragment extends Fragment{
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName;
    private String caller;
	private int hTextSize;
	private TextView tvSubjectName, tvSubject, tvTeacher, tvPeriod;
	private String[] SubjectArray;
	private String periodFrom, periodTo, room;
	private TableRow tr1, tr2, tr3, tr4, tr5;
	private FrameLayout flTitle;
	private int[] colorIntsOld = {0xff33B5E5, 0xff0099CC, 0xffAA66CC, 0xff9933CC, 0xff99CC00, 0xff669900,
			0xffFFBB33, 0xffFF8800, 0xffFF4444, 0xffCC0000, 0xff000000, 0xffFFFFFF};
	private String[] colorNamesOld = {"blue_light", "blue_dark", "purple_light", "purple_dark", "green_light", "green_dark",
			"orange_light", "orange_dark", "red_light", "red_dark", "black", "white", "gray_light", "gray_dark"};
    private String[] colorNames = {"red", "pink", "purple", "deep_purple", "indigo", "blue",
            "light_blue", "cyan", "teal", "green", "light_green", "lime", "yellow", "amber",
            "ornge", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
    private int[] colorInts = {0xffF44336, 0xffE91E63, 0xff9C27B0, 0xff673AB7, 0xff3F51B5, 0xff2196F3,
            0xff03A9F4, 0xff00BCD4, 0xff009688, 0xff4CAF50, 0xff8BC34A, 0xffCDDC39,
            0xffFFEB3B, 0xffFFC107, 0xffFF9800, 0xffFF5722, 0xff795548, 0xff9E9E9E, 0xff607D8B, 0xff000000, 0xffFFFFFF};
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lesson, container, false);

        fragmentName = "LessonFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        caller = getArguments().getString("caller");

        ((TimetableActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_fragment_lesson));
        //((TimetableActivity) getActivity()).getSupportActionBar().setTitle("");

        // needed to indicate that the fragment would
        // like to add items to the Options Menu
        setHasOptionsMenu(true);
        // update the actionbar to show the up carat/affordance
        ((TimetableActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int ttColor = ((TimetableActivity) getActivity()).getResources().getColor(R.color.color_timetable_appbar);
        ((TimetableActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ttColor));

        int dayInt = getArguments().getInt("dayInt");
        int periodInt = getArguments().getInt("periodInt");
    	String[] dayNames = getResources().getStringArray(R.array.DayNames);
        String[] dayIDs = getResources().getStringArray(R.array.DayIDs);
        String[] periodNumbers = getResources().getStringArray(R.array.PeriodNumbers);
		String sName = ((TimetableActivity)getActivity()).getPeriodName(dayIDs[dayInt], periodNumbers[periodInt]);
		String sAbbrev = ((TimetableActivity) getActivity()).getPeriodAbbrev(sName);
		
        SubjectArray = ((TimetableActivity) getActivity()).getSubjectInfoFromAbbrev(getActivity(), sAbbrev);
        tvSubjectName = (TextView) v.findViewById(R.id.tvSubjectName);
        setUpTvSubjectName(v);

        for (int i = 4; i < 9; i++){
        	if (SubjectArray[i].equals(dayIDs[dayInt])){
                int pF =Integer.parseInt(SubjectArray[i+5].replaceAll("[\\D]", ""));
                int pT =Integer.parseInt(SubjectArray[i+10].replaceAll("[\\D]", ""));
                boolean periodsCorrect = false;
                //Toast.makeText(getActivity(), "periodInt: "+periodInt, Toast.LENGTH_SHORT).show();
                for (int i2 = pF; i2 <= pT; i2++){
                    // periodInt + 1, because periodInts start with 0 and in SubjectArray they start with 1!
                    //Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
                    if (periodInt + 1 == i2) periodsCorrect = true;
                }
                if (periodsCorrect) {
                    periodFrom = SubjectArray[i + 5];
                    periodTo = SubjectArray[i + 10];
                    room = SubjectArray[i + 15];
                }
        	}
        }
        
        tvTeacher = (TextView) v.findViewById(R.id.tvTeacher);
        tvPeriod = (TextView) v.findViewById(R.id.tvPeriod);
        
        tvTeacher.setText(SubjectArray[2].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", "")
                + " (" + SubjectArray[3].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", "") + ")");

        String r = "";
        if (room != null) r = " (" + room.replace("[comma]", ",") + ")";
        tvPeriod.setText(dayNames[dayInt] + ", " + periodFrom + ". - " + periodTo + ". " + getResources().getString(R.string.period) + r);
        
        
    	int colorInt = 0xffFFFFFF;
    	for (int i = 0; i < colorNames.length; i++){
    		if (colorNames[i].equals(SubjectArray[24])){
    			colorInt = colorInts[i];
    		}
    	}
        //flTitle.setBackgroundColor(colorInt);
        //((TimetableActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorInt));
        
        int textColorInt = 0xff000000;
    	for (int i = 0; i < colorNames.length; i++){
    		if (colorNames[i].equals(SubjectArray[25])){
    			textColorInt = colorInts[i];
    		}
    	}
    	//tvSubjectName.setTextColor(textColorInt);

        //final float size = getActivity().getResources().getDimensionPixelSize(R.dimen.subject_circle_large);
        final float scale = getActivity().getResources().getDisplayMetrics().density;
        int bounds = (int) (96 * scale + 0.5f);

        GradientDrawable gd = new GradientDrawable();
        gd.setShape(1);
        gd.setColor(colorInt);
        gd.setBounds(0, 0, bounds, bounds);
        gd.setSize(bounds, bounds);

        //final float strokeSize = getActivity().getResources().getDimensionPixelSize(R.dimen.subject_circle_large_stroke);
        if (colorInt == 0xffFFFFFF){
            gd.setStroke(8, 0xff000000);
        } else {
            gd.setStroke(8, 0xffFFFFFF);
        }
        Button subjectCircle = (Button) v.findViewById(R.id.subject_circle);
        //subjectCircle.setBackgroundDrawable(gd);

        subjectCircle.setText(sAbbrev.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
        subjectCircle.setTextColor(textColorInt);
        //float subjectCircleTextSize = getActivity().getResources().getDimension(R.dimen.subject_circle_large_text_size);
        subjectCircle.setTextSize(34);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for lollipop and newer versions
            CardView subjectCircleCard = (CardView) v.findViewById(R.id.subject_circle_card);
            subjectCircleCard.setBackgroundDrawable(gd);
        } else {
            subjectCircle.setBackgroundDrawable(gd);
        }

    	
    	final String abbrev = sAbbrev;
        subjectCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSubject(abbrev);
            }
        });
    	/* flTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowSubject(abbrev);
			}
		}); */

        // indicate that the fragment would like to add items to the OptionsMenu
        //setHasOptionsMenu(true);

        // update the AppBar to show the up arrow
        //((TimetableActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((TimetableActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        return v;
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        //((TimetableActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((TimetableActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
    }*/

    @Override
    public void onPause(){
        ((TimetableActivity) getActivity()).endLessonFragment();
        super.onPause();
    }

    private void setUpTvSubjectName(View v){
    	tvSubjectName.setText(SubjectArray[0].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
    	//tvSubjectName.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Thin.ttf"));
        /*
        header1 = (LinearLayout) v.findViewById(R.id.header1);
        while (tvSubjectName.getWidth() < v.getWidth()){
        	hTextSize = hTextSize + 1;
        	tvSubjectName.setTextSize(hTextSize);
        }*/
    }
	
    public void ShowSubject(String abbrev){
    	if (!abbrev.equals("-") && !abbrev.equals(null)){
        	Bundle args = new Bundle();
            args.putString("abbrev", abbrev);
            args.putString("caller", "Timetable");
            
    		Intent i = new Intent(getActivity(), EditSubjectActivity.class);
            i.putExtras(args);
    		startActivityForResult(i, 0);
    	}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Toast.makeText(getActivity(), "itemSelected", Toast.LENGTH_SHORT).show();
        // Get item selected and deal with it.
        switch (item.getItemId()){
            case android.R.id.home:
                // called when up arrow in AppBar is pressed
                getActivity().onBackPressed();
                return true;
                /*if (caller.equals("Timetable")) {
                    Toast.makeText(getActivity(), "return to Timetable", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), TimetableActivity.class);
                    startActivityForResult(i, 0);
                    return true;
                } else if (caller.equals("Home")){
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    startActivityForResult(i, 0);
                    return true;
                } else {
                    Intent i = new Intent(getActivity(), TimetableActivity.class);
                    startActivityForResult(i, 0);
                    return true;
                } */
                //getActivity().onBackPressed();
                //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}