package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M on 24.05.2015.
 */
public class SubjectsListFragment extends Fragment {

    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    ListView listViewSubjects;
    private TextView slSectionTitleText, slSectionTitleButton;

    private static String[] subjectNames;
    private static String[] subjectAbbrev;
    private static String[] subjectBgColors;
    private static String[] subjectTextColors;
    private GradientDrawable gd;
    private static GradientDrawable[] icons;
    private static int[] colorInt;

    private String[] colorNames = {"red", "pink", "purple", "deep_purple", "indigo", "blue",
            "light_blue", "cyan", "teal", "green", "light_green", "lime", "yellow", "amber",
            "ornge", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
    private int[] colorInts = {0xffF44336, 0xffE91E63, 0xff9C27B0, 0xff673AB7, 0xff3F51B5, 0xff2196F3,
            0xff03A9F4, 0xff00BCD4, 0xff009688, 0xff4CAF50, 0xff8BC34A, 0xffCDDC39,
            0xffFFEB3B, 0xffFFC107, 0xffFF9800, 0xffFF5722, 0xff795548, 0xff9E9E9E, 0xff607D8B, 0xff000000, 0xffFFFFFF};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_subjects_list, container, false);

        fragmentName = "SubjectsListFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        slSectionTitleText = (TextView) v.findViewById(R.id.slSectionTitleText);
        slSectionTitleButton = (TextView) v.findViewById(R.id.slSectionTitleButton);

        slSectionTitleText.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
        slSectionTitleButton.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
        slSectionTitleButton.setTextColor(getActivity().getResources().getColor(R.color.color_timetable));

        String[][] subjectsArray = ((TimetableActivity) getActivity()).toArray(getActivity(), "Subjects.txt");
        int[] numberRowsCols = ((TimetableActivity) getActivity()).nmbrRowsCols(getActivity(), "Subjects.txt");
        subjectNames = new String[numberRowsCols[0]];
        subjectAbbrev = new String[numberRowsCols[0]];
        subjectBgColors = new String[numberRowsCols[0]];
        subjectTextColors = new String[numberRowsCols[0]];
        colorInt = new int[numberRowsCols[0]];
        for (int i = 0; i < numberRowsCols[0]; i++){
            subjectNames[i] = subjectsArray[i][0];
            subjectAbbrev[i] = subjectsArray[i][1];
            subjectBgColors[i] = subjectsArray[i][24];
            subjectTextColors[i] = subjectsArray[i][25];
        }

        for (int i2 = 0; i2 < subjectBgColors.length; i2++){
            boolean foundColor = false;
            for (int i3 = 0; i3 < colorNames.length; i3++){
                if (colorNames[i3].equals(subjectBgColors[i2])){
                    colorInt[i2] = colorInts[i3];
                    foundColor = true;
                }
            }
            if (!foundColor){
                colorInt[i2] = 0xffFFFFFF;
            }
        }

        icons = new GradientDrawable[subjectBgColors.length];
        final float scale = getResources().getDisplayMetrics().density;
        int bounds = (int) (40 * scale + 0.5f);

        for (int i2 = 0; i2 < subjectBgColors.length; i2++){
            gd = new GradientDrawable();
            gd.setShape(1);
            gd.setBounds(0, 0, bounds, bounds);
            gd.setSize(bounds, bounds);
            gd.setColor(colorInt[i2]);
            gd.mutate();
            icons[i2] = gd;
        }

        int verticalSpacing = (int) (getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_verticalSpacing));
        int listPaddingTop = (int) (getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_listPadding_top));
        int listPaddingBottom = (int) (getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_listPadding_bottom));
        int dividerPaddingLeft = (int) (getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_divider_paddingLeft));
        int dividerPaddingRight = (int) (getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_divider_paddingRight));
        int adapterItemSize = (int) getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_tile_height);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewSubjects);
        adapter = new MyAdapter(getActivity(), getData(), "1LineAvatarAndText");
        int viewHeight = (adapter.getItemCount() * (listPaddingTop + listPaddingBottom + adapterItemSize + verticalSpacing + getActivity().obtainStyledAttributes(new int[]{android.R.attr.listDivider}).getDrawable(0).getIntrinsicHeight()));
        recyclerView.getLayoutParams().height = viewHeight;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager((TimetableActivity) getActivity()));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener(){

            @Override
            public void onClick(View view, int position) {
                if(!subjectAbbrev[position].equals(null) && !subjectAbbrev[position].equals("-")) {
                    Bundle args = new Bundle();
                    args.putString("abbrev", subjectAbbrev[position]);
                    args.putString("caller", "Timetable");

                    Intent i0 = new Intent(getActivity(), EditSubjectActivity.class);
                    i0.putExtras(args);
                    startActivityForResult(i0, 0);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        // Get ListView object from xml
        //listViewSubjects = (ListView) v.findViewById(R.id.listViewSubjects);

        // Defined Array values to show in ListView
        /*String[][] subjectsArray = ((TimetableActivity) getActivity()).toArray(getActivity(), "Subjects.txt");
        int[] numberRowsCols = ((TimetableActivity) getActivity()).nmbrRowsCols(getActivity(), "Subjects.txt");
        String[] values = new String[numberRowsCols[0]];
        for (int i = 0; i < numberRowsCols[0]; i++){
            values[i] = subjectsArray[i][0];
        }*/


        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, subjectNames);

        // Assign adapter to ListView
        listViewSubjects.setAdapter(adapter);*/

        // ListView Item Click Listener
       /* listViewSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listViewSubjects.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getActivity(), "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG).show();
            }
        });*/

        return v;
    }

    public static List<Information> getData(){
        List<Information> data = new ArrayList<>();

        //int[] icons = {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};
        for (int i = 0; i < subjectNames.length && i < subjectBgColors.length; i++){
            Information current = new Information();
            current.icon = icons[i];
            current.title = subjectNames[i];
            data.add(current);
        }
        return data;
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

    @Override
    public void onPause(){
        ((TimetableActivity) getActivity()).endSubjectsListFragment();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
