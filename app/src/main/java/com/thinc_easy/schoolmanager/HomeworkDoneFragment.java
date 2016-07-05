package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by M on 30.01.2016.
 */
public class HomeworkDoneFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName;
    private RecyclerView recyclerView;
    private HomeworkAdapter adapter;
    private static String[] ID;
    private static String[] subjectNames;
    private static String[] date;
    private static String[] title;
    private static String[] content;
    private static String[] done;
    private static String[] subjectAbbrev;
    private static String[] subjectBgColors;
    private static String[] subjectTextColors;
    private GradientDrawable gd;
    private static GradientDrawable[] icons;
    private static int[] colorInt;
    private static int[] textColorInt;

    private String[] colorNames = {"red", "pink", "purple", "deep_purple", "indigo", "blue",
            "light_blue", "cyan", "teal", "green", "light_green", "lime", "yellow", "amber",
            "ornge", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
    private int[] colorInts = {0xffF44336, 0xffE91E63, 0xff9C27B0, 0xff673AB7, 0xff3F51B5, 0xff2196F3,
            0xff03A9F4, 0xff00BCD4, 0xff009688, 0xff4CAF50, 0xff8BC34A, 0xffCDDC39,
            0xffFFEB3B, 0xffFFC107, 0xffFF9800, 0xffFF5722, 0xff795548, 0xff9E9E9E, 0xff607D8B, 0xff000000, 0xffFFFFFF};

    private FloatingActionButton fabPlus;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_homework_done, container, false);

        fragmentName = "HomeworkDoneFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        int hwColor = getActivity().getResources().getColor(R.color.color_homework_appbar);
        ((HomeworkActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(hwColor));

        SimpleDateFormat form = new SimpleDateFormat(getActivity().getResources().getString(R.string.date_formatter_general));
        SimpleDateFormat formatter = new SimpleDateFormat(getActivity().getResources().getString(R.string.date_formatter_local));

        /*fabPlus = (FloatingActionButton) v.findViewById(R.id.fab);

        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeworkActivity) getActivity()).showEditHwDialog();
            }
        });*/

        String[][] subjectsArray = ((HomeworkActivity) getActivity()).toArray(getActivity(), "Subjects.txt");
        int[] sNumberRowsCols = ((HomeworkActivity) getActivity()).nmbrRowsCols(getActivity(), "Subjects.txt");
        String[][] hwArray = ((HomeworkActivity) getActivity()).toArray(getActivity(), "Homework.txt");
        int[] numberRowsCols = ((HomeworkActivity) getActivity()).nmbrRowsCols(getActivity(), "Homework.txt");
        ID = new String[numberRowsCols[0]];
        subjectNames = new String[numberRowsCols[0]];
        date = new String[numberRowsCols[0]];
        title = new String[numberRowsCols[0]];
        content = new String[numberRowsCols[0]];
        done = new String[numberRowsCols[0]];
        subjectAbbrev = new String[numberRowsCols[0]];
        subjectBgColors = new String[numberRowsCols[0]];
        subjectTextColors = new String[numberRowsCols[0]];
        colorInt = new int[numberRowsCols[0]];
        textColorInt = new int[numberRowsCols[0]];
        long datesMillis[] = new long[numberRowsCols[0]];

        for (int i = 0; i < numberRowsCols[0]; i++){
            ID[i] = hwArray[i][0];
            subjectNames[i] = hwArray[i][1];
            title[i] = hwArray[i][3];
            content[i] = hwArray[i][4];
            done[i] = hwArray[i][5];

            for (int s = 0; s < sNumberRowsCols[0]; s++){
                if (subjectsArray[s][0].equals(subjectNames[i])){
                    subjectAbbrev[i] = subjectsArray[s][1];
                    subjectBgColors[i] = subjectsArray[s][24];
                    subjectTextColors[i] = subjectsArray[s][25];
                }
            }

            colorInt[i] = 0xffFFFFFF;
            textColorInt[i] = 0xff000000;
            for (int c = 0; c < colorNames.length; c++){
                if (subjectBgColors[i].equals(colorNames[c])) colorInt[i] = colorInts[c];
                if (subjectTextColors[i].equals(colorNames[c])) textColorInt[i] = colorInts[c];
            }

            String dateUnForm = hwArray[i][2];
            java.util.Date d1 = null;
            try {
                d1 = form.parse(dateUnForm);
                date[i] = formatter.format(d1);
            } catch (java.text.ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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

        for (int i = 0; i < date.length; i++){
            try {
                Date d1 = form.parse(hwArray[i][2]);
                Calendar c = Calendar.getInstance();
                c.setTime(d1);
                datesMillis[i] = c.getTimeInMillis();
            } catch (java.text.ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Sort by date

        int[] order = new int[numberRowsCols[0]];
        long[] datesOrder = new long[numberRowsCols[0]];
        for (int d = 0; d < datesOrder.length; d++){
            datesOrder[d] = 0;
        }
        for (int o = 0; o < datesMillis.length; o++){
            boolean foundPlace = false;
            for (int d = 0; d < order.length; d++){
                if (!foundPlace) {
                    if (datesOrder[d] == 0) {
                        datesOrder[d] = datesMillis[o];
                        order[d] = o;
                        foundPlace = true;
                    } else if (datesOrder[d] < datesMillis[o]) {
                        int[] newOrder = new int[numberRowsCols[0]];
                        long[] newDatesOrder = new long[numberRowsCols[0]];
                        for(int r = 0; r < d; r++){
                            newOrder[r] = order[r];
                            newDatesOrder[r] = datesOrder[r];
                        }
                        for(int r1 = d; r1 < datesOrder.length - 1; r1++){
                            newOrder[r1 + 1] = order[r1];
                            newDatesOrder[r1 + 1] = datesOrder[r1];
                        }
                        newDatesOrder[d] = datesMillis[o];
                        newOrder[d] = o;
                        order = newOrder;
                        datesOrder = newDatesOrder;
                        foundPlace = true;
                    }
                }
            }
        }
        GradientDrawable nIcons[] = new GradientDrawable[icons.length];
        String[] nID = new String[ID.length];
        String[] nSAbbrev = new String[subjectAbbrev.length];
        String[] nDate = new String[date.length];
        String[] nTitle = new String[title.length];
        String[] nContent = new String[content.length];
        String[] nDone = new String[done.length];
        int[] nTextColorInt = new int[textColorInt.length];
        for (int n = 0; n < nID.length; n++){
            nIcons[n] = icons[order[n]];
            nID[n] = ID[order[n]];
            nSAbbrev[n] = subjectAbbrev[order[n]];
            nDate[n] = date[order[n]];
            nTitle[n] = title[order[n]];
            nContent[n] = content[order[n]];
            nDone[n] = done[order[n]];
            nTextColorInt[n] = textColorInt[order[n]];
        }
        icons = nIcons;
        ID = nID;
        subjectAbbrev = nSAbbrev;
        date = nDate;
        title = nTitle;
        content = nContent;
        done = nDone;
        textColorInt = nTextColorInt;


        // Select only done homework

        int count = 0;
        for (int z = 0; z < ID.length; z++){
            if (done[z].equals("yes")) count++;
        }

        GradientDrawable sIcons[] = new GradientDrawable[count];
        String[] sID = new String[count];
        String[] sSAbbrev = new String[count];
        String[] sDate = new String[count];
        String[] sTitle = new String[count];
        String[] sContent = new String[count];
        String[] sDone = new String[count];
        int[] sTextColorInt = new int[count];

        int count2 = 0;
        for (int c = 0; c < ID.length; c++){
            if (done[c].equals("yes")){
                sIcons[count2] = icons[c];
                sID[count2] = ID[c];
                sSAbbrev[count2] = subjectAbbrev[c];
                sDate[count2] = date[c];
                sTitle[count2] = title[c];
                sContent[count2] = content[c];
                sDone[count2] = done[c];
                sTextColorInt[count2] = textColorInt[c];
                count2++;
            }
        }

        icons = sIcons;
        ID = sID;
        subjectAbbrev = sSAbbrev;
        date = sDate;
        title = sTitle;
        content = sContent;
        done = sDone;
        textColorInt = sTextColorInt;


        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewHomeworkFull);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), (int) (getActivity().getResources().getDimension(R.dimen.list_2LineAvatarIcon_divider_paddingLeft)), (int) (getActivity().getResources().getDimension(R.dimen.list_2LineAvatarIcon_divider_paddingRight))));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration((int) (getActivity().getResources().getDimension(R.dimen.list_2LineAvatarIcon_verticalSpacing)), (int) (getActivity().getResources().getDimension(R.dimen.list_2LineAvatarIcon_listPadding_top)), (int) (getActivity().getResources().getDimension(R.dimen.list_2LineAvatarIcon_listPadding_bottom))));
        adapter = new HomeworkAdapter(getActivity(), getData(), "hwFull");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return v;
    }

    public static List<InformationHomeworkFull> getData(){
        List<InformationHomeworkFull> data = new ArrayList<>();

        //int[] icons = {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};
        // TODO sicher dass das die legths von subjectNames und bgColors sein sollen?
        //for (int i = 0; i < subjectNames.length && i < subjectBgColors.length; i++){
        for (int i = 0; i < ID.length && i < icons.length; i++){
            InformationHomeworkFull current = new InformationHomeworkFull();
            current.icon = icons[i];
            current.ID = ID[i];
            current.sAbbrev = subjectAbbrev[i];
            current.date = date[i];
            current.hTitle = title[i].replace("[comma]", ",");
            current.hContent = content[i].replace("[comma]", ",");
            current.done = done[i];
            current.sTextColor = textColorInt[i];
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
    public void onResume(){
        super.onResume();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
