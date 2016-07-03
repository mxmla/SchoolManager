package com.thinc_easy.schoolmanager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment1 extends Fragment{
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private RecyclerView recyclerView;
    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private MyAdapter adapter;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;
    public Intent DrawerAction1;
    public Intent DrawerAction2;
    public Intent DrawerAction3;

    private static String[] titles;
    private GradientDrawable gd;
    private static GradientDrawable[] icons;

    public NavigationDrawerFragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState!=null){
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer_1, container, false);

        titles = getResources().getStringArray(R.array.activity_titles);
        int[] colors = {getResources().getColor(R.color.color_home),
                getResources().getColor(R.color.color_timetable), getResources().getColor(R.color.color_homework),
                getResources().getColor(R.color.color_myschool), getResources().getColor(R.color.color_settings)};
        icons = new GradientDrawable[colors.length];

        final float scale = getResources().getDisplayMetrics().density;
        int bounds = (int) (20 * scale + 0.5f);

        for (int i2 = 0; i2 < colors.length; i2++){
            gd = new GradientDrawable();
            gd.setShape(1);
            gd.setBounds(0, 0, bounds, bounds);
            gd.setSize(bounds, bounds);
            gd.setColor(colors[i2]);
            gd.mutate();
            icons[i2] = gd;
        }

        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new MyAdapter(getActivity(), getData(), "drawerList");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener(){

            @Override
            public void onClick(View view, int position) {
                switch (position){
                    case 0:
                        if (getActivity().getClass().equals(MainActivity.class)){
                        } else {
                            Intent i0 = new Intent(getActivity(),
                                    MainActivity.class);
                            startActivityForResult(i0, 0); }
                        break;
                    case 1:
                        if (getActivity().getClass().equals(TimetableActivity.class)){
                        } else {
                            Intent i1 = new Intent(getActivity(),
                                    TimetableActivity.class);
                            startActivityForResult(i1, 0); }
                        break;
                    case 2:
                        if (getActivity().getClass().equals(HomeworkActivity.class)){
                        } else {
                            Intent i2 = new Intent(getActivity(),
                                    HomeworkActivity.class);
                            startActivityForResult(i2, 0); }
                        break;
                    case 3:
                        if (getActivity().getClass().equals(MySchoolActivity.class)){
                        } else {
                            Intent i3 = new Intent(getActivity(),
                                    MySchoolActivity.class);
                            startActivityForResult(i3, 0); }
                        break;
                    case 4:
                        if (getActivity().getClass().equals(SettingsActivity.class)){
                        } else {
                            Intent i3 = new Intent(getActivity(),
                                    SettingsActivity.class);
                            startActivityForResult(i3, 0); }
                        break;
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }

    public static List<Information> getData(){
        List<Information> data = new ArrayList<>();

        //int[] icons = {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};
        for (int i = 0; i < titles.length && i < icons.length; i++){
            Information current = new Information();
            current.icon = icons[i];
            current.title = titles[i];
            data.add(current);
        }
        return data;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar){
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer){
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer+"");
                }
                getActivity().invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        if(!mUserLearnedDrawer && !mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public void upArrow(){
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void noUpArrow(){
        mDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    public void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }



    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null){
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)){
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }
}
