package com.thinc_easy.schoolmanager;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.text.TextUtilsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
            "ornge", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
    private int[] colorInts = {0xffF44336, 0xffE91E63, 0xff9C27B0, 0xff673AB7, 0xff3F51B5, 0xff2196F3,
            0xff03A9F4, 0xff00BCD4, 0xff009688, 0xff4CAF50, 0xff8BC34A, 0xffCDDC39,
            0xffFFEB3B, 0xffFFC107, 0xffFF9800, 0xffFF5722, 0xff795548, 0xff9E9E9E, 0xff607D8B, 0xff000000, 0xffFFFFFF};
	private Button edit;
	
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

        String abbrev = getArguments().getString("abbrev");
        SubjectArray = ((EditSubjectActivity) getActivity()).getSubjectInfoFromAbbrev(getActivity(), abbrev);
        tvSubjectName = (TextView) v.findViewById(R.id.tvSubjectName);
        setUpTvSubjectName(v);
        
        tvSubject = (TextView) v.findViewById(R.id.tvSubject);
        tvTeacher = (TextView) v.findViewById(R.id.tvTeacher);
        tvPeriod1 = (TextView) v.findViewById(R.id.tvPeriod1);
        tvPeriod2 = (TextView) v.findViewById(R.id.tvPeriod2);
        tvPeriod3 = (TextView) v.findViewById(R.id.tvPeriod3);
        tvPeriod4 = (TextView) v.findViewById(R.id.tvPeriod4);
        tvPeriod5 = (TextView) v.findViewById(R.id.tvPeriod5);
        tr1 = (TableRow) v.findViewById(R.id.tr1);
        tr2 = (TableRow) v.findViewById(R.id.tr2);
        tr3 = (TableRow) v.findViewById(R.id.tr3);
        tr4 = (TableRow) v.findViewById(R.id.tr4);
        tr5 = (TableRow) v.findViewById(R.id.tr5);
        //flTitle = (FrameLayout) v.findViewById(R.id.FrameLayoutTitle);
        edit = (Button) v.findViewById(R.id.edit);
        
        tvSubject.setText(SubjectArray[0].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",") + " (" + SubjectArray[1].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",") + ")");
        tvTeacher.setText(SubjectArray[2].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", "")
                + " (" + SubjectArray[3].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", "") + ")");
        
        if (SubjectArray[4].equals("-")){
        	tr1.setVisibility(View.GONE);
        } else {
            String day = "";
            for (int i = 0; i < mDayIDs.length; i++){
                if (mDayIDs[i].equals(SubjectArray[4])) day = mDayNames[i];
            }
            String r = "";
            if (SubjectArray[19] != null) r = " (" + SubjectArray[19].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",") + ")";
        	tvPeriod1.setText(day + ", " + SubjectArray[9] + ". - " + SubjectArray[14] + ". " + getResources().getString(R.string.period) + r);
        }
        if (SubjectArray[5].equals("-")){
        	tr2.setVisibility(View.GONE);
        } else {
            String day = "";
            for (int i = 0; i < mDayIDs.length; i++){
                if (mDayIDs[i].equals(SubjectArray[5])) day = mDayNames[i];
            }
            String r = "";
            if (SubjectArray[20] != null) r = " (" + SubjectArray[20].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",") + ")";
        	tvPeriod2.setText(day + ", " + SubjectArray[10] + ". - " + SubjectArray[15] + ". " + getResources().getString(R.string.period) + r);
        }
        if (SubjectArray[6].equals("-")){
        	tr3.setVisibility(View.GONE);
        } else {
            String day = "";
            for (int i = 0; i < mDayIDs.length; i++){
                if (mDayIDs[i].equals(SubjectArray[6])) day = mDayNames[i];
            }
            String r ="";
            if (SubjectArray[21] != null) r = " (" + SubjectArray[21].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",") + ")";
        	tvPeriod3.setText(day + ", " + SubjectArray[11] + ". - " + SubjectArray[16] + ". " + getResources().getString(R.string.period) + r);
        }
        if (SubjectArray[7].equals("-")){
        	tr4.setVisibility(View.GONE);
    	} else {
            String day = "";
            for (int i = 0; i < mDayIDs.length; i++){
                if (mDayIDs[i].equals(SubjectArray[7])) day = mDayNames[i];
            }
            String r = "";
            if (SubjectArray[22] != null) r = " (" + SubjectArray[22].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",") + ")";
            tvPeriod4.setText(day + ", " + SubjectArray[12] + ". - " + SubjectArray[17] + ". " + getResources().getString(R.string.period) + r);
        }
        if (SubjectArray[8].equals("-")){
        	tr5.setVisibility(View.GONE);
        } else {
            String day = "";
            for (int i = 0; i < mDayIDs.length; i++){
                if (mDayIDs[i].equals(SubjectArray[8])) day = mDayNames[i];
            }
            String r = "";
            if (SubjectArray[23] != null) r = " (" + SubjectArray[23].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",") + ")";
        	tvPeriod5.setText(day + ", " + SubjectArray[13] + ". - " + SubjectArray[18] + ". " + getResources().getString(R.string.period) + r);
        }
        
    	int colorInt = 0xffFFFFFF;
    	for (int i = 0; i < colorNames.length; i++){
    		if (colorNames[i].equals(SubjectArray[24])){
    			colorInt = colorInts[i];
    		}
    	}
        //flTitle.setBackgroundColor(colorInt);
        tvSubjectName.setBackgroundColor(colorInt);
        ((EditSubjectActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorInt));
        
        int textColorInt = 0xff000000;
    	for (int i = 0; i < colorNames.length; i++){
    		if (colorNames[i].equals(SubjectArray[25])){
    			textColorInt = colorInts[i];
    		}
    	}
    	tvSubjectName.setTextColor(textColorInt);
    	
    	edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditSubjectActivity)getActivity()).CallEditSubjectFragment(SubjectArray[0]);
			}
		});


        // indicate that the fragment would like to add items to the OptionsMenu
        setHasOptionsMenu(true);

        // update the AppBar to show the up arrow
        ((EditSubjectActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return v;
    }

    private void setUpTvSubjectName(View v){
    	tvSubjectName.setText(SubjectArray[0].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
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