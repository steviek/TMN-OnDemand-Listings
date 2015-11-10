package com.sixbynine.movieoracle.ui.activity;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.Subscribes;
import com.sixbynine.movieoracle.util.Keys;

public abstract class BaseActivity extends AppCompatActivity {

    protected void setElevation(View view, int dip){
        ViewCompat.setElevation(view,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Keys.FLURRY_API_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.getClass().getAnnotation(Subscribes.class) != null) {
            MyApplication.getInstance().getBus().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.getClass().getAnnotation(Subscribes.class) != null) {
            MyApplication.getInstance().getBus().unregister(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @NonNull
    @Override
    public ActionBar getSupportActionBar() {
        ActionBar actionBar = super.getSupportActionBar();
        if (actionBar == null) {
            throw new NullPointerException();
        }
        return actionBar;
    }
}
