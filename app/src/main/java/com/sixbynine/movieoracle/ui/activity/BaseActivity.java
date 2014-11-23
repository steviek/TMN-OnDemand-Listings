package com.sixbynine.movieoracle.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

import com.crittercism.app.Crittercism;
import com.flurry.android.FlurryAgent;
import com.sixbynine.movieoracle.util.Keys;

/**
 * Created by steviekideckel on 11/2/14.
 */
public abstract class BaseActivity extends ActionBarActivity{

    private Toolbar mToolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        //mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //ViewCompat.setElevation(mToolbar, getResources().getDimensionPixelOffset(R.dimen.toolbar_elevation));
        //setSupportActionBar(mToolbar);

    }

    protected Toolbar getToolbar(){
        return mToolbar;
    }

    protected void setElevation(View view, int dip){
        ViewCompat.setElevation(view, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crittercism.initialize(getApplicationContext(), "5471a99308ebc1199c000002");
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
