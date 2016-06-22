package com.thinc_easy.schoolmanager;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewTimetableFragment extends Fragment {
	
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";
	private int mFirstSubject;
	private ArrayList<Integer> mSubjectsSelected;
	private ArrayList<String> mSubjectsSelectedNames = new ArrayList<String>();
	public Button mConfirmButton;
	
	public int[] mCheckboxesIds = {R.id.checkBox01, R.id.checkBox02, R.id.checkBox03, R.id.checkBox04, R.id.checkBox05, R.id.checkBox06, R.id.checkBox07, R.id.checkBox08, R.id.checkBox09, R.id.checkBox10, R.id.checkBox11, R.id.checkBox12, R.id.checkBox13, R.id.checkBox14, R.id.checkBox15, R.id.checkBox16, R.id.checkBox17, R.id.checkBox18, R.id.checkBox19};
	public CheckBox[] mCheckboxes;

	LinearLayout llCustomS;
	ImageView ivAdd;
	CardView cScard;
	EditText etC1;
	EditText etC2;
	int _intMyLineCount;
	private List<EditText> editTextList = new ArrayList<EditText>();
	private List<CheckBox> checkBoxList = new ArrayList<CheckBox>();
	private List<LinearLayout> linearLayoutList = new ArrayList<LinearLayout>();
	private List<String> customSubjectsArray = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_timetable, container, false);
        
        mSubjectsSelected = new ArrayList<Integer>();
        mCheckboxes = new CheckBox[19];
        for (int k = 0; k<19; k++) mCheckboxes[k] = (CheckBox) v.findViewById(mCheckboxesIds[k]);


		ivAdd = (ImageView) v.findViewById(R.id.ivAddCSubject);
		llCustomS = (LinearLayout) v.findViewById(R.id.customSubjectsPlaceholder);
		cScard = (CardView) v.findViewById(R.id.customSubjectsCard);

		ivAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				llCustomS.addView(linearLayout(_intMyLineCount));
				llCustomS.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				_intMyLineCount++;
			}
		});
        
        
        mFirstSubject=0;
        
        mConfirmButton = (Button) v.findViewById(R.id.confirm);
        
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				for (int i = 0; i < 19; i++) {
					if (mCheckboxes[i].isChecked()) {
						mSubjectsSelected.add(i);
						mSubjectsSelectedNames.add(mCheckboxes[i].getText().toString());
					}
				}

				CheckBox cbC1 = (CheckBox) v.findViewById(R.id.checkBox20);
				CheckBox cbC2 = (CheckBox) v.findViewById(R.id.checkBox21);
				etC1 = (EditText) v.findViewById(R.id.etCustom1);
				etC2 = (EditText) v.findViewById(R.id.etCustom2);

				if (cbC1.isChecked()){
					String sC;
					if (etC1.getText() == null || etC1.getText().toString().equals("")){
						sC = "[null]";
					} else {
						sC = etC1.getText().toString();
					}
					mSubjectsSelectedNames.add(sC);
				}

				if (cbC2.isChecked()){
					String sC;
					if (etC2.getText() == null || etC2.getText().toString().equals("")){
						sC = "[null]";
					} else {
						sC = etC2.getText().toString();
					}
					mSubjectsSelectedNames.add(sC);
				}

				for (int c = 0; c < _intMyLineCount; c++){
					CheckBox cb = checkBoxList.get(c);
					if (cb.isChecked()){
						EditText et = editTextList.get(c);
						String sC;
						if (et.getText() == null || et.getText().toString().equals("")){
							sC = "[null]";
						} else {
							sC = et.getText().toString();
						}
						mSubjectsSelectedNames.add(sC);
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
				File file5 = new File(getActivity().getExternalFilesDir(null), "Homework.txt");
				file5.delete();
				File file6 = new File(getActivity().getExternalFilesDir(null), "ActiveTtNotifications.txt");
				file6.delete();
				File file7 = new File(getActivity().getExternalFilesDir(null), "AllTtNotifications.txt");
				file7.delete();

				if (mSubjectsSelectedNames.size() > 0) {
					Bundle args = new Bundle();
					args.putString("subjects", "true");
					args.putInt("first_subject", mFirstSubject);
					args.putIntegerArrayList("subjects_to_add", mSubjectsSelected);
					args.putStringArrayList("subjects_to_add_names", mSubjectsSelectedNames);
					args.putString("caller", "NewTimetable");


					Intent i = new Intent(getActivity(),
							EditSubjectActivity.class);
					i.putExtras(args);
					startActivityForResult(i, 0);
				} else {
					Toast.makeText(getActivity(), getResources().getString(R.string.ToastSelectAtLeast1Subject), Toast.LENGTH_SHORT).show();
					/*Bundle args = new Bundle();
					args.putString("subjects", "false");
					args.putString("caller", "NewTimetable");

					Intent i = new Intent(getActivity(),
							EditSubjectActivity.class);
					i.putExtras(args);
					startActivityForResult(i, 0);*/
				}
			}
		});

		int ttColor = ((NewTimetableActivity) getActivity()).getResources().getColor(R.color.color_timetable_appbar);
		((NewTimetableActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ttColor));

		// indicate that the fragment would like to add items to the OptionsMenu
		setHasOptionsMenu(true);

		// update the AppBar to show the up arrow
		((NewTimetableActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return v;
    }


	private EditText editText(int _intID){
		EditText editText = new EditText(getActivity());
		editText.setId(_intID);
		editText.setHint(getActivity().getResources().getString(R.string.new_timetable_custom_subject_name_hint));
		editText.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
		//editText.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		//editText.setBackgroundColor(Color.WHITE);
		editTextList.add(editText);
		return editText;
	}

	private CheckBox checkBox(int _intID){
		CheckBox checkBox = new CheckBox(getActivity());
		checkBox.setId(_intID);
		//checkBox.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		//checkBox.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		checkBoxList.add(checkBox);
		return checkBox;
	}

	private LinearLayout linearLayout(int _intID){
		LinearLayout llC = new LinearLayout(getActivity());
		llC.setId(_intID);
		llC.addView(checkBox(_intID));
		llC.addView(editText(_intID));
		llC.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		llC.setOrientation(LinearLayout.HORIZONTAL);
		linearLayoutList.add(llC);
		return llC;

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