package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by M on 30.07.2015.
 */
public class ShareAppFragment extends Fragment {

    public static final String DEFAULT_EDIT_FRAGMENT_TAG = "editFragmentTag";

    private Button bShare;
    private EditText editText1;
    private TextView textView1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_share_app, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");

        // needed to indicate that the fragment would
        // like to add items to the Options Menu
        setHasOptionsMenu(true);
        // update the actionbar to show the up carat/affordance
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int ttColor = ((MainActivity) getActivity()).getResources().getColor(R.color.color_home);
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ttColor));

        bShare = (Button) v.findViewById(R.id.share);
        editText1 = (EditText) v.findViewById(R.id.editShareText);
        textView1 = (TextView) v.findViewById(R.id.tvLink);

        bShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customText = editText1.getText().toString();
                String linkText = textView1.getText().toString();
                String textToSend = customText + "\n" + linkText;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            }
        });

        return v;
    }

    @Override
    public void onPause(){
        ((MainActivity) getActivity()).endShareAppFragment();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // called when up arrow in AppBar is pressed
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
