package com.sixbynine.movieoracle;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.sixbynine.movieoracle.list.NewMoviesFragment;
import com.sixbynine.movieoracle.media.Catalogue;
import com.sixbynine.movieoracle.sql.allmedia.AllMediaDAO;
import com.sixbynine.movieoracle.sql.ondemandlistings.OnDemandListingsDAO;
import com.sixbynine.movieoracle.util.Keys;

public class MainActivity extends ActionBarActivity {
	public static final String TITLES_LIST = "titles_list";
	public static final String FREQUENCY = "frequency";
	public static final String NUM_TOP_RATED = "numTopRated";
	
	public Catalogue catalogue;

	private Fragment mContent;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuFragment mMenuFragment;
    private boolean mDrawerIsOpen;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		if(savedInstanceState == null){
			catalogue = getIntent().getParcelableExtra("catalogue");
			//mContent = new MoviesListFragment();
            mContent = NewMoviesFragment.newInstance(catalogue);
		}else{
			catalogue = savedInstanceState.getParcelable("catalogue");
			if(catalogue == null) catalogue = getIntent().getParcelableExtra("catalogue");
			
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		}

		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, mContent)
		.commit();

        mMenuFragment = new MenuFragment();
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, new MenuFragment())
		.commit();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close){

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mDrawerIsOpen = false;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mDrawerIsOpen = true;
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
    	case R.id.menu_settings:
    		launchSettings();
    		return true;
    	case R.id.menu_about:
    		Intent intent = new Intent(this, AboutFragment.class);
    		startActivity(intent);
    		return true;
    	case android.R.id.home:
            toggle();
    		return true;
    	default: 
    			return super.onOptionsItemSelected(item);
    	}
	}
	
	private void launchSettings(){
		Intent intent = new Intent(this, SettingsFragment.class);
		intent.putExtra(FREQUENCY, SplashActivity.checkOnDemandFrequency);
		intent.putExtra(NUM_TOP_RATED, SplashActivity.showTopRatedNumber);
		startActivity(intent);
	}

    private void toggle(){
        if(mDrawerIsOpen){
            mDrawerLayout.closeDrawers();
        }else{
            if(Build.VERSION.SDK_INT >= 14){
                mDrawerLayout.openDrawer(Gravity.START);
            }else{
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        }
    }
	
	

	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(BuildConfig.DEBUG) Log.i(((Object) this).getClass().getName(), "onDestroy() called");
		
		AllMediaDAO.destroy();
		OnDemandListingsDAO.destroy();
		//TODO: Destroy splash Activity
	}
	
	/* Prevent app from being killed on back */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Back?
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Back
            moveTaskToBack(true);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_MENU){
        	toggle();
        	return true;
        }
        else {
            // Return
            return super.onKeyDown(keyCode, event);
        }
    }

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		if(mDrawerIsOpen){
            toggle();
        }
	}

	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("catalogue", catalogue);
	}

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Keys.FLURRY_API_KEY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}
