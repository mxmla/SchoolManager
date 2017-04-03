package com.thinc_easy.schoolmanager;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.test.suitebuilder.TestMethod;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.vision.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewTimetableFragment extends Fragment {
	public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";
	private Tracker mTracker;
	private String fragmentName;
	private int mFirstSubject;
	private ArrayList<String> mSubjectsSelectedNames = new ArrayList<String>();
	public Button mConfirmButton;
	
	public int[] mCheckboxesIds = {R.id.checkBox01, R.id.checkBox02, R.id.checkBox03, R.id.checkBox04, R.id.checkBox05, R.id.checkBox06, R.id.checkBox07, R.id.checkBox08, R.id.checkBox09, R.id.checkBox10, R.id.checkBox11, R.id.checkBox12, R.id.checkBox13, R.id.checkBox14, R.id.checkBox15, R.id.checkBox16, R.id.checkBox17, R.id.checkBox18, R.id.checkBox19};
	public CheckBox[] mCheckboxes;

	LinearLayout llCustomS;
	ImageView ivAdd;
	CardView cScard;
	EditText etC1, etC2;
	CheckBox cbC1, cbC2;
	int _intMyLineCount;
	private List<EditText> editTextList = new ArrayList<EditText>();
	private List<CheckBox> checkBoxList = new ArrayList<CheckBox>();
	private List<LinearLayout> linearLayoutList = new ArrayList<LinearLayout>();
	private List<String> customSubjectsArray = new ArrayList<String>();
	private TextView tvSettingsTitle, tvSubjectsTitle, tvNumberABWeeks, tvABPreviewTitle, tvABPreview;
	private Switch switchAB;
	private Button bABPickDown, bABPickUp;
	private EditText etABPick;
	private String[] alphabet;
	private SharedPreferences prefs;
	private String folder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_timetable, container, false);

		fragmentName = "NewTimetableFragment";
		// Obtain the shared Tracker instance.
		SchoolManager application = (SchoolManager) getActivity().getApplication();
		mTracker = application.getDefaultTracker();

		setUpABCard(v);
		setUpCustomSubjects(v);
		setUpConfirm(v);

		alphabet = getResources().getStringArray(R.array.alphabet);
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		int ttColor = ((NewTimetableActivity) getActivity()).getResources().getColor(R.color.color_timetable_appbar);
		((NewTimetableActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ttColor));

		// indicate that the fragment would like to add items to the OptionsMenu
		setHasOptionsMenu(true);

		// update the AppBar to show the up arrow
		((NewTimetableActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return v;
    }

	private void setUpABCard(View v){
		tvSettingsTitle = (TextView) v.findViewById(R.id.newTtSettingsSectionTitleText);
		tvSubjectsTitle = (TextView) v.findViewById(R.id.newTtSubjectsSectionTitleText);
		tvNumberABWeeks = (TextView) v.findViewById(R.id.tvNumberABWeeks);
		tvABPreviewTitle = (TextView) v.findViewById(R.id.tvABPreviewTitle);
		tvABPreview = (TextView) v.findViewById(R.id.tvABPreview);
		switchAB = (Switch) v.findViewById(R.id.switch_a_b_week);
		bABPickDown = (Button) v.findViewById(R.id.bABNrPickDown);
		bABPickUp = (Button) v.findViewById(R.id.bABNrPickUp);
		etABPick = (EditText) v.findViewById(R.id.etNrPick);

		tvSettingsTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));
		tvSubjectsTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Roboto-Medium.ttf"));

		switchAB.setChecked(false);
		tvNumberABWeeks.setTextColor(getResources().getColor(R.color.Disabled));
		tvABPreviewTitle.setTextColor(getResources().getColor(R.color.Disabled));
		tvABPreview.setTextColor(getResources().getColor(R.color.Disabled));
		bABPickDown.setEnabled(false);
		bABPickUp.setEnabled(false);
		etABPick.setEnabled(false);

		switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if (isChecked){
					tvNumberABWeeks.setTextColor(getResources().getColor(R.color.Text));
					tvABPreviewTitle.setTextColor(getResources().getColor(R.color.Text));
					tvABPreview.setTextColor(getResources().getColor(R.color.Text));
					bABPickDown.setEnabled(true);
					bABPickUp.setEnabled(true);
					etABPick.setEnabled(true);
				} else {
					tvNumberABWeeks.setTextColor(getResources().getColor(R.color.Disabled));
					tvABPreviewTitle.setTextColor(getResources().getColor(R.color.Disabled));
					tvABPreview.setTextColor(getResources().getColor(R.color.Disabled));
					bABPickDown.setEnabled(false);
					bABPickUp.setEnabled(false);
					etABPick.setEnabled(false);
				}
			}
		});

		bABPickDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int val = 1;
				try {
					val = Integer.parseInt(etABPick.getText().toString());
				} catch(NumberFormatException nfe) {
					System.out.println("Could not parse " + nfe);
				}

				if (val > 1){
					val = val - 1;
				} else {
					val = 1;
				}

				etABPick.setText(String.valueOf(val));
			}
		});

		bABPickUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int val = 1;
				try {
					val = Integer.parseInt(etABPick.getText().toString());
				} catch(NumberFormatException nfe) {
					System.out.println("Could not parse " + nfe);
				}

				if (val < 26){
					val = val + 1;
				} else {
					val = 26;
				}

				etABPick.setText(String.valueOf(val));
			}
		});

		etABPick.setFilters(new InputFilter[]{new InputFilterMinMax("1", "26")});
		etABPick.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				int numbr = 1;
				try{
					numbr = Integer.parseInt(etABPick.getText().toString());
				} catch (NumberFormatException nfe){
					System.out.println("Could not parse " + nfe);
				}
				String AB = "A";
				for (int abc = 1; abc < numbr; abc++) AB = AB + " | " + alphabet[abc];
				tvABPreview.setText(AB);
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
	}

	private void setUpCustomSubjects(View v){
		ivAdd = (ImageView) v.findViewById(R.id.ivAddCSubject);
		llCustomS = (LinearLayout) v.findViewById(R.id.customSubjectsPlaceholder);
		cScard = (CardView) v.findViewById(R.id.customSubjectsCard);

		cbC1 = (CheckBox) v.findViewById(R.id.checkBox20);
		cbC2 = (CheckBox) v.findViewById(R.id.checkBox21);
		etC1 = (EditText) v.findViewById(R.id.etCustom1);
		etC2 = (EditText) v.findViewById(R.id.etCustom2);

		etC1.setSingleLine(true);
		etC2.setSingleLine(true);

		ivAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				llCustomS.addView(linearLayout(_intMyLineCount));
				llCustomS.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				_intMyLineCount++;
			}
		});
	}

	private void setUpConfirm(View v){
		mCheckboxes = new CheckBox[19];
		for (int k = 0; k<19; k++) mCheckboxes[k] = (CheckBox) v.findViewById(mCheckboxesIds[k]);


		mFirstSubject=0;

		mConfirmButton = (Button) v.findViewById(R.id.confirm);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mConfirmButton.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
			mConfirmButton.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
		} else {
			mConfirmButton.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
		}

		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				for (int i = 0; i < 19; i++) {
					if (mCheckboxes[i].isChecked()) {
						mSubjectsSelectedNames.add(mCheckboxes[i].getText().toString());
					}
				}

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
				/*File file = new File(getActivity().getExternalFilesDir(null), "Periods.txt");
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
				file7.delete();*/

				if (mSubjectsSelectedNames.size() > 0) {
					makeFolderSaveFile();

					if (!switchAB.isChecked()) prefs.edit().putBoolean("pref_key_show_week_selector", false).apply();

					Bundle args = new Bundle();
					args.putString("subjects", "true");
					args.putInt("first_subject", mFirstSubject);
					args.putStringArrayList("subjects_to_add_names", mSubjectsSelectedNames);
					args.putString("caller", "NewTimetable");


					Intent i = new Intent(getActivity(),
							EditSubjectActivity.class);
					i.putExtras(args);
					startActivityForResult(i, 0);
				} else {
					Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.ToastSelectAtLeast1Subject), Toast.LENGTH_SHORT).show();
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
	}

	private void makeFolderSaveFile(){
		int numbrAB = 1;
		if (switchAB.isChecked()) {
			try {
				numbrAB = Integer.parseInt(etABPick.getText().toString());
			} catch (NumberFormatException nfe) {
				System.out.println("Could not parse " + nfe);
			}
		}


		final String newFolder = DataStorageHandler.setUpNewTimetableReturnTtFolderPath(getActivity(), numbrAB);
	}


	private EditText editText(int _intID){
		EditText editText = new EditText(getActivity());
		editText.setId(_intID);
		editText.setHint(getActivity().getResources().getString(R.string.new_timetable_custom_subject_name_hint));
		editText.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
		editText.setSingleLine(true);
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

	@Override
	public void onResume(){
		super.onResume();

		Log.i("Analytics", "Setting screen name: " + fragmentName);
		mTracker.setScreenName("Image~" + fragmentName);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	public class InputFilterMinMax implements InputFilter {

		private int min, max;

		public InputFilterMinMax(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public InputFilterMinMax(String min, String max) {
			this.min = Integer.parseInt(min);
			this.max = Integer.parseInt(max);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			try {
				int input = Integer.parseInt(dest.toString() + source.toString());
				if (isInRange(min, max, input))
					return null;
			} catch (NumberFormatException nfe) { }
			return "";
		}

		private boolean isInRange(int a, int b, int c) {
			return b > a ? c >= a && c <= b : c >= b && c <= a;
		}
	}
}