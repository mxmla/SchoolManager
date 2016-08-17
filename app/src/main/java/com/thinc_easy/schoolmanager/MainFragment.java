package com.thinc_easy.schoolmanager;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainFragment extends Fragment {
	
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";
    private Tracker mTracker;
    private String fragmentName;
    public static final int NOTIFICATION_ID = 1;
    private String nowSubjectName, nowSubjectAbbrev,
            nowPeriodTo, nowPeriodFrom, nowDayName, nowRoom, nowTeacher;
    private int nowHour, nowMinute, nowPeriod, nowDayOfWeek, nowDayInt;
    private String nextRoom, nextTeacher, nextPeriodTo, nextSubjectName;
    private int nextPeriodStart, nextPeriodSearchStart, breakNextLesson;
    private boolean isLesson, isNextLesson, isTomorrowSubjects, isHomeworkTomorrow,
            isEndOfDay, beforeDayStart, dayEnd, isCreateTimetable;

    private TextView tvNowSubjectName, tvNowRoom, tvNowTeacher, tvNowSubjectAbbrev,
            tvNextSubjectAbbrev, tvNowTimes, tvNextTimes, tvNowPeriods, tvNextPeriods,
            tvNextSubjectName, tvNextRoom, tvNextTeacher;
    private View vNowColor, vNextColor;
    private CardView nowLessonCard, nextLessonCard, tomorrowLessonsCard, createTtCard, shareAppCard,
            cvNewFeature, cvAddedSchools, mySchoolUpdatedCard;
    RelativeLayout rlNews, ttSectionTitle;
    private Button createTtCardButton, shareAppCardShareButton, shareAppCardDontShareButton,
            bRefresh;
    private Button bTS1, bTS2, bTS3, bTS4, bTS5, bTS6, bTS7, bTS8, bTS9, bTS10, bTS11, bTS12,
            bTS13, bTS14, bTS15;
    private boolean mUserClickedDontShare;
    private int mOpenMainActivityCount;
    private String KEY_USER_CLICKED_DONT_SHARE = "user_clicked_dont_share";
    private String KEY_OPEN_MAIN_ACTIVITY_COUNT = "open_main_activity_count";
    public static final String PREF_FILE_NAME = "testpref";
    public HomeworkAdapter adapter;
    private boolean dismissedNewFeatureMySchool = false;
    private boolean dismissedAddedNewSchoolsInfo = false;

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
    private String strng;
    private SharedPreferences prefs;
	
	// This value is defined and consumed by app code, so any value will work.
    // There's no significance to this sample using 0.
    public static final int REQUEST_CODE = 0;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        int hColor = getActivity().getResources().getColor(R.color.color_home_appbar);
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(hColor));


        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserClickedDontShare = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_CLICKED_DONT_SHARE, "false"));

        fragmentName = "MainFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mOpenMainActivityCount = Integer.valueOf(readFromPreferences(getActivity(), KEY_OPEN_MAIN_ACTIVITY_COUNT, "0"));

        if (mOpenMainActivityCount % 10 == 0){

        }

        tvNowSubjectName = (TextView) v.findViewById(R.id.tvNowSubjectName);
        tvNowSubjectAbbrev = (TextView) v.findViewById(R.id.tvNowSubjectAbbrev);
        tvNextSubjectAbbrev = (TextView) v.findViewById(R.id.tvNextSubjectAbbrev);
        tvNowRoom = (TextView) v.findViewById(R.id.tvNowRoom);
        tvNowTeacher = (TextView) v.findViewById(R.id.tvNowTeacher);
        tvNowTimes = (TextView) v.findViewById(R.id.tvNowTimes);
        tvNextTimes = (TextView) v.findViewById(R.id.tvNextTimes);
        tvNowPeriods = (TextView) v.findViewById(R.id.tvNowPeriods);
        tvNextPeriods = (TextView) v.findViewById(R.id.tvNextPeriods);
        vNowColor = (View) v.findViewById(R.id.NowSubjectColorView);
        tvNextSubjectName = (TextView) v.findViewById(R.id.tvNextSubjectName);
        tvNextRoom = (TextView) v.findViewById(R.id.tvNextRoom);
        tvNextTeacher = (TextView) v.findViewById(R.id.tvNextTeacher);
        vNextColor = (View) v.findViewById(R.id.NextSubjectColorView);
        nowLessonCard = (CardView) v.findViewById(R.id.NowLessonCard);
        nextLessonCard = (CardView) v.findViewById(R.id.NextLessonCard);
        tomorrowLessonsCard = (CardView) v.findViewById(R.id.TomorrowLessonsCard);
        createTtCard = (CardView) v.findViewById(R.id.CardCreateTt);
        createTtCardButton = (Button) v.findViewById(R.id.CardCreateTtButton);
        shareAppCard = (CardView) v.findViewById(R.id.ShareApp);
        shareAppCardShareButton = (Button) v.findViewById(R.id.CardShareButton);
        shareAppCardDontShareButton = (Button) v.findViewById(R.id.CardDontShareButton);
        cvNewFeature = (CardView) v.findViewById(R.id.IntroducingMySchoolCard);
        cvAddedSchools = (CardView) v.findViewById(R.id.MySchoolAddedSchoolsCard);
        mySchoolUpdatedCard = (CardView) v.findViewById(R.id.MySchoolSiteUpdatedCard);
        rlNews = (RelativeLayout) v.findViewById(R.id.newsSectionTitle);
        ttSectionTitle = (RelativeLayout) v.findViewById(R.id.ttSectionTitle);
        //bRefresh = (Button) v.findViewById(R.id.bRefresh);
        isEndOfDay = true;
        isNextLesson = false;
        isTomorrowSubjects = false;
        isCreateTimetable = true;
        isHomeworkTomorrow = false;
        beforeDayStart = false;
        dayEnd = false;
        breakNextLesson = -1;

        nowPeriod = -1;
        nowDayName = "-";
        nowRoom = "-";
        nextPeriodStart = 0;
        nextRoom = "-";

        NowSubject(v);
        NextSubject(v);
        TomorrowSubjects(v);
        HomeworkCard(v);
        CreateTtCard(v);
        shareAppCard(v);
        //FloatingActionButton(v);
        TimetableSection(v);
        HomeworkSection(v);
        NewsSection(v);
        newFeatureCard(v);
        handleUserSchoolIDs();
        //MySchoolCheckForWebsiteUpdate(v);
        storeSchoolInDatabase();

        /*bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).refreshMainFragment();*/

                /*NowSubject(v);
                NextSubject(v);
                TomorrowSubjects(v);
                HomeworkCard(v);
                CreateTtCard(v);
                shareAppCard(v);
                FloatingActionButton(v);*/
            /*}
        });*/

        final TextView tvD2 = (TextView) v.findViewById(R.id.tvD2);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("refreshing", "onRefresh called from SwipeRefreshLayout");
                ((MainActivity) getActivity()).refreshMainFragment();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.color_home),
                getActivity().getResources().getColor(R.color.color_timetable),
                getActivity().getResources().getColor(R.color.color_homework),
                getActivity().getResources().getColor(R.color.color_settings));
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void TimetableSection(View v){
        TextView ttSectionTitleText = (TextView) v.findViewById(R.id.ttSectionTitleText);
        TextView ttSectionTitleButton = (TextView) v.findViewById(R.id.ttSectionTitleButton);

        ttSectionTitleText.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
        ttSectionTitleButton.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
        ttSectionTitleButton.setTextColor(getActivity().getResources().getColor(R.color.color_timetable));
        ttSectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TimetableActivity.class);
                startActivityForResult(i, 0);
            }
        });

        if (!isLesson && !isNextLesson && !isTomorrowSubjects && !isCreateTimetable){
            ttSectionTitle.setVisibility(View.GONE);
        } else {
            ttSectionTitle.setVisibility(View.VISIBLE);
        }
    }


    private void handleUserSchoolIDs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        final String keyUserID = getActivity().getResources().getString(R.string.pref_key_user_id);
        final String keySchoolID = getActivity().getResources().getString(R.string.pref_key_my_school_school_id);
        final String keyCountryID = getActivity().getResources().getString(R.string.pref_key_my_school_country_id);
        final String keyUserIDRegistered = getActivity().getResources().getString(R.string.pref_key_user_id_registered);
        final String keySchoolRegistered = getActivity().getResources().getString(R.string.pref_key_school_registered);
        final String keyCountryRegistered = getActivity().getResources().getString(R.string.pref_key_country_registered);
        final String keyStateRegistered = getActivity().getResources().getString(R.string.pref_key_state_registered);


        String userID = "[none]";
        if (prefs.contains(keyUserID)){
            userID = prefs.getString(keyUserID, "[none]");
        }
        if (userID == null || userID.equals("") || userID.equals("[none]")){
            SecureRandom random = new SecureRandom();
            userID = new BigInteger(130, random).toString(32);
            prefs.edit().putString(keyUserID, userID).apply();

            /*FirebaseMessaging.getInstance().subscribeToTopic("user_"+userID);
            Log.d("FCM", "Subscribed to user topic");
            prefs.edit().putBoolean(keyUserIDRegistered, true).apply();*/
        }


        if (!prefs.contains(keyUserIDRegistered) || prefs.getBoolean(keyUserIDRegistered, false)){

            FirebaseMessaging.getInstance().unsubscribeFromTopic("user_"+userID);
            Log.d("FCM", "Unsubscribed from user topic");
            prefs.edit().putBoolean(keyUserIDRegistered, false).apply();
        }

        if (!prefs.contains(keySchoolRegistered) || !prefs.getBoolean(keySchoolRegistered, false)){
            if (prefs.contains(keySchoolID)){

                FirebaseMessaging.getInstance().subscribeToTopic("school_"+prefs.getString(keySchoolID, "[none]"));
                Log.d("FCM", "Subscribed to school topic");
                FirebaseMessaging.getInstance().subscribeToTopic("state_"+prefs.getString(keySchoolID, "[none]").substring(0,6));
                Log.d("FCM", "Subscribed to state topic");
                prefs.edit().putBoolean(keySchoolRegistered, true).apply();
                prefs.edit().putBoolean(keyStateRegistered, true).apply();
            }
        }

        if (!prefs.contains(keySchoolRegistered) || !prefs.getBoolean(keySchoolRegistered, false)){
            if (prefs.contains(keyCountryID)){

                FirebaseMessaging.getInstance().subscribeToTopic("country_"+prefs.getString(keyCountryID, "[none]"));
                Log.d("FCM", "Subscribed to country topic");
                prefs.edit().putBoolean(keyCountryRegistered, true).apply();
            }
        }
    }


    private void HomeworkSection(View v){
        RelativeLayout hwSectionTitle = (RelativeLayout) v.findViewById(R.id.hwSectionTitle);
        TextView hwSectionTitleText = (TextView) v.findViewById(R.id.hwSectionTitleText);
        TextView hwSectionTitleButton = (TextView) v.findViewById(R.id.hwSectionTitleButton);

        if(!isHomeworkTomorrow){
            hwSectionTitle.setVisibility(View.GONE);
        } else {
            hwSectionTitleText.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
            hwSectionTitleButton.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
            hwSectionTitleButton.setTextColor(getActivity().getResources().getColor(R.color.color_homework));
            hwSectionTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), HomeworkActivity.class);
                    startActivityForResult(i, 0);
                }
            });
        }
    }

    private void NewsSection(View v){
        TextView newsSectionTitleText = (TextView) v.findViewById(R.id.newsSectionTitleText);
        newsSectionTitleText.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
    }

    private void MySchoolCheckForWebsiteUpdate(View v){
        //TODO "randomly" indicates updated site every now and then
        Button goToMySchoolUpdated = (Button) v.findViewById(R.id.bGoToMySchoolUpdated);
        Button dismissMySchoolUpdated = (Button) v.findViewById(R.id.bDismissMySchoolUpdated);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMySchoolUpdated.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
            goToMySchoolUpdated.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
        } else {
            goToMySchoolUpdated.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
        }

        goToMySchoolUpdated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MySchoolActivity.class);
                startActivityForResult(i, 0);
            }
        });

        dismissMySchoolUpdated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putString("website1codeLastTime", strng).apply();
                mySchoolUpdatedCard.setVisibility(View.GONE);
                if (cvNewFeature.getVisibility() == View.GONE
                        && cvAddedSchools.getVisibility() == View.GONE
                        && mySchoolUpdatedCard.getVisibility() == View.GONE){
                    rlNews.setVisibility(View.GONE);
                } else {
                    rlNews.setVisibility(View.VISIBLE);
                }
            }
        });

        mySchoolUpdatedCard.setVisibility(View.GONE);

        String[][] AllSchoolsURLs = DataStorageHandler.toArray(getActivity(), "schools/AllSchoolsURLs.txt", true);
        String prefKeySchoolID = getActivity().getResources().getString(R.string.pref_key_my_school_school_id);
        int whichWebPage = 1;

        boolean foundURL = false;
        if (prefs.contains(prefKeySchoolID)) {
            String schoolID = prefs.getString(prefKeySchoolID, "[none]");
            if (schoolID != null && !schoolID.equals("[none]")) {
                for (int i = 0; i < AllSchoolsURLs.length; i++) {
                    if (AllSchoolsURLs[i][0].equals(schoolID) && AllSchoolsURLs[i][1].equals(String.valueOf(whichWebPage))) {
                        final String urlString = AllSchoolsURLs[i][2];

                        final MainActivity activityReference = (MainActivity) getActivity();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                URL url;
                                HttpURLConnection urlConnection = null;
                                StringBuilder stringBuilder = null;
                                SharedPreferences sharedprefs = PreferenceManager.getDefaultSharedPreferences(activityReference);
                                try {
                                    url = new URL(urlString);

                                    urlConnection = (HttpURLConnection) url
                                            .openConnection();

                                    InputStream in = urlConnection.getInputStream();

                                    InputStreamReader isw = new InputStreamReader(in);

                                    int data = isw.read();
                                    stringBuilder = new StringBuilder();
                                    while (data != -1) {
                                        char current = (char) data;
                                        data = isw.read();
                                        stringBuilder.append(current);
                                    }

                                    String string = stringBuilder.toString();
                                    strng = string;

                                    System.out.println("strng");
                                    if (sharedprefs.contains("website1codeLastTime")) {

                                        String oldStrng = sharedprefs.getString("website1codeLastTime", "[none]");
                                        int compare = string.compareTo(oldStrng);
                                        if (string.equals(oldStrng) || (-10 <= compare && compare <= 10)) {

                                            System.out.println("EQUALSSSS");

                                            activityReference.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mySchoolUpdatedCard.setVisibility(View.GONE);

                                                    if (cvNewFeature.getVisibility() == View.GONE
                                                            && cvAddedSchools.getVisibility() == View.GONE
                                                            && mySchoolUpdatedCard.getVisibility() == View.GONE) {
                                                        rlNews.setVisibility(View.GONE);
                                                    } else {
                                                        rlNews.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });

                                        } else {
                                            //System.out.println(string);
                                            System.out.println("Does NOTTT equal: "+String.valueOf(compare));
                                            //System.out.println(oldStrng);

                                            activityReference.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mySchoolUpdatedCard.setVisibility(View.VISIBLE);
                                                    rlNews.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (urlConnection != null) {
                                        urlConnection.disconnect();
                                    }
                                }

                            }
                        });

                        thread.start();
                    }
                }
            }
        }

        if (!foundURL){
            mySchoolUpdatedCard.setVisibility(View.GONE);
            if (cvNewFeature.getVisibility() == View.GONE && cvAddedSchools.getVisibility() == View.GONE
                    && mySchoolUpdatedCard.getVisibility() == View.GONE) {
                rlNews.setVisibility(View.GONE);
            } else {
                rlNews.setVisibility(View.VISIBLE);
            }
        }
    }

    private void NowSubject(View v){
        nowRoom = "-";
        nowTeacher = "-";
        nowSubjectAbbrev = " ";

        Calendar calNow = Calendar.getInstance();
        nowHour = calNow.get(Calendar.HOUR_OF_DAY);
        nowMinute = calNow.get(Calendar.MINUTE);
        nowDayOfWeek = calNow.get(Calendar.DAY_OF_WEEK);

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());

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

        String[] times = {p1start, p1end, p2start, p2end, p3start, p3end, p4start, p4end, p5start, p5end, p6start, p6end,
                p7start, p7end, p8start, p8end, p9start, p9end, p10start, p10end, p11start, p11end, p12start, p12end};
        String[] timesStart = {p1start, p2start, p3start, p4start, p5start, p6start, p7start, p8start, p9start, p10start, p11start, p12start};
        String[] timesEnd = {p1end, p2end, p3end, p4end, p5end, p6end, p7end, p8end, p9end, p10end, p11end, p12end};

        int hp1s=0, hp1e=0, hp2s=0, hp2e=0, hp3s=0, hp3e=0, hp4s=0, hp4e=0, hp5s=0, hp5e=0, hp6s=0, hp6e=0, hp7s=0, hp7e=0, hp8s=0, hp8e=0,
                hp9s=0, hp9e=0, hp10s=0, hp10e=0, hp11s=0, hp11e=0, hp12s=0, hp12e=0;
        int mp1s=0, mp1e=0, mp2s=0, mp2e=0, mp3s=0, mp3e=0, mp4s=0, mp4e=0, mp5s=0, mp5e=0, mp6s=0, mp6e=0, mp7s=0, mp7e=0, mp8s=0, mp8e=0,
                mp9s=0, mp9e=0, mp10s=0, mp10e=0, mp11s=0, mp11e=0, mp12s=0, mp12e=0;

        int[] hourInts = {hp1s, hp1e, hp2s, hp2e, hp3s, hp3e, hp4s, hp4e, hp5s, hp5e, hp6s, hp6e, hp7s, hp7e, hp8s, hp8e,
                hp9s, hp9e, hp10s, hp10e, hp11s, hp11e, hp12s, hp12e};
        int[] minuteInts = {mp1s, mp1e, mp2s, mp2e, mp3s, mp3e, mp4s, mp4e, mp5s, mp5e, mp6s, mp6e, mp7s, mp7e, mp8s, mp8e,
                mp9s, mp9e, mp10s, mp10e, mp11s, mp11e, mp12s, mp12e};
        int[] hourIntsStart = {hp1s, hp2s, hp3s, hp4s, hp5s, hp6s, hp7s, hp8s, hp9s, hp10s, hp11s, hp12s};
        int[] minuteIntsStart = {mp1s, mp2s, mp3s, mp4s, mp5s, mp6s, mp7s, mp8s, mp9s, mp10s, mp11s, mp12s};

        int[] periodNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        int[] dayNumbers = {2, 3, 4, 5, 6, 7, 1};
        String[] mDayNames = getActivity().getResources().getStringArray(R.array.DayIDs);

        isLesson = false;

        for (int i = 0; i < timesStart.length; i++) {
            int nowTimeMinutes = nowHour * 60 + nowMinute;

            String[] timeStartSplit = timesStart[i].split(":");
            int hS = Integer.valueOf(timeStartSplit[0]);
            int mS = Integer.valueOf(timeStartSplit[1]);
            int timeSminutes = hS * 60 + mS;

            String[] timeEndSplit = timesEnd[i].split(":");
            int hE = Integer.valueOf(timeEndSplit[0]);
            int mE = Integer.valueOf(timeEndSplit[1]);
            int timeEminutes = hE * 60 + mE;

            if (timeSminutes <= nowTimeMinutes & nowTimeMinutes <= timeEminutes) {
                nowPeriod = periodNumbers[i];
                isLesson = true;
            }
            if (i + 1 < timesStart.length) {
                String[] timeStartNextSplit = timesStart[i + 1].split(":");
                int hSN = Integer.valueOf(timeStartNextSplit[0]);
                int mSN = Integer.valueOf(timeStartNextSplit[1]);
                int timeSNminutes = hSN * 60 + mSN;
                if (timeEminutes <= nowTimeMinutes & nowTimeMinutes <= timeSNminutes){
                    breakNextLesson = periodNumbers[i+1];
                }
            }
        }

        int nowTimeMinutes = nowHour * 60 + nowMinute;

        String[] timeLastPEndSplit = p12end.split(":");
        int hLPE = Integer.valueOf(timeLastPEndSplit[0]);
        int mLPE = Integer.valueOf(timeLastPEndSplit[1]);
        int timeLPEminutes = hLPE * 60 + mLPE;
        if (timeLPEminutes < nowTimeMinutes){
            dayEnd = true;
        }

        String[] timeFirstPStartSplit = p1start.split(":");
        int hFPS = Integer.valueOf(timeFirstPStartSplit[0]);
        int mFPS = Integer.valueOf(timeFirstPStartSplit[1]);
        int timeFPSminutes = hFPS * 60 + mFPS;
        if (timeFPSminutes > nowTimeMinutes){
            beforeDayStart = true;
        }

        for (int i3 = 0; i3 < dayNumbers.length; i3++) {
            if (nowDayOfWeek == dayNumbers[i3]) {
                nowDayInt = i3;
                nowDayName = mDayNames[i3];
            }
        }

        nowSubjectName = ((MainActivity) getActivity()).getSubjectFromPeriod(getActivity(), nowDayName, String.valueOf(nowPeriod));
        if (nowSubjectName.equals("-")) isLesson = false;

        if (isLesson) {

            TextView tvTitle = (TextView) v.findViewById(R.id.CardNowLessonTitle);
            //LinearLayout llTitle = (LinearLayout) v.findViewById(R.id.CardNowLessonHeaderBar);

            tvTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Regular.ttf"));


            String[] subjectInfo = ((MainActivity) getActivity()).getSubjectInfo(getActivity(), nowSubjectName);

            nowTeacher = subjectInfo[2];
            nowSubjectAbbrev = subjectInfo[1];

            if (nowDayName.equals(subjectInfo[4]) && !subjectInfo[9].equals("-") && Integer.valueOf(subjectInfo[9]) <= nowPeriod && !subjectInfo[14].equals("-") && nowPeriod <= Integer.valueOf(subjectInfo[14])) {
                nowRoom = subjectInfo[19];
                nowPeriodTo = subjectInfo[14];
                nowPeriodFrom = subjectInfo[9];
            }
            if (nowDayName.equals(subjectInfo[5]) && !subjectInfo[10].equals("-") && Integer.valueOf(subjectInfo[10]) <= nowPeriod && !subjectInfo[15].equals("-") && nowPeriod <= Integer.valueOf(subjectInfo[15])) {
                nowRoom = subjectInfo[20];
                nowPeriodTo = subjectInfo[15];
                nowPeriodFrom = subjectInfo[10];
            }
            if (nowDayName.equals(subjectInfo[6]) && !subjectInfo[11].equals("-") && Integer.valueOf(subjectInfo[11]) <= nowPeriod && !subjectInfo[16].equals("-") && nowPeriod <= Integer.valueOf(subjectInfo[16])) {
                nowRoom = subjectInfo[21];
                nowPeriodTo = subjectInfo[16];
                nowPeriodFrom = subjectInfo[11];
            }
            if (nowDayName.equals(subjectInfo[7]) && !subjectInfo[12].equals("-") && Integer.valueOf(subjectInfo[12]) <= nowPeriod && !subjectInfo[17].equals("-") && nowPeriod <= Integer.valueOf(subjectInfo[17])) {
                nowRoom = subjectInfo[22];
                nowPeriodTo = subjectInfo[17];
                nowPeriodFrom = subjectInfo[12];
            }
            if (nowDayName.equals(subjectInfo[8]) && !subjectInfo[13].equals("-") && Integer.valueOf(subjectInfo[13]) <= nowPeriod && !subjectInfo[18].equals("-") && nowPeriod <= Integer.valueOf(subjectInfo[18])) {
                nowRoom = subjectInfo[23];
                nowPeriodTo = subjectInfo[18];
                nowPeriodFrom = subjectInfo[13];
            }

            String nowColor = subjectInfo[24];

            int colorInt = 0xffFFFFFF;
            for (int i = 0; i < colorNames.length; i++) {
                if (colorNames[i].equals(nowColor)) {
                    colorInt = colorInts[i];
                }
            }
            /*((GradientDrawable)vNowColor.getBackground()).setColor(colorInt);
            if (nowColor.equals("white")){
                ((GradientDrawable)vNowColor.getBackground()).setStroke(1, 0xff000000);
            }*/

            final float scale = getActivity().getResources().getDisplayMetrics().density;
            int bounds = (int) (56 * scale + 0.5f);

            GradientDrawable gd = new GradientDrawable();
            gd.setShape(1);
            gd.setColor(colorInt);
            gd.setBounds(0, 0, bounds, bounds);
            gd.setSize(bounds, bounds);

            if (colorInt == 0xffFFFFFF){
                gd.setStroke(5, 0xff000000);
            } else {
                gd.setStroke(5, 0x00FFFFFF);
            }
            vNowColor.setBackgroundDrawable(gd);
            //vNowColor.setBackgroundColor(colorInt);

            String nowTextColor = subjectInfo[25];

            int tColorInt = 0xffFFFFFF;
            for (int i = 0; i < colorNames.length; i++) {
                if (colorNames[i].equals(nowTextColor)) {
                    tColorInt = colorInts[i];
                }
            }

            tvNowSubjectName.setText(nowSubjectName.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
            tvNowRoom.setText(nowRoom.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
            tvNowTeacher.setText(nowTeacher.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]",""));
            tvNowSubjectAbbrev.setText(nowSubjectAbbrev.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
            tvNowSubjectAbbrev.setTextColor(tColorInt);
            tvNowSubjectAbbrev.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));

            String periods = getActivity().getResources().getString(R.string.period)+" "+nowPeriodFrom+" - "+nowPeriodTo;
            if (nowPeriodFrom != null && nowPeriodTo != null && nowPeriodFrom.equals(nowPeriodTo))
                periods = getActivity().getResources().getString(R.string.periods)+" "+nowPeriodFrom;
            tvNowPeriods.setText(periods);
            TextView CardNowLessonTitle = (TextView) v.findViewById(R.id.CardNowLessonTitle);
            CardNowLessonTitle.setText(getActivity().getResources().getString(R.string.NowSubject));

            String timeNowFrom = "";
            String timeNowTo = "";
            for (int p = 0; p < periodNumbers.length; p++){
                if (nowPeriodFrom != null && DataStorageHandler.isStringNumeric(nowPeriodFrom) && Integer.valueOf(nowPeriodFrom) == periodNumbers[p]) timeNowFrom = timesStart[p];
                if (nowPeriodTo != null && DataStorageHandler.isStringNumeric(nowPeriodTo) && Integer.valueOf(nowPeriodTo) == periodNumbers[p]) timeNowTo = timesEnd[p];
            }
            tvNowTimes.setText(DataStorageHandler.formatTime(timeNowFrom)+" - "+DataStorageHandler.formatTime(timeNowTo));

            nowLessonCard.setVisibility(View.VISIBLE);

            nowLessonCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putString("caller", "home");
                    args.putString("action", "lesson");
                    args.putInt("dayInt", nowDayInt);
                    args.putInt("periodInt", nowPeriod - 1);

                    Intent i = new Intent(getActivity(), TimetableActivity.class);
                    i.putExtras(args);
                    startActivityForResult(i, 0);
                }
            });

        } else {
            nowLessonCard.setVisibility(View.GONE);
        }
    }

    private void NextSubject(View v){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
        String[] timesStart = {p1start, p2start, p3start, p4start, p5start, p6start, p7start, p8start, p9start, p10start, p11start, p12start};
        String[] timesEnd = {p1end, p2end, p3end, p4end, p5end, p6end, p7end, p8end, p9end, p10end, p11end, p12end};

        final int[] periodNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        isNextLesson = false;
        if (isLesson && nowPeriodTo != null && DataStorageHandler.isStringNumeric(nowPeriodTo) && Integer.valueOf(nowPeriodTo) < 12) {
            nextPeriodSearchStart = Integer.valueOf(nowPeriodTo) + 1;
            isNextLesson = true;
        }
        if (!isLesson && nowPeriod > 0) {
            nextPeriodSearchStart = Integer.valueOf(nowPeriod) + 1;
            isNextLesson = true;
        }
        if (!isLesson && nowPeriod == -1 && beforeDayStart == true) {
            nextPeriodSearchStart = 1;
            isNextLesson = true;
        }
        if (!isLesson && nowPeriod == -1 && breakNextLesson > 0) {
            nextPeriodSearchStart = breakNextLesson;
            isNextLesson = true;
        }
        if (dayEnd) isNextLesson = false;

        nextPeriodStart = 0;
        if (isNextLesson){
            boolean already = false;
            for (int p = nextPeriodSearchStart; p <= 12; p++) {
                String pSName = ((MainActivity) getActivity()).getSubjectFromPeriod(getActivity(), nowDayName, String.valueOf(p));
                if (!already && !pSName.equals("-") && !pSName.equals("") && !pSName.equals(null)) {
                    nextPeriodStart = p;
                    already = true;
                }
            }

            if (0 < nextPeriodStart && nextPeriodStart <= 12) {
                nextSubjectName = ((MainActivity) getActivity()).getSubjectFromPeriod(getActivity(), nowDayName, String.valueOf(nextPeriodStart));

                String[] subjectInfo = ((MainActivity) getActivity()).getSubjectInfo(getActivity(), nextSubjectName);

                nextTeacher = subjectInfo[2];
                String nextSubjectAbbrev = subjectInfo[1];

                String nextPeriodFrom = "";

                if (nowDayName.equals(subjectInfo[4]) && !subjectInfo[9].equals("-") && Integer.valueOf(subjectInfo[9]) <= nextPeriodStart && !subjectInfo[14].equals("-") && nextPeriodStart <= Integer.valueOf(subjectInfo[14])) {
                    nextRoom = subjectInfo[19];
                    nextPeriodTo = subjectInfo[14];
                    nextPeriodFrom = subjectInfo[9];
                }
                if (nowDayName.equals(subjectInfo[5]) && !subjectInfo[10].equals("-") && Integer.valueOf(subjectInfo[10]) <= nextPeriodStart && !subjectInfo[15].equals("-") && nextPeriodStart <= Integer.valueOf(subjectInfo[15])) {
                    nextRoom = subjectInfo[20];
                    nextPeriodTo = subjectInfo[15];
                    nextPeriodFrom = subjectInfo[10];
                }
                if (nowDayName.equals(subjectInfo[6]) && !subjectInfo[11].equals("-") && Integer.valueOf(subjectInfo[11]) <= nextPeriodStart && !subjectInfo[16].equals("-") && nextPeriodStart <= Integer.valueOf(subjectInfo[16])) {
                    nextRoom = subjectInfo[21];
                    nextPeriodTo = subjectInfo[16];
                    nextPeriodFrom = subjectInfo[11];
                }
                if (nowDayName.equals(subjectInfo[7]) && !subjectInfo[12].equals("-") && Integer.valueOf(subjectInfo[12]) <= nextPeriodStart && !subjectInfo[17].equals("-") && !subjectInfo[17].equals("-") && nextPeriodStart <= Integer.valueOf(subjectInfo[17])) {
                    nextRoom = subjectInfo[22];
                    nextPeriodTo = subjectInfo[17];
                    nextPeriodFrom = subjectInfo[12];
                }
                if (nowDayName.equals(subjectInfo[8]) && !subjectInfo[13].equals("-") && Integer.valueOf(subjectInfo[13]) <= nextPeriodStart && !subjectInfo[18].equals("-") && nextPeriodStart <= Integer.valueOf(subjectInfo[18])) {
                    nextRoom = subjectInfo[23];
                    nextPeriodTo = subjectInfo[18];
                    nextPeriodFrom = subjectInfo[13];
                }

                String nextColor = subjectInfo[24];

                int colorInt = 0xffFFFFFF;
                for (int i = 0; i < colorNames.length; i++) {
                    if (colorNames[i].equals(nextColor)) {
                        colorInt = colorInts[i];
                    }
                }

                final float scale = getActivity().getResources().getDisplayMetrics().density;
                int bounds = (int) (56 * scale + 0.5f);

                GradientDrawable gd = new GradientDrawable();
                gd.setShape(1);
                gd.setColor(colorInt);
                gd.setBounds(0, 0, bounds, bounds);
                gd.setSize(bounds, bounds);

                if (colorInt == 0xffFFFFFF){
                    gd.setStroke(5, 0xff000000);
                } else {
                    gd.setStroke(5, 0x00FFFFFF);
                }
                vNextColor.setBackgroundDrawable(gd);
                //vNextColor.setBackgroundColor(colorInt);

                String nextTextColor = subjectInfo[25];

                int tColorInt = 0xffFFFFFF;
                for (int i = 0; i < colorNames.length; i++) {
                    if (colorNames[i].equals(nextTextColor)) {
                        tColorInt = colorInts[i];
                    }
                }

                tvNextSubjectName.setText(nextSubjectName.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
                tvNextRoom.setText(nextRoom.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
                tvNextTeacher.setText(nextTeacher.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ",").replace("[null]", ""));
                tvNextSubjectAbbrev.setText(nextSubjectAbbrev.replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
                tvNextSubjectAbbrev.setTextColor(tColorInt);
                tvNextSubjectAbbrev.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));

                String periods = getActivity().getResources().getString(R.string.period)+" "+nextPeriodFrom+" - "+nextPeriodTo;
                if (nextPeriodFrom != null && nextPeriodTo != null &&
                        nextPeriodFrom.equals(nextPeriodTo)) periods = getActivity().getResources().getString(R.string.periods)+" "+nextPeriodFrom;
                tvNextPeriods.setText(periods);
                TextView CardNextLessonTitle = (TextView) v.findViewById(R.id.CardNextLessonTitle);
                CardNextLessonTitle.setText(getActivity().getResources().getString(R.string.NextSubject));

                String timeNextFrom = "";
                String timeNextTo = "";
                for (int p = 0; p < periodNumbers.length; p++){
                    if (nextPeriodFrom != null && DataStorageHandler.isStringNumeric(nextPeriodFrom) && Integer.valueOf(nextPeriodFrom) == periodNumbers[p]) timeNextFrom = timesStart[p];
                    if (nextPeriodTo != null && DataStorageHandler.isStringNumeric(nextPeriodTo) && Integer.valueOf(nextPeriodTo) == periodNumbers[p]) timeNextTo = timesEnd[p];
                }
                tvNextTimes.setText(DataStorageHandler.formatTime(timeNextFrom)+" - "+DataStorageHandler.formatTime(timeNextTo));

            } else {
                isNextLesson = false;
            }
        } else {
            isNextLesson = false;
        }

        if (isNextLesson){
            nextLessonCard.setVisibility(View.VISIBLE);

            nextLessonCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putString("caller", "home");
                    args.putString("action", "lesson");
                    args.putInt("dayInt", nowDayInt);
                    args.putInt("periodInt", nextPeriodStart-1);

                    Intent i = new Intent(getActivity(), TimetableActivity.class);
                    i.putExtras(args);
                    startActivityForResult(i, 0);
                }
            });
        } else {
            nextLessonCard.setVisibility(View.GONE);
        }
    }

    private void hideNextLessonCard(View v){
        //nextLessonCard.setVisibility(View.GONE);
    }

    private void TomorrowSubjects(View v){
        if (isLesson || isNextLesson){
            tomorrowLessonsCard.setVisibility(View.GONE);
        } else {
            tomorrowLessonsCard.setVisibility(View.VISIBLE);

            String[] dayNames = getActivity().getResources().getStringArray(R.array.DayIDs);
            int tomorrowDayInt = 0;
            String tomorrowDayName = "-";
            for (int j = 0; j < dayNames.length; j++){
                if (dayNames[j].equals(nowDayName)){
                    if (j+1 < dayNames.length) {
                        tomorrowDayInt = j + 1;
                    } else {
                        tomorrowDayInt = 0;
                    }
                }
            }
            tomorrowDayName = dayNames[tomorrowDayInt];

            LinearLayout bRow1 = (LinearLayout) v.findViewById(R.id.buttonRow1);
            LinearLayout bRow2 = (LinearLayout) v.findViewById(R.id.buttonRow2);
            LinearLayout bRow3 = (LinearLayout) v.findViewById(R.id.buttonRow3);

            bRow1.setVisibility(View.VISIBLE);
            bRow2.setVisibility(View.VISIBLE);
            bRow3.setVisibility(View.VISIBLE);

            String[][] tomorrowSubjects = ((MainActivity) getActivity()).ADaysSubjects(getActivity(), tomorrowDayName);
            bTS1 = (Button) v.findViewById(R.id.button1);
            bTS2 = (Button) v.findViewById(R.id.button2);
            bTS3 = (Button) v.findViewById(R.id.button3);
            bTS4 = (Button) v.findViewById(R.id.button4);
            bTS5 = (Button) v.findViewById(R.id.button5);
            bTS6 = (Button) v.findViewById(R.id.button6);
            bTS7 = (Button) v.findViewById(R.id.button7);
            bTS8 = (Button) v.findViewById(R.id.button8);
            bTS9 = (Button) v.findViewById(R.id.button9);
            bTS10 = (Button) v.findViewById(R.id.button10);
            bTS11 = (Button) v.findViewById(R.id.button11);
            bTS12 = (Button) v.findViewById(R.id.button12);
            bTS13 = (Button) v.findViewById(R.id.button13);
            bTS14 = (Button) v.findViewById(R.id.button14);
            bTS15 = (Button) v.findViewById(R.id.button15);
            Button[] tSButtons = {bTS1, bTS2, bTS3, bTS4, bTS5, bTS6, bTS7, bTS8, bTS9, bTS10, bTS11, bTS12, bTS13, bTS14, bTS15};

            for (Button b : tSButtons) b.setVisibility(View.VISIBLE);


            boolean[] buttonUsed = new boolean[15];

            for (int i = 0; i < tomorrowSubjects.length; i++){
                int colorInt = 0xffFFFFFF;
                for (int i2 = 0; i2 < colorNames.length; i2++){
                    if (colorNames[i2].equals(tomorrowSubjects[i][2])){
                        colorInt = colorInts[i2];
                    }
                }

                int textColorInt = 0xff000000;
                for (int i3 = 0; i3 < colorNames.length; i3++){
                    if (colorNames[i3].equals(tomorrowSubjects[i][3])){
                        textColorInt = colorInts[i3];
                    }
                }

                final float scale = getActivity().getResources().getDisplayMetrics().density;
                int bounds = (int) (56 * scale + 0.5f);

                GradientDrawable gd = new GradientDrawable();
                gd.setShape(1);
                gd.setColor(colorInt);
                gd.setBounds(0, 0, bounds, bounds);
                gd.setSize(bounds, bounds);

                if (colorInt == 0xffFFFFFF){
                    gd.setStroke(8, 0xff000000);
                }
                tSButtons[i].setBackgroundDrawable(gd);

                tSButtons[i].setText(tomorrowSubjects[i][1].replace("[newline]", System.getProperty("line.separator")).replace("[comma]", ","));
                tSButtons[i].setTextColor(textColorInt);
                tSButtons[i].setTextSize(14);
                tSButtons[i].setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));

                final int finalPeriodStartInt = Integer.parseInt(tomorrowSubjects[i][4]) - 1;
                final int finalTomorrowDayInt = tomorrowDayInt;
                tSButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle extras = new Bundle();
                        extras.putString("caller", "home");
                        extras.putString("action", "lesson");
                        extras.putInt("dayInt", finalTomorrowDayInt);
                        extras.putInt("periodInt", finalPeriodStartInt);
                        Intent i = new Intent(getActivity(), TimetableActivity.class);
                        i.putExtras(extras);
                        startActivityForResult(i, 0);
                    }
                });

                buttonUsed[i] = true;
            }

            if (tomorrowSubjects.length <= 0){
                tomorrowLessonsCard.setVisibility(View.GONE);
                isTomorrowSubjects = false;
            } else {
                if (tomorrowSubjects.length < 11) bRow3.setVisibility(View.GONE);
                if (tomorrowSubjects.length < 6) bRow2.setVisibility(View.GONE);

                for (int i25 = 0; i25 < buttonUsed.length; i25++){
                    if (!buttonUsed[i25]) tSButtons[i25].setVisibility(View.INVISIBLE);
                }

                TextView tvTitle = (TextView) v.findViewById(R.id.CardTomorrowLessonsTitle);
                //LinearLayout llTitle = (LinearLayout) v.findViewById(R.id.CardTomorrowLessonsHeaderBar);

                tvTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Regular.ttf"));
                /*llTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getActivity(), TimetableActivity.class);
                        startActivityForResult(i, 0);
                    }
                });*/


                isTomorrowSubjects = true;
            }
        }
    }

    /*private void FloatingActionButton(View v){
        final FloatingActionButton fabPlus = (FloatingActionButton) v.findViewById(R.id.fab);
        final View overlay = v.findViewById(R.id.overlay);
        final FloatingActionButton fabHomework = (FloatingActionButton) v.findViewById(R.id.fabHomework);
        final TextView tvFabHomework = (TextView) v.findViewById(R.id.fabHomeworkTextView);

        final float fabMargin = getActivity().getResources().getDimension(R.dimen.fab_margin);
        final float fabNormalSize = getActivity().getResources().getDimension(R.dimen.fab_normal_size);
        final float fabMiniSize = getActivity().getResources().getDimension(R.dimen.fab_mini_size);
        final float oldY = -1f * (fabMargin + fabNormalSize);

        fabHomework.setX(-1f * (fabMargin + ((fabNormalSize - fabMiniSize) / 2f)));
        fabHomework.setY(oldY);
        tvFabHomework.setX(-1f * ((2f * fabMargin) + fabNormalSize + tvFabHomework.getWidth()));
        tvFabHomework.setY(oldY);
        tvFabHomework.setTypeface(Typeface.createFromAsset(getActivity().getResources().getAssets(), "Roboto-Medium.ttf"));

        overlay.setClickable(false);

        final boolean[] fabMenu = {true, false}; //available = true; open = false

        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabMenu[0] && !fabMenu[1]){
                    fabMenu[0] = false;
                    fabMenu[1] = true;
                    fabPlus.setClickable(false);
                    overlay.setClickable(false);

                    overlay.setVisibility(View.VISIBLE);
                    overlay.animate().alpha(0.80f).setDuration(250);
                    fabPlus.animate().rotationBy(135).setDuration(125).setStartDelay(125);
                    fabHomework.setVisibility(View.VISIBLE);
                    tvFabHomework.setVisibility(View.VISIBLE);
                    fabHomework.animate().alpha(1f).translationYBy(-1f * fabMargin).setDuration(125).setStartDelay(125);
                    tvFabHomework.animate().alpha(1f).translationYBy(-1f * fabMargin).setDuration(125).setStartDelay(125);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fabMenu[0] = true;
                            fabPlus.setClickable(true);
                            overlay.setClickable(true);
                        }
                    }, 500);
                } else if (fabMenu[0] && fabMenu[1]){
                    fabMenu[0] = false;
                    fabMenu[1] = false;
                    fabPlus.setClickable(false);
                    overlay.setClickable(false);

                    overlay.animate().alpha(0f).setDuration(250);
                    fabHomework.setVisibility(View.VISIBLE);
                    tvFabHomework.setVisibility(View.VISIBLE);
                    fabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
                    tvFabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
                    fabPlus.animate().rotationBy(-135).setDuration(250).setStartDelay(125);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fabMenu[0] = true;
                            fabPlus.setClickable(true);
                            overlay.setVisibility(View.GONE);
                        }
                    }, 500);
                }
            }
        });

        fabHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabMenu[0] && fabMenu[1]) {
                    fabMenu[0] = false;
                    fabMenu[1] = false;

                    ((MainActivity) getActivity()).fabHomeworkClicked();

                    overlay.animate().alpha(0f).setDuration(250);
                    fabHomework.setVisibility(View.VISIBLE);
                    tvFabHomework.setVisibility(View.VISIBLE);
                    fabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
                    tvFabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
                    fabPlus.animate().rotationBy(-135).setDuration(250).setStartDelay(125);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fabMenu[0] = true;
                            fabPlus.setClickable(true);
                            overlay.setVisibility(View.GONE);
                        }
                    }, 500);
                }
            }
        });

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabMenu[0] && fabMenu[1]){
                    fabMenu[0] = false;
                    fabMenu[1] = false;
                    fabPlus.setClickable(false);
                    overlay.setClickable(false);

                    overlay.animate().alpha(0f).setDuration(250);
                    fabHomework.setVisibility(View.VISIBLE);
                    tvFabHomework.setVisibility(View.VISIBLE);
                    fabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
                    tvFabHomework.animate().alpha(0f).translationYBy(fabMargin).setDuration(125).setStartDelay(125);
                    fabPlus.animate().rotationBy(-135).setDuration(250).setStartDelay(125);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fabMenu[0] = true;
                            fabPlus.setClickable(true);
                            overlay.setVisibility(View.GONE);
                        }
                    }, 500);
                }
            }
        });
    }*/

    private void HomeworkCard(View v){
        CardView homeworkCard = (CardView) v.findViewById(R.id.CardHomework);
        //LinearLayout homeworkCardHeaderBar = (LinearLayout) v.findViewById(R.id.CardHomeworkHeaderBar);
        //Button homeworkCardButton = (Button) v.findViewById(R.id.CardHomeworkViewAllButton);
        //TextView homeworkCardTitle = (TextView) v.findViewById(R.id.CardHomeworkTitle);
        TextView homeworkCardDueTomorrow = (TextView) v.findViewById(R.id.CardHomeworkDueTomorrow);

        SimpleDateFormat form = new SimpleDateFormat(getActivity().getResources().getString(R.string.date_formatter_general));
        SimpleDateFormat formatter = new SimpleDateFormat(getActivity().getResources().getString(R.string.date_formatter_local));

        String[][] subjectsArray = ((MainActivity) getActivity()).toArray(getActivity(), "Subjects.txt");
        int[] sNumberRowsCols = ((MainActivity) getActivity()).nmbrRowsCols(getActivity(), "Subjects.txt");
        String[][] hwArray = ((MainActivity) getActivity()).toArray(getActivity(), "Homework.txt");
        int[] numberRowsCols = ((MainActivity) getActivity()).nmbrRowsCols(getActivity(), "Homework.txt");
        String[] ID = new String[numberRowsCols[0]];
        String[] subjectNames = new String[numberRowsCols[0]];
        String[] date = new String[numberRowsCols[0]];
        String[] title = new String[numberRowsCols[0]];
        String[] content = new String[numberRowsCols[0]];
        String[] done = new String[numberRowsCols[0]];
        String[] subjectAbbrev = new String[numberRowsCols[0]];
        String[] subjectBgColors = new String[numberRowsCols[0]];
        String[] subjectTextColors = new String[numberRowsCols[0]];
        int[] colorInt = new int[numberRowsCols[0]];
        int[] textColorInt = new int[numberRowsCols[0]];
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

        GradientDrawable[] icons = new GradientDrawable[subjectBgColors.length];
        final float scale = getResources().getDisplayMetrics().density;
        int bounds = (int) (40 * scale + 0.5f);

        for (int i2 = 0; i2 < subjectBgColors.length; i2++){
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(1);
            gd.setBounds(0, 0, bounds, bounds);
            gd.setSize(bounds, bounds);
            gd.setColor(colorInt[i2]);
            gd.mutate();
            icons[i2] = gd;
        }

        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        cal.set(Calendar.DAY_OF_YEAR, today + 1);
        String tomorrow = form.format(cal.getTime());

        boolean[] yesOrNo = new boolean[date.length];
        int howMany = 0;
        for (int i = 0; i < date.length; i++){
            if (hwArray[i][2].equals(tomorrow)){
                yesOrNo[i] = true;
                howMany = howMany + 1;
            } else {
                yesOrNo[i] = false;
            }
        }
        if (howMany <= 0){
            homeworkCard.setVisibility(View.GONE);
            isHomeworkTomorrow = false;
        } else {
            /*homeworkCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), HomeworkActivity.class);
                    startActivityForResult(i, 0);
                }
            });*/
            //homeworkCardTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Light.ttf"));
            homeworkCardDueTomorrow.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));

            GradientDrawable nIcons[] = new GradientDrawable[howMany];
            String[] nID = new String[howMany];
            String[] nSAbbrev = new String[howMany];
            String[] nDate = new String[howMany];
            String[] nTitle = new String[howMany];
            String[] nContent = new String[howMany];
            String[] nDone = new String[howMany];
            int[] nTextColorInt = new int[howMany];
            int nextPosition = 0;
            for (int n = 0; n < date.length; n++) {
                if (yesOrNo[n]) {
                    nIcons[nextPosition] = icons[n];
                    nID[nextPosition] = ID[n];
                    nSAbbrev[nextPosition] = subjectAbbrev[n];
                    nDate[nextPosition] = date[n];
                    nTitle[nextPosition] = title[n];
                    nContent[nextPosition] = content[n];
                    nDone[nextPosition] = done[n];
                    nTextColorInt[nextPosition] = textColorInt[n];

                    nextPosition = nextPosition + 1;
                }
            }
            icons = nIcons;
            ID = nID;
            subjectAbbrev = nSAbbrev;
            date = nDate;
            title = nTitle;
            content = nContent;
            done = nDone;
            textColorInt = nTextColorInt;

            int verticalSpacing = (int) (getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_verticalSpacing));
            int listPaddingTop = (int) (getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_listPadding_top));
            int listPaddingBottom = (int) (getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_listPadding_bottom));
            int dividerPaddingLeft = (int) (getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_divider_paddingLeft));
            int dividerPaddingRight = (int) (getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_divider_paddingRight));
            int adapterItemSize = (int) getActivity().getResources().getDimension(R.dimen.list_1LineAvatarIcon_tile_height);

            List<InformationHomeworkFull> myData = getData(ID, subjectNames, subjectAbbrev, date, title, content, done, subjectBgColors, textColorInt, icons);
            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewHomeworkSmall);
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), dividerPaddingLeft, dividerPaddingRight));
            recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(verticalSpacing, listPaddingTop, listPaddingBottom));
            adapter = new HomeworkAdapter(getActivity(), myData, "hwSmall");
            int viewHeight = (adapter.getItemCount() * (listPaddingTop + listPaddingBottom + adapterItemSize + verticalSpacing + getActivity().obtainStyledAttributes(new int[]{android.R.attr.listDivider}).getDrawable(0).getIntrinsicHeight()));
            recyclerView.getLayoutParams().height = viewHeight;
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(((MainActivity)getActivity())));

            isHomeworkTomorrow = true;
        }
    }

    public static List<InformationHomeworkFull> getData(String[] ID, String[] subjectNames, String[] subjectAbbrev, String[] date, String[] title, String[] content, String[] done, String[] subjectBgColors, int[] textColorInt, GradientDrawable[] icons){
        List<InformationHomeworkFull> data = new ArrayList<>();

        //int[] icons = {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher};
        for (int i = 0; i < ID.length; i++){
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

    private void CreateTtCard(View v){
        if (prefs.contains("CreateTimetableCardDismissed")) {
            if (prefs.getBoolean("CreateTimetableCardDismissed", false)) {
                isCreateTimetable = false;
            } else {
                isCreateTimetable = true;
            }
        } else {
            isCreateTimetable = true;
        }

        if (isCreateTimetable) {
            int rows = ((MainActivity) getActivity()).nmbrRowsCols(getActivity(), "Periods.txt")[0];
            int cols = ((MainActivity) getActivity()).nmbrRowsCols(getActivity(), "Periods.txt")[1];

            if (rows > 0 && cols > 0) {
                createTtCard.setVisibility(View.GONE);
                isCreateTimetable = false;
            } else {
                isCreateTimetable = true;
                createTtCard.setVisibility(View.VISIBLE);
            /*
            createTtCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), NewTimetableActivity.class);
                    startActivityForResult(i, 0);
                }
            });*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    createTtCardButton.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
                    createTtCardButton.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
                } else {
                    createTtCardButton.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
                }
                createTtCardButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), NewTimetableActivity.class);
                        startActivityForResult(i, 0);
                    }
                });

                Button createTtCardDismiss = (Button) v.findViewById(R.id.CardCreateTtDismissButton);
                createTtCardDismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prefs.edit().putBoolean("CreateTimetableCardDismissed", true).apply();
                        createTtCard.setVisibility(View.GONE);
                        isCreateTimetable = false;
                        if (!isLesson && !isNextLesson && !isTomorrowSubjects && !isCreateTimetable) {
                            ttSectionTitle.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }

    private void newFeatureCard(View v){
        Button bNewFeatureDismiss = (Button) v.findViewById(R.id.bDismissNewFeature);
        Button bNewFeatureGoTo = (Button) v.findViewById(R.id.bGoToNewFeature);
        Button bAddedSchoolsDismiss = (Button) v.findViewById(R.id.bDismissAddedSchools);
        Button bAddedSchoolsGoTo = (Button) v.findViewById(R.id.bAddedSchoolsGoTo);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        dismissedNewFeatureMySchool = false;
        dismissedAddedNewSchoolsInfo = false;

        final String dismissedNewFeatureMySchoolKey = "dismissed_new_feature_my_school";
        final String dismissedAddedNewSchoolsInfoKey = "dismissed_added_new_schools_info";

        if (prefs.contains(dismissedNewFeatureMySchoolKey))
            dismissedNewFeatureMySchool = prefs.getBoolean(dismissedNewFeatureMySchoolKey, false);
        if (prefs.contains(dismissedAddedNewSchoolsInfoKey))
            dismissedAddedNewSchoolsInfo = prefs.getBoolean(dismissedAddedNewSchoolsInfoKey, false);

        if (dismissedNewFeatureMySchool) cvNewFeature.setVisibility(View.GONE);
        if (dismissedAddedNewSchoolsInfo) cvAddedSchools.setVisibility(View.GONE);
        if (cvNewFeature.getVisibility() == View.GONE && cvAddedSchools.getVisibility() == View.GONE
                && mySchoolUpdatedCard.getVisibility() == View.GONE){
            rlNews.setVisibility(View.GONE);
        } else {
            rlNews.setVisibility(View.VISIBLE);
        }

        bNewFeatureDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvNewFeature.setVisibility(View.GONE);
                dismissedNewFeatureMySchool = true;
                if (cvNewFeature.getVisibility() == View.GONE && cvAddedSchools.getVisibility() == View.GONE
                        && mySchoolUpdatedCard.getVisibility() == View.GONE){
                    rlNews.setVisibility(View.GONE);
                } else {
                    rlNews.setVisibility(View.VISIBLE);
                }

                prefs.edit().putBoolean(dismissedNewFeatureMySchoolKey, true).apply();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bNewFeatureGoTo.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
            bNewFeatureGoTo.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
        } else {
            bNewFeatureGoTo.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
        }

        bNewFeatureGoTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MySchoolActivity.class);
                startActivityForResult(i, 0);
            }
        });

        bAddedSchoolsDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvAddedSchools.setVisibility(View.GONE);
                dismissedAddedNewSchoolsInfo = true;
                if (cvNewFeature.getVisibility() == View.GONE && cvAddedSchools.getVisibility() == View.GONE
                        && mySchoolUpdatedCard.getVisibility() == View.GONE){
                    rlNews.setVisibility(View.GONE);
                } else {
                    rlNews.setVisibility(View.VISIBLE);
                }

                prefs.edit().putBoolean(dismissedAddedNewSchoolsInfoKey, true).apply();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bAddedSchoolsGoTo.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
            bAddedSchoolsGoTo.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
        } else {
            bAddedSchoolsGoTo.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
        }

        bAddedSchoolsGoTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MySchoolSettingsActivity.class);
                startActivityForResult(i, 0);
            }
        });
    }

    private void shareAppCard(View v){
        //TODO show Card again and use counter to reset mUserClickedDontShare.
        shareAppCard.setVisibility(View.GONE);
        /*TextView tvTitle = (TextView) v.findViewById(R.id.CardNowLessonTitle);
        LinearLayout llTitle = (LinearLayout) v.findViewById(R.id.CardNextLessonHeaderBar);

        tvTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Light.ttf"));
        llTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).shareApp();
            }
        });

        shareAppCard.setVisibility(View.VISIBLE);
        mUserClickedDontShare = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_CLICKED_DONT_SHARE, "false"));

        if (!mUserClickedDontShare) {
            shareAppCardShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).shareApp();
                }
            });
            shareAppCardDontShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveToPreferences(getActivity(), KEY_USER_CLICKED_DONT_SHARE, "true");
                }
            });
        } else {
            shareAppCard.setVisibility(View.GONE);
        }*/
    }

    private void storeSchoolInDatabase(){
        Log.d("FBDB", "FBDatabaseSchoolNrUsersStored: " + String.valueOf(prefs.getBoolean("FBDatabaseSchoolNrUsersStored", false)));
        final String prefKeySchoolID = getActivity().getResources().getString(R.string.pref_key_my_school_school_id);

        if (prefs.contains(prefKeySchoolID)) {
            final String schoolID = prefs.getString(prefKeySchoolID, "[none]");

            if (schoolID != null && !schoolID.equals("[none]")){
                if (!prefs.contains("FBDatabaseSchoolNrUsersStored") || !prefs.getBoolean("FBDatabaseSchoolNrUsersStored", false)) {

                    transactionsNrUsersNew(0, 10, schoolID);

                    final String prefKeyUserID = getActivity().getResources().getString(R.string.pref_key_user_id);

                    if (prefs.contains(prefKeyUserID)) {
                        final String userID = prefs.getString(getActivity().getResources().getString(R.string.pref_key_user_id), "[none]");

                        if (userID != null && !userID.equals("[none]")) {
                            if (!prefs.contains("FBDatabaseSchoolUserIDStored") || !prefs.getBoolean("FBDatabaseSchoolUserIDStored", false)) {

                                transactionsUserIDNew(0, 10, schoolID, userID);
                            }
                        }
                    }
                }
            }
        }
    }

    private void transactionsNrUsersNew(final int tries, final int maxTries, final String school){
        if (tries <= maxTries && school != null && !school.equals("[none]")) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Schools NrUsers/"+school);

            myRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    final Object obj1 = mutableData.getValue();
                    System.out.println("old: "+String.valueOf(obj1));

                    if (obj1 != null) {
                        final long l1 = (long) obj1;
                        mutableData.setValue(l1 + 1);
                        System.out.println("new: "+String.valueOf(l1+1));
                    } else {
                        mutableData.setValue(1);
                        System.out.println("new: created (1)");
                    }

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (databaseError != null){
                        Log.d("FBDB", "transactionsNrUsersNew:onComplete:Error: " + String.valueOf(tries) + databaseError);
                        prefs.edit().putBoolean("FBDatabaseSchoolNrUsersStored", false).apply();
                        transactionsNrUsersNew(tries+1, maxTries, school);
                    } else {
                        // Transaction completed
                        Log.d("FBDB", "transactionsNrUsersNew:onComplete: " + String.valueOf(tries) + databaseError);
                        prefs.edit().putBoolean("FBDatabaseSchoolNrUsersStored", true).apply();
                    }
                }
            });
        }
    }

    private void transactionsUserIDNew(final int tries, final int maxTries, final String school, final String user){
        if (tries <= maxTries && school != null && !school.equals("[none]")
                && user != null && !user.equals("[none]")) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Schools Users/"+school+"/"+user);

            myRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    mutableData.setValue(true);

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (databaseError != null){
                        Log.d("FBDB", "transactionsUserIDNew:onComplete:Error: " + String.valueOf(tries) + databaseError);
                        prefs.edit().putBoolean("FBDatabaseSchoolUserIDStored", false).apply();
                        transactionsUserIDNew(tries+1, maxTries, school, user);
                    } else {
                        // Transaction completed
                        Log.d("FBDB", "transactionsUserIDNew:onComplete: " + String.valueOf(tries) + databaseError);
                        prefs.edit().putBoolean("FBDatabaseSchoolUserIDStored", true).apply();
                    }
                }
            });
        }
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

}