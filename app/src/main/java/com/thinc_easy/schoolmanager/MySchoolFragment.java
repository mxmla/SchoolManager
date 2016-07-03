package com.thinc_easy.schoolmanager;

import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by wassm on 2016-07-02.
 */
public class MySchoolFragment extends Fragment {

    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_school, container, false);

        int hColor = getActivity().getResources().getColor(R.color.color_myschool_appbar);
        ((MySchoolActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(hColor));


        String url = "http://www.domgymnasium-verden.de/buch/Über_uns.html";
        WebView webView = (WebView) v.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl(url);

        return v;
    }
}


