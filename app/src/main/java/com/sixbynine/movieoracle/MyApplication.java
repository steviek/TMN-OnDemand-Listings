package com.sixbynine.movieoracle;

import android.app.Application;

import com.sixbynine.movieoracle.util.Prefs;

/**
 * Created by steviekideckel on 10/23/14.
 */
public class MyApplication extends Application{

    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Prefs.onApplicationCreated();
    }

    public static MyApplication getInstance(){
        return sInstance;
    }
}
