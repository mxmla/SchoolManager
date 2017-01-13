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
import android.net.Uri;
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
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
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
import java.util.GregorianCalendar;
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
            tvNextSubjectName, tvNextRoom, tvNextTeacher,
            tvSchoolChallengeSubtitle, tvScoolChallengeCounter;
    private View vNowColor, vNextColor;
    private CardView nowLessonCard, nextLessonCard, tomorrowLessonsCard, createTtCard, shareAppCard,
            cvNewFeature, cvAddedSchools, mySchoolUpdatedCard, cSchoolChallenge, cAppUpdate, cvNewFeatureABWeeks;
    RelativeLayout rlNews, ttSectionTitle;
    private LinearLayout llSchoolChallengeCounter;
    private View vSchoolChallengeCounter;
    private Button createTtCardButton, shareAppCardShareButton, shareAppCardDontShareButton,
            bRefresh;
    private Button bTS1, bTS2, bTS3, bTS4, bTS5, bTS6, bTS7, bTS8, bTS9, bTS10, bTS11, bTS12,
            bTS13, bTS14, bTS15, bSchoolChallengeInviteFriends, bAppUpdate;
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
    private String ttFolder, timetable_filepath;
    private String[] allABs;

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

        ttFolder = prefs.getString(getActivity().getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        allABs = DataStorageHandler.AllABs(getActivity(), ttFolder);
        timetable_filepath = ttFolder + "/" + getActivity().getResources().getString(R.string.file_name_timetable);

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
        cSchoolChallenge = (CardView) v.findViewById(R.id.SchoolChallengeCard);
        tvSchoolChallengeSubtitle = (TextView) v.findViewById(R.id.CardSchoolChallengeSubtitle);
        tvScoolChallengeCounter = (TextView) v.findViewById(R.id.CardSchoolChallengeCounter);
        llSchoolChallengeCounter = (LinearLayout) v.findViewById(R.id.CardSchoolChallengeLLCounter);
        vSchoolChallengeCounter = (View) v.findViewById(R.id.CardSchoolChallengeCounterDividerView);
        bSchoolChallengeInviteFriends = (Button) v.findViewById(R.id.bSchoolChallengeInviteFriends);
        cAppUpdate = (CardView) v.findViewById(R.id.AppUpdateCard);
        bAppUpdate = (Button) v.findViewById(R.id.bGoToUpdateApp);
        cvNewFeatureABWeeks = (CardView) v.findViewById(R.id.IntroducingABWeeksCard);
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
        MySchoolCheckForWebsiteUpdate(v);
        storeSchoolInDatabase();
        schoolChallengeCard(v);
        appUpdateCard();
        newFeatureABWeeksCard(v);
        reviewAppCards(v);

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

    private void NewsSectionShowHide(){
        if (cvNewFeature.getVisibility() == View.GONE
                && cvAddedSchools.getVisibility() == View.GONE
                && mySchoolUpdatedCard.getVisibility() == View.GONE
                && cSchoolChallenge.getVisibility() == View.GONE
                && cAppUpdate.getVisibility() == View.GONE
                && cvNewFeatureABWeeks.getVisibility() == View.GONE){
            rlNews.setVisibility(View.GONE);
        } else {
            rlNews.setVisibility(View.VISIBLE);
        }
    }

    private void MySchoolCheckForWebsiteUpdate(View v){
        //TODO "randomly" indicates updated site every now and then
        if (prefs.contains("MySchoolChallenge-c1-20160818-complete") && prefs.getBoolean("MySchoolChallenge-c1-20160818-complete", false)) {
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
                    NewsSectionShowHide();
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
                                    StringBuilder newsBuilder = null;
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

                                            //if (current )
                                        }

                                        String string = stringBuilder.toString();
                                        strng = string;

                                        System.out.println("strng");
                                        if (sharedprefs.contains("website1codeLastTime")) {

                                            String oldStrng = sharedprefs.getString("website1codeLastTime", "[none]");
                                            int compare = string.compareTo(oldStrng);
                                            if (string.equals(oldStrng) || (-100 <= compare && compare <= 100)) {

                                                System.out.println("EQUALSSSS");

                                                activityReference.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mySchoolUpdatedCard.setVisibility(View.GONE);

                                                        NewsSectionShowHide();
                                                    }
                                                });

                                            } else {
                                                //System.out.println(string);
                                                System.out.println("Does NOTTT equal: " + String.valueOf(compare));
                                                //System.out.println(oldStrng);

                                                activityReference.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mySchoolUpdatedCard.setVisibility(View.VISIBLE);
                                                        rlNews.setVisibility(View.VISIBLE);
                                                    }
                                                });
                                            }
                                        } else {
                                            sharedprefs.edit().putString("website1codeLastTime", strng).apply();
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

            if (!foundURL) {
                mySchoolUpdatedCard.setVisibility(View.GONE);
                NewsSectionShowHide();
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
        final int nowTimeMinutes = nowHour * 60 + nowMinute;

        int[] periodNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        int[] dayNumbers = {2, 3, 4, 5, 6, 7, 1};
        String[] mDayNames = getActivity().getResources().getStringArray(R.array.DayIDs);
        int dayNow = 0;
        for (int d = 0; d < dayNumbers.length; d++){
            if (dayNumbers[d] == nowDayOfWeek) dayNow = d;
        }

        final int nowABint = DataStorageHandler.getCurrentAB(getActivity(), ttFolder, calNow);
        final String nowAB = (nowABint < allABs.length) ? allABs[nowABint] : allABs[0];
        final String nowTtFilepath = timetable_filepath + "_" + nowAB + ".txt";
        final String[][] tt_array = DataStorageHandler.TimetableLessons(getActivity(), ttFolder, nowAB);

        isLesson = false;
        beforeDayStart = false;
        if (tt_array.length > 0) beforeDayStart = true;
        dayEnd = true;

        for (int i = 0; i < tt_array.length; i++) {
            if (tt_array[i].length >= 9 && DataStorageHandler.isStringNumeric(tt_array[i][1]) &&
                    Integer.parseInt(tt_array[i][1]) == dayNow &&
                    DataStorageHandler.isStringNumeric(tt_array[i][2]) &&
                    DataStorageHandler.isStringNumeric(tt_array[i][3]) && tt_array[i][0].length()>=4) {

                final int timeSminutes = Integer.parseInt(tt_array[i][2]);
                final int timeEminutes = Integer.parseInt(tt_array[i][3]);

                if (timeSminutes <= nowTimeMinutes & nowTimeMinutes <= timeEminutes) {
                    isLesson = true;
                    beforeDayStart = false;
                    dayEnd = false;

                    nowRoom = tt_array[i][7].replace("[none]","").replace("[null]","");
                    nowSubjectAbbrev = tt_array[i][4];

                    final String[] subjectInfo = DataStorageHandler.SubjectInfo(getActivity(), ttFolder, tt_array[i][0].substring(0,4));
                    nowTeacher = subjectInfo[2];
                    nowSubjectName = subjectInfo[0];
                    if (nowSubjectName.equals("-")) isLesson = false;

                    final String custom = tt_array[i][8];
                    final String lessonID = tt_array[i][0];
                    final String color1 = tt_array[i][5];
                    final String color2 = tt_array[i][6];

                    setUpNowLessonCard(v, lessonID, nowSubjectName, nowSubjectAbbrev, nowRoom, nowTeacher, color1, color2, timeSminutes, timeEminutes, custom);


                    nowLessonCard.setVisibility(View.VISIBLE);

                    final String date_week = DataStorageHandler.formatDateGeneralFormat(getActivity(), Calendar.getInstance());
                    nowLessonCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle args = new Bundle();
                            args.putString("caller", "home");
                            args.putString("action", "lesson");
                            args.putString("date_week", date_week);
                            args.putString("lessonID", lessonID);

                            Intent i = new Intent(getActivity(), TimetableActivity.class);
                            i.putExtras(args);
                            startActivityForResult(i, 0);
                        }
                    });

                } else if (timeSminutes > nowTimeMinutes || nowTimeMinutes < timeEminutes){
                    dayEnd = false;

                } else if (timeSminutes < nowTimeMinutes || nowTimeMinutes > timeEminutes){
                    beforeDayStart = false;

                }
            }
        }

        if (!isLesson) {
            nowLessonCard.setVisibility(View.GONE);
        }
    }

    private void setUpNowLessonCard(View v, String lessonID, String sName, String sAbbrev, String place, String teacher,
                                    String color1, String color2, int timeSminutes, int timeEminutes, String custom){

        tvNowSubjectName.setText(sName.replace("[none]", "").replace("[null]", "").replace("[comma]", ","));

        place = place.replace("[none]", "").replace("[null]", "").replace("[comma]", ",");
        if (!place.equals("")) {
            tvNowRoom.setText(place);
        } else {
            tvNowRoom.setText("--");
            tvNowRoom.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
        }

        teacher = teacher.replace("[none]", "").replace("[null]", "").replace("[comma]", ",");
        if (!teacher.equals("")) {
            tvNowTeacher.setText(teacher);
        } else {
            tvNowTeacher.setText("--");
            tvNowTeacher.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
        }

        TextView tvTitle = (TextView) v.findViewById(R.id.CardNowLessonTitle);
        tvTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Regular.ttf"));


        final float scale = getActivity().getResources().getDisplayMetrics().density;
        int bounds = (int) (56 * scale + 0.5f);

        int colorInt = 0xff000000;
        for (int c = 0; c < colorNames.length; c++){
            if (colorNames[c].equals(color1)) colorInt = colorInts[c];
        }
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


        int tColorInt = 0xffFFFFFF;
        for (int c = 0; c < colorNames.length; c++) {
            if (colorNames[c].equals(color2)) tColorInt = colorInts[c];
        }
        tvNowSubjectAbbrev.setText(sAbbrev.replace("[none]", "").replace("[null]", "").replace("[comma]", ","));
        tvNowSubjectAbbrev.setTextColor(tColorInt);
        tvNowSubjectAbbrev.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));


        String timeS = String.valueOf(timeSminutes / 60) + ":" + String.valueOf(timeSminutes%60);
        timeS = DataStorageHandler.formatTime(timeS);

        String timeE = String.valueOf(timeEminutes / 60) + ":" + String.valueOf(timeEminutes%60);
        timeE = DataStorageHandler.formatTime(timeE);

        if (custom.equals("true")){
            tvNowPeriods.setText(timeS+" - "+timeE);
            tvNowTimes.setText("");
            tvNowTimes.setVisibility(View.GONE);

        } else {
            final String[] lesson_array = DataStorageHandler.LessonInfo(getActivity(), ttFolder, lessonID);
            final String pF = lesson_array[3];
            final String pT = lesson_array[4];

            tvNowPeriods.setText(getActivity().getResources().getString(R.string.period)+" "+pF+" - "+pT);
            tvNowTimes.setText(timeS+" - "+timeE);
        }
    }

    private void NextSubject(View v){
        isNextLesson = false;

        if (!dayEnd) {
            Calendar calNow = Calendar.getInstance();
            nowHour = calNow.get(Calendar.HOUR_OF_DAY);
            nowMinute = calNow.get(Calendar.MINUTE);
            nowDayOfWeek = calNow.get(Calendar.DAY_OF_WEEK);
            final int nowTimeMinutes = nowHour * 60 + nowMinute;

            final int[] periodNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
            int[] dayNumbers = {2, 3, 4, 5, 6, 7, 1};
            int dayNow = 0;
            for (int d = 0; d < dayNumbers.length; d++){
                if (dayNumbers[d] == nowDayOfWeek) dayNow = d;
            }

            final int nowABint = DataStorageHandler.getCurrentAB(getActivity(), ttFolder, calNow);
            final String nowAB = (nowABint < allABs.length) ? allABs[nowABint] : allABs[0];
            final String[][] tt_array = DataStorageHandler.TimetableLessons(getActivity(), ttFolder, nowAB);

            int earliest_row = -1;
            int earliest_tSMin = -1;

            for (int i = 0; i < tt_array.length; i++){
                if (tt_array[i].length >= 9 && DataStorageHandler.isStringNumeric(tt_array[i][1]) &&
                        Integer.parseInt(tt_array[i][1]) == dayNow &&
                        DataStorageHandler.isStringNumeric(tt_array[i][2]) &&
                        DataStorageHandler.isStringNumeric(tt_array[i][3]) && tt_array[i][0].length()>=4) {
                    final int timeSminutes = Integer.parseInt(tt_array[i][2]);
                    final int timeEminutes = Integer.parseInt(tt_array[i][3]);

                    if (nowTimeMinutes <= timeSminutes && nowTimeMinutes <= timeEminutes) {
                        isNextLesson = true;
                        System.out.println("Now: "+nowTimeMinutes+", then: "+timeSminutes);

                        if (earliest_row >= 0 && earliest_tSMin >= 0){
                            if (timeSminutes < earliest_tSMin){
                                earliest_row = i;
                                earliest_tSMin = timeSminutes;
                            }

                        } else {
                            earliest_row = i;
                            earliest_tSMin = timeSminutes;
                        }
                    }
                }
            }

            if (isNextLesson && earliest_row >= 0 && earliest_tSMin >= 0){
                final int r = earliest_row;

                final String lessonID = tt_array[r][0];
                final String[] subjectInfo = DataStorageHandler.SubjectInfo(getActivity(), ttFolder, lessonID.substring(0,4));

                nextSubjectName = subjectInfo[0];
                if (nextSubjectName.equals("-")) isNextLesson = false;
                nextTeacher = subjectInfo[2];
                final String nextSubjectAbbrev = tt_array[r][4];
                final String place = tt_array[r][7];
                final String color1 = tt_array[r][5];
                final String color2 = tt_array[r][6];
                final int timeSminutes = Integer.parseInt(tt_array[r][2]);
                final int timeEminutes = Integer.parseInt(tt_array[r][3]);
                final String custom = tt_array[r][8];

                setUpNextLessonCard(v, lessonID, nextSubjectName, nextSubjectAbbrev, place, nextTeacher,
                        color1, color2, timeSminutes, timeEminutes, custom);


                nextLessonCard.setVisibility(View.VISIBLE);

                final String date_week = DataStorageHandler.formatDateGeneralFormat(getActivity(), Calendar.getInstance());
                nextLessonCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle args = new Bundle();
                        args.putString("caller", "home");
                        args.putString("action", "lesson");
                        args.putString("date_week", date_week);
                        args.putString("lessonID", lessonID);

                        Intent i = new Intent(getActivity(), TimetableActivity.class);
                        i.putExtras(args);
                        startActivityForResult(i, 0);
                    }
                });
            }
        }

        if (!isNextLesson){
            nextLessonCard.setVisibility(View.GONE);
        }
    }

    private void setUpNextLessonCard(View v, String lessonID, String sName, String sAbbrev, String place, String teacher,
                                     String color1, String color2, int timeSminutes, int timeEminutes, String custom){
        System.out.println(lessonID+": "+sName+", "+timeSminutes+", "+timeEminutes);

        int colorInt = 0xffFFFFFF;
        for (int i = 0; i < colorNames.length; i++) {
            if (colorNames[i].equals(color1)) {
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

        int tColorInt = 0xffFFFFFF;
        for (int i = 0; i < colorNames.length; i++) {
            if (colorNames[i].equals(color2)) {
                tColorInt = colorInts[i];
            }
        }
        tvNextSubjectAbbrev.setText(sAbbrev.replace("[none]","").replace("[null]","").replace("[comma]", ","));
        tvNextSubjectAbbrev.setTextColor(tColorInt);
        tvNextSubjectAbbrev.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));

        tvNextSubjectName.setText(sName.replace("[none]","").replace("[null]","").replace("[comma]", ","));

        place = place.replace("[none]","").replace("[null]","").replace("[comma]", ",");
        if (!place.equals("")) {
            tvNextRoom.setText(place);
        } else {
            tvNextRoom.setText("--");
            tvNextRoom.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
        }

        teacher = teacher.replace("[none]","").replace("[null]","").replace("[comma]", ",");
        if (!teacher.equals("")) {
            tvNextTeacher.setText(teacher);
        } else {
            tvNextTeacher.setText("--");
            tvNextTeacher.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
        }

        TextView CardNextLessonTitle = (TextView) v.findViewById(R.id.CardNextLessonTitle);
        CardNextLessonTitle.setText(getActivity().getResources().getString(R.string.NextSubject));


        String timeS = String.valueOf(timeSminutes / 60) + ":" + String.valueOf(timeSminutes%60);
        timeS = DataStorageHandler.formatTime(timeS);

        String timeE = String.valueOf(timeEminutes / 60) + ":" + String.valueOf(timeEminutes%60);
        timeE = DataStorageHandler.formatTime(timeE);

        if (custom.equals("true")){
            tvNextPeriods.setText(timeS+" - "+timeE);
            tvNextTimes.setText("");
            tvNextTimes.setVisibility(View.GONE);

        } else {
            final String[] lesson_array = DataStorageHandler.LessonInfo(getActivity(), ttFolder, lessonID);
            final String pF = lesson_array[3];
            final String pT = lesson_array[4];

            tvNextPeriods.setText(getActivity().getResources().getString(R.string.period)+" "+pF+" - "+pT);
            tvNextTimes.setText(timeS+" - "+timeE);
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


            Calendar calNow = Calendar.getInstance();
            nowHour = calNow.get(Calendar.HOUR_OF_DAY);
            nowMinute = calNow.get(Calendar.MINUTE);
            nowDayOfWeek = calNow.get(Calendar.DAY_OF_WEEK);

            Calendar calTom = (Calendar) calNow.clone();
            calTom.add(Calendar.DAY_OF_YEAR, 1);
            int tomDayOfWeek = calTom.get(Calendar.DAY_OF_WEEK);

            int[] dayNumbers = {2, 3, 4, 5, 6, 7, 1};

            int dayID = -1;

            for (int i3 = 0; i3 < dayNumbers.length; i3++) {
                if (tomDayOfWeek == dayNumbers[i3]) {
                    nowDayInt = i3;
                    dayID = i3;
                }
            }

            LinearLayout bRow1 = (LinearLayout) v.findViewById(R.id.buttonRow1);
            LinearLayout bRow2 = (LinearLayout) v.findViewById(R.id.buttonRow2);
            LinearLayout bRow3 = (LinearLayout) v.findViewById(R.id.buttonRow3);

            bRow1.setVisibility(View.VISIBLE);
            bRow2.setVisibility(View.VISIBLE);
            bRow3.setVisibility(View.VISIBLE);


            final int nowABint = DataStorageHandler.getCurrentAB(getActivity(), ttFolder, Calendar.getInstance());
            final String nowAB = (nowABint < allABs.length) ? allABs[nowABint] : allABs[0];
            final String[][] tt_array = DataStorageHandler.TimetableLessons(getActivity(), ttFolder, nowAB);

            ArrayList<String[]> lessons_list = new ArrayList<>();
            for (int l = 0; l < tt_array.length; l++){
                if (tt_array[l].length >= 9 && DataStorageHandler.isStringNumeric(tt_array[l][1]) &&
                        Integer.parseInt(tt_array[l][1]) == dayID) lessons_list.add(tt_array[l]);
            }
            String[][] tomorrow_lessons = new String[lessons_list.size()][];
            for (int s = 0; s < lessons_list.size(); s++){
                tomorrow_lessons[s] = lessons_list.get(s);
            }


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

            for (int i = 0; i < tomorrow_lessons.length; i++){
                int colorInt = 0xffFFFFFF;
                for (int i2 = 0; i2 < colorNames.length; i2++){
                    if (colorNames[i2].equals(tomorrow_lessons[i][5])){
                        colorInt = colorInts[i2];
                    }
                }
                System.out.println("color: "+tomorrow_lessons[i][5]+", "+colorInt);

                int textColorInt = 0xff000000;
                for (int i3 = 0; i3 < colorNames.length; i3++){
                    if (colorNames[i3].equals(tomorrow_lessons[i][6])){
                        textColorInt = colorInts[i3];
                    }
                }
                System.out.println("text color: "+tomorrow_lessons[i][6]+", "+textColorInt);

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

                tSButtons[i].setText(tomorrow_lessons[i][4].replace("[none]","").replace("[null]", "").replace("[comma]", ","));
                tSButtons[i].setTextColor(textColorInt);
                tSButtons[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.subject_circle_small_text_size));
                tSButtons[i].setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));

                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DAY_OF_YEAR, 1);
                final String lessonID = tomorrow_lessons[i][0];
                final String date_week = DataStorageHandler.formatDateGeneralFormat(getActivity(), tomorrow);
                tSButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle extras = new Bundle();
                        extras.putString("caller", "home");
                        extras.putString("action", "lesson");
                        extras.putString("date_week", date_week);
                        extras.putString("lessonID", lessonID);
                        Intent i = new Intent(getActivity(), TimetableActivity.class);
                        i.putExtras(extras);
                        startActivityForResult(i, 0);
                    }
                });

                buttonUsed[i] = true;
            }

            if (tomorrow_lessons.length <= 0){
                tomorrowLessonsCard.setVisibility(View.GONE);
                isTomorrowSubjects = false;
            } else {
                if (tomorrow_lessons.length < 11) bRow3.setVisibility(View.GONE);
                if (tomorrow_lessons.length < 6) bRow2.setVisibility(View.GONE);

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

        final String homework_filepath = ttFolder + "/" + getActivity().getResources().getString(R.string.file_name_homework);
        final String[][] hwArray = ((MainActivity) getActivity()).toArray(getActivity(), homework_filepath);
        final int[] numberRowsCols = ((MainActivity) getActivity()).nmbrRowsCols(getActivity(), homework_filepath);

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

            if (hwArray[i][1].length() >= 4) {
                final String[] subjectInfo = DataStorageHandler.SubjectInfo(getActivity(), ttFolder, hwArray[i][1].substring(0,4));
                subjectAbbrev[i] = subjectInfo[1];
                subjectBgColors[i] = subjectInfo[4];
                subjectTextColors[i] = subjectInfo[5];
            } else {
                subjectAbbrev[i] = "-";
                subjectBgColors[i] = "[none]";
                subjectTextColors[i] = "[none]";
            }

            colorInt[i] = 0xffFFFFFF;
            textColorInt[i] = 0xff000000;
            for (int c = 0; c < colorNames.length; c++){
                if (subjectBgColors[i].equals(colorNames[c])) colorInt[i] = colorInts[c];
                if (subjectTextColors[i].equals(colorNames[c])) textColorInt[i] = colorInts[c];
            }

            String dateUnForm = hwArray[i][2];

            Date d1 = DataStorageHandler.getDateFromGeneralDateFormat(getActivity(), dateUnForm);
            Calendar cL = new GregorianCalendar();
            cL.setTime(d1);
            date[i] = DataStorageHandler.formatDateLocalFormat(getActivity(), cL);


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
        cal.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrow = DataStorageHandler.formatDateGeneralFormat(getActivity(), cal);

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
            if (myData.size()>1) recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(verticalSpacing, listPaddingTop, listPaddingBottom));
            adapter = new HomeworkAdapter(getActivity(), myData, "hwSmall");
            int viewHeight = (adapter.getItemCount() * (listPaddingTop + listPaddingBottom + adapterItemSize + verticalSpacing + getActivity().obtainStyledAttributes(new int[]{android.R.attr.listDivider}).getDrawable(0).getIntrinsicHeight()));
            recyclerView.getLayoutParams().height = viewHeight;
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(((MainActivity)getActivity())));

            isHomeworkTomorrow = true;
            homeworkCard.setVisibility(View.VISIBLE);
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
            final String subjects_filepath = ttFolder + "/" + getActivity().getResources().getString(R.string.file_name_subjects);
            int rows = ((MainActivity) getActivity()).nmbrRowsCols(getActivity(), subjects_filepath)[0];
            int cols = ((MainActivity) getActivity()).nmbrRowsCols(getActivity(), subjects_filepath)[1];
            int rowsOld = ((MainActivity) getActivity()).nmbrRowsCols(getActivity(), "Periods.txt")[0];
            int colsOld = ((MainActivity) getActivity()).nmbrRowsCols(getActivity(), "Periods.txt")[1];

            if ((rows > 0 && cols > 0) || (rowsOld > 0 && colsOld > 0)) {
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
        NewsSectionShowHide();

        bNewFeatureDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvNewFeature.setVisibility(View.GONE);
                dismissedNewFeatureMySchool = true;
                NewsSectionShowHide();

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
                NewsSectionShowHide();

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

    private void newFeatureABWeeksCard(View v){
        Button bNewFeatureDismiss = (Button) v.findViewById(R.id.bDismissNewFeatureABWeeks);
        Button bNewFeatureGoTo = (Button) v.findViewById(R.id.bGoToNewFeatureABWeeks);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        final String dismissedNewFeatureABWeeksKey = "dismissed_new_feature_ab_weeks";

        if (prefs.contains(dismissedNewFeatureABWeeksKey)) {
            if (prefs.getBoolean(dismissedNewFeatureABWeeksKey, false)) cvNewFeatureABWeeks.setVisibility(View.GONE);
        }
        NewsSectionShowHide();

        bNewFeatureDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvNewFeatureABWeeks.setVisibility(View.GONE);
                NewsSectionShowHide();

                prefs.edit().putBoolean(dismissedNewFeatureABWeeksKey, true).apply();
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
                Intent i = new Intent(getActivity(), ABWeekSettingsActivity.class);
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

    private void schoolChallengeCard(View v){
        if (prefs.contains(getActivity().getResources().getString(R.string.pref_key_my_school_school_id)) ||
                prefs.contains(getActivity().getResources().getString(R.string.pref_key_my_school_school_name))){

            final String schoolID = prefs.getString(getActivity().getResources().getString(R.string.pref_key_my_school_school_id), "[none]");
            final String schoolName = prefs.getString(getActivity().getResources().getString(R.string.pref_key_my_school_school_name), "[none]");

            if (schoolID != null && !schoolID.equals("[none]") && schoolName != null && !schoolName.equals("[none]")) {

                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference ref = database.getReference();
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isChallenge = false;
                        if (dataSnapshot.child("School Challenges (active)/c1 (20160818)").exists()){
                            final Object obj1 = dataSnapshot.child("School Challenges (active)/c1 (20160818)").getValue();

                            if (obj1 != null) {
                                isChallenge = (boolean) obj1;

                                if (isChallenge){
                                    rlNews.setVisibility(View.VISIBLE);
                                    cSchoolChallenge.setVisibility(View.VISIBLE);
                                    tvSchoolChallengeSubtitle.setText(schoolName);

                                    llSchoolChallengeCounter.setVisibility(View.GONE);
                                    vSchoolChallengeCounter.setVisibility(View.GONE);

                                    boolean foundValue = false;
                                    if (dataSnapshot.child("Schools NrUsers/" + schoolID).exists()){
                                        final Object obj2 = dataSnapshot.child("Schools NrUsers/" + schoolID).getValue();

                                        if (obj2 != null) {
                                            final long nrUsers = (long) obj2;
                                            llSchoolChallengeCounter.setVisibility(View.VISIBLE);
                                            vSchoolChallengeCounter.setVisibility(View.VISIBLE);

                                            if (nrUsers < 10) {
                                                tvScoolChallengeCounter.setText(String.valueOf(nrUsers));
                                                prefs.edit().putBoolean("MySchoolChallenge-c1-20160818-complete", false).apply();
                                                prefs.edit().putBoolean("dismissed_schoolchallenge_completed_card", false).apply();
                                            } else {
                                                if (prefs.contains("dismissed_schoolchallenge_completed_card")
                                                        && prefs.getBoolean("dismissed_schoolchallenge_completed_card", false)){
                                                    cSchoolChallenge.setVisibility(View.GONE);
                                                    NewsSectionShowHide();
                                                } else {
                                                    LinearLayout llcounter =
                                                            (LinearLayout) getActivity().findViewById(R.id.CardSchoolChallengeLLCounter);
                                                    llcounter.setVisibility(View.GONE);
                                                    View divview =
                                                            (View) getActivity().findViewById(R.id.CardSchoolChallengeCounterDividerView);
                                                    divview.setVisibility(View.GONE);
                                                    TextView cardTitle = (TextView) getActivity().findViewById(R.id.CardSchoolChallengeTitle);
                                                    cardTitle.setText(getActivity().getResources().getString(R.string.card_school_challenge_complete_title));
                                                    TextView cardContent = (TextView) getActivity().findViewById(R.id.CardSchoolChallengeContent);
                                                    cardContent.setText(getActivity().getResources().getString(R.string.card_school_challenge_complete_content));
                                                    Button dismiss = (Button) getActivity().findViewById(R.id.bSchoolChallengeDismiss);
                                                    dismiss.setVisibility(View.VISIBLE);
                                                    dismiss.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            cSchoolChallenge.setVisibility(View.GONE);
                                                            NewsSectionShowHide();
                                                            prefs.edit().putBoolean("dismissed_schoolchallenge_completed_card", true).apply();
                                                        }
                                                    });
                                                }

                                                prefs.edit().putBoolean("MySchoolChallenge-c1-20160818-complete", true).apply();
                                            }
                                            foundValue = true;
                                        }
                                    }
                                    if (!foundValue) {
                                        llSchoolChallengeCounter.setVisibility(View.GONE);
                                        vSchoolChallengeCounter.setVisibility(View.GONE);
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        bSchoolChallengeInviteFriends.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
                                        bSchoolChallengeInviteFriends.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
                                    } else {
                                        bSchoolChallengeInviteFriends.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
                                    }

                                    final String linkText = getActivity().getResources().getString(R.string.link_to_play_store_page);

                                    bSchoolChallengeInviteFriends.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent sendIntent = new Intent();
                                            sendIntent.setAction(Intent.ACTION_SEND);
                                            sendIntent.putExtra(Intent.EXTRA_TEXT, linkText);
                                            sendIntent.setType("text/plain");
                                            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

                                            int nrShares = 0;
                                            if (prefs.contains(getActivity().getResources().getString(R.string.pref_key_number_shares))){
                                                nrShares = prefs.getInt(getActivity().getResources().getString(R.string.pref_key_number_shares), 0);
                                            }
                                            nrShares ++;
                                            prefs.edit().putInt(getActivity().getResources().getString(R.string.pref_key_number_shares), nrShares).apply();

                                            mTracker.send(new HitBuilders.EventBuilder()
                                                    .setCategory("MySchool - Share app (MainFrag-SchoolChallenge)")
                                                    .setAction("S: "+schoolID+" ("+schoolName+")")
                                                    .build());
                                        }
                                    });
                                }
                            }
                        }
                        if (!isChallenge) {
                            cSchoolChallenge.setVisibility(View.GONE);
                            NewsSectionShowHide();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("FBDB", "SchoolChallenge:DatabaseError: "+databaseError);
                    }
                });
            } else {
                cSchoolChallenge.setVisibility(View.GONE);
                NewsSectionShowHide();
            }
        } else {
            cSchoolChallenge.setVisibility(View.GONE);
            NewsSectionShowHide();
        }
    }

    private void appUpdateCard(){

        final int app_version_code = BuildConfig.VERSION_CODE;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean newVersion = false;
                int latestVersion = 0;
                if (dataSnapshot.child("App Latest Version").exists()){
                    final Object obj1 = dataSnapshot.child("App Latest Version").getValue();

                    if (obj1 != null) {
                        long longLV = (long) obj1;
                        latestVersion = (int) longLV;

                        if (latestVersion > app_version_code) newVersion = true;
                        if (newVersion) {
                            rlNews.setVisibility(View.VISIBLE);
                            cAppUpdate.setVisibility(View.VISIBLE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                bAppUpdate.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
                                bAppUpdate.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
                            } else {
                                bAppUpdate.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
                            }

                            bAppUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }

                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("MainFrag - AppUpdate")
                                            .setAction("Update button clicked")
                                            .build());
                                }
                            });
                        }
                    }
                }
                if (!newVersion) {
                    cAppUpdate.setVisibility(View.GONE);
                    NewsSectionShowHide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FBDB", "SchoolChallenge:DatabaseError: "+databaseError);
            }
        });
    }

    private void reviewAppCards(View v){
        int opensCount = 0;
        if (prefs.contains("reviewAppDismissCountOpensSince")){
            opensCount = prefs.getInt("reviewAppDismissCountOpensSince", 0);
        }
        if (opensCount > 30 || !prefs.contains("reviewAppDismissCountOpensSince")){
            final CardView cRating = (CardView) v.findViewById(R.id.RatingCard);
            final RatingBar rbRate = (RatingBar) v.findViewById(R.id.ratingbarApp);
            Button bRatingOK = (Button) v.findViewById(R.id.bRatingOK);

            final CardView cReview = (CardView) v.findViewById(R.id.ReviewAppCard);
            final Button bReviewOK = (Button) v.findViewById(R.id.bReviewOK);
            final Button bReviewNo = (Button) v.findViewById(R.id.bReviewNo);

            cRating.setVisibility(View.VISIBLE);
            bRatingOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    float rating = rbRate.getRating();
                    if (rating >= 4f){
                        cRating.setVisibility(View.GONE);
                        cReview.setVisibility(View.VISIBLE);

                        bReviewOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                                prefs.edit().putInt("reviewAppDismissCountOpensSince", 0).apply();

                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("ReviewApp")
                                        .setAction("Review OK")
                                        .build());
                            }
                        });

                        bReviewNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cReview.setVisibility(View.GONE);
                                prefs.edit().putInt("reviewAppDismissCountOpensSince", 0).apply();

                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("ReviewApp")
                                        .setAction("Review No")
                                        .build());
                            }
                        });

                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ReviewApp")
                                .setAction("Rating: "+ String.valueOf(rating))
                                .build());

                    } else if (rating > 1f) {
                       cRating.setVisibility(View.GONE);
                        prefs.edit().putInt("reviewAppDismissCountOpensSince", 0).apply();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ReviewApp")
                                .setAction("Rating: "+ String.valueOf(rating))
                                .build());
                    }
                }
            });
        } else {
            prefs.edit().putInt("reviewAppDismissCountOpensSince", opensCount+1).apply();
        }
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