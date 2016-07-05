package com.thinc_easy.schoolmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class DialogColorChooser extends DialogFragment implements View.OnClickListener{
	Communicator communicator;
	private Tracker mTracker;
	private String fragmentName;
	private TextView title;
	private Button bOK, bCancel;
	private Button cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9, cb10, cb11, cb12, cb13, cb14, cb15, cb16, cb17, cb18, cb19, cb20, cb21;
	private String selected, which, whichFragment;
	private int[] colors = {R.color.blue_light, R.color.blue_dark, R.color.purple_light, R.color.purple_light, R.color.green_light, R.color.green_dark,
			R.color.orange_light, R.color.orange_dark, R.color.red_light, R.color.red_dark, R.color.black, R.color.white, R.color.gray_light, R.color.gray_dark};
	private int[] sColors = {R.color.black, R.color.black, R.color.black, R.color.black, R.color.black, R.color.black, 
			R.color.black, R.color.black, R.color.black, R.color.black, R.color.orange_dark, R.color.orange_dark, R.color.black, R.color.black};
	private int[] colorIntsOld = {0xff33B5E5, 0xff0099CC, 0xffAA66CC, 0xff9933CC, 0xff99CC00, 0xff669900,
			0xffFFBB33, 0xffFF8800, 0xffFF4444, 0xffCC0000, 0xff000000, 0xffFFFFFF};
	private int[] sColorIntsOld = {0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000,
			0xff000000, 0xff000000, 0xff000000, 0xffFF8800, 0xffFF8800};
	private String[] colorNamesOld = {"blue_light", "blue_dark", "purple_light", "purple_dark", "green_light", "green_dark",
			"orange_light", "orange_dark", "red_light", "red_dark", "black", "white", "gray_light", "gray_dark"};
    private String[] colorNames = {"red", "pink", "purple", "deep_purple", "indigo", "blue",
            "light_blue", "cyan", "teal", "green", "light_green", "lime", "yellow", "amber",
            "ornge", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
    private int[] colorInts = {0xffF44336, 0xffE91E63, 0xff9C27B0, 0xff673AB7, 0xff3F51B5, 0xff2196F3,
            0xff03A9F4, 0xff00BCD4, 0xff009688, 0xff4CAF50, 0xff8BC34A, 0xffCDDC39,
            0xffFFEB3B, 0xffFFC107, 0xffFF9800, 0xffFF5722, 0xff795548, 0xff9E9E9E, 0xff607D8B, 0xff000000, 0xffFFFFFF};
    private int[] sColorInts = {0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000,
            0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000,
            0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xffFF8800, 0xffFF8800};



    public void onAttach(Activity activity){
		super.onAttach(activity);
		communicator = (Communicator) activity;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		fragmentName = "DialogColorChooser";
		// Obtain the shared Tracker instance.
		SchoolManager application = (SchoolManager) getActivity().getApplication();
		mTracker = application.getDefaultTracker();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_color_chooser, null);
		builder.setView(view);
		which = getArguments().getString("which");
		whichFragment = getArguments().getString("whichFragment");
		selected = "-";
		
		title = (TextView) view.findViewById(R.id.tvTitle);
		if (which == "background") title.setText(getResources().getString(R.string.background));
		if (which == "text") title.setText(getResources().getString(R.string.text));
		
		setUpButtons(view);
		
		bOK = (Button) view.findViewById(R.id.buttonOK);
		bOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				communicator.onDialogMessage(selected, which, whichFragment);
				dismiss();
			}
		});
		bCancel = (Button) view.findViewById(R.id.buttonCancel);
		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		Dialog dialog = builder.create();
		return dialog;
	}
	/*
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setCancelable(false);
		return inflater.inflate(R.layout.dialog_color_chooser, null);
	}*/

	@Override
	public void onClick(View v) {
		/*if (v.getId() == ){
			communicator.onDialogMessage("white");
			dismiss();
		}*/
	}
	
	private void setUpButtons(View v){
		cb1 = (Button) v.findViewById(R.id.button1);
		cb2 = (Button) v.findViewById(R.id.button2);
		cb3 = (Button) v.findViewById(R.id.button3);
		cb4 = (Button) v.findViewById(R.id.button4);
		cb5 = (Button) v.findViewById(R.id.button5);
		cb6 = (Button) v.findViewById(R.id.button6);
		cb7 = (Button) v.findViewById(R.id.button7);
		cb8 = (Button) v.findViewById(R.id.button8);
		cb9 = (Button) v.findViewById(R.id.button9);
		cb10 = (Button) v.findViewById(R.id.button10);
		cb11 = (Button) v.findViewById(R.id.button11);
		cb12 = (Button) v.findViewById(R.id.button12);
        cb13 = (Button) v.findViewById(R.id.button13);
        cb14 = (Button) v.findViewById(R.id.button14);
        cb15 = (Button) v.findViewById(R.id.button15);
        cb16 = (Button) v.findViewById(R.id.button16);
        cb17 = (Button) v.findViewById(R.id.button17);
        cb18 = (Button) v.findViewById(R.id.button18);
        cb19 = (Button) v.findViewById(R.id.button19);
        cb20 = (Button) v.findViewById(R.id.button20);
        cb21 = (Button) v.findViewById(R.id.button21);
		
		final Button[] buttons = {cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9, cb10, cb11, cb12, cb13, cb14, cb15, cb16, cb17, cb18, cb19, cb20, cb21};
		
		for (int i = 0; i < buttons.length; i++){
			/*GradientDrawable gd = (GradientDrawable) buttons[i].getBackground().mutate();
			gd.setColor(colors[i]);*/
			//((GradientDrawable)buttons[i].getBackground()).setColor(colors[i]);


			final float scale = getActivity().getResources().getDisplayMetrics().density;
			int bounds = (int) (56 * scale + 0.5f);
			
			GradientDrawable gd = new GradientDrawable();
			gd.setShape(1);
			gd.setColor(colorInts[i]);
			gd.setBounds(0, 0, bounds, bounds);
			gd.setSize(bounds, bounds);
			
			if (i == 20){
				gd.setStroke(1, 0xff000000);
			} else {
				gd.setStroke(3, colorInts[i]);
			}
			buttons[i].setBackgroundDrawable(gd);
			
			final int sColorInt = sColorInts[i];
			final String colorName = colorNames[i];
			final Button button = buttons[i];
			buttons[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selected = colorName;
					for (int i2 = 0; i2 < buttons.length; i2++){
						GradientDrawable gd2 = (GradientDrawable) buttons[i2].getBackground().mutate();
						gd2.setStroke(3, colorInts[i2]);
					}
					GradientDrawable gd3 = (GradientDrawable) button.getBackground().mutate();
					gd3.setStroke(3, sColorInt);
					button.setBackgroundDrawable(gd3);
				}
			});
				
		}
	}

	@Override
	public void onResume(){
		super.onResume();

		Log.i("Analytics", "Setting screen name: " + fragmentName);
		mTracker.setScreenName("Image~" + fragmentName);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}
	
	interface Communicator{
		public void onDialogMessage(String message, String which, String whichFragment);
	}
}
