package com.thinc_easy.schoolmanager;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.drive.internal.StringListResponse;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wassm on 2016-09-12.
 */
public class ABWeekSettingsFragment extends Fragment {
    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Tracker mTracker;
    private String fragmentName;
    private SharedPreferences prefs;
    private Button saveButton;
    private String[] alphabet;
    private String ttFolder;
    private EditText etABPick;
    private Switch switchAB;
    private int activeAB;
    private ArrayList<RadioButton> rbList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ab_week_settings, container, false);

        fragmentName = "ABWeekSettingsFragment";
        // Obtain the shared Tracker instance.
        SchoolManager application = (SchoolManager) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        alphabet = getActivity().getResources().getStringArray(R.array.alphabet);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        ttFolder = prefs.getString(getActivity().getResources().getString(R.string.pref_key_current_timetable_filename), "[none]");
        activeAB = 0;

        saveButton = (Button) v.findViewById(R.id.bSaveABSelection);
        setUpABCard(v);
        setUpSaveButton();

        return v;
    }

    @Override
    public void onPause(){
        //saveData();
        super.onPause();
    }

    private void setUpABCard(View v){
        final TextView tvNumberABWeeks = (TextView) v.findViewById(R.id.ab_tvNumberABWeeks);
        final TextView tvABPreviewTitle = (TextView) v.findViewById(R.id.ab_tvABPreviewTitle);
        final TextView tvABPreview = (TextView) v.findViewById(R.id.ab_tvABPreview);
        switchAB = (Switch) v.findViewById(R.id.ab_switch_a_b_week);
        final Button bABPickDown = (Button) v.findViewById(R.id.ab_bABNrPickDown);
        final Button bABPickUp = (Button) v.findViewById(R.id.ab_bABNrPickUp);
        etABPick = (EditText) v.findViewById(R.id.ab_etNrPick);

        final LinearLayout ll_select = (LinearLayout) v.findViewById(R.id.ll_active_ab);
        final CardView cv_select = (CardView) v.findViewById(R.id.cv_active_ab);

        switchAB.requestFocus();

        final RadioGroup rg = new RadioGroup(getActivity());
        LinearLayout.LayoutParams rglp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int card_padding = (int) (getActivity().getResources().getDimension(R.dimen.card_padding)+0.5f);
        rglp.setMargins(card_padding, card_padding, card_padding, card_padding);
        rg.setLayoutParams(rglp);
        ll_select.addView(rg);

        String[] allABs = DataStorageHandler.AllABs(getActivity(), ttFolder);
        if (allABs.length > 1){
            switchAB.setChecked(true);
            tvNumberABWeeks.setTextColor(getResources().getColor(R.color.Text));
            tvABPreviewTitle.setTextColor(getResources().getColor(R.color.Text));
            tvABPreview.setTextColor(getResources().getColor(R.color.Text));
            bABPickDown.setEnabled(true);
            bABPickUp.setEnabled(true);
            etABPick.setEnabled(true);
            cv_select.setVisibility(View.VISIBLE);

        } else {
            switchAB.setChecked(false);
            tvNumberABWeeks.setTextColor(getResources().getColor(R.color.Disabled));
            tvABPreviewTitle.setTextColor(getResources().getColor(R.color.Disabled));
            tvABPreview.setTextColor(getResources().getColor(R.color.Disabled));
            bABPickDown.setEnabled(false);
            bABPickUp.setEnabled(false);
            etABPick.setEnabled(false);
            cv_select.setVisibility(View.GONE);
        }

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
                    cv_select.setVisibility(View.VISIBLE);
                } else {
                    tvNumberABWeeks.setTextColor(getResources().getColor(R.color.Disabled));
                    tvABPreviewTitle.setTextColor(getResources().getColor(R.color.Disabled));
                    tvABPreview.setTextColor(getResources().getColor(R.color.Disabled));
                    bABPickDown.setEnabled(false);
                    bABPickUp.setEnabled(false);
                    etABPick.setEnabled(false);
                    cv_select.setVisibility(View.GONE);
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
                /*int count = rg.getChildCount();
                rg.removeViewAt(count -1);*/
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
                /*int count = rg.getChildCount();
                if (count <= alphabet.length) {
                    RadioButton rb = new RadioButton(getActivity());
                    rb.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    rb.setText(alphabet[count]);
                    rg.addView(rb);
                }*/
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

                // Update the selector
                final int currentChilds = rg.getChildCount();
                if (numbr < currentChilds){
                    for (int r = currentChilds-1; r >= numbr ; r--){
                        rg.removeViewAt(numbr);
                        if (rbList.get(numbr).isChecked()) rbList.get(numbr-1).setChecked(true);
                        rbList.remove(numbr);
                    }
                } else if (numbr > currentChilds) {
                    for (int a = currentChilds; a < numbr && a < alphabet.length; a++) {
                        RadioButton rb = new RadioButton(getActivity());
                        rb.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        rb.setText(alphabet[a]);
                        rb.setId(rg.getChildCount());
                        rg.addView(rb);
                        rbList.add(rb);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        etABPick.setText(String.valueOf(allABs.length));
        final int currentAB = DataStorageHandler.getCurrentAB(getActivity(), ttFolder, Calendar.getInstance());
        for (int a = 0; a < allABs.length && a < alphabet.length; a++){
            /*RadioButton rb = new RadioButton(getActivity());
            rb.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rb.setText(alphabet[a]);
            rb.setId(rg.getChildCount());
            rg.addView(rb);
            rbList.add(rb);*/
            if (currentAB == a && rbList.size()>a) rbList.get(a).setChecked(true);
        }
    }

    private void setUpSaveButton(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            saveButton.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.button_color_state_list));
            saveButton.setTextColor(getActivity().getResources().getColor(R.color.TextDarkBg));
        } else {
            saveButton.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newABs = 1;
                if (switchAB.isChecked() && DataStorageHandler.isStringNumeric(etABPick.getText().toString()))
                    newABs = Integer.parseInt(etABPick.getText().toString());
                DataStorageHandler.AddRemoveABs(getActivity(), ttFolder, newABs);

                int active = 0;
                for (int r = 0; r < rbList.size(); r++){
                    if (rbList.get(r).isChecked()) active = r;
                }
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.WEEK_OF_YEAR, -1 * active);
                final String week_a_reference = DataStorageHandler.formatDateGeneralFormat(getActivity(), cal);
                System.out.println(active + ": "+week_a_reference);
                prefs.edit().putString("week_a_reference", week_a_reference).apply();

                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
            }
        });
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
