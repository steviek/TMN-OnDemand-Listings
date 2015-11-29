package com.sixbynine.movieoracle.ui.activity;

import android.support.v7.app.AppCompatActivity;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.Subscribes;

public abstract class BaseActivity extends AppCompatActivity {

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
}
