package com.thinc_easy.schoolmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.sql.Time;

public class SubjectFragment extends Fragment{
	
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";
    private Tracker mTracker;
    private String fragmentName;
	private int hTextSize;
	private TextView tvSubjectName, tvSubject, tvTeacher, tvPeriod1, tvPeriod2, tvPeriod3, tvPeriod4, tvPeriod5;
	private String[] SubjectArray;
    private String[] mDayIDs, mDayNames;
	private TableRow tr1, tr2, tr3, tr4, tr5;
	private FrameLayout flTitle;
	private int[] colorIntsOld = {0xff33B5E5, 0xff0099CC, 0xffAA66CC, 0xff9933CC, 0xff99CC00, 0xff669900,
			0xffFFBB33, 0xffFF8800, 0xffFF4444, 0xffCC0000, 0xff000000, 0xffFFFFFF};
	private String[] colorNamesOld = {"blue_light", "blue_dark", "purple_light", "purple_dark", "green_light", "green_dark",
			"orange_light", "orange_dark", "red_light", "red_dark", "black", "white", "gray_light", "gray_dark"};
    private String[] colorNames = {"red", "pink", "purple", "deep_purple", "indigo", "blue",
            "light_blue", "cyan", "teal", "green", "light_green", "lime", "yellow", "amber",
            "orange", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
    private int[] colorInts = {0xffF44336, 0xffE91E63, 0xff9C27B0, 0xff673AB7, 0xff3F51B5, 0xff2196F3,
            0xff03A9F4, 0xff00BCD4, 0xff009688, 0xff4CAF50, 0xff8BC34A, 0xffCDDC39,
            0xffFFEB3B, 0xffFFC107, 0xffFF9800, 0xffFF5722, 0xff795548, 0xff9E9E9E, 0xff607D8B, 0xff000000, 0xffFFFFFF};
	private Button edit;
    private String ttFolder;
    private SharedPreferences prefs;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_subject, container, false);

        fragmentName = "SubjectFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        ((EditSubjectActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_fragment_subject));

        mDayIDs = getResources().getStringArray(R.array.DayIDs);
        mDayNames = getResources().getStringArray(R.array.DayNames);


        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ttFolder = prefs.getString(getActivity().getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        //subjectsFilepath = ttFolder + "/" + getActivity().getResources().getString(R.string.file_name_lessons);

        final String ID = getArguments().getString("ID");
        SubjectArray = DataStorageHandler.SubjectInfo(getActivity(), ttFolder, ID);
        tvSubjectName = (TextView) v.findViewById(R.id.tvSubjectName);
        setUpTvSubjectName(v);
        
        tvSubject = (TextView) v.findViewById(R.id.tvSubject);
        tvTeacher = (TextView) v.findViewById(R.id.tvTeacher);
        //flTitle = (FrameLayout) v.findViewById(R.id.FrameLayoutTitle);
        edit = (Button) v.findViewById(R.id.edit);

        String subject = SubjectArray[0].replace("[comma]", ",").replace("[none]","").replace("[null]","");
        String sAbr = SubjectArray[1].replace("[comma]", ",").replace("[none]","").replace("[null]","");
        tvSubject.setText((sAbr.equals("")) ? subject : subject+" (" +sAbr+ ")");

        String teacher = SubjectArray[2].replace("[comma]", ",").replace("[none]","").replace("[null]","");
        String tAbbrev = SubjectArray[3].replace("[comma]", ",").replace("[none]","").replace("[null]","");
        tvTeacher.setText((tAbbrev.equals("")) ? teacher : teacher+" ("+tAbbrev+ ")");

        
    	int colorInt = 0xffFFFFFF;
    	for (int i = 0; i < colorNames.length; i++){
    		if (colorNames[i].equals(SubjectArray[4])){
    			colorInt = colorInts[i];
    		}
    	}
        //flTitle.setBackgroundColor(colorInt);
        tvSubjectName.setBackgroundColor(colorInt);
        ((EditSubjectActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorInt));
        
        int textColorInt = 0xff000000;
    	for (int i = 0; i < colorNames.length; i++){
    		if (colorNames[i].equals(SubjectArray[5])){
    			textColorInt = colorInts[i];
    		}
    	}
    	tvSubjectName.setTextColor(textColorInt);

        setUpLessons(v, ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            edit.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
            edit.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
        } else {
            edit.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
        }
    	
    	edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditSubjectActivity)getActivity()).CallEditSubjectFragment(ID);
			}
		});


        // indicate that the fragment would like to add items to the OptionsMenu
        setHasOptionsMenu(true);

        // update the AppBar to show the up arrow
        ((EditSubjectActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return v;
    }

    private void setUpTvSubjectName(View v){
    	tvSubjectName.setText(SubjectArray[0].replace("[comma]", ",").replace("[none]", "").replace("[null]", ""));
    	tvSubjectName.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Regular.ttf"));
        tvSubjectName.setSingleLine(true);
        tvSubjectName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvSubjectName.setSelected(true);
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

    private void setUpLessons(View v, String subjectID){
        CardView cv = (CardView) v.findViewById(R.id.card_lessons);
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.ll_subject_lessons);

        final String[][] subjectLessons = DataStorageHandler.SubjectLessons(getActivity(), ttFolder, subjectID);
        boolean isLesson = false;
        int count = 1;
        if (subjectLessons != null && subjectLessons.length > 0){
            isLesson = true;
            for (int r = 0; r < subjectLessons.length; r++){
                final String AB = subjectLessons[r][1];
                final String day = subjectLessons[r][2];
                final String custom = subjectLessons[r][3];
                final String periodF = subjectLessons[r][4];
                final String periodT = subjectLessons[r][5];
                final String timeS = subjectLessons[r][6];
                final String timeE = subjectLessons[r][7];
                final String place = subjectLessons[r][8];
                ll.addView(lesson_element(count, AB, day, custom, periodF, periodT, timeS, timeE, place));
                count++;
            }

        }
        if (isLesson) {
            final int card_margin = (int) (getActivity().getResources().getDimension(R.dimen.card_margin)+0.5f);
            LinearLayout.LayoutParams cvlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cvlp.setMargins(card_margin, card_margin, card_margin, card_margin);
            cv.setLayoutParams(cvlp);

            LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.setLayoutParams(lllp);
        } else {
            cv.setVisibility(View.GONE);
        }
    }

    private LinearLayout lesson_element(int count, String AB, String day, String custom, String pF, String pT, String timeS, String timeE, String place){
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (getActivity().getResources().getDimension(R.dimen.list_1_line_row_height)+0.5f)));

        ll.addView(tv_period(count, AB, day));
        ll.addView(tv_lesson(custom, pF, pT, timeS, timeE, place));

        return ll;
    }

    private TextView tv_period(int count, String AB, String day){
        TextView tv = new TextView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,0,(int) (getActivity().getResources().getDimension(R.dimen.card_padding)+0.5f),0);
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
        tv.setSingleLine(true);
        final String number = String.valueOf(count);

        String text = "";

        if (DataStorageHandler.isStringNumeric(day) && Integer.parseInt(day) < mDayNames.length)
            text = text+mDayNames[Integer.parseInt(day)];

        if (AB != null && !AB.replace("[none]","").replace("[null]","").equals(""))
            text = text+" ("+AB.replace("[none]","").replace("[null]","")+")";
        tv.setText(text);

        return tv;
    }

    private TextView tv_lesson(String custom, String pF, String pT, String timeS, String timeE, String place){
        TextView tv = new TextView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //lp.setMargins(0,0,(int) (getActivity().getResources().getDimension(R.dimen.card_padding)+0.5f),0);
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setTextColor(getActivity().getResources().getColor(R.color.Text));
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tv.setSelected(true);

        String text = "";

        String timing = "";
        if (custom.equals("true")){
            if (timeS != null & timeE != null) timing = timeS+" - "+timeE;
        } else {
            final String period = getActivity().getResources().getString(R.string.period);
            if (pF != null) timing = (pT != null && !pT.equals(pF)) ? period+" "+pF+" - "+pT : period+" "+pF;
        }
        text = (!text.equals("")) ? text+" |  "+timing : timing;

        if (!place.replace("[none]","").replace("[null]","").equals(""))
            text = text+"  |  "+place.replace("[none]","").replace("[null]","");

        tv.setText(text);

        return tv;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem delete = menu.findItem(R.id.action_delete_subject);
        delete.setVisible(true);
        //inflater.inflate(R.menu.menu_subject_fragment, menu);
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
                return false;
            case R.id.action_delete_subject:
                ((EditSubjectActivity) getActivity()).menuActionDeleteSubject(SubjectArray[0]);
                return true;
            case R.id.action_settings:
                ((EditSubjectActivity) getActivity()).menuActionSettings();
                return true;
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