package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class NewTimetableFragment extends Fragment {
	
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";
	private int mFirstSubject;
	private ArrayList<Integer> mSubjectsSelected;
	public Button mConfirmButton;
	
	public int[] mCheckboxesIds = {R.id.checkBox01, R.id.checkBox02, R.id.checkBox03, R.id.checkBox04, R.id.checkBox05, R.id.checkBox06, R.id.checkBox07, R.id.checkBox08, R.id.checkBox09, R.id.checkBox10, R.id.checkBox11, R.id.checkBox12, R.id.checkBox13, R.id.checkBox14, R.id.checkBox15, R.id.checkBox16, R.id.checkBox17, R.id.checkBox18, R.id.checkBox19};
	public CheckBox[] mCheckboxes;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_timetable, container, false);
        
        mSubjectsSelected = new ArrayList<Integer>();
        mCheckboxes = new CheckBox[19];
        for (int k = 0; k<19; k++) mCheckboxes[k] = (CheckBox) v.findViewById(mCheckboxesIds[k]);
        
        
        mFirstSubject=0;
        
        mConfirmButton = (Button) v.findViewById(R.id.confirm);
        
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				for (int i = 0; i < 19; i++) {
					if (mCheckboxes[i].isChecked()) {
						mSubjectsSelected.add(i);
					}
				}
				DataStorageHandler.deleteAllSubjectNotifications(getActivity());
				File file = new File(getActivity().getExternalFilesDir(null), "Periods.txt");
				file.delete();
				File file2 = new File(getActivity().getExternalFilesDir(null), "Subjects.txt");
				file2.delete();
				File file3 = new File(getActivity().getExternalFilesDir(null), "TtFields.txt");
				file3.delete();
				File file4 = new File(getActivity().getExternalFilesDir(null), "Notifications.txt");
				file4.delete();

				if (mSubjectsSelected.size() > 0) {
					Bundle args = new Bundle();
					args.putString("subjects", "true");
					args.putInt("first_subject", mFirstSubject);
					args.putIntegerArrayList("subjects_to_add", mSubjectsSelected);
					args.putString("caller", "NewTimetable");

					Intent i = new Intent(getActivity(),
							EditSubjectActivity.class);
					i.putExtras(args);
					startActivityForResult(i, 0);
				} else {
					//Toast.makeText(getActivity(), getResources().getString(R.string.ToastSelectAtLeast1Subject), Toast.LENGTH_SHORT).show();Bundle args = new Bundle();
					Bundle args = new Bundle();
					args.putString("subjects", "false");
					args.putString("caller", "NewTimetable");

					Intent i = new Intent(getActivity(),
							EditSubjectActivity.class);
					i.putExtras(args);
					startActivityForResult(i, 0);
				}
			}
		});

		int ttColor = ((NewTimetableActivity) getActivity()).getResources().getColor(R.color.color_timetable);
		((NewTimetableActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ttColor));

		// indicate that the fragment would like to add items to the OptionsMenu
		setHasOptionsMenu(true);

		// update the AppBar to show the up arrow
		((NewTimetableActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return v;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Get item selected and deal with it.
		switch (item.getItemId()){
			case android.R.id.home:
				// called when up arrow in AppBar is pressed
				getActivity().onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}