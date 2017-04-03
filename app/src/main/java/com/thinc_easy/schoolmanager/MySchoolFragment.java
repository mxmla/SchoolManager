package com.thinc_easy.schoolmanager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wassm on 2016-07-02.
 */
public class MySchoolFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName;
    private SharedPreferences prefs;
    private WebView webView;
    private TextView tvNoSchool;
    private Button goToSettings;
    private String url, schoolID, prefKeySchoolID;
    private int whichWebPage;
    private ProgressDialog progDailog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_school, container, false);

        fragmentName = "MySchoolFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        int hColor = getActivity().getResources().getColor(R.color.color_myschool_appbar);
        ((MySchoolActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(hColor));

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        prefKeySchoolID = getActivity().getResources().getString(R.string.pref_key_my_school_school_id);

        whichWebPage = 1;

        tvNoSchool = (TextView) v.findViewById(R.id.tvNoSchool);
        goToSettings = (Button) v.findViewById(R.id.selectSchoolButton);

        webView = (WebView) v.findViewById(R.id.webView);
        loadURL();

        return v;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadURL(){
        String[][] AllSchoolsURLs = DataStorageHandler.toArray(getActivity(), "schools/AllSchoolsURLs.txt", true);

        boolean foundURL = false;
        if (prefs.contains(prefKeySchoolID)){
            schoolID = prefs.getString(prefKeySchoolID, "[none]");
            if (schoolID != null && !schoolID.equals("[none]")){
                for (int i = 0; i < AllSchoolsURLs.length; i++){
                    if (AllSchoolsURLs[i][0].equals(schoolID) && AllSchoolsURLs[i][1].equals(String.valueOf(whichWebPage))){
                        url = AllSchoolsURLs[i][2];
                        saveWebsiteCode(url);

                        tvNoSchool.setVisibility(View.GONE);
                        goToSettings.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setBuiltInZoomControls(true);
                        webView.getSettings().setDisplayZoomControls(false);
                        webView.getSettings().setSupportMultipleWindows(false);
                        /*
                        webView.getSettings().setDomStorageEnabled(true);
                        webView.setPadding(0, 0, 0, 0);
                        webView.setInitialScale(getScale());
                        */

                        webView.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                webView.loadUrl(url);
                            }
                        }, 500);
                        //webView.loadData(url, "text/html; charset=UTF-8", null);
                        webView.clearView();
                        webView.measure(100, 100);
                        webView.getSettings().setUseWideViewPort(true);
                        webView.getSettings().setLoadWithOverviewMode(true);

                        webView.setWebViewClient(new WebViewClient(){
                            /*@Override
                            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                                handler.proceed();
                            }*/

                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url){
                                if (url.endsWith(".pdf") && !url.contains("https://docs.google.com/gview?embedded=true&url=")){
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/gview?embedded=true&url="+url)));
                                    //view.loadUrl("https://docs.google.com/gview?embedded=true&url="+url);
                                } else {
                                    view.loadUrl(url);
                                }
                                return false;
                            }
                            @Override
                            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                Log.i("WEB_VIEW", "error code:" + errorCode);
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(failingUrl)));
                                super.onReceivedError(view, errorCode, description, failingUrl);
                            }


                        });
                        webView.setWebChromeClient(new WebChromeClient(){
                            @Override
                            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg)
                            {
                                /*WebView newWebView = new WebView(getContext());
                                addView(newWebView);
                                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                                transport.setWebView(newWebView);
                                resultMsg.sendToTarget();*/
                                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                                transport.setWebView(webView);
                                resultMsg.sendToTarget();
                                return true;
                            }
                        });
                        webView.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                                    WebView webView = (WebView) v;

                                    switch(keyCode) {
                                        case KeyEvent.KEYCODE_BACK:
                                            if(webView.canGoBack()) {
                                                webView.goBack();
                                                return true;
                                            }
                                            break;
                                    }
                                }

                                return false;
                            }
                        });
                        foundURL = true;
                        Toast.makeText(getActivity().getApplicationContext(), "Loading...", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        if (!foundURL){
            webView.setVisibility(View.GONE);
            tvNoSchool.setVisibility(View.VISIBLE);
            goToSettings.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                goToSettings.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.my_school_button_color_state_list));
                goToSettings.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
            } else {
                goToSettings.setTextColor(getActivity().getResources().getColor(R.color.color_myschool));
            }

            goToSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = new Intent(getActivity(),
                            MySchoolSettingsActivity.class);
                    startActivityForResult(i1, 0);
                }
            });
        }
    }

    private void saveWebsiteCode(String urlToOpen){
        final MySchoolActivity activityReference = (MySchoolActivity) getActivity();
        final String urlString = urlToOpen;

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

                    String strng = stringBuilder.toString();
                    sharedprefs.edit().putString("website1codeLastTime", strng).apply();

                    System.out.println("strng");
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

    public void webViewGoBack(){
        webView.goBack();
    }

    @Override
    public void onResume(){
        super.onResume();

        String newSchoolID = prefs.getString(prefKeySchoolID, "[none]");
        if (!newSchoolID.equals(schoolID)) loadURL();

        Log.i("Analytics", "Setting screen name: " + fragmentName);
        mTracker.setScreenName("Image~" + fragmentName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private int getScale(){
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width);/*/new Double(PIC_WIDTH);
        val = val * 100d;*/
        return val.intValue();
    }


    /*@Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Toast.makeText(getActivity(), "onKey", Toast.LENGTH_SHORT).show();

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    Toast.makeText(getActivity(), String.valueOf(webView.canGoBack()), Toast.LENGTH_SHORT).show();
                    if (webView.canGoBack()){
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
    }*/
}


