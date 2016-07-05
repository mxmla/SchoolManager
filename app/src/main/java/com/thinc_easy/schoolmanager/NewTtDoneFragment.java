package com.thinc_easy.schoolmanager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class NewTtDoneFragment extends Fragment{

	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

	private Tracker mTracker;
	private String fragmentName;
	TextView tvShowMaths;
	String mathsTeacher;
	Button mNextButton;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_tt_done, container, false);

		fragmentName = "NewTtDoneFragment";
		// Obtain the shared Tracker instance.
		SchoolManager application = (SchoolManager) getActivity().getApplication();
		mTracker = application.getDefaultTracker();

		int ttColor = ((EditSubjectActivity) getActivity()).getResources().getColor(R.color.color_timetable_appbar);
		((EditSubjectActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ttColor));

        /*
        tvShowMaths = (TextView) v.findViewById(R.id.tvD3);
        mathsTeacher = (String)((EditSubjectActivity) getActivity()).mathsTeacher();
        tvShowMaths.setText(mathsTeacher);
        */
        mNextButton = (Button) v.findViewById(R.id.confirm);
        
		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(),
						TimetableActivity.class);
				startActivityForResult(i, 0);	
			}
		});
		
        return v;
	}

	@Override
	public void onResume(){
		super.onResume();

		Log.i("Analytics", "Setting screen name: " + fragmentName);
		mTracker.setScreenName("Image~" + fragmentName);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}
}
