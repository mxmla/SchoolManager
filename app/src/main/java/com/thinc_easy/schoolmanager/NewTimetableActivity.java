package com.thinc_easy.schoolmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class NewTimetableActivity extends ActionBarActivity {

    private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_timetable);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

		Fragment mNewTimetableFragment = getSupportFragmentManager().findFragmentByTag
				(NewTimetableFragment.DEFAULT_EDIT_FRAGMENT_TAG);


        if (mNewTimetableFragment == null) {
            mNewTimetableFragment = new NewTimetableFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, mNewTimetableFragment,
                    NewTimetableFragment.DEFAULT_EDIT_FRAGMENT_TAG);
            transaction.commit();
        }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_timetable, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
/*
	private void setDefaultFont() {

	    try {
	        final Typeface bold = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");
	        final Typeface italic = Typeface.createFromAsset(getAssets(), "Roboto-Italic.ttf");
	        final Typeface boldItalic = Typeface.createFromAsset(getAssets(), "Roboto-BoldItalic.ttf");
	        final Typeface regular = Typeface.createFromAsset(getAssets(),"Roboto-Bold.ttf");
	        final Typeface light = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
	        final Typeface lightItalic = Typeface.createFromAsset(getAssets(), "Roboto-LightItalic.ttf");
	        final Typeface thin = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
	        final Typeface thinItalic = Typeface.createFromAsset(getAssets(), "Roboto-ThinItalic.ttf");
	        final Typeface condensedLight = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Light.ttf");
	        final Typeface condensedLightItalic = Typeface.createFromAsset(getAssets(), "RobotoCondensed-LightItalic.ttf");

	        Field DEFAULT = Typeface.class.getDeclaredField("DEFAULT");
	        DEFAULT.setAccessible(true);
	        DEFAULT.set(null, regular);

	        Field DEFAULT_BOLD = Typeface.class.getDeclaredField("DEFAULT_BOLD");
	        DEFAULT_BOLD.setAccessible(true);
	        DEFAULT_BOLD.set(null, bold);

	        Field sDefaults = Typeface.class.getDeclaredField("sDefaults");
	        sDefaults.setAccessible(true);
	        sDefaults.set(null, new Typeface[]{
	                regular, bold, italic, boldItalic, light, lightItalic, thin, thinItalic, 
	                condensedLight, condensedLightItalic
	        });

	    } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

	}
	
}
*/

/*
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
 
import com.example.common.activities.SampleActivityBase;
import com.example.common.logger.Log;
 
import com.example.sa_test2.cardstream.CardStream;
import com.example.sa_test2.cardstream.CardStreamFragment;
import com.example.sa_test2.cardstream.CardStreamState;
import com.example.sa_test2.cardstream.OnCardClickListener;
import com.example.sa_test2.cardstream.StreamRetentionFragment;
 
public class NewTimetableActivity extends SampleActivityBase implements CardStream {
    public static final String TAG = "NewTimetableActivity";
    public static final String FRAGTAG = "NewTimetableFragment";
 
    private CardStreamFragment mCardStreamFragment;
 
    private StreamRetentionFragment mRetentionFragment;
    private static final String RETENTION_TAG = "retention";
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_timetable);
 
        FragmentManager fm = getSupportFragmentManager();
        NewTimetableFragment fragment =
                (NewTimetableFragment) fm.findFragmentByTag(FRAGTAG);
 
        if (fragment == null) {
            FragmentTransaction transaction = fm.beginTransaction();
            fragment = new NewTimetableFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }
 
        // Use fragment as click listener for cards, but must implement correct interface
        if(!(fragment instanceof OnCardClickListener)){
            throw new ClassCastException("BatchStepSensorFragment must " +
                    "implement OnCardClickListener interface.");
        }
        OnCardClickListener clickListener = (OnCardClickListener) fm.findFragmentByTag(FRAGTAG);
 
        mRetentionFragment = (StreamRetentionFragment) fm.findFragmentByTag(RETENTION_TAG);
        if (mRetentionFragment == null) {
            mRetentionFragment = new StreamRetentionFragment();
            fm.beginTransaction().add(mRetentionFragment, RETENTION_TAG).commit();
        } else {
            // If the retention fragment already existed, we need to pull some state.
            // pull state out
            CardStreamState state = mRetentionFragment.getCardStream();
 
            // dump it in CardStreamFragment.
            mCardStreamFragment =
                    (CardStreamFragment) fm.findFragmentById(R.id.fragment_cardstream);
            mCardStreamFragment.restoreState(state, clickListener);
        }
    }
 
    public CardStreamFragment getCardStream() {
        if (mCardStreamFragment == null) {
            mCardStreamFragment = (CardStreamFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_cardstream);
        }
        return mCardStreamFragment;
    }
 
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CardStreamState state = getCardStream().dumpState();
        mRetentionFragment.storeCardStream(state);
    }
    */
}
