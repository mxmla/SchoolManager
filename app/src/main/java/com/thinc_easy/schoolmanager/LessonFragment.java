package com.thinc_easy.schoolmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class LessonFragment extends BottomSheetDialogFragment{
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName;
    private String caller;
    private ActionBarActivity context;
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
            "orange", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
    private int[] colorInts = {0xffF44336, 0xffE91E63, 0xff9C27B0, 0xff673AB7, 0xff3F51B5, 0xff2196F3,
            0xff03A9F4, 0xff00BCD4, 0xff009688, 0xff4CAF50, 0xff8BC34A, 0xffCDDC39,
            0xffFFEB3B, 0xffFFC107, 0xffFF9800, 0xffFF5722, 0xff795548, 0xff9E9E9E, 0xff607D8B, 0xff000000, 0xffFFFFFF};
    private SharedPreferences prefs;
    private String ttFolder, lessonsFilepath;
    private float text_size_content, text_size_secondary;
    private int list_row_height_2_line;
    private BottomSheetBehavior bottomSheetBehavior;
    private int thisColor1, thisColor2;

//    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
//
//        @Override
//        public void onStateChanged(@NonNull View bottomSheet, int newState) {
//            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                dismiss();
//            }
//        }
//
//        @Override
//        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//        }
//    };
	
//    @Override
//    public void setupDialog(Dialog dialog, int style){
//        super.setupDialog(dialog, style);
//        View v = View.inflate(getContext(), R.layout.fragment_lesson, null);
//        dialog.setContentView(v);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lesson, container, false);

        fragmentName = "LessonFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        if (getArguments().containsKey("caller")){
            caller = getArguments().getString("caller");
        } else {
            caller = "Timetable";
        }

        if (caller.equals("Timetable")){
            context = ((TimetableActivity) getActivity());
        } else if (caller.equals("Home")) {
            context = ((MainActivity) getActivity());
        } else {
            context = ((TimetableActivity) getActivity());
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        NestedScrollView nsv = (NestedScrollView) v.findViewById(R.id.NestedScrollView1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nsv.setElevation(getActivity().getResources().getDimension(R.dimen.lesson_bottom_sheet_elevation));
        }

        bottomSheetBehavior = BottomSheetBehavior.from(v.findViewById(R.id.NestedScrollView1));
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    context.getSupportActionBar().setTitle(getString(R.string.title_fragment_lesson));
                    //((TimetableActivity) getActivity()).getSupportActionBar().setTitle("");

                    // needed to indicate that the fragment would
                    // like to add items to the Options Menu
                    setHasOptionsMenu(true);
                    // update the actionbar to show the up carat/affordance
//                    ((TimetableActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    ((TimetableActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

                    context.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(thisColor1));

                    if (caller.equals("Timetable")) {
                        ((TimetableActivity) getActivity()).extendLessonFragment();
                    } else if (caller.equals("Home")){
                        ((MainActivity) getActivity()).extendLessonFragment();
                    } else {
                        ((TimetableActivity) getActivity()).extendLessonFragment();
                    }

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    if (caller.equals("Timetable")) {
                        ((TimetableActivity) getActivity()).endLessonFragment();
                    } else if (caller.equals("Home")){
                        ((MainActivity) getActivity()).endLessonFragment();
                    } else {
                        ((TimetableActivity) getActivity()).endLessonFragment();
                    }
                    setHasOptionsMenu(false);

                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    if (caller.equals("Timetable")) {
                        ((TimetableActivity) getActivity()).collapseLessonFragment();
                    } else if (caller.equals("Home")){
                        ((MainActivity) getActivity()).collapseLessonFragment();
                    } else {
                        ((TimetableActivity) getActivity()).collapseLessonFragment();
                    }
                    setHasOptionsMenu(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        String lessonID = "[none]";
        String dateWeek = "[none]";
        if (getArguments().containsKey("lessonID")) lessonID = getArguments().getString("lessonID");
        if (getArguments().containsKey("lessonID")) dateWeek = getArguments().getString("date_week");

        ttFolder = prefs.getString(getActivity().getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        lessonsFilepath = ttFolder + "/" + getActivity().getResources().getString(R.string.file_name_lessons);

        text_size_content = getActivity().getResources().getDimension(R.dimen.lesson_bottom_sheet_text_size_content);
        text_size_secondary = getActivity().getResources().getDimension(R.dimen.lesson_bottom_sheet_text_size_secondary);
        list_row_height_2_line = (int) (getActivity().getResources().getDimension(R.dimen.lesson_bottom_sheet_list_row_height_2_line) + 0.5f);

        if (lessonID != null && lessonID.length() >= 4) {
            setUpLessonInfo(v, lessonID, dateWeek);
            setUpButtonsViewEdit(v, lessonID);
            setUpHomework(v, lessonID, dateWeek);
        }

//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) v.getParent()).getLayoutParams();
//        CoordinatorLayout.Behavior behavior = params.getBehavior();
//
//        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
//            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
//        }

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
        if (caller.equals("Timetable")) {
            ((TimetableActivity) getActivity()).endLessonFragment();
        } else if (caller.equals("Home")){
            ((MainActivity) getActivity()).endLessonFragment();
        } else {
            ((TimetableActivity) getActivity()).endLessonFragment();
        }
        super.onPause();
    }

    private String[] getTodaysDateGeneralLocal(String dateWeek, String dayInt){
        String[] todayDate = new String[] {"[null]", "[null]"};

        final String[] days = new String[] {"6", "0", "1", "2", "3", "4", "5"};
        int dInt = -1;
        for (int d = 0; d < days.length; d++){
            if (dayInt.equals(days[d])) dInt = d;
        }
        if (dInt >= 0){
            final Date date = DataStorageHandler.getDateFromGeneralDateFormat(getActivity(), dateWeek);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_WEEK, dInt + 1);

            String todayDateLocal = DataStorageHandler.formatDateLocalFormat(getActivity(), cal);
            String todayDateGeneral = DataStorageHandler.formatDateGeneralFormat(getActivity(), cal);
            todayDate = new String[] {todayDateGeneral, todayDateLocal};
        }

        return todayDate;
    }

    private void setUpLessonInfo(View v, String lessonID, String dateWeek){
        TextView tv_header = (TextView) v.findViewById(R.id.tv_bottom_sheet_header);
        TextView tv_subheader = (TextView) v.findViewById(R.id.tv_bottom_sheet_subheader);
        TextView tv_place = (TextView) v.findViewById(R.id.tv_lesson_place);
        TextView tv_time = (TextView) v.findViewById(R.id.tv_lesson_time);
        TextView tv_teacher = (TextView) v.findViewById(R.id.tv_lesson_teacher);

        final String[] lessonInfo = DataStorageHandler.LessonInfo(getActivity(), ttFolder, lessonID);
        final String[] subjectInfo = DataStorageHandler.SubjectInfo(getActivity(), ttFolder, lessonID.substring(0,4));

        final String sColor1 = subjectInfo[4];
        final String sColor2 = subjectInfo[5];
        thisColor1 = (getActivity().getResources().getColor(R.color.color_timetable_appbar));
        thisColor2 = 0xffFFFFFF;
        for (int c = 0; c < colorNames.length; c++){
            if (sColor1.equals(colorNames[c]) && c < colorInts.length && colorInts[c] != 0xffFFFFFF) thisColor1 = colorInts[c];
            if (sColor2.equals(colorNames[c]) && c < colorInts.length) thisColor2 = colorInts[c];
        }

        final String sName = subjectInfo[0].replace("[null]", "").replace("[none]", "").replace("[comma]", ",");
        final String sAbbrev = subjectInfo[1].replace("[null]", "").replace("[none]", "").replace("[comma]", ",");
        final String header_text = (sAbbrev.equals("")) ? sName : sName + " (" + sAbbrev + ")";
        tv_header.setText(header_text);
        tv_header.setSelected(true);

        String subheader_text = "";
        final String day = lessonInfo[1].replace(" ", "").replace("[none]", "").replace("[comma]", ",");
        final String[] dayNames = getActivity().getResources().getStringArray(R.array.DayNames);
        if (DataStorageHandler.isStringNumeric(day)) subheader_text = dayNames[Integer.parseInt(day)];

        final String todayDate = getTodaysDateGeneralLocal(dateWeek, day)[1];
        subheader_text = (todayDate.equals("[none]")) ? subheader_text : subheader_text + ", " + todayDate;
        tv_subheader.setText(subheader_text);
        tv_subheader.setSelected(true);


        final String place_text = lessonInfo[7].replace("[null]", "").replace("[none]", "").replace("[comma]", ",");
        tv_place.setText(place_text);

        final String custom = lessonInfo[2];
        if (custom.equals("true")) {
            final String timeS = lessonInfo[5];
            final String timeE = lessonInfo[6];
            tv_time.setText(timeS + " - " + timeE);
        } else {
            final String pF = lessonInfo[3].replace(" ", "").replace("[none]", "");
            final String pT = lessonInfo[4].replace(" ", "").replace("[none]", "");
            final String period = getActivity().getResources().getString(R.string.lesson_period);
            String time_text = period + " " + pF + " - " + pT;
            if (DataStorageHandler.isStringNumeric(pF) && DataStorageHandler.isStringNumeric(pT)){
                final int f = Integer.parseInt(pF);
                final int t = Integer.parseInt(pT);
                final String fTime = prefs.getString("pref_key_period"+f+"_start", "[none]");
                final String tTime = prefs.getString("pref_key_period"+t+"_end", "[none]");

                if (!fTime.equals("[none]") && !tTime.equals("[none]")) time_text = time_text + "  |  " + fTime + " - " + tTime;
            }
            tv_time.setText(time_text);
        }

        final String teacherName = subjectInfo[2].replace("[null]", "").replace("[none]", "").replace("[comma]", ",");
        final String teacherAbbrev = subjectInfo[3].replace("[null]", "").replace("[none]", "").replace("[comma]", ",");
        final String teacher_text = (teacherAbbrev.equals("")) ? teacherName : teacherName + " (" + teacherAbbrev + ")";
        tv_teacher.setText(teacher_text);
    }

    private void setUpButtonsViewEdit(View v, final String lessonID){
        TextView b_view_subject = (TextView) v.findViewById(R.id.b_view_subject);
        TextView b_edit = (TextView) v.findViewById(R.id.b_edit);
        final String subjectID = lessonID.substring(0,4);

        b_view_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lessonID.length() >= 4) ShowSubject(lessonID.substring(0,4));
            }
        });

        b_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lessonID.length() >= 4) EditSubject(lessonID.substring(0,4));
            }
        });
    }

    private void setUpHomework(View v, final String lessonID, String dateWeek){
        LinearLayout ll_homework = (LinearLayout) v.findViewById(R.id.ll_homework);
        ll_homework.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout rl_homework = (RelativeLayout) v.findViewById(R.id.rl_homework);
        View v_divider = (View) v.findViewById(R.id.v_divider_homework);
        RelativeLayout rl_add_homework = (RelativeLayout) v.findViewById(R.id.rl_add_homework);

        LinearLayout ll = new LinearLayout(getActivity());
        RelativeLayout.LayoutParams lllp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lllp.setMargins((int) (getActivity().getResources().getDimension(R.dimen.lesson_bottom_sheet_text_margin_left)+0.5f),0,0,0);
        ll.setLayoutParams(lllp);
        ll.setOrientation(LinearLayout.VERTICAL);

        final String[][] subjectHomework = DataStorageHandler.SubjectHomework(getActivity(), ttFolder, lessonID.substring(0,4));

        final String[] lessonInfo = DataStorageHandler.LessonInfo(getActivity(), ttFolder, lessonID);
        final String day = lessonInfo[1].replace(" ", "").replace("[none]", "").replace("[comma]", ",");
        final String todayDateGeneral = getTodaysDateGeneralLocal(dateWeek, day)[0];

        int ID = 100;
        boolean isHomework = false;
        for (int i = 0; i < subjectHomework.length; i++){
            System.out.println("h: "+subjectHomework[i][1]+", l: "+todayDateGeneral);
            if (subjectHomework[i].length >= 3 && subjectHomework[i][1].equals(todayDateGeneral)){
                System.out.println("equals!!");
                rl_homework.setVisibility(View.VISIBLE);
                v_divider.setVisibility(View.VISIBLE);

                final String homeworkID = subjectHomework[i][0];
                final String title = subjectHomework[i][2];
                final String content = subjectHomework[i][3];
                ll_homework.addView(homeworkElement(title, content, homeworkID, ID));
                ID++;
                isHomework = true;
            }
        }

        //rl_homework.addView(ll);
        rl_homework.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if(!isHomework){
            rl_homework.setVisibility(View.GONE);
            v_divider.setVisibility(View.GONE);
        }

        rl_add_homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (caller.equals("Timetable")) {
                    ((TimetableActivity) getActivity()).showEditHwDialog(lessonID.substring(0,4), todayDateGeneral);
                } else if (caller.equals("Home")){
                    ((MainActivity) getActivity()).showEditHwDialog(lessonID.substring(0,4), todayDateGeneral);
                } else {
                    ((TimetableActivity) getActivity()).showEditHwDialog(lessonID.substring(0,4), todayDateGeneral);
                }
            }
        });
    }

    private RelativeLayout homeworkElement(String title, String content, final String homeworkID, int ID){
        System.out.println("homeworkElement: "+title);
        RelativeLayout rl = new RelativeLayout(getActivity());
        rl.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lpRL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, list_row_height_2_line);
        rl.setLayoutParams(lpRL);

        title = title.replace("[none]", "").replace("[null]", "").replace("[comma]", ",");
        if (title.length() == 0) title = "--";
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setId(ID);
        tvTitle.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvTitle.setText(title);
        tvTitle.setTextColor(getActivity().getResources().getColor(R.color.color_lesson_bottom_sheet_text_content));
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_size_content);
        tvTitle.setSingleLine(true);
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvTitle.setSelected(true);


        content = content.replace("[none]", "").replace("[null]", "").replace("[comma]", ",");
        if (content.length() == 0) content = "--";
        TextView tvContent = new TextView(getActivity());
        RelativeLayout.LayoutParams lpContent = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpContent.addRule(RelativeLayout.BELOW, tvTitle.getId());
        tvContent.setLayoutParams(lpContent);
        tvContent.setText(content);
        tvContent.setTextColor(getActivity().getResources().getColor(R.color.color_lesson_bottom_sheet_text_secondary));
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_size_secondary);
        tvContent.setSingleLine(true);
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvContent.setSelected(true);

        rl.addView(tvTitle);
        rl.addView(tvContent);

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (caller.equals("Timetable")) {
                    ((TimetableActivity) getActivity()).showViewHwDialog(homeworkID);
                } else if (caller.equals("Home")){
                    ((MainActivity) getActivity()).showViewHwDialog(homeworkID);
                } else {
                    ((TimetableActivity) getActivity()).showViewHwDialog(homeworkID);
                }
            }
        });

        return rl;
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
	
    public void ShowSubject(String subjectID){
    	if (!subjectID.equals("-") && !subjectID.equals(null)){
        	Bundle args = new Bundle();
            args.putString("subjectID", subjectID);
            args.putString("action", "view_subject");
            
    		Intent i = new Intent(getActivity(), EditSubjectActivity.class);
            i.putExtras(args);
    		startActivityForResult(i, 0);
    	}
    }

    public void EditSubject(String subjectID){
        if (!subjectID.equals("-") && !subjectID.equals(null)){
            Bundle args = new Bundle();
            args.putString("subjectID", subjectID);
            args.putString("action", "edit_subject");

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
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                getActivity().onBackPressed();
                if (caller.equals("Timetable")) {
                    ((TimetableActivity) getActivity()).endLessonFragment();
                } else if (caller.equals("Home")){
                    ((MainActivity) getActivity()).endLessonFragment();
                } else {
                    ((TimetableActivity) getActivity()).endLessonFragment();
                }
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

        if (caller.equals("Timetable")) {
            ((TimetableActivity) getActivity()).resumeLessonFragment();
        } else if (caller.equals("Home")){
            ((MainActivity) getActivity()).resumeLessonFragment();
        } else {
            ((TimetableActivity) getActivity()).resumeLessonFragment();
        }
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            if (caller.equals("Timetable")) {
                ((TimetableActivity) getActivity()).extendLessonFragment();
            } else if (caller.equals("Home")){
                ((MainActivity) getActivity()).extendLessonFragment();
            } else {
                ((TimetableActivity) getActivity()).extendLessonFragment();
            }
            context.getSupportActionBar().setTitle(getString(R.string.title_fragment_lesson));
            context.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(thisColor1));
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}