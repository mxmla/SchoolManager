package com.thinc_easy.schoolmanager;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AnotherCustomSubjectFragment extends Fragment{

	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";
	private Button bYes;
	private Button bNo;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_another_custom_subject, container, false);

		int ttColor = ((EditSubjectActivity) getActivity()).getResources().getColor(R.color.color_timetable);
		((EditSubjectActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ttColor));
        
        bYes = (Button) v.findViewById(R.id.buttonYes);
        bNo = (Button) v.findViewById(R.id.buttonNo);
        
        bYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditSubjectActivity)getActivity()).EditCustom();
			}
		});
        
        bNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditSubjectActivity)getActivity()).Completed();
			}
		});
        
        return v;
	}

}
